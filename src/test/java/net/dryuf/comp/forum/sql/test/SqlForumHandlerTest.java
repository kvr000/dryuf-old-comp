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

package net.dryuf.comp.forum.sql.test;

import java.util.LinkedList;
import java.util.List;

import net.dryuf.core.Dryuf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.dryuf.comp.forum.ForumHandler;
import net.dryuf.comp.forum.ForumRecord;
import net.dryuf.comp.forum.sql.SqlForumBo;
import net.dryuf.comp.forum.sql.SqlForumHandler;
import net.dryuf.core.EntityHolder;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class SqlForumHandlerTest extends net.dryuf.tenv.AppTenvObject
{
	public SqlForumHandlerTest()
	{
	}

	public void			addForumComment(ForumHandler forumHandler, String comment)
	{
		ForumRecord record = new ForumRecord();
		record.setContent(comment);
		forumHandler.addComment(record);
	}

	public SqlForumHandler		initForum(int count)
	{
		SqlForumHandler forumHandler;
		forumHandler = (SqlForumHandler)getAppContainer().createBeaned(SqlForumBo.class, null).openCreateForumRef(createCallerContext(), Dryuf.dotClassname(SqlForumHandlerTest.class), "test", "test");
		forumHandler.cleanForum();

		for (int i = 0; i < count; i++) {
			addForumComment(forumHandler, "comment "+i);
		}

		return forumHandler;
	}

	@Test
	public void                     testContent()
	{
		SqlForumHandler forumHandler = initForum(2);
		List<EntityHolder<ForumRecord>> comments = new LinkedList<EntityHolder<ForumRecord>>();
		Assert.assertEquals(2, forumHandler.listComments(comments, 0, null));
		Assert.assertEquals("comment 1", (comments.get(0).getEntity()).getContent());
		Assert.assertEquals("comment 0", (comments.get(1).getEntity()).getContent());
	}
}
