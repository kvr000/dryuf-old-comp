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

package net.dryuf.comp.gallery.sql;

import java.io.IOException;
import java.util.List;

import net.dryuf.core.Dryuf;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import net.dryuf.comp.gallery.GalleryHeader;
import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.comp.gallery.GallerySource;
import net.dryuf.comp.gallery.dao.GalleryHeaderDao;
import net.dryuf.comp.gallery.dao.GalleryRecordDao;
import net.dryuf.comp.gallery.dao.GallerySectionDao;
import net.dryuf.comp.gallery.dao.GallerySourceDao;
import net.dryuf.core.EntityHolder;
import net.dryuf.dao.DaoPrimaryKeyConstraintException;
import net.dryuf.io.FileData;
import net.dryuf.io.FileDataImpl;
import net.dryuf.service.file.FileStoreService;
import net.dryuf.service.image.ImageResizeService;


public class SqlGalleryHandler extends net.dryuf.comp.gallery.GenericGalleryHandler
{
	public				SqlGalleryHandler(SqlGalleryBo galleryBo, EntityHolder<GalleryHeader> galleryHeaderHolder)
	{
		super(galleryHeaderHolder.getRole());

		this.galleryBo = galleryBo;

		this.galleryRecordDao = galleryBo.getGalleryRecordDao();
		this.gallerySectionDao = galleryBo.getGallerySectionDao();
		this.galleryHeaderDao = galleryBo.getGalleryHeaderDao();

		this.galleryHeaderHolder = galleryHeaderHolder;
		this.galleryHeader = galleryHeaderHolder.getEntity();

		this.isMulti = galleryHeader.getIsMulti();
		this.width = galleryHeader.getMaxWidth();
		this.height = galleryHeader.getMaxHeight();
		this.thumbScale = galleryHeader.getThumbScale();
	}

	public void			deleteGallery()
	{
		this.cleanGallery();
		this.galleryHeaderDao.remove(this.galleryHeader);
		if (!this.galleryHeader.getDisplayName().matches("^[-_a-zA-Z0-9]+$"))
			throw new RuntimeException("gallery has invalid characters: "+this.galleryHeader.getDisplayName());
		galleryBo.getGalleryStoreService().removePath(Dryuf.dotClassname(SqlGalleryHandler.class)+"/"+galleryHeader.getGalleryId()+"/");
		this.galleryHeader = null;
	}

	@Override
	public void			cleanGallery()
	{
		gallerySectionDao.runTransactionedSafe(() -> {
			for (GallerySection section: this.listSections()) {
				this.currentSection = section;
				this.deleteSection();
			}
			return null;
		});
	}

	public void			updateSection()
	{
		gallerySectionDao.updateSectionStats(this.currentSection.getPk());
		this.updateHeader();
	}

	public void			updateHeader()
	{
		galleryHeaderDao.updateHeaderStats(galleryHeader.getGalleryId());
	}

	public void			uploadRecordData(FileData content)
	{
		galleryBo.getGalleryStoreService().putFile(Dryuf.dotClassname(SqlGalleryBo.class)+"/"+galleryHeader.getGalleryId()+"/"+buildPath(currentSection.getDisplayName(), null), validateRecordPath(currentRecord.getDisplayName()), content);
	}

	public void			uploadRecordThumb(FileData content)
	{
		galleryBo.getGalleryStoreService().putFile(Dryuf.dotClassname(SqlGalleryBo.class)+"/"+galleryHeader.getGalleryId()+"/"+buildPath(currentSection.getDisplayName(), "thumb/"), validateRecordPath(currentRecord.getDisplayName()), content);
	}

	@Override
	public FileData			getRecordData(GalleryRecord record)
	{
		return galleryBo.getGalleryStoreService().getFile(Dryuf.dotClassname(SqlGalleryBo.class)+"/"+galleryHeader.getGalleryId()+"/"+buildPath(currentSection.getDisplayName(), null), validateRecordPath(currentRecord.getDisplayName()));
	}

	@Override
	public FileData			getRecordThumb(GalleryRecord record)
	{
		return galleryBo.getGalleryStoreService().getFile(Dryuf.dotClassname(SqlGalleryBo.class)+"/"+galleryHeader.getGalleryId()+"/"+buildPath(currentSection.getDisplayName(), "thumb/"), validateRecordPath(currentRecord.getDisplayName()));
	}

	public boolean			isMulti()
	{
		return this.isMulti;
	}

	@Override
	public void			read()
	{
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
	public GallerySection		getSectionByRecord(GalleryRecord record)
	{
		throw new RuntimeException("TODO");
	}

	@Override
	public boolean			addSection(GallerySection section)
	{
		long lastFailed = -1;
		for (;;) {
			Long counter = gallerySectionDao.getMaxSectionCounter(galleryHeader.getGalleryId());
			if (counter == null)
				counter = 0L;
			else
				++counter;
			section.setGalleryId(galleryHeader.getGalleryId());
			section.setSectionCounter(counter);
			try {
				gallerySectionDao.insertTxNew(section);
				return true;
			}
			catch (DaoPrimaryKeyConstraintException ex) {
				if (counter == lastFailed)
					throw ex;
				lastFailed = counter;
				return false;
			}
		}
	}

	public void			deleteSection()
	{
		for (GalleryRecord record: listSectionRecords()) {
			this.currentRecord = record;
			this.deleteRecord();
		}
		galleryBo.getGalleryStoreService().removePath(this.buildPath(this.currentSection.getDisplayName(), null));
		this.gallerySectionDao.removeByPk(this.currentSection.getPk());
	}

	public boolean			addRecord(GalleryRecord record, FileData imageContent)
	{
		byte[] resized;
		try {
			resized = getImageResizeService().resizeToMaxWh(IOUtils.toByteArray(imageContent.getInputStream()), width, height, true, FilenameUtils.getExtension(imageContent.getName()));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (this.currentSection == null)
			throw new NullPointerException("currentSection");
		String name = validateRecordPath(imageContent.getName());
		String path = buildPath(this.currentSection.getDisplayName(), null);
		String thumb = buildPath(this.currentSection.getDisplayName(), "thumb");

		long lastFailed = -1;
		for (;;) {
			Long counter = galleryRecordDao.getMaxRecordCounter(currentSection.getPk());
			if (counter == null)
				counter = 0L;
			else
				++counter;
			record.setRecordCounter(counter);
			record.setGallerySection(this.getCurrentSection().getPk());
			if (record.getCreated() == null)
				record.setCreated(System.currentTimeMillis());
			try {
				galleryRecordDao.insertTxNew(record);
				break;
			}
			catch (DaoPrimaryKeyConstraintException ex) {
				if (counter == lastFailed)
					throw ex;
				lastFailed = counter;
				return false;
			}
		}

		getGalleryStoreService().putFile(path, name, FileDataImpl.createFromNameBytes(name, resized));
		getGalleryStoreService().putFile(thumb, name, FileDataImpl.createFromNameBytes(name, getImageResizeService().resizeScale(resized, thumbScale, true, FilenameUtils.getExtension(imageContent.getName()))));
		return true;
	}

	public void			deleteRecord()
	{
		String fname = validateRecordPath(this.currentRecord.getDisplayName());
		String path = this.buildPath(this.currentSection.getDisplayName(), null);
		String thumb = this.buildPath(this.currentSection.getDisplayName(), "thumb");
		getGalleryStoreService().removeFile(path, fname);
		getGalleryStoreService().removeFile(thumb, fname);
		this.galleryRecordDao.remove(this.currentRecord);
		this.updateSection();
	}

	public List<GallerySection>	listSections()
	{
		return this.gallerySectionDao.listByCompos(galleryHeader.getGalleryId());
	}

	public List<GalleryRecord>	listSectionRecords()
	{
		return galleryRecordDao.listByCompos(currentSection.getPk());
	}

	public List<GallerySource>	listRecordSources()
	{
		return gallerySourceDao.listByCompos(currentRecord.getPk());
	}

	@Override
	public GallerySection		setCurrentSectionIdx(long sectionIndex)
	{
		return this.currentSection = this.gallerySectionDao.loadByPk(new GallerySection.Pk(galleryHeader.getGalleryId(), sectionIndex));
	}

	public GallerySection		setCurrentSection(String sectionName)
	{
		return this.currentSection = this.gallerySectionDao.loadByDisplay(galleryHeader.getGalleryId(), sectionName);
	}

	public GalleryRecord		setCurrentRecord(String section, String thumb, String record)
	{
		if (this.isMulti) {
			if (this.setCurrentSection(section) == null)
				return null;
		}
		this.currentThumb = thumb;
		return this.currentRecord = this.galleryRecordDao.loadByDisplay(currentSection.getPk(), record);
	}

	public GallerySection		loadSection(int sectionCounter)
	{
		return gallerySectionDao.loadByPk(new GallerySection.Pk(getGalleryId(), (long)sectionCounter));
	}

	public GalleryRecord[]		getSectionDirs()
	{
		GalleryRecord older = galleryRecordDao.loadSectionedPrevious(currentRecord.getPk());
		GalleryRecord newer = galleryRecordDao.loadSectionedNext(currentRecord.getPk());
		return new GalleryRecord[]{ older, newer };
	}

	public GalleryRecord[]		getFullDirs()
	{
		GalleryRecord older = galleryRecordDao.loadFullPrevious(currentRecord.getPk());
		GalleryRecord newer = galleryRecordDao.loadFullNext(currentRecord.getPk());
		return new GalleryRecord[]{ older, newer };
	}

	protected String		validateRecordPath(String displayName)
	{
		if (!displayName.matches("^[-_a-zA-Z0-9.]+$"))
			throw new RuntimeException("record has invalid characters: "+displayName);
		return displayName;
	}

	protected String		buildPath(String sectionDisplayName, String thumbPath)
	{
		StringBuilder path = new StringBuilder(Dryuf.dotClassname(SqlGalleryHandler.class)+"/"+galleryHeader.getGalleryId()+"/");
		if (!sectionDisplayName.matches("^[-_a-zA-Z0-9]+$"))
			throw new RuntimeException("section has invalid characters: "+sectionDisplayName);
		if (thumbPath != null && !thumbPath.matches("^[-_a-zA-Z0-9]+$"))
			throw new RuntimeException("thumbPath has invalid characters: "+thumbPath);
		if (this.isMulti)
			path.append(sectionDisplayName).append("/");
		if (thumbPath != null)
			path.append(thumbPath).append("/");
		return path.toString();
	}

	public String			getCurrentRecordPath(String innerDir)
	{
		throw new RuntimeException("TODO");
	}

	protected ImageResizeService	getImageResizeService()
	{
		return galleryBo.getImageResizeService();
	}

	protected FileStoreService	getGalleryStoreService()
	{
		return galleryBo.getGalleryStoreService();
	}

	protected long			getGalleryId()
	{
		return galleryHeader.getGalleryId();
	}

	public EntityHolder<GalleryHeader> galleryHeaderHolder;

	public GalleryHeader		galleryHeader;

	protected GallerySection	currentSection;

	protected GalleryRecord		currentRecord;

	protected String		currentThumb;

	protected boolean		isMulti = false;

	int				width;

	int				height;

	double				thumbScale;

	protected SqlGalleryBo		galleryBo;

	protected GalleryHeaderDao	galleryHeaderDao;

	protected GallerySectionDao	gallerySectionDao;

	protected GalleryRecordDao	galleryRecordDao;

	protected GallerySourceDao	gallerySourceDao;
}
