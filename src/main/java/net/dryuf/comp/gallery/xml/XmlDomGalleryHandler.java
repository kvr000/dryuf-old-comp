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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import net.dryuf.io.ResourceResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.comp.gallery.GallerySource;
import net.dryuf.core.CallerContext;
import net.dryuf.io.FileData;
import net.dryuf.io.FileDataImpl;
import net.dryuf.xml.util.DomUtil;


public class XmlDomGalleryHandler extends net.dryuf.comp.gallery.ReadonlyGalleryHandler
{
	public				XmlDomGalleryHandler(CallerContext callerContext, String galleryDir)
	{
		super(callerContext, galleryDir);
	}

	@Override
	public void			read()
	{
		readBase();
	}

	public int			supportsResource(String name)
	{
		return name.equals("gallery.xml") ? getCallerContext().checkRole("Gallery.config") ? 1 : -1 : 0;
	}

	public void			readBase()
	{
		if (this.galleryDoc != null)
			return;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			galleryDoc = dBuilder.parse(callerContext.getBeanTyped("resourceResolver", ResourceResolver.class).getMandatoryResourceAsStream(this.galleryDir+"gallery.xml"));
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		catch (SAXException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		galleryElement = galleryDoc.getDocumentElement();

		//locationsElement = DomUtil.getSingleElement(galleryElement, "locations");
		sectionsElement = DomUtil.getSingleElement(galleryElement, "sections");

		this.isMulti = DomUtil.getAttributeDefault(sectionsElement, "multi", true);
	}

	@Override
	public boolean			isMulti()
	{
		return this.isMulti;
	}

	@Override
	public GallerySection		getCurrentSection()
	{
		return currentSection;
	}

	@Override
	public GalleryRecord		getCurrentRecord()
	{
		return currentRecord;
	}

	@Override
	public List<GallerySection>	listSections()
	{
		if (sections == null) {
			readBase();
			sections = new LinkedList<GallerySection>();
			NodeList sectionNodes = DomUtil.getImmediateElementsByTagName(sectionsElement, "section");
			for (int i = 0; i < sectionNodes.getLength(); i++) {
				GallerySectionDom section = new GallerySectionDom((Element)sectionNodes.item(i));
				sections.add(section);
			}
		}
		return sections;
	}

	@Override
	public List<GalleryRecord>	listSectionRecords()
	{
		if (currentSection.records == null) {
			currentSection.records = new LinkedList<GalleryRecord>();
			NodeList recordNodes = DomUtil.getImmediateElementsByTagName(currentSection.recordsElement, "record");
			for (int i = 0; i < recordNodes.getLength(); i++) {
				GalleryRecordDom record = new GalleryRecordDom(currentSection, (Element)recordNodes.item(i));
				record.setRecordCounter((long)i);
				currentSection.records.add(record);
			}
		}
		return currentSection.records;
	}

	@Override
	public List<GallerySource>	listRecordSources()
	{
		if (currentRecord.sourcesElement == null)
			return null;
		LinkedList<GallerySource> sources = new LinkedList<GallerySource>();
		NodeList sourceNodes = DomUtil.getImmediateElementsByTagName(currentRecord.sourcesElement, "source");
		for (int i = 0; i < sourceNodes.getLength(); i++) {
			GallerySourceDom source = new GallerySourceDom(currentRecord, (Element)sourceNodes.item(i));
			source.setSourceCounter((long)i);
			sources.add(source);
		}
		return sources;

	}

	@Override
	public GallerySection		setCurrentSectionIdx(long idx)
	{
		if (sectionsElement == null)
			readBase();
		NodeList sectionNodes = DomUtil.getImmediateElementsByTagName(sectionsElement, "section");
		if (idx >= sectionNodes.getLength())
			return currentSection = null;
		return currentSection = new GallerySectionDom((Element)sectionNodes.item((int)idx));
	}

	@Override
	public GallerySection		setCurrentSection(String name)
	{
		if (sectionsElement == null)
			readBase();
		currentSection = null;
		NodeList sectionNodes = DomUtil.getImmediateElementsByTagName(sectionsElement, "section");
		for (int i = 0; i < sectionNodes.getLength(); i++) {
			Element sectionElement = (Element)sectionNodes.item(i);
			if (name.equals(DomUtil.getAttributeMandatory(sectionElement, "id"))) {
				if (currentSection != null)
					throw new RuntimeException("section name not unique: "+name);
				currentSection = new GallerySectionDom((Element)sectionNodes.item(i));
			}
		}
		if (currentSection != null)
			this.currentSectionName = currentSection.getDisplayName()+"/";
		return currentSection;
	}

	@Override
	public GalleryRecord		setCurrentRecord(String section, String thumb, String record)
	{
		if (sectionsElement == null)
			readBase();
		if (this.isMulti()) {
			if (setCurrentSection(section) == null)
				return null;
		}
		else {
			setCurrentSectionIdx(0);
		}
		currentRecord = null;
		NodeList recordNodes = DomUtil.getImmediateElementsByTagName(currentSection.recordsElement, "record");
		for (int i = 0; i < recordNodes.getLength(); i++) {
			Element recordElement = (Element)recordNodes.item(i);
			Element sourcesElement;
			if (thumb == null && (sourcesElement = DomUtil.getOptionalElement(recordElement, "sources")) != null) {
				NodeList sourceNodes = DomUtil.getImmediateElementsByTagName(sourcesElement, "source");
				for (int si = 0; si < sourceNodes.getLength(); si++) {
					Element sourceElement = (Element)sourceNodes.item(si);
					if (record.equals(DomUtil.getAttributeMandatory(sourceElement, "file"))) {
						if (currentRecord != null)
							throw new RuntimeException("record name not unique: "+record);
						currentRecord = new GalleryRecordDom(currentSection, (Element)recordNodes.item(i));
						currentRecord.setRecordCounter((long)i);
						currentSource = record;
					}
				}
			}
			else {
				if (record.equals(DomUtil.getAttributeMandatory(recordElement, "file"))) {
					if (currentRecord != null)
						throw new RuntimeException("record name not unique: "+record);
					currentRecord = new GalleryRecordDom(currentSection, (Element)recordNodes.item(i));
					currentRecord.setRecordCounter((long)i);
					currentSource = null;
				}
			}
		}
		this.currentThumb = thumb;
		return this.currentRecord;
	}

	@Override
	public GallerySection		getSectionByRecord(GalleryRecord record)
	{
		return ((GalleryRecordDom)record).section;
	}

	@Override
	public GalleryRecord[]		getSectionDirs()
	{
		return new GalleryRecord[]{ this.currentRecord.getSectionPrevious(), this.currentRecord.getSectionNext() };
	}

	@Override
	public GalleryRecord[]		getFullDirs()
	{
		return new GalleryRecord[]{ this.currentRecord.getFullPrevious(), this.currentRecord.getFullNext() };
	}

	@Override
	public FileData			getResourceData(String name)
	{
		if (supportsResource(name) == 0)
			throw new RuntimeException("resource unsupported");
		return FileDataImpl.createFromFilename(galleryDir+name);
	}

	@Override
	public void			uploadResourceData(String name, FileData data)
	{
		if (supportsResource(name) <= 0)
			throw new RuntimeException("resource writing unsupported");
		try {
			FileUtils.copyInputStreamToFile(data.getInputStream(), new File(galleryDir+name));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void			uploadRecordData(FileData input)
	{
		if (!getCallerContext().checkRole("Gallery.admin"))
			throw new RuntimeException("denied to upload record");
		try {
			File path = getRecordFullPath(false);
			path.getParentFile().mkdirs();
			FileUtils.copyInputStreamToFile(input.getInputStream(), path);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void			uploadRecordThumb(FileData input)
	{
		if (!getCallerContext().checkRole("Gallery.admin"))
			throw new RuntimeException("denied to upload record");
		try {
			File path = getRecordFullPath(true);
			path.getParentFile().mkdirs();
			FileUtils.copyInputStreamToFile(input.getInputStream(), path);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected File			getRecordFullPath(boolean isThumb)
	{
		StringBuilder path = new StringBuilder(galleryDir);
		if (isMulti) {
			if (currentSection.getDisplayName().indexOf("/") >= 0)
				throw new RuntimeException("section name contains '/'");
			if (currentSection.getDisplayName().startsWith("."))
				throw new RuntimeException("section name starts with '.'");
			path.append(currentSection.getDisplayName()).append("/");
		}
		if (isThumb)
			path.append("thumb/");
		if (currentRecord.getDisplayName().indexOf("/") >= 0)
			throw new RuntimeException("record name contains '/'");
		if (currentRecord.getDisplayName().startsWith("."))
			throw new RuntimeException("record name starts with '.'");
		path.append(currentSource != null ? currentSource : currentRecord.getDisplayName());
		return new File(path.toString());
	}

	public static class GallerySectionDom extends GallerySection
	{
		private static final long serialVersionUID = 1L;

		public				GallerySectionDom(Element sectionElement)
		{
			this.sectionElement = sectionElement;
			this.recordsElement = DomUtil.getSingleElement(sectionElement, "records");
		}

		public String			getDisplayName()
		{
			String r;
			if ((r = super.getDisplayName()) == null) {
				setDisplayName(r = DomUtil.getAttributeMandatory(sectionElement, "id"));
			}
			return r;
		}

		public String			getTitle()
		{
			return DomUtil.getSubElementContentDefault(sectionElement, "title", getDisplayName());
		}

		public String			getDescription()
		{
			return DomUtil.getSubElementContentDefault(sectionElement, "description", "");
		}

		public Long			getLastAdded()
		{
			return null;
		}

		public Long			getRecordCount()
		{
			return (long) DomUtil.getImmediateElementsByTagName(DomUtil.getSingleElement(sectionElement, "records"), "record").getLength();
		}

		public Element			sectionElement;
		public Element			recordsElement;

		public List<GalleryRecord>	records;
		public String			location;
	}

	public static class GalleryRecordDom extends GalleryRecord
	{
		private static final long serialVersionUID = 1L;

		public				GalleryRecordDom(GallerySectionDom section, Element recordElement)
		{
			this.section = section;
			this.recordElement = recordElement;
			this.sourcesElement = DomUtil.getOptionalElement(recordElement, "sources");
		}

		public Long			getCreated()
		{
			return null;
		}

		public String			getDisplayName()
		{
			String r;
			if ((r = super.getDisplayName()) == null) {
				setDisplayName(r = DomUtil.getAttributeMandatory(recordElement, "file"));
			}
			return r;
		}

		public int			getRecordType()
		{
			int r;
			if ((r = super.getRecordType()) == RecordType.RT_Unknown) {
				String recordTypeString = DomUtil.getAttributeDefault(recordElement, "recordType", "picture");
				switch (recordTypeString) {
				case "picture":
					r = RecordType.RT_Picture;
					break;

				case "video":
					r = RecordType.RT_Video;
					break;

				default:
					throw new RuntimeException("Unsupported recordType: "+recordTypeString);
				}
				super.setRecordType(r);
			}
			return r;
		}

		public String			getTitle()
		{
			return DomUtil.getSubElementContentDefault(recordElement, "title", getDisplayName());
		}

		public String			getDescription()
		{
			String r;
			if ((r = super.getDescription()) == null) {
				if ((r = DomUtil.getSubElementContentDefault(recordElement, "description", (String)null)) == null)
					r = getTitle();
				super.setDescription(r);
			}
			return r;
		}

		public GalleryRecordDom		getSectionPrevious()
		{
			Element previous = DomUtil.getPreviousSameSibling(recordElement);
			if (previous == null)
				return null;
			return new GalleryRecordDom(section, previous);
		}

		public GalleryRecordDom		getSectionNext()
		{
			Element next = DomUtil.getNextSameSibling(recordElement);
			if (next == null)
				return null;
			return new GalleryRecordDom(section, next);
		}

		public GalleryRecordDom		getFullPrevious()
		{
			GalleryRecordDom previousRecord;
			if ((previousRecord = getSectionPrevious()) != null)
				return previousRecord;
			Element previousSectionElement;
			if ((previousSectionElement = DomUtil.getPreviousSameSibling(section.sectionElement)) == null)
				return null;
			GallerySectionDom previousSection = new GallerySectionDom(previousSectionElement);
			Element lastRecordElement;
			if ((lastRecordElement = DomUtil.getLastElementByName(previousSection.recordsElement, "record")) == null)
				return null;
			return new GalleryRecordDom(previousSection, lastRecordElement);
		}

		public GalleryRecordDom		getFullNext()
		{
			GalleryRecordDom nextRecord;
			if ((nextRecord = getSectionNext()) != null)
				return nextRecord;
			Element nextSectionElement;
			if ((nextSectionElement = DomUtil.getNextSameSibling(section.sectionElement)) == null)
				return null;
			GallerySectionDom nextSection = new GallerySectionDom(nextSectionElement);
			Element firstRecordElement;
			if ((firstRecordElement = DomUtil.getFirstElementByName(nextSection.recordsElement, "record")) == null)
				return null;
			return new GalleryRecordDom(nextSection, firstRecordElement);
		}

		public GallerySectionDom	section;

		public Element			recordElement;

		public Element			sourcesElement;
	}

	public static class GallerySourceDom extends GallerySource
	{
		private static final long serialVersionUID = 1L;

		public				GallerySourceDom(GalleryRecordDom record, Element sourceElement)
		{
			this.sourceElement = sourceElement;
		}

		public Long			getCreated()
		{
			return null;
		}

		public String			getDisplayName()
		{
			String r;
			if ((r = super.getDisplayName()) == null) {
				setDisplayName(r = DomUtil.getAttributeMandatory(sourceElement, "file"));
			}
			return r;
		}

		public String			getMimeType()
		{
			String r;
			if ((r = super.getMimeType()) == null) {
				setMimeType(r = DomUtil.getAttributeMandatory(sourceElement, "mimeType"));
			}
			return r;
		}

		public Element			sourceElement;
	}

	protected boolean		isMulti;

	protected Document		galleryDoc;

	protected Element		galleryElement;

	//protected Element		locationsElement;

	protected Element		sectionsElement;

	protected List<GallerySection>	sections;

	protected GallerySectionDom	currentSection;

	protected GalleryRecordDom	currentRecord;

	protected String		currentSource;
}
