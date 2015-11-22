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
import net.dryuf.io.FileData;


public interface GalleryHandler
{
	public CallerContext		getCallerContext();

	public void			read();

	public int			supportsResource(String name);

	public boolean			isMulti();

	public GallerySection		setCurrentSectionIdx(long idx);
	public GallerySection		setCurrentSection(String name);
	public GalleryRecord		setCurrentRecord(String section, String thumb, String recordName);

	public boolean			addSection(GallerySection sectionInfo);
	public boolean			addRecord(GalleryRecord recordInfo, FileData imageContent);

	public List<GallerySection>	listSections();
	public List<GalleryRecord>	listSectionRecords();
	public GallerySection		getSectionByRecord(GalleryRecord record);

	public List<GallerySource>	listRecordSources();

	public GallerySection		getCurrentSection();
	public GalleryRecord		getCurrentRecord();

	public GalleryRecord[]		getSectionDirs();
	public GalleryRecord[]		getFullDirs();

	public FileData			getResourceData(String name);

	public FileData			getRecordData(GalleryRecord record);
	public FileData			getRecordThumb(GalleryRecord record);

	public void			uploadResourceData(String name, FileData resourceData);

	public void			uploadRecordData(FileData input);
	public void			uploadRecordThumb(FileData input);

	public void			cleanGallery();
}
