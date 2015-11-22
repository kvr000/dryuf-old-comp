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

import net.dryuf.core.Dryuf;
import net.dryuf.text.markdown.MarkdownService;
import org.apache.commons.lang3.StringUtils;

import net.dryuf.comp.forum.ForumHandler;
import net.dryuf.comp.forum.ForumRecord;
import net.dryuf.comp.forum.bo.ForumBo;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.Options;
import net.dryuf.time.util.DateTimeUtil;
import net.dryuf.mvp.Presenter;
import net.dryuf.srvui.Response;
import net.dryuf.xml.util.XmlFormat;


public class GuestbookPresenter extends net.dryuf.mvp.ChildPresenter
{
	public				GuestbookPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		String refBase = options.getOptionDefault("refBase", Dryuf.dotClassname(GuestbookFormPresenter.class));
		String refKey = (String) options.getOptionMandatory("refKey");

		forumBo = getCallerContext().getBeanTyped("forumBo", net.dryuf.comp.forum.bo.ForumBo.class);

		forumHandler = forumBo.openCreateForumRef(this.getCallerContext(), refBase, refKey, "");
		cssClass = Dryuf.dashClassname(options.getOptionDefault("cssClass", Dryuf.dotClassname(GuestbookPresenter.class)));
	}

	@Override
	public Presenter		init()
	{
		super.init();

		if (markdownService == null)
			markdownService = getCallerContext().getBeanTyped("markdownService", MarkdownService.class);

		form = Presenter.createSubPresenter(GuestbookFormPresenter.class, this, Options.NONE);
		return this;
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

		outputFormat("<div class='%s'>\n", cssClass);
		List<EntityHolder<ForumRecord>> comments = new LinkedList<EntityHolder<ForumRecord>>();
		forumHandler.listComments(comments, 0, null);
		for (EntityHolder<ForumRecord> recordHolder: comments) {
			ForumRecord record = recordHolder.getEntity();
			outputFormat("<hr class='separator' />\n<div class=\"header\">");
			if (!StringUtils.isEmpty(record.getEmail()))
				outputFormat("<a class='email' href=\"mailto:%S\"><span class='name'>%S</span></a>", record.getEmail(), record.getName());
			else
				outputFormat("<span class='name'>%S</span>", StringUtils.defaultString(record.getName(), ""));
			if (!StringUtils.isEmpty(record.getWebpage()))
				outputFormat(" (<a class='webpage' href=\"http://%S\">%S</a>)", record.getWebpage(), record.getWebpage());
			outputFormat(" - <span class='added'>%S</span></div>\n", DateTimeUtil.formatLocalReadable(Long.valueOf(record.getCreated())));
			outputFormat("<div class='content'>%s</div>\n", markdownService.convertToXhtml(record.getContent()));
		}
		output("</div>\n");
	}

	protected ForumHandler		forumHandler;

	public ForumHandler		getForumHandler()
	{
		return this.forumHandler;
	}

	protected MarkdownService	markdownService;

	public void			setMarkdownService(MarkdownService markdownService_)
	{
		this.markdownService = markdownService_;
	}

	protected ForumBo		forumBo;
	protected String		cssClass;

	protected GuestbookFormPresenter form;
}
