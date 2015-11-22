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

import net.dryuf.core.CallerContext;
import net.dryuf.io.FileData;


public abstract class GenericGalleryHandler extends java.lang.Object implements net.dryuf.comp.gallery.GalleryHandler
{
	public				GenericGalleryHandler(CallerContext callerContext)
	{
		this.callerContext = callerContext;
	}

	@Override
	public int			supportsResource(String name)
	{
		return 0;
	}

	@Override
	public void			uploadResourceData(String name, FileData input)
	{
		throw new UnsupportedOperationException("uploadResourceData not supported");
	}

	@Override
	public FileData			getResourceData(String name)
	{
		throw new UnsupportedOperationException("getResourceData not supported");
	}

	@Override
	public void			cleanGallery()
	{
		throw new UnsupportedOperationException("getResourceData not supported");
	}

	protected CallerContext		callerContext;

	public CallerContext		getCallerContext()
	{
		return this.callerContext;
	}
}
