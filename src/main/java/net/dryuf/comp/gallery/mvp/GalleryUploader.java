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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.core.CallerContext;
import net.dryuf.core.Options;


public class GalleryUploader extends net.dryuf.srvui.UiBased
{
	public				GalleryUploader(CallerContext callerContext, Options options, GalleryHandler galleryHandler)
	{
		super(callerContext);
		if ((this.galleryHandler = galleryHandler) == null)
			throw new NullPointerException("galleryHandler");
		if (!(this.targetUrl = (String) options.getOptionMandatory("targetUrl")).endsWith("/"))
			throw new RuntimeException("targetUrl must end with '/'");
		this.sid = (String) options.getOptionMandatory("sid");
		this.galleryHandler.read();
	}

	public void			uploadResources()
	{
		try {
			net.dryuf.io.HttpUtil.putRaw(this.targetUrl+"gallery.xml", "text/xml", IOUtils.toByteArray(galleryHandler.getResourceData("gallery.xml").getInputStream()), new String[]{ "cookie", "PHPSESSID="+this.sid });
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void			uploadData()
	{
		for (GallerySection section: this.galleryHandler.listSections()) {
			this.uploadSection(section.getDisplayName());
		}
	}

	public void			uploadSection(String sectionName)
	{
		if (this.galleryHandler.setCurrentSection(sectionName) == null)
			throw new RuntimeException("unable to set "+sectionName);
		for (GalleryRecord picture: this.galleryHandler.listSectionRecords()) {
			this.uploadRecord(sectionName, picture.getDisplayName());
		}
	}

	public void			uploadRecord(String sectionName, String pictureName)
	{
		if (this.galleryHandler.setCurrentRecord(sectionName, null, pictureName) == null)
			throw new RuntimeException("unable to set "+sectionName+"/"+pictureName);
		String subPath = (this.galleryHandler.isMulti() ? sectionName+"/" : "")+pictureName;
		String thumbPath = (this.galleryHandler.isMulti() ? sectionName+"/" : "")+"thumb/"+pictureName;
		try {
			net.dryuf.io.HttpUtil.putRaw(this.targetUrl+subPath, "image/jpeg", FileUtils.readFileToByteArray(new File(subPath)), new String[]{ "cookie", "PHPSESSID="+this.sid });
			net.dryuf.io.HttpUtil.putRaw(this.targetUrl+thumbPath, "image/jpeg", FileUtils.readFileToByteArray(new File(thumbPath)), new String[]{ "cookie", "PHPSESSID="+this.sid });
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected GalleryHandler	galleryHandler;
	protected String		targetUrl;
	protected String		sid;
}
