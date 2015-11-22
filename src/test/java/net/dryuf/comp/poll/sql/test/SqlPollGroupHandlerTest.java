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

package net.dryuf.comp.poll.sql.test;

import javax.validation.constraints.NotNull;

import net.dryuf.core.Dryuf;
import net.dryuf.tenv.AppTenvObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.dryuf.comp.poll.PollGroupHandler;
import net.dryuf.comp.poll.PollHandler;
import net.dryuf.comp.poll.PollHeader;
import net.dryuf.comp.poll.PollOption;
import net.dryuf.comp.poll.sql.SqlPollBo;
import net.dryuf.core.AppContainer;
import net.dryuf.core.AppContainerAware;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class SqlPollGroupHandlerTest extends AppTenvObject implements AppContainerAware
{
	@Override
	public void			afterAppContainer(@NotNull AppContainer appContainer)
	{
		super.afterAppContainer(appContainer);
		sqlPollBo = appContainer.postProcessBean(new SqlPollBo(), "pollBo", null);
	}

	public PollHandler		initPoll(CallerContext callerContext, String methodName)
	{
		PollGroupHandler pollGroupHandler = sqlPollBo.openCreateGroupRef(callerContext, SqlPollGroupHandlerTest.class.getName(), "test");
		EntityHolder<PollHeader> pollHolder;
		PollHandler pollHandler;
		if ((pollHolder = pollGroupHandler.getLastHeader()) != null) {
			PollHeader poll = pollHolder.getEntity();
			pollHandler = pollGroupHandler.openPoll(poll.getPollId());
			pollHandler.cleanPoll();
			pollHandler.cleanOptions();
		}
		else {
			PollHeader poll = new PollHeader();
			poll.setCreated(System.currentTimeMillis());
			poll.setRefBase(Dryuf.dotClassname(SqlPollGroupHandlerTest.class));
			poll.setRefKey(methodName);
			poll.setDescription("poll description");
			pollHandler = pollGroupHandler.createPoll(poll);
		}
		return pollHandler;
	}

	@Test
	public void			testHandler()
	{
		PollHandler pollHandler = initPoll(this.createCallerContext(), "testHandler");

		PollOption option;
		option = new PollOption();
		option.setPollId(pollHandler.getPollDetail().getPollId());
		option.setOptionId(1);
		option.setDescription("option 1");
		sqlPollBo.getPollOptionDao().insert(option);
		option = new PollOption();
		option.setPollId(pollHandler.getPollDetail().getPollId());
		option.setOptionId(2);
		option.setDescription("option 2");
		sqlPollBo.getPollOptionDao().insert(option);

		pollHandler.addPollVote(0, 1);
	}

	protected SqlPollBo		sqlPollBo;
}
