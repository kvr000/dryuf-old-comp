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

package net.dryuf.comp.devel.mvp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.dryuf.comp.devel.DevelFile;
import net.dryuf.comp.devel.dao.DevelFileDao;
import net.dryuf.core.Options;
import net.dryuf.mvp.ChildPresenter;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;
import net.dryuf.srvui.Response;


public class DevelFilesFilePresenter extends ChildPresenter
{
	DevelFileDao			develFileDao;


	public				DevelFilesFilePresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		develFileDao = getCallerContext().getBeanTyped("develFileDao", DevelFileDao.class);
	}

	public boolean			processFinal()
	{
		String filename = parentPresenter.getRootPresenter().getLastElementWithoutSlash();
		develFile = develFileDao.loadByPk(Long.valueOf(filename));
		if (develFile == null) {
			return this.createNotFoundPresenter().process();
		}
		else if (getRootPresenter().needPathSlash(true) == null) {
			return false;
		}
		return super.processFinal();
	}

	public boolean			processCommon()
	{
		String action = this.getRootPresenter().getRequest().getParam("action");
		if (action.equals("GET")) {
			Response response = getRootPresenter().getResponse();
			response.setContentType("application/octet-stream");
			response.setDateHeader("Last-Modified", develFile.getCreated());
			try {
				response.setHeader("Content-Disposition", "inline; filename="+URLEncoder.encode(develFile.getFileName(), "UTF-8"));
			}
			catch (UnsupportedEncodingException ex) {
				throw new RuntimeException(ex);
			}
			try {
				response.getOutputStream().write(develFile.getFileContent());
			}
			catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}
		else if (action.equals("DELETE")) {
			return true;
		}
		else if (action.equals("DELETE_CONFIRM")) {
			develFileDao.removeByPk(develFile.getPk());
			this.getRootPresenter().redirect(PageUrl.createRelative("../?performed=DELETE"));
		}
		else {
			throw new RuntimeException("unknown action: "+action);
		}
		return false;
	}

	public void			render()
	{
		this.outputFormat("<p>Please <a href=\"%U\">confirm deletion of %S</a>.</p>\n", PageUrl.createRelative("?action=DELETE_CONFIRM"), develFile.getFileName());
	}

	DevelFile			develFile;
}
