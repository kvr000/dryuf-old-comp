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

import java.util.List;

import net.dryuf.comp.poll.PollHeader;
import net.dryuf.comp.poll.PollOption;
import net.dryuf.comp.poll.dao.PollHeaderDao;
import net.dryuf.comp.poll.dao.PollOptionDao;
import net.dryuf.comp.poll.dao.PollRecordDao;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;


public class SqlPollHandler extends java.lang.Object implements net.dryuf.comp.poll.PollHandler
{
	public				SqlPollHandler(SqlPollBo owner, EntityHolder<PollHeader> pollHeaderHolder)
	{
		this.pollHeaderHolder = pollHeaderHolder;
		this.pollHeader = pollHeaderHolder.getEntity();

		this.pollHeaderDao = owner.getPollHeaderDao();
		this.pollOptionDao = owner.getPollOptionDao();
		this.pollRecordDao = owner.getPollRecordDao();
	}

	public CallerContext		getCallerContext()
	{
		return pollHeaderHolder.getRole();
	}

	public void			deletePoll()
	{
		this.cleanPoll();
		cleanOptions();
		pollHeaderDao.remove(pollHeader);
	}

	public void			cleanPoll()
	{
		pollOptionDao.removeByCompos(pollHeader.getPollId());
		updateHeader();
	}

	public void			cleanOptions()
	{
		pollOptionDao.removeByCompos(pollHeader.getPollId());
		this.updateHeader();
	}

	public void			updateHeader()
	{
		pollHeaderDao.updateStatistics(pollHeader.getPollId());
		pollHeader = pollHeaderDao.loadByPk(pollHeader.getPollId());
	}

	public PollHeader		getPollDetail()
	{
		return this.pollHeader;
	}

	public EntityHolder<PollHeader>	getHolder()
	{
		return this.pollHeaderHolder;
	}

	public List<PollOption>		getPollOptions()
	{
		return pollOptionDao.listByCompos(pollHeader.getPollId());
	}

	@Override
	public long			getPollTotal()
	{
		return this.pollHeader.getTotalVotes();
	}

	@Override
	public void			addPollVote(long userId, int optionId)
	{
		pollRecordDao.addPollVote(pollHeader.getPollId(), userId, optionId);
		this.updateHeader();
	}

	protected PollHeaderDao		pollHeaderDao;
	protected PollOptionDao		pollOptionDao;
	protected PollRecordDao		pollRecordDao;

	protected PollHeader		pollHeader;
	protected EntityHolder<PollHeader> pollHeaderHolder;
};


