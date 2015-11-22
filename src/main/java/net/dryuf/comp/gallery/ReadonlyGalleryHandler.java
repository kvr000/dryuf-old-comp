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

package net.dryuf.comp.gallery;

import net.dryuf.core.CallerContext;
import net.dryuf.io.FileData;
import net.dryuf.io.ResourceResolver;


public abstract class ReadonlyGalleryHandler extends net.dryuf.comp.gallery.GenericGalleryHandler
{
	public				ReadonlyGalleryHandler(CallerContext callerContext, String galleryDir)
	{
		super(callerContext);
		if (!(this.galleryDir = galleryDir).endsWith("/"))
			throw new RuntimeException("gallery path must end with '/'");
	}

	@Override
	public boolean			addSection(GallerySection section_info)
	{
		throw new UnsupportedOperationException("addSection");
	}

	@Override
	public boolean			addRecord(GalleryRecord picture_info, FileData input)
	{
		throw new UnsupportedOperationException("addRecord");
	}

	@Override
	public void			uploadRecordData(FileData input)
	{
		throw new UnsupportedOperationException("uploadRecordData not supported");
	}

	@Override
	public void			uploadRecordThumb(FileData input)
	{
		throw new UnsupportedOperationException("uploadRecordThumb not supported");
	}

	@Override
	public FileData			getRecordData(GalleryRecord record)
	{
		return (getCallerContext().getBeanTyped("resourceResolver", ResourceResolver.class)).getResource(galleryDir+currentSectionName+record.getDisplayName());
	}

	@Override
	public FileData			getRecordThumb(GalleryRecord record)
	{
		return (getCallerContext().getBeanTyped("resourceResolver", ResourceResolver.class)).getResource(galleryDir+currentSectionName+currentThumb+record.getDisplayName());
	}

	protected String		galleryDir;

	protected String		currentSectionName = "";

	protected String		currentThumb;
}
