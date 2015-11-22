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

package net.dryuf.comp.gallery.sql.test;

import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.comp.gallery.sql.SqlGalleryBo;
import net.dryuf.core.Dryuf;
import org.junit.Assert;
import org.junit.Test;

import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.io.FileDataImpl;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class SqlGalleryHandlerTest extends net.dryuf.tenv.AppTenvObject
{
	public SqlGalleryHandlerTest()
	{
	}

	protected void			addGalleryRecord(GalleryHandler handler, String displayName, String title, String description)
	{
		GalleryRecord record = new GalleryRecord();
		record.setDisplayName(displayName);
		record.setTitle(title);
		record.setDescription(description);
		handler.addRecord(record, FileDataImpl.createFromNameBytes("a.jpeg", IMAGE_DATA));
	}

	public GalleryHandler		initGallery()
	{
		GalleryHandler galleryHandler;
		galleryHandler = getAppContainer().createBeaned(SqlGalleryBo.class, null).openCreateGalleryRef(createCallerContext(), Dryuf.dotClassname(SqlGalleryHandlerTest.class.getName()), "test");
		galleryHandler.cleanGallery();

		GallerySection gallerySection = new GallerySection();
		gallerySection.setDisplayName("section1");
		gallerySection.setTitle("section1");
		gallerySection.setDescription("section1");
		galleryHandler.addSection(gallerySection);
		galleryHandler.setCurrentSectionIdx(0);
		addGalleryRecord(galleryHandler, "picture11.jpg", "picture11", "description11");
		addGalleryRecord(galleryHandler, "picture12.jpg", "picture12", "description12");

		gallerySection = new GallerySection();
		gallerySection.setDisplayName("section2");
		gallerySection.setTitle("section2");
		gallerySection.setDescription("section2");
		galleryHandler.addSection(gallerySection);
		galleryHandler.setCurrentSection("section2");
		addGalleryRecord(galleryHandler, "picture21.jpg", "picture21", "description21");
		addGalleryRecord(galleryHandler, "picture22.jpg", "picture22", "description22");

		return galleryHandler;
	}

	@Test
	public void                     testHandler()
	{
		galleryHandler = initGallery();

		galleryHandler.setCurrentSectionIdx(0);
		List<GalleryRecord> records = galleryHandler.listSectionRecords();
		Assert.assertEquals(2, records.size());
		Assert.assertEquals("picture11", records.get(0).getTitle());
		Assert.assertEquals("picture12", records.get(1).getTitle());

		Assert.assertNotNull(galleryHandler.setCurrentRecord("section2", null, "picture21.jpg"));
		GalleryRecord[] section_dirs = galleryHandler.getSectionDirs();
		GalleryRecord[] full_dirs = galleryHandler.getFullDirs();

		Assert.assertNull(section_dirs[0]);
		Assert.assertNotNull(section_dirs[1]);
		Assert.assertEquals("picture22", section_dirs[1].getTitle());
		Assert.assertEquals("picture12", full_dirs[0].getTitle());
		Assert.assertEquals("picture22", full_dirs[1].getTitle());
	}

	public GalleryHandler		galleryHandler;

	static final byte[]		IMAGE_DATA = new byte[]{ (byte)0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a, 0x00, 0x00, 0x00, 0x0d, 0x49, 0x48, 0x44, 0x52, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01, 0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53, (byte) 0xde, 0x00, 0x00, 0x00, 0x01, 0x73, 0x52, 0x47, 0x42, 0x00, (byte) 0xae, (byte) 0xce, 0x1c, (byte) 0xe9, 0x00, 0x00, 0x00, 0x09, 0x70, 0x48, 0x59, 0x73, 0x00, 0x00, 0x0b, 0x13, 0x00, 0x00, 0x0b, 0x13, 0x01, 0x00, (byte) 0x9a, (byte) 0x9c, 0x18, 0x00, 0x00, 0x00, 0x07, 0x74, 0x49, 0x4d, 0x45, 0x07, (byte) 0xdb, 0x0c, 0x0f, 0x0a, 0x2f, 0x29, 0x74, (byte) 0x87, (byte) 0x9e, (byte) 0xcc, 0x00, 0x00, 0x00, 0x19, 0x74, 0x45, 0x58, 0x74, 0x43, 0x6f, 0x6d, 0x6d, 0x65, 0x6e, 0x74, 0x00, 0x43, 0x72, 0x65, 0x61, 0x74, 0x65, 0x64, 0x20, 0x77, 0x69, 0x74, 0x68, 0x20, 0x47, 0x49, 0x4d, 0x50, 0x57, (byte) 0x81, 0x0e, 0x17, 0x00, 0x00, 0x00, 0x0c, 0x49, 0x44, 0x41, 0x54, 0x08, (byte) 0xd7, 0x63, (byte) 0xf8, (byte) 0xff, (byte) 0xff, 0x3f, 0x00, 0x05, (byte) 0xfe, 0x02, (byte) 0xfe, (byte) 0xdc, (byte) 0xcc, 0x59, (byte) 0xe7, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4e, 0x44, (byte) 0xae, 0x42, 0x60, (byte) 0x82 };
}
