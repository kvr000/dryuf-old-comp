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

package net.dryuf.comp.poll.sql;

import java.util.LinkedList;
import java.util.List;

import net.dryuf.comp.poll.PollGroup;
import net.dryuf.comp.poll.PollHandler;
import net.dryuf.comp.poll.PollHeader;
import net.dryuf.comp.poll.dao.PollGroupDao;
import net.dryuf.comp.poll.dao.PollHeaderDao;
import net.dryuf.comp.poll.dao.PollOptionDao;
import net.dryuf.comp.poll.dao.PollRecordDao;
import net.dryuf.core.EntityHolder;


public class SqlPollGroupHandler extends java.lang.Object implements net.dryuf.comp.poll.PollGroupHandler
{
	public			SqlPollGroupHandler(SqlPollBo owner, EntityHolder<PollGroup> pollGroupHolder)
	{
		this.owner = owner;

		this.pollGroupHolder = pollGroupHolder;
		this.pollGroup = pollGroupHolder.getEntity();

		this.pollGroupDao = owner.getPollGroupDao();
		this.pollHeaderDao = owner.getPollHeaderDao();
	}

	public EntityHolder<PollGroup>		getHolder()
	{
		return this.pollGroupHolder;
	}

	public List<EntityHolder<PollHeader>> listHeaders()
	{
		List<EntityHolder<PollHeader>> headers = new LinkedList<EntityHolder<PollHeader>>();
		this.pollHeaderDao.listDynamic(headers, this.pollGroupHolder, null, null, null, null);
		return headers;
	}

	public EntityHolder<PollHeader>	getLastHeader()
	{
		Long pollId = pollGroupDao.getLatestHeaderId(pollGroup.getGroupId());
		if (pollId == null)
			return null;
		return pollHeaderDao.retrieveDynamic(pollGroupHolder, pollId);
	}

	public PollHandler		createPoll(PollHeader pollHeader)
	{
		pollHeader.setGroupId(pollGroup.getGroupId());
		pollHeaderDao.insert(pollHeader);
		return new SqlPollHandler(owner, new EntityHolder<PollHeader>(pollHeader, pollGroupHolder.getRole()));
	}

	public PollHandler		openPoll(long pollId)
	{
		EntityHolder<PollHeader> pollHeaderHolder = pollHeaderDao.retrieveDynamic(pollGroupHolder, pollId);
		if (pollHeaderHolder == null)
			return null;
		return new SqlPollHandler(owner, pollHeaderHolder);
	}

	public void			cleanPolls()
	{
		for (EntityHolder<PollHeader> pollHeaderHolder: this.listHeaders()) {
			owner.deletePollStatic(pollHeaderHolder.getRole(), (pollHeaderHolder.getEntity()).getPollId());
		}
	}

	public void			deleteGroup()
	{
		this.cleanPolls();
		pollGroupDao.removeByPk(pollGroup.getGroupId());
		this.pollGroup = null;
		this.pollGroupHolder = null;
	}

	protected PollGroupDao			pollGroupDao;
	protected PollOptionDao			pollOptionDao;
	protected PollHeaderDao			pollHeaderDao;
	protected PollRecordDao			pollRecordDao;

	protected PollGroup			pollGroup;
	protected EntityHolder<PollGroup>	pollGroupHolder;

	protected SqlPollBo			owner;
}
