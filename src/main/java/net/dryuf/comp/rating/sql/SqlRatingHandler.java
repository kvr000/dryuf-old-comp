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

import java.util.List;
import java.util.concurrent.Callable;

import net.dryuf.comp.rating.RatingHandler;
import net.dryuf.comp.rating.RatingHeader;
import net.dryuf.comp.rating.RatingRecord;
import net.dryuf.comp.rating.dao.RatingHeaderDao;
import net.dryuf.comp.rating.dao.RatingRecordDao;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;


public class SqlRatingHandler extends java.lang.Object implements RatingHandler
{
	public				SqlRatingHandler(SqlRatingBo sqlRatingBo, EntityHolder<RatingHeader> ratingHeaderHolder, int maxRating)
	{
		this.ratingHeaderDao = sqlRatingBo.getRatingHeaderDao();
		this.ratingRecordDao = sqlRatingBo.getRatingRecordDao();

		this.callerContext = ratingHeaderHolder.getRole();
		this.ratingHeaderHolder = ratingHeaderHolder;
		this.ratingHeader = ratingHeaderHolder.getEntity();
		this.maxRating = maxRating;

	}

	public void			deleteRating()
	{
		this.cleanRating();
		ratingHeaderDao.removeByPk(ratingHeader.getRatingId());
	}

	public void			cleanRating()
	{
		ratingRecordDao.removeByCompos(ratingHeader.getRatingId());
		this.updateHeader();
	}

	public RatingHeader		getRatingDetail()
	{
		return this.ratingHeader;
	}

	public int			getMaxRating()
	{
		return this.maxRating;
	}

	public double			getRatingValue()
	{
		return this.ratingHeader.getRating();
	}

	public void			updateStat()
	{
	}

	public void			addRating(final long userId, final int value)
	{
		if (value > this.maxRating)
			throw new RuntimeException("value > maxRating");
		try {
			ratingRecordDao.runTransactionedNew(new Callable<Object>() {
				@Override
				public Object call()
				{
					ratingRecordDao.addRatingValue(ratingHeader.getRatingId(), userId, value);
					updateHeader();
					return null;
				}
			});
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void			removeUserRating(final long userId)
	{
		try {
			ratingRecordDao.runTransactionedNew(new Callable<Object>() {
				@Override
				public Object call()
				{
					ratingRecordDao.removeByPk(new RatingRecord.Pk(ratingHeader.getRatingId(), userId));
					updateHeader();
					return null;
				}
			});
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<RatingRecord>	listRatings()
	{
		throw new UnsupportedOperationException("listRatings");
	}

	@Override
	public void			refresh()
	{
		try {
			ratingHeader = ratingHeaderDao.runTransactionedNew(new Callable<RatingHeader>() {
				@Override
				public RatingHeader call() throws Exception
				{
					return ratingHeaderDao.loadByPk(ratingHeader.getPk());
				}
			});
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		ratingHeaderHolder.setEntity(ratingHeader);
	}

	protected void			updateHeader()
	{
		ratingHeaderDao.updateStatistics(ratingHeader.getRatingId());
	}

	protected int			maxRating;

	protected CallerContext		callerContext;

	public CallerContext		getCallerContext()
	{
		return this.callerContext;
	}

	protected RatingHeader		ratingHeader;

	protected EntityHolder<RatingHeader> ratingHeaderHolder;

	protected RatingHeaderDao	ratingHeaderDao;

	protected RatingRecordDao	ratingRecordDao;
}
