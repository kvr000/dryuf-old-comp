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
import net.dryuf.comp.gallery.xml.XmlGalleryHandler;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class XmlGalleryHandlerTest extends net.dryuf.tenv.AppTenvObject
{
	@Test
	public void			testHandler()
	{
		List<GalleryRecord> records;
		XmlGalleryHandler galleryHandler = new XmlGalleryHandler(createCallerContext(), "net/dryuf/comp/gallery/xml/test/gallery_ut0/");
		List<GallerySection> sections = galleryHandler.listSections();
		Assert.assertEquals(3, sections.size());

		GalleryRecord record;
		galleryHandler.setCurrentSectionIdx(0);
		records = galleryHandler.listSectionRecords();
		Assert.assertEquals(2, records.size());
		record = records.get(0);
		Assert.assertEquals("img_5989.jpg", record.getDisplayName());
		Assert.assertEquals("img_5989.jpg", record.getTitle());
		Assert.assertEquals("first picture", record.getDescription());
		record = records.get(1);
		Assert.assertEquals("img_5993.jpg", record.getDisplayName());
		Assert.assertEquals("img_5993.jpg", record.getTitle());
		Assert.assertEquals("second picture", record.getDescription());

		galleryHandler.setCurrentSectionIdx(1);
		records = galleryHandler.listSectionRecords();
		Assert.assertEquals(2, records.size());
		record = records.get(0);
		Assert.assertEquals("20111113_194931.jpg", record.getTitle());
		record = records.get(1);
		Assert.assertEquals("20111113_201204.jpg", record.getTitle());
		record = records.get(0);
		Assert.assertTrue(record.getTitle().equals("20111113_194931.jpg") == false || record.getDisplayName().equals("20111113_194931.jpg") == false || record.getDescription().equals("Formula Rosa"));
		record = records.get(1);
		Assert.assertTrue(record.getTitle().equals("20111113_201204.jpg") == false || record.getDisplayName().equals("20111113_201204.jpg") == false || record.getDescription().equals("Ferrari World - entrance gate."));
	}
}
