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

import java.util.List;

import net.dryuf.core.CallerContext;


public abstract class SimpleGalleryHandler extends net.dryuf.comp.gallery.MemoryGalleryHandler
{
	public				SimpleGalleryHandler(CallerContext callerContext, String galleryDir)
	{
		super(callerContext, galleryDir);
	}

	public void			initFromList(List<String> files, List<String> locations)
	{
		long idx = 0;
		this.read_init();
		this.currentSection = new GallerySectionMemory();
		this.currentSection.setDisplayName("");
		GalleryRecordMemory last = null;
		for (String file: files) {
			GalleryRecordMemory p = new GalleryRecordMemory();
			p.setRecordType(GalleryRecord.RecordType.RT_Picture);
			p.setTitle(file);
			p.setDisplayName(file);
			p.setRecordCounter(idx++);
			p.setGallerySection(this.currentSection.getPk());
			p.sectionPrevious = last;
			p.fullPrevious = last;
			if (last != null) {
				last.sectionNext = p;
				last.fullNext = p;
			}
			last = p;
			read_addRecord(this.currentSection, p);
		}
		this.currentSection.setRecordCount(idx);
		this.read_addSection(this.currentSection);
	}

	public boolean			isMulti()
	{
		return false;
	}

	public List<GallerySource>	listRecordSources()
	{
		return null;
	}
}
