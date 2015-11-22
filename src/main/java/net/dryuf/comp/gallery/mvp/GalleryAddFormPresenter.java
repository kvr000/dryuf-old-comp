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

package net.dryuf.comp.gallery.mvp;

import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.comp.gallery.form.GalleryAddForm;
import net.dryuf.core.Options;
import net.dryuf.meta.ActionDef;
import net.dryuf.mvp.Presenter;
import net.dryuf.text.util.TextUtil;


public class GalleryAddFormPresenter extends net.dryuf.mvp.BeanFormPresenter<GalleryAddForm>
{
	public			GalleryAddFormPresenter(Presenter parentPresenter, Options options, GalleryHandler galleryHandler)
	{
		super(parentPresenter, options);

		this.galleryHandler = galleryHandler;
	}

	public boolean			processCommon()
	{
		if (this.getCallerContext().getUserId() == null) {
			new net.dryuf.mvp.NeedLoginPresenter(this.parentPresenter, net.dryuf.core.Options.buildListed("messageClass", GalleryAddFormPresenter.class, "message", "You need to --login-- to add pictures"));
		}
		return super.process();
	}

	public void			render()
	{
		if (this.getCallerContext().getUserId() == null) {
			renderLeadChild();
		}
		else {
			super.render();
		}
	}

	public boolean			performAdd(ActionDef action)
	{
		GalleryAddForm galleryAddForm = getBackingObject();
		galleryAddForm.setPicture(TextUtil.convertNameToDisplay(galleryAddForm.getPicture()));
		GalleryRecord galleryRecord = new GalleryRecord();
		if (!this.galleryHandler.addRecord(galleryRecord, (getRequest().getFile(getFormFieldName("picture")+"File")))) {
			this.addMessageLocalized(Presenter.MSG_Error, GalleryAddFormPresenter.class, "Picture of the same name already exists");
		}
		else {
			this.addMessageLocalized(Presenter.MSG_Info, GalleryAddFormPresenter.class, "Picture added");
		}
		getRootPresenter().getResponse().redirect(".");
		return false;
	}

	@Override
	public GalleryAddForm		createBackingObject()
	{
		return new GalleryAddForm();
	}

	protected GalleryHandler	galleryHandler;
}
