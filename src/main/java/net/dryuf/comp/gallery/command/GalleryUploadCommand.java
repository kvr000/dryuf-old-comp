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

package net.dryuf.comp.gallery.command;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import net.dryuf.comp.gallery.mvp.GalleryUploader;
import net.dryuf.textual.LineTrimTextual;
import net.dryuf.process.command.AbstractCommand;
import net.dryuf.process.command.ExternalCommandRunner;
import net.dryuf.process.command.GetOptions;
import net.dryuf.process.command.GetOptionsStd;
import net.dryuf.core.StringUtil;
import net.dryuf.util.CollectionUtil;
import net.dryuf.util.MapUtil;


public class GalleryUploadCommand extends AbstractCommand
{
	public static final int		ACTION_Config = 0;
	public static final int		ACTION_Data = 1;

	public void			main(String[] arguments)
	{
		System.exit(ExternalCommandRunner.createFromClassPath().runNew(GalleryUploadCommand.class, arguments));
	}

	@Override
	public String			parseArguments(String[] arguments)
	{
		options = MapUtil.createHashMap(
				"G",			(Object)false,
				"D",			false
				);
		return super.parseArguments(arguments);
	}

	@Override
	protected String		validateArguments()
	{
		if ((Boolean)options.get("G"))
			actions.add(ACTION_Config);
		if ((Boolean)options.get("D"))
			actions.add(ACTION_Data);
		if (actions.size() == 0)
			return getUiContext().localize(GalleryUploadCommand.class, "You have to specify action, e.g. -G or -D");

		return null;
	}

	@Override
	public int			reportUsage(String reason)
	{
		return commandRunner.reportUsage(
				reason,
				"Options: -u url -s sid [-h] [-G] [-D]\n"+
				"	-u url	URL target (including final /)\n"+
				"	-s sid	session id\n"+
				"	-G	upload gallery+xml	\n"+
				"	-D	upload records data\n"
				);
	}

	@Override
	public int			process()
	{
		GalleryUploader galleryUploader = new net.dryuf.comp.gallery.mvp.GalleryUploader(callerContext, net.dryuf.core.Options.buildListed(
			"targetUrl",		options.get("u"),
			"sid",			options.get("s")
		), new net.dryuf.comp.gallery.xml.XmlDomGalleryHandler(callerContext, "./"));

		for (int action: actions) {
			switch (action) {
			case ACTION_Config:
				galleryUploader.uploadResources();
				break;

			case ACTION_Data:
				if (((String[])options.get("")).length == 0) {
					galleryUploader.uploadData();
				}
				else {
					for (String element: (String[])options.get("")) {
						String[] match;
						if ((match = StringUtil.matchText("^([^/]+)/([^/]+)$", element)) != null) {
							galleryUploader.uploadRecord(match[1], match[2]);
						}
						else if ((match = StringUtil.matchText("^([^/]+)/$", element)) != null) {
							galleryUploader.uploadSection(match[1]);
						}
						else {
							return commandRunner.reportUsage("unexpected parameter "+element+"\nexpecting either full image filename or section/\n", null);
						}
					}
				}
				break;

			default:
				throw new RuntimeException("unhandled action: "+action);
			}
		}
		return 0;
	}

	protected List<Integer>		actions = new LinkedList<Integer>();

	@Override
	public GetOptions		getOptionsDefinition()
	{
		return optionsDefinition;
	}

	protected Map<String, Object>	options;

	public Map<String, Object>	getOptions()
	{
		return this.options;
	}

	protected static GetOptions	optionsDefinition = new GetOptionsStd()
			.setDefinition(MapUtil.createHashMap(
						"u",			LineTrimTextual.class,
						"s",			LineTrimTextual.class,
						"G",			null,
						"D",			null,
						"h",			null
						))
			.setMandatories(CollectionUtil.createLinkedHashSet(
						"u",
						"s"
						))
			.setMinParameters(0)
			.setMaxParameters(0x7fffffff);
}
