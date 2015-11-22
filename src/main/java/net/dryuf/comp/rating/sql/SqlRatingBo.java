/*
 * Dryuf framework
 *
 * ----------------------------------------------------------------------------------
 *
 * Copyright (C) 2000-2015 Zbyněk Vyškovský
 *
 * ----------------------------------------------------------------------------------
 *
 * LICENSE:
 *
 * This file is part of Dryuf
 *
 * Dryuf is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * Dryuf is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dryuf; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * @author	2000-2015 Zbyněk Vyškovský
 * @link	mailto:kvr@matfyz.cz
 * @link	http://kvr.matfyz.cz/software/java/dryuf/
 * @link	http://github.com/dryuf/
 * @license	http://www.gnu.org/licenses/lgpl.txt GNU Lesser General Public License v3
 */

package net.dryuf.comp.rating.sql;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import net.dryuf.comp.rating.RatingHandler;
import net.dryuf.comp.rating.RatingHeader;
import net.dryuf.comp.rating.bo.RatingBo;
import net.dryuf.comp.rating.dao.RatingHeaderDao;
import net.dryuf.comp.rating.dao.RatingRecordDao;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;
import net.dryuf.util.MapUtil;


public class SqlRatingBo extends java.lang.Object implements RatingBo
{
	public				SqlRatingBo()
	{
	}

	@Override
	public EntityHolder<RatingHeader> getRatingObject(CallerContext callerContext, long ratingId)
	{
		List<EntityHolder<RatingHeader>> objects = new LinkedList<EntityHolder<RatingHeader>>();
		if (ratingHeaderDao.listDynamic(objects, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("ratingId", (Object)ratingId), null, null, null) == 0)
			return null;
		return objects.get(0);
	}

	@Override
	public EntityHolder<RatingHeader> getRatingObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		List<EntityHolder<RatingHeader>> objects = new LinkedList<EntityHolder<RatingHeader>>();
		if (ratingHeaderDao.listDynamic(objects, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("refBase", (Object)refBase, "refKey", refKey), null, null, null) == 0)
			return null;
		return objects.get(0);
	}

	@Override
	public EntityHolder<RatingHeader> getCreateRatingObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<RatingHeader> objectHolder;
		if ((objectHolder = getRatingObjectRef(callerContext, refBase, refKey)) == null) {
			try {
				RatingHeader header = new RatingHeader();
				header.setRefBase(refBase);
				header.setRefKey(refKey);
				ratingHeaderDao.insert(header);
			}
			catch (net.dryuf.dao.DaoUniqueConstraintException ex) {
			}
			if ((objectHolder = getRatingObjectRef(callerContext, refBase, refKey)) == null) {
				throw new RuntimeException("failed to create rating object");
			}
		}
		return objectHolder;
	}

	@Override
	public RatingHandler		openCreateRatingRef(CallerContext callerContext, String refBase, String refKey, int maxRating)
	{
		return new SqlRatingHandler(this, getCreateRatingObjectRef(callerContext, refBase, refKey), maxRating);
	}

	@Override
	public boolean			deleteRatingStatic(CallerContext callerContext, long objectId)
	{
		this.cleanRating(objectId);
		ratingHeaderDao.removeByPk(objectId);
		return true;
	}

	public void			cleanRating(long ratingId)
	{
		ratingRecordDao.removeByCompos(ratingId);
		ratingHeaderDao.updateStatistics(ratingId);
	}

	@Override
	public boolean			deleteRatingStaticRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<RatingHeader> objectHolder;
		if ((objectHolder = getRatingObjectRef(callerContext, refBase, refKey)) != null) {
			this.deleteRatingStatic(callerContext, (objectHolder.getEntity()).getRatingId());
			return true;
		}
		return false;
	}

	@Inject
	protected RatingHeaderDao		ratingHeaderDao;

	public RatingHeaderDao		getRatingHeaderDao()
	{
		return this.ratingHeaderDao;
	}

	@Inject
	protected RatingRecordDao		ratingRecordDao;

	public RatingRecordDao		getRatingRecordDao()
	{
		return this.ratingRecordDao;
	}
}
