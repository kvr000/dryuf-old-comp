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
import java.util.LinkedList;
import java.util.List;

import net.dryuf.textual.TextualManager;
import net.dryuf.srvui.PageUrl;
import org.apache.commons.io.IOUtils;

import net.dryuf.comp.devel.DevelFile;
import net.dryuf.comp.devel.dao.DevelFileDao;
import net.dryuf.comp.devel.form.DevelAddFileForm;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.Options;
import net.dryuf.io.FileData;
import net.dryuf.textual.UtcDateTimeTextual;
import net.dryuf.meta.ActionDef;
import net.dryuf.mvp.Presenter;


public class DevelFilesPresenter extends net.dryuf.mvp.BeanFormPresenter<DevelAddFileForm>
{
	public				DevelFilesPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		develFileDao = getCallerContext().getBeanTyped("develFileDao", DevelFileDao.class);
	}

	@Override
	public DevelAddFileForm		createBackingObject()
	{
		return new DevelAddFileForm();
	}

	public boolean			processCommon()
	{
		@SuppressWarnings("unused")
		String uploaded;
		if ((uploaded = this.getRequest().getParamDefault("uploaded", null)) != null) {
			this.addMessageLocalized(Presenter.MSG_Info, DevelFilesPresenter.class, "Your file has been successfully uploaded.");
		}
		return super.processCommon();
	}

	public boolean			processMore(String element)
	{
		return Presenter.createSubPresenter(DevelFilesFilePresenter.class, this, Options.NONE).process();
	}

	public void			render()
	{
		if (getLeadChild() != null) {
			getLeadChild().render();
		}
		else {
			super.render();

			UtcDateTimeTextual dtTextual = TextualManager.createTextual(net.dryuf.textual.UtcDateTimeTextual.class, this.getCallerContext());
			List<EntityHolder<DevelFile>> develFiles = new LinkedList<EntityHolder<DevelFile>>();
			develFileDao.listDynamic(develFiles, new EntityHolder<Object>(null, this.getCallerContext()), null, null, null, null);
			this.output("<table>\n");
			this.outputFormat("\t<tr><th>%S</th><th>%S</th><th>%S</th><th>%S</th></tr>\n", "", "Time", "Name", "Size");
			for (EntityHolder<DevelFile> develFileEnt: develFiles) {
				DevelFile develFile = develFileEnt.getEntity();
				try {
					this.outputFormat("\t<tr><td><a href=\"%S/?action=GET\">%S</a> <a href=\"%S/?action=DELETE\">%S</a></td><td align='right'>%K</td><td>%S</td><td align='right'>%S</td></tr>\n",
							URLEncoder.encode(develFile.getCreated().toString(), "UTF-8"), "Get",
							URLEncoder.encode(develFile.getCreated().toString(), "UTF-8"), "Delete",
							dtTextual, develFile.getCreated(),
							develFile.getFileName(),
							develFile.getFileSize().toString());
				}
				catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
			this.output("</table>\n");
		}
	}

	public boolean			performAddFile(ActionDef action) throws IOException
	{
		@SuppressWarnings("unused")
		DevelAddFileForm develAddFileForm = getBackingObject();
		FileData file = getRequest().getFile(this.getFormFieldName("file"));

		DevelFile develFile = new DevelFile();
		develFile.setCreated(System.currentTimeMillis());
		develFile.setFileName(file.getName());
		develFile.setFileContent(IOUtils.toByteArray(file.getInputStream()));
		develFile.setFileSize(file.getSize());
		develFileDao.insert(develFile);
		getRootPresenter().redirect(PageUrl.createFinal("?done=POST&name="+URLEncoder.encode(file.getName(), "UTF-8")));
		return false;
	}

	DevelFileDao			develFileDao;
}
