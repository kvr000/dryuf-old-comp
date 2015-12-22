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

import net.dryuf.comp.forum.ForumHandler;
import net.dryuf.comp.forum.ForumRecord;
import net.dryuf.comp.forum.form.GuestbookForm;
import net.dryuf.core.Options;
import net.dryuf.meta.ActionDef;
import net.dryuf.mvp.Presenter;
import net.dryuf.core.StringUtil;


public class GuestbookFormPresenter extends net.dryuf.mvp.BeanFormPresenter<GuestbookForm>
{
	public				GuestbookFormPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
		this.forumHandler = ((GuestbookPresenter)parentPresenter).getForumHandler();
	}

	public static String[]		decodeIdentity(String id)
	{
		String regs[];
		if ((regs = StringUtil.matchText("^([^|]*)\\|([^|]*)\\|([^|]*)$", id)) != null) {
			return new String[] { regs[1], regs[2], regs[3] };
		}
		else {
			return new String[] { "", "", "" };
		}
	}

	public static String		encodeIdentity(String name, String email, String webpage)
	{
		if (name == null)
			name = "";
		if (email == null)
			email = "";
		if (webpage == null)
			webpage = "";
		return name+"|"+email+"|"+webpage;
	}

	@Override
	public GuestbookForm		createBackingObject()
	{
		return new GuestbookForm();
	}

	@Override
	public String			initData()
	{
		super.initData();
		GuestbookForm backing = this.backingObject;
		String forvIds = getRequest().getCookie("guestbookIds");
		if (forvIds != null) {
			try {
				String[] decoded = decodeIdentity(forvIds);
				backing.setName(decoded[0]);
				backing.setEmail(decoded[1]);
				backing.setWebpage(decoded[2]);
			}
			catch (Exception ex) {
				backing.setName("");
				backing.setEmail("");
				backing.setWebpage("");
			}
		}
		return null;
	}

	public boolean			performAdd(ActionDef action)
	{
		GuestbookForm backing = this.backingObject;
		String name = backing.getName();
		String email = backing.getEmail();
		String webpage = backing.getWebpage();
		String content = backing.getContent().replaceAll("\r\n", "\n");
		String ids = encodeIdentity(name, email, webpage);
		ForumRecord forumRecord = new ForumRecord();
		forumRecord.setContent(content);
		forumRecord.setEmail(email);
		forumRecord.setWebpage(webpage);
		forumRecord.setName(name);
		forumHandler.addComment(forumRecord);
		rootPresenter.getResponse().setCookie("guestbookIds", ids, 365*86400);
		rootPresenter.getResponse().redirect(".");
		return false;
	}

	protected ForumHandler		forumHandler;
}
