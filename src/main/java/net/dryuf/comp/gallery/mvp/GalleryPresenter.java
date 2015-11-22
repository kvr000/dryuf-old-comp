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
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.core.Options;
import net.dryuf.mvp.Presenter;


public class GalleryPresenter extends net.dryuf.mvp.ChildPresenter
{
	public static final int		MODE_GALLERY = 0;
	public static final int		MODE_GALLERYSECTION = 1;
	public static final int		MODE_SECTION = 2;
	public static final int		MODE_IMAGE = 3;

	public				GalleryPresenter(Presenter parentPresenter, Options options, GalleryHandler galleryHandler)
	{
		super(parentPresenter, options);
		if ((this.galleryHandler = galleryHandler) == null)
			throw new NullPointerException("galleryHandler");
		this.baseUrl = options.getOptionDefault("baseUrl", "");
	}

	public boolean			process()
	{
		this.galleryHandler.read();
		if (!this.galleryHandler.isMulti()) {
			String section = "";
			if (this.galleryHandler.setCurrentSectionIdx(0) == null)
				throw new RuntimeException("unable to set default section");
			return new GallerySectionPresenter(this, net.dryuf.core.Options.NONE, section).process();
		}
		else {
			return super.process();
		}
	}

	public boolean			processMore(String element)
	{
		rootPresenter = this.getRootPresenter();
		if (galleryHandler.supportsResource(element) != 0) {
			if (rootPresenter.needPathSlash(false) == null)
				return true;
			return new GalleryResourcePresenter(this, Options.NONE).process();
		}
		if (rootPresenter.needPathSlash(true) == null)
			return false;
		if (this.galleryHandler.setCurrentSection(element) == null) {
			return this.createNotFoundPresenter().process();
		}
		else {
			return new GallerySectionPresenter(this, net.dryuf.core.Options.NONE, element).process();
		}
	}

	public int			getMode()
	{
		if (this.galleryHandler.getCurrentRecord() != null)
			return MODE_IMAGE;
		else if (!this.galleryHandler.isMulti())
			return MODE_GALLERYSECTION;
		else if (this.galleryHandler.getCurrentSection() != null)
			return MODE_SECTION;
		else
			return MODE_GALLERY;
	}

	public void			injectRenderReference(Runnable callback)
	{
		this.renderReference = callback;
	}

	public void			render()
	{
		if (renderLeadChild())
			return;

		this.output("<a name=\"gallery/\"></a><table class=\"net-dryuf-comp-gallery-GalleryPresenter\" width=\"100%\">");
		this.outputFormat("<tr class=\"reference\"><td colspan='2'>");
		if (this.renderReference != null)
			this.renderReference.run();
		this.outputFormat("</td></tr>");
		for (GallerySection section: this.galleryHandler.listSections()) {
			this.outputFormat("<tr class=\"section\"><td><a href=%A>%S</a></td><td>%S</td></tr>", section.getDisplayName()+"/#gallery", section.getTitle(), section.getDescription());
		}
		this.output("</table>\n");
	}

	public GalleryHandler		getGalleryHandler()
	{
		return this.galleryHandler;
	}

	protected GalleryHandler	galleryHandler;

	public Runnable			getRenderReference()
	{
		return this.renderReference;
	}

	protected Runnable		renderReference = null;

	public String			getBaseUrl()
	{
		return this.baseUrl;
	}

	protected String		baseUrl;
}
