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
import net.dryuf.core.Dryuf;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.Options;
import net.dryuf.textual.DateTimeTextual;
import net.dryuf.textual.TextualManager;
import net.dryuf.security.textual.UserAccountTextual;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;
import net.dryuf.srvui.Response;


public class SimpleForumPresenter extends net.dryuf.mvp.ChildPresenter
{
	public			SimpleForumPresenter(Presenter parentPresenter, net.dryuf.core.Options options, ForumHandler forumHandler)
	{
		super(parentPresenter, options);

		this.options = options;

		this.forumHandler = forumHandler;
		this.cssClass = Dryuf.dashClassname(options.getOptionDefault("cssClass", Dryuf.dotClassname(SimpleForumPresenter.class)));
		this.cssFile = options.getOptionDefault("cssFile", null);
	}

	public void			prepare()
	{
		Response response = getRootPresenter().getResponse();
		response.setDateHeader("Expires", System.currentTimeMillis());
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, must revalidate");
		super.prepare();
		if (this.cssFile != null)
			this.getRootPresenter().addLinkedFile("css", PageUrl.createResource(this.cssFile));
	}

	public boolean			process()
	{
		if (this.getCallerContext().checkRole("Forum.add")) {
			new net.dryuf.comp.forum.mvp.SimpleForumFormPresenter(this, net.dryuf.core.Options.NONE, this.forumHandler);
		}
		else {
			new net.dryuf.mvp.NeedLoginPresenter(this, Options.buildListed("messageClass", SimpleForumPresenter.class, "message", "You need to --login-- to post messages."));
		}
		return super.process();
	}

	public void			render()
	{
		this.outputFormat("<div class=\"%S\">\n", this.cssClass);
		super.render();
		if (this.getCallerContext().checkRole("Forum.get")) {
			List<EntityHolder<ForumRecord>> comments = new LinkedList<EntityHolder<ForumRecord>>();
			this.forumHandler.listComments(comments, 0, null);
			UserAccountBo userAccountBo = this.options.getOptionDefault("userAccountBo", null);
			if (userAccountBo == null)
				userAccountBo = (UserAccountBo) getCallerContext().getBean("userAccountBo");
			UserAccountTextual userAccountTextual = options.getOptionDefault("userAccountTextual", null);
			if (userAccountTextual == null)
				userAccountTextual = TextualManager.createTextual(net.dryuf.security.textual.UserAccountTextual.class, this.getCallerContext());
			DateTimeTextual datetimeTextual = options.getOptionDefault("dateTimeTextual", null);
			if (datetimeTextual == null)
				datetimeTextual = TextualManager.createTextual(net.dryuf.textual.DateTimeTextual.class, this.getCallerContext());

			if (comments.size() == 0) {
				this.outputFormat("<p class='noComments'>%W</p>\n", SimpleForumPresenter.class, "No comments have been added yet.");
			}
			else {
				boolean even = true;
				this.outputFormat("<table class='comments'>\n", net.dryuf.core.Dryuf.dashClassname(this.cssClass));
				for (EntityHolder<ForumRecord> recordHolder: comments) {
					ForumRecord record = recordHolder.getEntity();
					this.outputFormat("<tr class='header-%s'><td><span class='author'>%K</span>, <span class='addedTime'>%K</span></td></tr><tr class='row-%s'><td class='content'>%S</td></tr>\n",
						even ? "even" : "odd", userAccountTextual, record.getUserId(), datetimeTextual, record.getCreated(), even ? "even" : "odd", record.getContent());
				}
				this.outputFormat("</table>\n");
			}
		}
		else {
			new net.dryuf.mvp.NeedLoginPresenter(this, net.dryuf.mvp.NoLeadChildPresenter.NOLEAD_OPTIONS.cloneAddingListed("messageClass", SimpleForumPresenter.class, "message", "You need to --login-- to see messages.")).render();
		}
		this.output("</div>\n");
	}

	protected ForumHandler		forumHandler;
	protected String		cssClass;
	protected String		cssFile;

	protected Options		options;
}
