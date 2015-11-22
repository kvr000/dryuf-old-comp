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
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.comp.gallery.GallerySource;
import net.dryuf.core.Options;
import net.dryuf.net.util.UrlUtil;
import net.dryuf.mvp.Presenter;


public class GallerySectionRecordPresenter extends net.dryuf.mvp.ChildPresenter
{
	public			GallerySectionRecordPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
		gallerySectionPresenter = (GallerySectionPresenter)parentPresenter;
		if ((this.galleryHandler = gallerySectionPresenter.getGalleryHandler()) == null)
			throw new NullPointerException("galleryHandler");
		this.renderReference = gallerySectionPresenter.getRenderReference();
		this.baseUrl = gallerySectionPresenter.getBaseUrl();
	}

	public void			render()
	{
		if (renderLeadChild())
			return;

		GallerySection currentSection = this.galleryHandler.getCurrentSection();
		GalleryRecord currentRecord = this.galleryHandler.getCurrentRecord();
		this.output("<table width=\"100%\">\n\t<tr>");
		this.outputFormat("<td width=\"33%%\" align='left'>%W: %S / %S</td>",GallerySectionPresenter.class, "Image", String.valueOf(currentRecord.getRecordCounter()+1), String.valueOf(currentSection.getRecordCount()));
		this.output("<td width=\"34%\" align='center'>");
		GalleryRecord[] sectionDirs = this.galleryHandler.getSectionDirs();
		GalleryRecord[] fullDirs = this.galleryHandler.isMulti() ? this.galleryHandler.getFullDirs() : sectionDirs;
		if (sectionDirs[0] != null) {
			this.outputFormat("<a href=%A>%W</a>", UrlUtil.encodeUrl(sectionDirs[0].getDisplayName())+".html#gallery", GallerySectionRecordPresenter.class, "Previous");
		}
		else if (fullDirs[0] != null) {
			this.outputFormat("<a href=%A>%W</a>", "../"+UrlUtil.encodeUrl(galleryHandler.getSectionByRecord(fullDirs[0]).getDisplayName())+"/"+UrlUtil.encodeUrl(fullDirs[0].getDisplayName())+".html#gallery", GallerySectionRecordPresenter.class, "Previous");
		}
		else {
			this.outputFormat("%W", GallerySectionRecordPresenter.class, "Previous");
		}
		if (this.galleryHandler.isMulti())
			this.outputFormat(" <a name=\"gallery\"></a><a href=\".\">%S</a> ", currentSection.getTitle());
		else
			this.outputFormat(" <a name=\"gallery\"></a><a href=\".\">%W</a> ", GallerySectionRecordPresenter.class, "Album");
		if (sectionDirs[1] != null) {
			this.outputFormat("<a href=%A>%W</a>", UrlUtil.encodeUrl(sectionDirs[1].getDisplayName())+".html#gallery", GallerySectionRecordPresenter.class, "Next");
		}
		else if (fullDirs[1] != null) {
			this.outputFormat("<a href=%A>%W</a>", "../"+UrlUtil.encodeUrl(galleryHandler.getSectionByRecord(fullDirs[1]).getDisplayName())+"/"+UrlUtil.encodeUrl(fullDirs[1].getDisplayName())+".html#gallery", GallerySectionRecordPresenter.class, "Next");
		}
		else {
			this.outputFormat("%W", GallerySectionRecordPresenter.class, "Next");
		}
		this.outputFormat("</td><td width=\"33%%\" align='center'>");
		if (this.renderReference != null)
			this.renderReference.run();
		this.outputFormat("</td></tr>\n");
		this.outputFormat("\t<tr><td colspan='3'>\n");
		switch (currentRecord.getRecordType()) {
		case GalleryRecord.RecordType.RT_Picture:
			this.outputFormat("\t\t<img alt=%A src=%A />", currentRecord.getTitle(), this.baseUrl+UrlUtil.encodeUrl(currentRecord.getDisplayName()));
			break;

		case GalleryRecord.RecordType.RT_Video:
			this.outputFormat("\t\t<video controls>\n", currentRecord.getTitle(), this.baseUrl+UrlUtil.encodeUrl(currentRecord.getDisplayName()));
			for (GallerySource source: galleryHandler.listRecordSources()) {
				this.outputFormat("\t\t\t<source src=%A type=%A />\n", source.getDisplayName(), source.getMimeType());
			}
			this.outputFormat("\t\t</video>\n");
			break;

		default:
			this.outputFormat("\t\t<div class='msg_type_400'>Unknown gallery record type: %S</div>\n", Integer.toString(currentRecord.getRecordType()));
		}
		this.outputFormat("\t</td></tr>\n");
		this.outputFormat("\t<tr><td colspan='3'>%S</td></tr>\n", currentRecord.getDescription() != null ? currentRecord.getDescription() : currentRecord.getTitle());
		this.output("</table>\n");
	}

	protected GallerySectionPresenter gallerySectionPresenter;

	protected GalleryHandler	galleryHandler;

	protected Runnable		renderReference;

	protected String		baseUrl;
}
