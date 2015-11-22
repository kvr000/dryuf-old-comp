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

package net.dryuf.comp.gallery.xml.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.comp.gallery.GallerySource;
import net.dryuf.comp.gallery.xml.XmlDomGalleryHandler;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class XmlDomGalleryHandlerTest extends net.dryuf.tenv.AppTenvObject
{
	@Test
	public void			testAll()
	{
		List<GalleryRecord> records;
		XmlDomGalleryHandler galleryHandler = new XmlDomGalleryHandler(createCallerContext(), "net/dryuf/comp/gallery/xml/test/gallery_ut0/");
		List<GallerySection> sections = galleryHandler.listSections();
		Assert.assertEquals(3, sections.size());

		GalleryRecord record;
		List<GallerySource> sources;
		GallerySource source;

		galleryHandler.setCurrentSectionIdx(0);
		records = galleryHandler.listSectionRecords();
		Assert.assertEquals(2, records.size());
		record = records.get(0);
		Assert.assertEquals("img_5989.jpg", record.getTitle());
		Assert.assertEquals("img_5989.jpg", record.getDisplayName());
		Assert.assertEquals("first picture", record.getDescription());
		Assert.assertEquals(GalleryRecord.RecordType.RT_Picture, record.getRecordType());
		record = records.get(1);
		Assert.assertEquals("img_5993.jpg", record.getDisplayName());
		Assert.assertEquals("img_5993.jpg", record.getTitle());
		Assert.assertEquals("second picture", record.getDescription());
		Assert.assertEquals(GalleryRecord.RecordType.RT_Picture, record.getRecordType());

		galleryHandler.setCurrentSectionIdx(1);
		records = galleryHandler.listSectionRecords();
		Assert.assertEquals(2, records.size());
		record = records.get(0);
		Assert.assertEquals("20111113_194931.jpg", record.getTitle());
		Assert.assertEquals(GalleryRecord.RecordType.RT_Picture, record.getRecordType());
		Assert.assertNotNull(galleryHandler.setCurrentRecord((sections.get(1)).getDisplayName(), null, "20111113_194931.jpg"));
		sources = galleryHandler.listRecordSources();
		Assert.assertNull(sources);

		record = records.get(1);
		Assert.assertEquals("20111113_201204.jpg", record.getTitle());
		Assert.assertEquals(GalleryRecord.RecordType.RT_Picture, record.getRecordType());
		Assert.assertTrue(record.getTitle().equals("20111113_194931.jpg") == false || record.getDisplayName().equals("20111113_194931.jpg") == false || record.getDescription().equals("Formula Rosa"));
		Assert.assertTrue(record.getTitle().equals("20111113_201204.jpg") == false || record.getDisplayName().equals("20111113_201204.jpg") == false || record.getDescription().equals("Ferrari World - entrance gate."));

		Assert.assertNotNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), null, "20111113_194931.jpg"));
		Assert.assertNotNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), null, "20111113_201204.jpg"));

		galleryHandler.setCurrentSectionIdx(2);
		records = galleryHandler.listSectionRecords();
		Assert.assertEquals(1, records.size());
		record = records.get(0);
		Assert.assertEquals("bum.jpg", record.getTitle());
		Assert.assertEquals("bum.jpg", record.getDisplayName());
		Assert.assertEquals("bum", record.getDescription());
		Assert.assertEquals(GalleryRecord.RecordType.RT_Video, record.getRecordType());
		Assert.assertNotNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), "thumb/", record.getDisplayName()));
		sources = galleryHandler.listRecordSources();
		Assert.assertNotNull(sources);
		Assert.assertEquals(1, sources.size());
		source = sources.get(0);
		Assert.assertEquals("bum.mp4", source.getDisplayName());

		Assert.assertNotNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), "thumb/", "bum.jpg"));
		Assert.assertNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), null, "bum.jpg"));
		Assert.assertNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), "thumb/", "bum.mp4"));
		Assert.assertNotNull(galleryHandler.setCurrentRecord(galleryHandler.getCurrentSection().getDisplayName(), null, "bum.mp4"));
	}

	@Test
	public void			testSection()
	{
		XmlDomGalleryHandler galleryHandler = new XmlDomGalleryHandler(createCallerContext(), "net/dryuf/comp/gallery/xml/test/gallery_ut0/");

		GallerySection section;
		Assert.assertNotNull(section = galleryHandler.setCurrentSection("ferrari-world"));
		Assert.assertEquals(2L, (long)section.getRecordCount());
		Assert.assertEquals("Visiting Ferrari World", section.getDescription());
	}

	@Test
	public void			testRecordCounter()
	{
		XmlDomGalleryHandler galleryHandler = new XmlDomGalleryHandler(createCallerContext(), "net/dryuf/comp/gallery/xml/test/gallery_ut0/");

		GalleryRecord galleryRecord;
		Assert.assertNotNull(galleryRecord = galleryHandler.setCurrentRecord("ferrari-world", null, "20111113_194931.jpg"));
		Assert.assertEquals(0L, (long)galleryRecord.getRecordCounter());
	}
}
