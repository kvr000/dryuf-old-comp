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

import java.util.List;

import net.dryuf.comp.forum.ForumHeader;
import net.dryuf.comp.forum.ForumRecord;
import net.dryuf.comp.forum.dao.ForumHeaderDao;
import net.dryuf.comp.forum.dao.ForumRecordDao;
import net.dryuf.comp.forum.sql.SqlForumBo;
import net.dryuf.core.CallerContext;
import net.dryuf.core.Dryuf;
import net.dryuf.core.EntityHolder;
import net.dryuf.util.MapUtil;


public class SqlForumHandler extends net.dryuf.comp.forum.GenericForumHandler
{
	public				SqlForumHandler(SqlForumBo sqlForumBo, EntityHolder<ForumHeader> forumHeaderHolder)
	{
		super(forumHeaderHolder.getRole());

		this.sqlForumBo = sqlForumBo;
		this.callerContext = forumHeaderHolder.getRole();
		ForumHeader forumHeader = forumHeaderHolder.getEntity();

		this.forumHeaderHolder = forumHeaderHolder;
		this.forumHeader = forumHeader;

		this.forumHeaderDao = sqlForumBo.getForumHeaderDao();
		this.forumRecordDao = sqlForumBo.getForumRecordDao();
	}

	public long			listComments(List<EntityHolder<ForumRecord>> comments, long start, Long limit)
	{
		return forumRecordDao.listDynamic(comments, this.forumHeaderHolder, null, null, start, limit);
	}

	public long			addComment(ForumRecord forumRecord)
	{
		forumRecord.setForumId(forumHeader.getForumId());
		forumRecord.setCreated(System.currentTimeMillis());
		for (;;) {
			Long counter = forumHeaderDao.getMaxCounter(this.forumHeader.getForumId());
			if (counter == null)
				counter = 0L;
			forumRecord.setCounter(counter+1);
			try {
				forumRecordDao.insertTxNew(forumRecord);
				sqlForumBo.updateForumHeader(this.forumHeader.getForumId());;
				return forumRecord.getPk().getCounter();
			}
			catch (net.dryuf.dao.DaoPrimaryKeyConstraintException ex) {
				continue;
			}
		}
	}

	public EntityHolder<ForumRecord> loadComment(long counter)
	{
		return forumRecordDao.retrieveDynamic(forumHeaderHolder, new ForumRecord.Pk(this.forumHeader.getForumId(), counter));
	}

	public boolean			updateComment(long counter, String content)
	{
		EntityHolder<ForumRecord> forumRecordHolder;
		if ((forumRecordHolder = this.loadComment(counter)) == null)
			return false;
		this.forumRecordDao.updateDynamic(forumRecordHolder, (forumRecordHolder.getEntity()).getPk(), MapUtil.createLinkedHashMap("content", (Object)content, "lastEdit", System.currentTimeMillis()));
		return true;
	}

	public void			cleanForum()
	{
		forumRecordDao.removeByCompos(forumHeader.getForumId());
	}

	CallerContext			callerContext;

	public CallerContext		getCallerContext()
	{
		return this.callerContext;
	}

	SqlForumBo			sqlForumBo;

	protected ForumHeaderDao	forumHeaderDao;

	protected ForumRecordDao	forumRecordDao;

	protected ForumHeader		forumHeader;
	protected EntityHolder<ForumHeader> forumHeaderHolder;
}
