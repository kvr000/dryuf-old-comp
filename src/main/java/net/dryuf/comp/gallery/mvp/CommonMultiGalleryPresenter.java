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

import net.dryuf.core.Options;
import net.dryuf.core.ReportException;
import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.core.StringUtil;
import net.dryuf.mvp.Presenter;


public abstract class CommonMultiGalleryPresenter extends net.dryuf.mvp.ChildPresenter
{
	public				CommonMultiGalleryPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		if ((store = (String) options.getOptionDefault("store", null)) == null)
			store = getRootPresenter().getCurrentPath();
	}

	public boolean			process()
	{
		String page;
		if ((page = this.getPathGallery()) == null) {
			return this.processNoGallery();
		}
		else if (page.equals("")) {
			return false;
		}
		else {
			GalleryPresenter galleryPresenter = new GalleryPresenter(this, Options.NONE, this.openGalleryHandler(page));
			galleryPresenter.injectRenderReference(new Runnable() { public void run() { renderGalleryReference(); } });
		}
		return super.process();
	}

	public boolean			processNoGallery()
	{
		return this.processFinal();
	}

	/**
	 * Gets gallery path.
	 *
	 * @return null
	 * 	if there was no path passed
	 * @return ""
	 * 	if redirect is required due to missing slash
	 * @return path with slash at the end
	 * 	if gallery was passed
	 */
	public String			getPathGallery()
	{
		String page;
		if ((page = this.getRootPresenter().getPathElement()) != null) {
			if (this.getRootPresenter().needPathSlash(true) == null)
				return "";
			if (StringUtil.matchText("^([a-zA-Z0-9][-0-9A-Za-z_]*)$", page) == null)
				throw new ReportException("wrong page");
			return page+"/";
		}
		return null;
	}

	/**
	 * Opens gallery handler.
	 *
	 * @param page
	 * 	the last element in path
	 *
	 * @return null
	 * 	if there was no path passed
	 * @return gallery handler
	 * 	if found
	 */
	public abstract GalleryHandler	openGalleryHandler(String page);

	public void			renderGalleryReference()
	{
	}

	public void			render()
	{
		super.render();
		if (this.getLeadChild() == null) {
			this.renderGalleries();
		}
	}

	public void			renderGalleries()
	{
	}

	protected String		store;
}
