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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import javax.inject.Inject;

import net.dryuf.util.CollectionUtil;
import net.dryuf.util.MapUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.comp.gallery.convert.DigikamGalleryReader;
import net.dryuf.comp.gallery.xml.XmlDomGalleryHandler;
import net.dryuf.core.AppContainer;
import net.dryuf.core.Dryuf;
import net.dryuf.process.command.AbstractCommand;
import net.dryuf.process.command.ExternalCommandRunner;
import net.dryuf.process.command.GetOptions;
import net.dryuf.process.command.GetOptionsStd;
import net.dryuf.service.image.ImageResizeService;


public class Digikam2GalleryCommand extends AbstractCommand
{
	@SuppressWarnings({"resource", "cast"})
	public void			main(String[] arguments)
	{
		System.exit(new ExternalCommandRunner(((AppContainer)new ClassPathXmlApplicationContext().getBean("appContainer", AppContainer.class)).createCallerContext()).runNew(Digikam2GalleryCommand.class, arguments));
	}

	@Override
	public String			parseArguments(String[] arguments)
	{
		options = MapUtil.createHashMap(
				"l",			(Object)false,
				"m",			false,
				"O",			true,
				"s",			null,
				"S",			null
				);
		return super.parseArguments(arguments);
	}

	@Override
	public int			reportUsage(String reason)
	{
		return commandRunner.reportUsage(
				reason,
				"Options: -f dbfile [-m] [-O] -r root -g gallery\n"+
				"	-f db-file		path to digikam database\n"+
				"	-p gallery-root		gallery root\n"+
				"	-g gallery-name		gallery name\n"+
				"	-m			is multi section gallery\n"+
				"	-O			do not use orig directory\n"+
				"	-s sort-field		sort field\n"+
				"	-L			list galleries\n"+
				"	-S			generated scaled\n"
				);
	}

	@Override
	public String			validateArguments()
	{
		if (options.containsKey("L"))
			return null;
		if (options.containsKey("g")) {
			if (!options.containsKey("p"))
				return getCallerContext().getUiContext().localize(Digikam2GalleryCommand.class, "Option p is mandatory when g is specified.");
			return null;
		}

		return getUiContext().localize(Digikam2GalleryCommand.class, "One of options -L or -g is mandatory.");
	}

	@Override
	public int			process()
	{
		galleryReader = new net.dryuf.comp.gallery.convert.DigikamGalleryReader(callerContext, net.dryuf.core.Options.buildListed(
				"isMulti",			options.get("m"),
				"useOrig",			options.get("O"),
				"sortField",			options.get("s"),
				"databaseFile",			options.get("f")
				));

		int error;

		if ((Boolean)options.get("L") && (error = processList()) != 0) {
			return error;
		}

		if ((Boolean)options.get("g") && (error = processGenerating()) != 0)
			return error;

		if ((Boolean)options.get("S") && (error = processScale()) != 0)
			return error;

		return 0;
	}

	protected int			processList()
	{
		System.out.format("%-19s %-39s %-39s %8s\n", "label", "specificPath", "relativePath", "id");
		for (Map<String, Object> gallery: galleryReader.listGalleries()) {
			System.out.format("%-19s %-39s %-39s %8d\n", gallery.get("label"), gallery.get("specificPath"), gallery.get("relativePath"), gallery.get("id"));
		}
		return 0;
	}

	protected int			processGenerating()
	{
		if (galleryReader.setGalleries((String)options.get("p"), (String)options.get("g")) == 0) {
			System.err.format("Warning: no gallery of name %s found\n", (String)options.get("g"));
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		galleryReader.writeToXmlGallery(new net.dryuf.comp.gallery.convert.XmlGalleryWriter(callerContext, stream));
		System.out.print(new String(stream.toByteArray()));
		return 0;
	}

	protected int			processScale()
	{
		int errors = 0;
		XmlDomGalleryHandler handler = new XmlDomGalleryHandler(getCallerContext(), "./");
		for (GallerySection section: handler.listSections()) {
			String sectionRoot = handler.isMulti() ? section.getDisplayName()+"/" : "";
			handler.setCurrentSection(section.getDisplayName());
			for (GalleryRecord record: handler.listSectionRecords()) {
				try {
					File origFile = new File(sectionRoot+"orig/"+record.getDisplayName());
					File recordFile = new File(sectionRoot+record.getDisplayName());
					File thumbFile = new File(sectionRoot+"thumb/"+record.getDisplayName());
					if (!recordFile.exists() || origFile.lastModified() > recordFile.lastModified()) {
						System.err.print("updating main for "+record.getDisplayName()+"\n");
						byte[] scaledRecord = imageResizeService.resizeToMaxWh(FileUtils.readFileToByteArray(origFile), 1000, 750, true, FilenameUtils.getExtension(record.getDisplayName()));
						FileUtils.writeByteArrayToFile(recordFile, scaledRecord);
					}
					if (!thumbFile.exists() || recordFile.lastModified() > thumbFile.lastModified()) {
						new File(sectionRoot+"thumb").mkdirs();
						System.err.print("updating thumb for "+record.getDisplayName()+"\n");
						byte[] scaledThumb = imageResizeService.resizeScale(FileUtils.readFileToByteArray(recordFile), 0.2, true, FilenameUtils.getExtension(record.getDisplayName()));
						FileUtils.writeByteArrayToFile(thumbFile, scaledThumb);
					}
				}
				catch (Exception e) {
					System.err.print("Failed to process "+sectionRoot+record.getDisplayName()+": "+e.getMessage()+"\n");
					++errors;
					//throw new RuntimeException(e);
				}
			}
		}
		System.err.print("Done processing, "+errors+" errors noticed\n");
		return errors != 0 ? 1 : 0;
	}

	protected DigikamGalleryReader	galleryReader;

	protected List<Integer>		actions = new LinkedList<Integer>();

	protected Map<String, Object>	options;

	public Map<String, Object>	getOptions()
	{
		return this.options;
	}

	@Inject
	ImageResizeService		imageResizeService;

	@Override
	public GetOptions		getOptionsDefinition()
	{
		return optionsDefinition;
	}

	protected static GetOptions	optionsDefinition = new GetOptionsStd()
			.setDefinition(MapUtil.createHashMap(
				"m",			null,
				"O",			null,
				"f",			net.dryuf.textual.TextTextual.class,
				"p",			net.dryuf.textual.TextTextual.class,
				"g",			net.dryuf.textual.TextTextual.class,
				"s",			net.dryuf.textual.TextTextual.class,
				"h",			null,
				"L",			null,
				"S",			null
			))
			.setMandatories(CollectionUtil.createLinkedHashSet(
				"f"
			));
}
