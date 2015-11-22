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

package net.dryuf.comp.gallery.mvp;

import net.dryuf.comp.gallery.GalleryHandler;
import net.dryuf.core.CallerContext;
import net.dryuf.core.Options;
import net.dryuf.core.RoleContext;
import net.dryuf.mvp.Presenter;


public class OptionedGalleryPresenter extends net.dryuf.comp.gallery.mvp.GalleryPresenter
{
	public			OptionedGalleryPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options, createGalleryHandler(parentPresenter, options));
	}

	public static GalleryHandler	createGalleryHandler(Presenter parentPresenter, Options options)
	{
		CallerContext callerContext = parentPresenter.getCallerContext();
		String[] roleMapping;
		if ((roleMapping = (String[])options.getOptionDefault("roleMapping", null)) != null)
			callerContext = RoleContext.createMapped(callerContext, roleMapping);
		String galleryType = (String) options.getOptionMandatory("galleryType");
		switch (galleryType) {
		case "xml":
			return new net.dryuf.comp.gallery.xml.XmlDomGalleryHandler(callerContext, parentPresenter.getRootPresenter().getRealPath());

		case "dir":
			return new net.dryuf.comp.gallery.dir.DirGalleryHandler(callerContext, parentPresenter.getRootPresenter().getRealPath());

		default:
			throw new RuntimeException("unknown gallery handler type: "+galleryType+"");
		}
	}
}
