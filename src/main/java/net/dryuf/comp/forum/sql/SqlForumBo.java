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

package net.dryuf.comp.forum.sql;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import net.dryuf.comp.forum.ForumHandler;
import net.dryuf.comp.forum.ForumHeader;
import net.dryuf.comp.forum.dao.ForumHeaderDao;
import net.dryuf.comp.forum.dao.ForumRecordDao;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;


public class SqlForumBo extends java.lang.Object implements net.dryuf.comp.forum.bo.ForumBo
{
	public				SqlForumBo()
	{
	}

	public EntityHolder<ForumHeader> getForumObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		List<EntityHolder<ForumHeader>> headers = new LinkedList<EntityHolder<ForumHeader>>();
		if (forumHeaderDao.listDynamic(headers, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), net.dryuf.util.MapUtil.createLinkedHashMap("refBase", (Object)refBase, "refKey", refKey), null, null, null) == 0)
			return null;
		return headers.get(0);
	}

	public EntityHolder<ForumHeader> getCreateForumObjectRef(CallerContext callerContext, String refBase, String refKey, String label)
	{
		EntityHolder<ForumHeader> forumHolder;
		if ((forumHolder = getForumObjectRef(callerContext, refBase, refKey)) == null) {
			try {
				ForumHeader header = new ForumHeader();
				header.setRefBase(refBase);
				header.setRefKey(refKey);
				header.setLabel(label);
				forumHeaderDao.insert(header);
			}
			catch (net.dryuf.dao.DaoUniqueConstraintException ex) {
			}
			if ((forumHolder = getForumObjectRef(callerContext, refBase, refKey)) == null) {
				throw new RuntimeException("failed to create forum object");
			}
		}
		return forumHolder;
	}

	public ForumHandler		openCreateForumRef(CallerContext callerContext, String refBase, String refKey, String label)
	{
		return new SqlForumHandler(this, getCreateForumObjectRef(callerContext, refBase, refKey, label));
	}

	public boolean			deleteStaticRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<ForumHeader> forumHolder;
		if ((forumHolder = getForumObjectRef(callerContext, refBase, refKey)) != null) {
			this.deleteForum((forumHolder.getEntity()).getForumId());
			return true;
		}
		return false;
	}

	public void			deleteForum(long forumId)
	{
		this.cleanForum(forumId);
		forumHeaderDao.removeByPk(forumId);
	}

	public void			cleanForum(long forumId)
	{
		forumRecordDao.removeByCompos(forumId);
		this.updateForumHeader(forumId);
	}

	public void			updateForumHeader(long forumId)
	{
		forumHeaderDao.updateRecordStats(forumId);
	}

	public ForumHeaderDao		getForumHeaderDao()
	{
		return this.forumHeaderDao;
	}

	public ForumRecordDao		getForumRecordDao()
	{
		return this.forumRecordDao;
	}

	@Inject
	protected ForumHeaderDao	forumHeaderDao;

	@Inject
	protected ForumRecordDao	forumRecordDao;
}
