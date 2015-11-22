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

import javax.inject.Inject;

import net.dryuf.comp.poll.PollGroup;
import net.dryuf.comp.poll.PollGroupHandler;
import net.dryuf.comp.poll.PollHandler;
import net.dryuf.comp.poll.PollHeader;
import net.dryuf.comp.poll.bo.PollBo;
import net.dryuf.comp.poll.dao.PollGroupDao;
import net.dryuf.comp.poll.dao.PollHeaderDao;
import net.dryuf.comp.poll.dao.PollOptionDao;
import net.dryuf.comp.poll.dao.PollRecordDao;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;
import net.dryuf.service.time.TimeBo;
import net.dryuf.util.MapUtil;


public class SqlPollBo extends java.lang.Object implements PollBo
{
	public				SqlPollBo()
	{
	}

	@Override
	public EntityHolder<PollGroup>	getGroupObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		List<EntityHolder<PollGroup>> groups = new LinkedList<EntityHolder<PollGroup>>();
		if (pollGroupDao.listDynamic(groups, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("refBase", (Object)refBase, "refKey", refKey), null, null, null) == 0)
			return null;
		return groups.get(0);
	}

	@Override
	public EntityHolder<PollGroup>	getGroupObject(CallerContext callerContext, long groupId)
	{
		List<EntityHolder<PollGroup>> groups = new LinkedList<EntityHolder<PollGroup>>();
		if (pollGroupDao.listDynamic(groups, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("groupId", (Object)groupId), null, null, null) == 0)
			return null;
		return groups.get(0);
	}

	@Override
	public EntityHolder<PollGroup>	getCreateGroupObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<PollGroup> groupHolder;
		if ((groupHolder = getGroupObjectRef(callerContext, refBase, refKey)) == null) {
			try {
				PollGroup header = new PollGroup();
				header.setRefBase(refBase);
				header.setRefKey(refKey);
				pollGroupDao.insert(header);
			}
			catch (net.dryuf.dao.DaoUniqueConstraintException ex) {
			}
			if ((groupHolder = getGroupObjectRef(callerContext, refBase, refKey)) == null) {
				throw new RuntimeException("failed to create poll group");
			}
		}
		return groupHolder;
	}

	@Override
	public PollGroupHandler		openCreateGroupRef(CallerContext callerContext, String refBase, String refKey)
	{
		return new SqlPollGroupHandler(this, getCreateGroupObjectRef(callerContext, refBase, refKey));
	}

	@Override
	public boolean			deleteGroupStaticRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<PollGroup> groupHolder;
		if ((groupHolder = getGroupObjectRef(callerContext, refBase, refKey)) != null) {
			this.deleteGroupStatic(groupHolder.getRole(), (groupHolder.getEntity()).getGroupId());
			return true;
		}
		return false;
	}

	@Override
	public boolean			deleteGroupStatic(CallerContext callerContext, long groupId)
	{
		EntityHolder<PollGroup> pollGroup = getGroupObject(callerContext, groupId);
		if (pollGroup == null)
			return false;
		new SqlPollGroupHandler(this, pollGroup).deleteGroup();
		return true;
	}

	@Override
	public EntityHolder<PollHeader>	getPollObject(CallerContext callerContext, long pollId)
	{
		List<EntityHolder<PollHeader>> objects = new LinkedList<EntityHolder<PollHeader>>();
		if (pollHeaderDao.listDynamic(objects, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("pollId", (Object)pollId), null, null, null) == 0)
			return null;
		return objects.get(0);
	}

	@Override
	public EntityHolder<PollHeader>	getPollObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		List<EntityHolder<PollHeader>> objects = new LinkedList<EntityHolder<PollHeader>>();
		if (pollHeaderDao.listDynamic(objects, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("refBase", (Object)refBase, "refKey", refKey), null, null, null) == 0)
			return null;
		return objects.get(0);
	}

	@Override
	public EntityHolder<PollHeader>	getCreatePollObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<PollHeader> objectHolder;
		if ((objectHolder = getPollObjectRef(callerContext, refBase, refKey)) == null) {
			try {
				PollHeader header = new PollHeader();
				header.setRefBase(refBase);
				header.setRefKey(refKey);
				pollHeaderDao.insert(header);
			}
			catch (net.dryuf.dao.DaoUniqueConstraintException ex) {
			}
			if ((objectHolder = getPollObjectRef(callerContext, refBase, refKey)) == null) {
				throw new RuntimeException("failed to create poll object");
			}
		}
		return objectHolder;
	}

	@Override
	public PollHandler		openCreatePollRef(CallerContext callerContext, String refBase, String refKey)
	{
		return new SqlPollHandler(this, getCreatePollObjectRef(callerContext, refBase, refKey));
	}

	@Override
	public boolean			deletePollStatic(CallerContext callerContext, long objectId)
	{
		this.cleanPoll(objectId);
		pollHeaderDao.removeByPk(objectId);
		return true;
	}

	public void			cleanPoll(long pollId)
	{
		pollRecordDao.removeByCompos(pollId);
		pollHeaderDao.updateStatistics(pollId);
	}

	@Override
	public boolean			deletePollStaticRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<PollHeader> objectHolder;
		if ((objectHolder = getPollObjectRef(callerContext, refBase, refKey)) != null) {
			this.deletePollStatic(objectHolder.getRole(), (objectHolder.getEntity()).getPollId());
			return true;
		}
		return false;
	}

	@Inject
	protected PollGroupDao		pollGroupDao;

	public PollGroupDao		getPollGroupDao()
	{
		return this.pollGroupDao;
	}

	@Inject
	protected PollHeaderDao		pollHeaderDao;

	public PollHeaderDao		getPollHeaderDao()
	{
		return this.pollHeaderDao;
	}

	@Inject
	protected PollOptionDao		pollOptionDao;

	public PollOptionDao		getPollOptionDao()
	{
		return this.pollOptionDao;
	}

	@Inject
	protected PollRecordDao		pollRecordDao;

	public PollRecordDao		getPollRecordDao()
	{
		return this.pollRecordDao;
	}

	@Inject
	protected TimeBo		timeBo;

	public TimeBo			getTimeBo()
	{
		return this.timeBo;
	}
}
