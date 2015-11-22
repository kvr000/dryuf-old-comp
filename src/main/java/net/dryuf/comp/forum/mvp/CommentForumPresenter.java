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

package net.dryuf.comp.forum.mvp;

import java.util.LinkedList;
import java.util.List;

import net.dryuf.comp.forum.ForumHandler;
import net.dryuf.comp.forum.ForumRecord;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.Options;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;
import net.dryuf.srvui.Response;


public class CommentForumPresenter extends net.dryuf.mvp.ChildPresenter
{
	public			CommentForumPresenter(Presenter parentPresenter, Options options, ForumHandler forumHandler)
	{
		super(parentPresenter, options);

		this.forumHandler = forumHandler;
		this.getRootPresenter().addLinkedFile("css", PageUrl.createRooted("/css/net/dryuf/comp/forum/CommentForum.css"));
	}

	public void			prepare()
	{
		Response response = getRootPresenter().getResponse();
		response.setDateHeader("Expires", System.currentTimeMillis());
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, must revalidate");
		super.prepare();
	}

	public void			render()
	{
		super.render();

		List<EntityHolder<ForumRecord>> comments = new LinkedList<EntityHolder<ForumRecord>>();
		this.forumHandler.listComments(comments, 0, null);

		this.output("<table class=\"net-dryuf-forum-CommentForum\">\n");
		this.outputFormat("<tr><td colspan='2'>%W</td></tr>", CommentForumPresenter.class, "Add new comment");

		for (EntityHolder<ForumRecord> recordHolder: comments) {
			ForumRecord record = recordHolder.getEntity();
			this.output("<tr><td colspan='2'><hr/></td></tr>");
			outputFormat("<tr class=\"comment\"><td colspan='2'>%S</td>\n", record.getContent());
			this.output("</tr>\n");
		}
		this.output("</table>\n");
	}

	protected ForumHandler		forumHandler;
}
