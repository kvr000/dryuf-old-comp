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

package net.dryuf.comp.gallery.xml;

import net.dryuf.io.ResourceResolver;
import org.xml.sax.Attributes;

import net.dryuf.comp.gallery.GalleryLocation;
import net.dryuf.core.CallerContext;
import net.dryuf.core.ReportException;
import net.dryuf.xml.XmlMappedParser;
import net.dryuf.xml.XmlMappedParser.XmlMappedTree;


public class XmlGalleryHandler extends net.dryuf.comp.gallery.SimpleGalleryHandler
{
	public				XmlGalleryHandler(CallerContext callerContext, String galleryDir)
	{
		super(callerContext, galleryDir);
	}

	public void			read()
	{
		if (this.sections != null)
			return;
		XmlMappedParser parser = new net.dryuf.xml.XmlMappedParser();
		this.read_init();
		new Reader(this, parser);
		parser.processStream(callerContext.getBeanTyped("resourceResolver", ResourceResolver.class).getMandatoryResourceAsStream(this.galleryDir+"gallery.xml"));
	}

	public int			supportsResource(String name)
	{
		return name.equals("gallery.xml") ? getCallerContext().checkRole("Gallery.config") ? 1 : -1 : 0;
	}

	public boolean			isMulti()
	{
		return this.isMulti;
	}

	public boolean			isMulti;


	protected static class Reader
	{
		public String			getAttrMandatory(Attributes attrList, String key)
		{
			String value = attrList.getValue(key);
			if (value == null)
				throw new ReportException("value not found: "+key);
			return value;
		}

		public String			getAttrDefault(Attributes attrList, String key, String defaultValue)
		{
			String value = attrList.getValue(key);
			if (value == null)
				return defaultValue;
			return value;
		}

		public				Reader(XmlGalleryHandler owner, XmlMappedParser parser)
		{
			this.owner = owner;
			if (readMapping == null) {
				readMapping = XmlMappedTree.create(Reader.class, null, null,
					"gallery",			 XmlMappedTree.create(Reader.class, null, null,
						"locations",			XmlMappedTree.create(Reader.class, null, null,
							"location",			XmlMappedTree.create(Reader.class, "startLocation", "endLocation")
						),
						"sections",			XmlMappedTree.create(Reader.class, "startSections", "endSections",
							"section",			XmlMappedTree.create(Reader.class, "startSection", "endSection",
								"title",			XmlMappedTree.create(Reader.class, null, "endSectionTitle"),
								"description",			XmlMappedTree.create(Reader.class, null, "endSectionDescription"),
								"records",			XmlMappedTree.create(Reader.class, null, null,
									"record",			XmlMappedTree.create(Reader.class, "startRecord", "endRecord",
										"title",			XmlMappedTree.create(Reader.class, null, "endRecordTitle"),
										"description",			XmlMappedTree.create(Reader.class, null, "endRecordDescription")
									)
								)
							)
						)
					)
				);
			}
			parser.setupMapped(this, readMapping);
			this.read_idxSection = 0;
		}

		public void			startLocation(String tag, Attributes attrList)
		{
			this.read_curLocation = new GalleryLocation();
			this.read_curLocation.setName(getAttrMandatory(attrList, "id"));
			this.read_curLocation.setStore(getAttrMandatory(attrList, "store"));
			this.read_curLocation.setThumb(getAttrMandatory(attrList, "thumb"));
		}

		public void			endLocation(String tag, String content)
		{
			if (owner.locationsHash.isEmpty())
				owner.locationsHash.put("", read_curLocation);
			owner.locationsHash.put(read_curLocation.getName(), read_curLocation);
		}

		public void			startSections(String tag, Attributes attrList)
		{
			owner.isMulti = attrList.getValue("multi") != null ? Boolean.valueOf(attrList.getValue("multi")).booleanValue() : true;
		}

		public void			startSection(String tag, Attributes attrList)
		{
			this.read_currentSection = new GallerySectionMemory();
			this.read_currentSection.setDisplayName(getAttrMandatory(attrList, "id"));
			this.read_currentSection.location = getAttrMandatory(attrList, "location");
			this.read_currentSection.setTitle("");
			this.read_currentSection.setDescription("");
			this.read_idxSection = 0;
		}

		public void			endSectionTitle(String tag, String content)
		{
			this.read_currentSection.setTitle(content);
		}

		public void			endSectionDescription(String tag, String content)
		{
			this.read_currentSection.setDescription(content);
		}

		public void			endSection(String tag, String content)
		{
			if (this.read_currentRecord != null) {
				this.read_currentRecord.sectionNext = null;
				this.read_currentRecord.fullNext = null;
			}
			if (this.read_currentSection.location == null)
				this.read_currentSection.location = "";
			this.read_currentSection.setRecordCount(this.read_idxSection);
			owner.read_addSection(this.read_currentSection);
		}

		public void			endSections(String tag, String content)
		{
			if (this.read_currentRecord != null)
				this.read_currentRecord.fullNext = null;
		}

		public void			startRecord(String tag, Attributes attrList)
		{
			GalleryRecordMemory oldRecord = this.read_currentRecord;
			this.read_currentRecord = new GalleryRecordMemory();
			this.read_currentRecord.fullPrevious = oldRecord;
			if (oldRecord != null)
				oldRecord.fullNext = this.read_currentRecord;
			if (this.read_idxSection == 0) {
				if (oldRecord != null)
					oldRecord.sectionNext = null;
				this.read_currentRecord.sectionPrevious = null;
			}
			else {
				this.read_currentRecord.sectionPrevious = oldRecord;
				oldRecord.sectionNext = this.read_currentRecord;
			}
			String file = getAttrMandatory(attrList, "file");
			this.read_currentRecord.setGallerySection(this.read_currentSection.getPk());
			this.read_currentRecord.setDisplayName(file);
			this.read_currentRecord.setTitle(file);
			this.read_currentRecord.setDescription(file);
			this.read_currentRecord.setLocation(getAttrDefault(attrList, "location", ""));
		}

		public void			endRecordTitle(String tag, String content)
		{
			this.read_currentRecord.setTitle(content);
		}

		public void			endRecordDescription(String tag, String content)
		{
			this.read_currentRecord.setDescription(content);
		}

		public void			endRecord(String tag, String content)
		{
			this.read_currentRecord.setRecordCounter(this.read_idxSection++);
			owner.read_addRecord(this.read_currentSection, this.read_currentRecord);
		}

		public XmlGalleryHandler	owner;

		public GalleryLocation		read_curLocation;
		public GallerySectionMemory	read_currentSection;
		public GalleryRecordMemory	read_currentRecord;
		public long			read_idxSection;
	}

	protected static XmlMappedTree	readMapping = null;
}
