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
import net.dryuf.comp.gallery.mvp.GalleryPresenter;
import net.dryuf.core.Options;
import net.dryuf.io.FileData;
import net.dryuf.io.FileDataImpl;
import net.dryuf.mvp.Presenter;
import net.dryuf.mvp.GenericFilePresenter;


public class GalleryResourcePresenter extends GenericFilePresenter
{
	public				GalleryResourcePresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
		galleryPresenter = (GalleryPresenter) parentPresenter;
		if ((this.galleryHandler = galleryPresenter.getGalleryHandler()) == null)
			throw new NullPointerException("galleryHandler");
		this.renderReference = galleryPresenter.getRenderReference();
		this.baseUrl = galleryPresenter.getBaseUrl();
		this.resourceName = getRootPresenter().getLastElement();
	}

	protected FileData		resolveFileData()
	{
		return galleryHandler.getResourceData(resourceName);
	}

	public boolean			processPut()
	{
		if (galleryHandler.supportsResource(resourceName) < 0) {
			return createUnallowedMethodPresenter().process();
		}
		galleryHandler.uploadResourceData(resourceName, FileDataImpl.createFromStream(getRootPresenter().getRequest().getInputStream()));
		return false;
	}

	protected GalleryPresenter	galleryPresenter;

	public GalleryPresenter		getGalleryPresenter()
	{
		return this.galleryPresenter;
	}

	protected Runnable		renderReference;

	public Runnable			getRenderReference()
	{
		return this.renderReference;
	}

	protected String		baseUrl;

	public String			getBaseUrl()
	{
		return this.baseUrl;
	}

	protected GalleryHandler	galleryHandler;

	public GalleryHandler		getGalleryHandler()
	{
		return this.galleryHandler;
	}

	protected String		resourceName;

	public String			getResourceName()
	{
		return this.resourceName;
	}
}
