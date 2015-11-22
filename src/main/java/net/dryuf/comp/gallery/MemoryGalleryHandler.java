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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.dryuf.core.CallerContext;
import net.dryuf.core.ReportException;


public abstract class MemoryGalleryHandler extends net.dryuf.comp.gallery.ReadonlyGalleryHandler
{
	public static class GallerySectionMemory extends GallerySection
	{
		private static final long serialVersionUID = 1L;

		public List<GalleryRecord>	records = new LinkedList<GalleryRecord>();
		public Map<String, GalleryRecord> recordsHash = new HashMap<String, GalleryRecord>();
		public String			location;
	}

	public static class GalleryRecordMemory extends GalleryRecord
	{
		private static final long serialVersionUID = 1L;

		public GalleryRecord		sectionPrevious;
		public GalleryRecord		sectionNext;
		public GalleryRecord		fullPrevious;
		public GalleryRecord		fullNext;
	}

	public static class GallerySourceMemory extends GallerySource
	{
		private static final long serialVersionUID = 1L;
	}

	public				MemoryGalleryHandler(CallerContext callerContext, String galleryDir)
	{
		super(callerContext, galleryDir);
	}

	public GallerySection		getCurrentSection()
	{
		return currentSection;
	}

	public GalleryRecord		getCurrentRecord()
	{
		return currentRecord;
	}

	public List<GallerySection>	listSections()
	{
		read();
		return sections;
	}

	@Override
	public List<GalleryRecord>	listSectionRecords()
	{
		read();
		return currentSection.records;
	}

	public GallerySection		setCurrentSectionIdx(long idx)
	{
		if (idx >= sections.size())
			return null;
		return currentSection = (GallerySectionMemory) sections.get((int)idx);
	}

	public GallerySection		setCurrentSection(String name)
	{
		if ((this.currentSection = (GallerySectionMemory) sectionsHash.get(name)) != null)
			this.currentSectionName = this.currentSection.getDisplayName()+"/";
		return this.currentSection;
	}

	public GalleryRecord		setCurrentRecord(String section, String thumb, String record)
	{
		this.read();
		if (this.isMulti()) {
			if (setCurrentSection(section) == null)
				return null;
		}
		else {
			setCurrentSectionIdx(0);
		}
		this.currentThumb = thumb;
		return this.currentRecord = (GalleryRecordMemory) currentSection.recordsHash.get(record);
	}

	@Override
	public GallerySection		getSectionByRecord(GalleryRecord record)
	{
		return sectionsCounterHash.get(record.getGallerySection().getSectionCounter());
	}

	public GalleryRecord[]		getSectionDirs()
	{
		return new GalleryRecord[]{ this.currentRecord.sectionPrevious, this.currentRecord.sectionNext };
	}

	public GalleryRecord[]		getFullDirs()
	{
		return new GalleryRecord[]{ this.currentRecord.fullPrevious, this.currentRecord.fullNext };
	}

	protected void			read_init()
	{
		sections = new LinkedList<GallerySection>();
		locationsHash = new HashMap<String, GalleryLocation>();
	}

	protected void			read_addSection(GallerySectionMemory section)
	{
		if (sectionsHash.get(section.getDisplayName()) != null)
			throw new ReportException("section name "+section.getDisplayName()+" not unique "+sectionsHash);
		sections.add(section);
		sectionsHash.put(section.getDisplayName(), section);
		sectionsCounterHash.put(section.getSectionCounter(), section);
	}

	protected void			read_addRecord(GallerySectionMemory section, GalleryRecordMemory record)
	{
		if (section.recordsHash.get(record.getDisplayName()) != null)
			throw new ReportException("record "+section.getDisplayName()+"/"+record.getDisplayName()+" not unique");
		section.records.add(record);
		section.recordsHash.put(record.getDisplayName(), record);
	}

	protected GallerySectionMemory	currentSection;
	protected GalleryRecordMemory	currentRecord;
	protected String		currentThumb;

	public List<GallerySection> sections;
	public Map<String, GallerySection> sectionsHash = new HashMap<String, GallerySection>();
	public Map<Long, GallerySectionMemory> sectionsCounterHash = new HashMap<Long, GallerySectionMemory>();
	public Map<String, GalleryLocation> locationsHash;
};
