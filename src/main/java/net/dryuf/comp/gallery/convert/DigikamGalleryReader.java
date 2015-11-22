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

package net.dryuf.comp.gallery.convert;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.dryuf.util.MapUtil;
import org.apache.commons.io.FilenameUtils;

import net.dryuf.core.CallerContext;
import net.dryuf.core.Dryuf;
import net.dryuf.core.Options;
import net.dryuf.sql.SqlHelper;


public class DigikamGalleryReader extends java.lang.Object
{
	public			DigikamGalleryReader(net.dryuf.core.CallerContext callerContext, net.dryuf.core.Options options)
	{
		this.callerContext = callerContext;

		this.useOrig = options.getOptionDefault("useOrig", true);
		this.isMulti = (Boolean) options.getOptionMandatory("isMulti");
		this.databaseFile = (String) options.getOptionMandatory("databaseFile");
		if ((this.sortField = options.getOptionDefault("sortField", null)) == null)
			this.sortField = "imageid";
		switch (this.sortField) {
		case "imageid":
		case "creationDate":
			break;

		default:
			throw new RuntimeException("invalid sortField: "+this.sortField);
		}


		try {
			Class.forName("org.sqlite.JDBC");
			dbConnection = DriverManager.getConnection("jdbc:sqlite:"+this.databaseFile+";open_mode=1");
		}
		catch (Exception e1) {
			throw new RuntimeException(e1);
		}

		this.disabledTagId = (String)SqlHelper.runField(this.dbConnection, "id", "Tags", "name", "disabled");
	}

	public List<Map<String, Object>> listGalleries()
	{
		return SqlHelper.queryRows(this.dbConnection, "SELECT AlbumRoots.label, AlbumRoots.specificPath, Albums.id, Albums.relativePath FROM Albums INNER JOIN AlbumRoots ON AlbumRoots.id = Albums.albumRoot", new Object[]{ });
	}

	public long			setGalleries(String rootId, String galleryPath)
	{
		galleryPath = galleryPath.replaceAll("/+$", "");
		Map<String, Object> albumRoot = SqlHelper.queryOneRow(this.dbConnection, "SELECT id, specificPath FROM AlbumRoots WHERE label = ?", new Object[]{ rootId });
		if (this.useOrig) {
			this.galleries = SqlHelper.queryRows(this.dbConnection, "SELECT id, relativePath, caption, collection FROM Albums WHERE albumRoot = ? AND relativePath LIKE ? AND relativePath LIKE '%/orig' ORDER BY relativePath", new Object[]{ albumRoot.get("id"), galleryPath+(this.isMulti ? "/%" : "")+"/orig" });
		}
		else {
			this.galleries = SqlHelper.queryRows(this.dbConnection, "SELECT id, relativePath, caption, collection FROM Albums WHERE albumRoot = ? AND relativePath LIKE ? AND relativePath NOT LIKE '%/thumb' AND relativePath NOT LIKE '%/orig' ORDER BY relativePath", new Object[]{ albumRoot.get("id"), galleryPath+(this.isMulti ? "/%" : "") });
		}
		return galleries.size();
	}

	public void			writeToXmlGallery(net.dryuf.comp.gallery.convert.XmlGalleryWriter writer)
	{
		writer.startOutput();
		writer.openLocations();
		writer.openLocation(net.dryuf.core.Options.buildListed("id", "", "store", "", "thumb", ""));
		writer.closeLocation();
		writer.closeLocations();
		writer.openSections(this.isMulti);
		for (Map<String, Object> gallery: this.galleries) {
			writer.openSection(net.dryuf.core.Options.buildListed("id", this.getGalleryName(gallery), "location", "", "title", gallery.get("caption")));
			writer.openRecords();
			for (Map<String, Object> image: SqlHelper.queryRows(this.dbConnection, "SELECT i.id, i.name FROM Images i LEFT JOIN ImageInformation info ON info.imageid = i.id WHERE i.album = ? AND i.id NOT IN (SELECT it.imageid FROM ImageTags it WHERE it.tagid = ?) ORDER BY info."+this.sortField+", i.id", new Object[]{ gallery.get("id"), this.disabledTagId })) {
				String origName = (String)image.get("name");
				String name = origName;
				String extension = FilenameUtils.getExtension(name).toLowerCase();
				if (videoExtensions.containsKey(extension)) {
					name = FilenameUtils.removeExtension(name)+".jpg";
				}
				writer.openRecord(net.dryuf.core.Options.buildListed("file", name, "title", this.getBestImageDesc(image), "description", this.getBestImageDesc(image), "recordType", videoExtensions.containsKey(extension) ? "video" : "picture"));
				if (videoExtensions.containsKey(extension)) {
					writer.openSources();
					writer.openSource(Options.buildListed("file", origName, "mimeType", videoExtensions.get(extension)));
					writer.closeSource();
					writer.closeSources();
				}
				writer.closeRecord();
			}
			writer.closeRecords();
			writer.closeSection();
		}
		writer.closeSections();
		writer.finishOutput();
	}

	public String			getGalleryName(Map<String, Object> gallery)
	{
		return new File(this.useOrig ? new File((String) gallery.get("relativePath")).getParent() : (String)gallery.get("relativePath")).getName();
	}

	@SuppressWarnings("cast")
	public String			getBestImageDesc(Map<String, Object> image)
	{
		List<Map<String, Object>> comments;
		comments = SqlHelper.queryRows(this.dbConnection, "SELECT language, comment FROM ImageComments WHERE imageId = ? ORDER BY id", new Object[]{ image.get("id") });
		if (comments.size() == 0)
			return null;
		return (String) ((Map<String, Object>)comments.get(0)).get("comment");
	}

	protected String		disabledTagId = null;

	protected CallerContext		callerContext;

	protected String		databaseFile;

	protected boolean		isMulti;

	protected List<Map<String, Object>> galleries;

	protected boolean		useOrig;

	protected String		sortField;

	protected Connection		dbConnection;

	protected static Map<String, String> videoExtensions = MapUtil.createHashMap("mp4", "video/mp4", "avi", "video/avi");
}
