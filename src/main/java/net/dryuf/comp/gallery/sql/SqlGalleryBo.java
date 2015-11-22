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

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.comp.gallery.GalleryHeader;
import net.dryuf.comp.gallery.bo.GalleryBo;
import net.dryuf.comp.gallery.dao.GalleryHeaderDao;
import net.dryuf.comp.gallery.dao.GalleryRecordDao;
import net.dryuf.comp.gallery.dao.GallerySectionDao;
import net.dryuf.core.CallerContext;
import net.dryuf.core.EntityHolder;
import net.dryuf.service.file.FileStoreService;
import net.dryuf.service.image.ImageResizeService;
import net.dryuf.util.MapUtil;


public class SqlGalleryBo extends java.lang.Object implements GalleryBo
{
	@Override
	public EntityHolder<GalleryHeader> getGalleryObject(CallerContext callerContext, long galleryId)
	{
		List<EntityHolder<GalleryHeader>> objects = new LinkedList<EntityHolder<GalleryHeader>>();
		if (galleryHeaderDao.listDynamic(objects, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("galleryId", (Object)galleryId), null, null, null) == 0)
			return null;
		return objects.get(0);
	}

	@Override
	public EntityHolder<GalleryHeader> getGalleryObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		List<EntityHolder<GalleryHeader>> objects = new LinkedList<EntityHolder<GalleryHeader>>();
		if (galleryHeaderDao.listDynamic(objects, net.dryuf.core.EntityHolder.createRoleOnly(callerContext), MapUtil.createLinkedHashMap("refBase", (Object)refBase, "refKey", refKey), null, null, null) == 0)
			return null;
		return objects.get(0);
	}

	@Override
	public EntityHolder<GalleryHeader> getCreateGalleryObjectRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<GalleryHeader> objectHolder;
		if ((objectHolder = getGalleryObjectRef(callerContext, refBase, refKey)) == null) {
			try {
				GalleryHeader header = new GalleryHeader();
				header.setRefBase(refBase);
				header.setRefKey(refKey);
				header.setDisplayName(refKey);
				header.setLastAdded(System.currentTimeMillis());
				galleryHeaderDao.insert(header);
			}
			catch (net.dryuf.dao.DaoUniqueConstraintException ex) {
			}
			if ((objectHolder = getGalleryObjectRef(callerContext, refBase, refKey)) == null) {
				throw new RuntimeException("failed to create gallery object");
			}
		}
		return objectHolder;
	}

	@Override
	public GalleryHandler		openGallery(CallerContext callerContext, long galleryId)
	{
		EntityHolder<GalleryHeader> galleryHeaderHolder;
		if ((galleryHeaderHolder = getGalleryObject(callerContext, galleryId)) != null)
			new SqlGalleryHandler(this, galleryHeaderHolder);
		return null;
	}

	@Override
	public GalleryHandler		openGalleryRef(CallerContext callerContext, String refBase, String refKey)
	{
		EntityHolder<GalleryHeader> galleryHeaderHolder;
		if ((galleryHeaderHolder = getGalleryObjectRef(callerContext, refBase, refKey)) != null)
			new SqlGalleryHandler(this, galleryHeaderHolder);
		return null;
	}

	@Override
	public GalleryHandler		openCreateGalleryRef(CallerContext callerContext, String refBase, String refKey)
	{
		return new SqlGalleryHandler(this, getCreateGalleryObjectRef(callerContext, refBase, refKey));
	}

	@Override
	public boolean			deleteGalleryStatic(CallerContext callerContext, long galleryId)
	{
		SqlGalleryHandler galleryHandler;
		if ((galleryHandler = (SqlGalleryHandler)openGallery(callerContext, galleryId)) != null)
			galleryHandler.deleteGallery();
		return galleryHandler != null;
	}

	@Override
	public boolean			deleteGalleryStaticRef(CallerContext callerContext, String refBase, String refKey)
	{
		SqlGalleryHandler galleryHandler;
		if ((galleryHandler = (SqlGalleryHandler)openGalleryRef(callerContext, refBase, refKey)) != null)
			galleryHandler.deleteGallery();
		return galleryHandler != null;
	}

	@Inject
	protected FileStoreService	galleryStoreService;

	public FileStoreService		getGalleryStoreService()
	{
		return this.galleryStoreService;
	}

	@Inject
	protected ImageResizeService	imageResizeService;

	public ImageResizeService	getImageResizeService()
	{
		return this.imageResizeService;
	}

	@Inject
	protected GalleryHeaderDao	galleryHeaderDao;

	public GalleryHeaderDao		getGalleryHeaderDao()
	{
		return this.galleryHeaderDao;
	}

	@Inject
	protected GallerySectionDao	gallerySectionDao;

	public GallerySectionDao	getGallerySectionDao()
	{
		return this.gallerySectionDao;
	}

	@Inject
	protected GalleryRecordDao	galleryRecordDao;

	public GalleryRecordDao		getGalleryRecordDao()
	{
		return this.galleryRecordDao;
	}
}
