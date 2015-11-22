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
import net.dryuf.comp.gallery.mvp.GalleryPresenter;
import net.dryuf.core.Options;
import net.dryuf.core.StringUtil;
import net.dryuf.mvp.Presenter;


public class GallerySectionPresenter extends net.dryuf.mvp.ChildPresenter
{
	public				GallerySectionPresenter(Presenter parentPresenter, Options options, String section)
	{
		super(parentPresenter, options);
		galleryPresenter = (GalleryPresenter) parentPresenter;
		if ((this.galleryHandler = galleryPresenter.getGalleryHandler()) == null)
			throw new NullPointerException("galleryHandler");
		this.renderReference = galleryPresenter.getRenderReference();
		this.baseUrl = galleryPresenter.getBaseUrl();
		this.section = section;
	}

	public boolean			processMore(String element)
	{
		rootPresenter = this.getRootPresenter();
		String match[];
		if ((match = StringUtil.matchText("^(.*)\\.html$", element)) != null) {
			String image = match[1];
			if ((rootPresenter.needPathSlash(false)) == null)
				return true;
			if (this.galleryHandler.setCurrentRecord(this.section, null, image) == null) {
				return Presenter.createSubPresenter(net.dryuf.mvp.NotFoundPresenter.class, this, net.dryuf.core.Options.buildListed("content", this.localize(GallerySectionPresenter.class, "Requested picture not found, please go back to <a href=\"+/\">gallery section</a>"))).process();
			}
			return Presenter.createSubPresenter(GallerySectionRecordPresenter.class, this, net.dryuf.core.Options.NONE).process();
		}
		else {
			String thumb = null;
			if (element.equals("thumb")) {
				thumb = element+"/";
			}
			if (thumb != null) {
				if (rootPresenter.needPathSlash(true) == null)
					return false;
				element = rootPresenter.getPathElement();
			}
			if (this.galleryHandler.setCurrentRecord(this.section, thumb, element) != null) {
				if (rootPresenter.needPathSlash(false) == null)
					return true;
				return Presenter.createSubPresenter(GallerySectionFilePresenter.class, this, net.dryuf.core.Options.buildListed("thumb", thumb)).process();
			}
			return this.createDefaultPresenter().process();
		}
	}

	public void			render()
	{
		if (renderLeadChild())
			return;

		this.output("<table width=\"100%\"><tr width=\"100%\">");
		if (this.galleryHandler.isMulti()) {
			this.outputFormat("<td><a href=\"../#gallery\">%W</a></td><td align='right'>", GallerySectionPresenter.class, "Back to gallery");
			if (this.renderReference != null)
				this.renderReference.run();
			this.output("</td>");
		}
		else {
			this.output("<td></td><td align='center'>");
			if (this.renderReference != null)
				this.renderReference.run();
			this.output("</td>");
		}
		this.output("</tr><tr><td colspan='2'>");
		for (GalleryRecord picture: this.galleryHandler.listSectionRecords()) {
			this.outputFormat("<a href=%A><img alt=%A src=%A /></a> ", picture.getDisplayName()+".html#gallery", picture.getTitle(), this.baseUrl+"thumb/"+picture.getDisplayName());
		}
		this.output("</td></tr></table>\n");
	}

	protected GalleryPresenter	galleryPresenter;

	public Runnable			getRenderReference()
	{
		return this.renderReference;
	}

	protected Runnable		renderReference;

	public String			getBaseUrl()
	{
		return this.baseUrl;
	}

	protected String		baseUrl;

	public String			getSection()
	{
		return this.section;
	}

	String				section;

	public GalleryHandler		getGalleryHandler()
	{
		return this.galleryHandler;
	}

	protected GalleryHandler	galleryHandler;
}
