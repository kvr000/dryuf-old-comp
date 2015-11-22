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

import java.io.InputStream;

import javax.validation.constraints.NotNull;

import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.core.Options;
import net.dryuf.io.FileData;
import net.dryuf.io.FileDataImpl;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;
import net.dryuf.mvp.RootPresenter;
import net.dryuf.mvp.GenericFilePresenter;


public class GallerySectionFilePresenter extends GenericFilePresenter
{
	public				GallerySectionFilePresenter(@NotNull Presenter parentPresenter, @NotNull Options options)
	{
		super(parentPresenter, options);
		if ((this.galleryHandler = ((GallerySectionPresenter)parentPresenter).getGalleryHandler()) == null)
			throw new NullPointerException("galleryHandler");
		this.thumb = (String) options.getOptionDefault("thumb", null);
	}

	public FileData			resolveFileData()
	{
		if (thumb == null) {
			return this.galleryHandler.getRecordData(galleryHandler.getCurrentRecord());
		}
		else {
			return this.galleryHandler.getRecordThumb(galleryHandler.getCurrentRecord());
		}
	}

	public boolean			processPut()
	{
		RootPresenter rootPresenter = this.getRootPresenter();
		InputStream input = rootPresenter.getRequest().getInputStream();
		if (!this.galleryHandler.getCallerContext().checkRole("Gallery.admin")) {
			this.parentPresenter.createDeniedPresenter();
			return true;
		}
		if (this.thumb == null) {
			this.galleryHandler.uploadRecordData(FileDataImpl.createFromNameStream(galleryHandler.getCurrentRecord().getDisplayName(), input));
		}
		else {
			this.galleryHandler.uploadRecordThumb(FileDataImpl.createFromStream(input));
		}
		rootPresenter.redirect(PageUrl.createFinal(rootPresenter.getFullUrl()));
		return false;
	}

	protected GalleryHandler	galleryHandler;

	protected String		thumb;
}
