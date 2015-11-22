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
import net.dryuf.comp.forum.form.SimpleForumForm;
import net.dryuf.meta.ActionDef;


public class SimpleForumFormPresenter extends net.dryuf.mvp.BeanFormPresenter<SimpleForumForm>
{
	public				SimpleForumFormPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options, ForumHandler forumHandler)
	{
		super(parentPresenter, options);
		this.forumHandler = forumHandler;
	}


	@Override
	public SimpleForumForm		createBackingObject()
	{
		return new SimpleForumForm();
	}

	public boolean			performAdd(ActionDef action)
	{
		SimpleForumForm simpleForumForm = getBackingObject();
		ForumRecord forumRecord = new ForumRecord();
		forumRecord.setContent(simpleForumForm.getContent());
		forumRecord.setUserId((Long)this.getCallerContext().getUserId());
		this.forumHandler.addComment(forumRecord);
		this.getRootPresenter().getResponse().redirect(".");
		return false;
	}

	public void			render()
	{
		this.output("<div class='add'>\n");
		super.render();
		this.output("</div>\n");
	}

	protected ForumHandler		forumHandler;
}
