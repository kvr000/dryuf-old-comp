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

import java.io.IOException;
import java.io.OutputStream;

import java.nio.charset.StandardCharsets;

import net.dryuf.core.CallerContext;
import net.dryuf.core.Options;
import net.dryuf.xml.util.XmlFormat;


public class XmlGalleryWriter extends java.lang.Object
{
	public				XmlGalleryWriter(CallerContext callerContext, OutputStream out)
	{
		this.callerContext = callerContext;
		this.out = out;
	}

	public void			writeRaw(String s)
	{
		try {
			out.write(s.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void			startOutput()
	{
		writeRaw("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		writeRaw(
				"<gallery\n"+
				"\txmlns=\"http://dryuf.org/schema/net/dryuf/comp/gallery/xml/gallery/\"\n"+
				"\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
				"\txsi:schemaLocation=\"\n"+
				"\t\thttp://dryuf.org/schema/net/dryuf/comp/gallery/xml/gallery/ http://www.znj.cz/schema/net/dryuf/comp/gallery/xml/gallery.xsd\n"+
				"\t\">\n"
				);
	}

	public void			finishOutput()
	{
		writeRaw("</gallery>\n");
		try {
			this.out.flush();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void			openLocations()
	{
		XmlFormat.formatStream(out, callerContext, "\t<locations>\n");
	}

	public void			openLocation(net.dryuf.core.Options options)
	{
		XmlFormat.formatStream(out, callerContext, "\t\t<location id=%A store=%A thumb=%A>\n", options.getOptionMandatory("id"), options.getOptionMandatory("store"), options.getOptionMandatory("thumb"));
	}

	public void			closeLocation()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t</location>\n");
	}

	public void			closeLocations()
	{
		XmlFormat.formatStream(out, callerContext, "\t</locations>\n");
	}

	public void			openSections(boolean isMulti)
	{
		XmlFormat.formatStream(out, callerContext, "\t<sections multi=%A>\n", isMulti ? 1 : 0);
	}

	public void			writeOptionalElement(int indent, String element, Options options, String option)
	{
		String value;
		if ((value = options.getOptionDefault(option, null)) != null) {
			for (; indent > 0; indent--)
				XmlFormat.formatStream(out, callerContext, "\t");
			XmlFormat.formatStream(out, callerContext, "<"+element+">%S</"+element+">\n", value);
		}
	}

	public void			writeOptionalAttr(String attr, Options options, String option)
	{
		String value;
		if ((value = options.getOptionDefault(option, null)) != null) {
			XmlFormat.formatStream(out, callerContext, " %s=%A", attr, value);
		}
	}

	public void			openSection(net.dryuf.core.Options options)
	{
		XmlFormat.formatStream(out, callerContext, "\t\t<section id=%A location=%A>\n", options.getOptionMandatory("id"), options.getOptionMandatory("location"));
		this.writeOptionalElement(3, "title", options, "title");
		this.writeOptionalElement(3, "description", options, "description");
	}

	public void			openRecords()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t<records>\n");
	}

	public void			openRecord(net.dryuf.core.Options options)
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t\t<record file=%A", options.getOptionMandatory("file"));
		this.writeOptionalAttr("recordType", options, "recordType");
		this.writeOptionalAttr("location", options, "location");
		writeRaw(">\n");
		this.writeOptionalElement(5, "description", options, "description");
		this.writeOptionalElement(5, "title", options, "title");
	}

	public void			openSources()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t\t\t<sources>\n");
	}

	public void			openSource(Options options)
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t\t\t\t<source");
		writeOptionalAttr("file", options, "file");
		writeOptionalAttr("mimeType", options, "mimeType");
		XmlFormat.formatStream(out, callerContext, ">");
	}

	public void			closeSource()
	{
		XmlFormat.formatStream(out, callerContext, "</source>\n");
	}

	public void			closeSources()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t\t\t</sources>\n");
	}

	public void			closeRecord()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t\t</record>\n");
	}

	public void			closeRecords()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t\t</records>\n");
	}

	public void			closeSection()
	{
		XmlFormat.formatStream(out, callerContext, "\t\t</section>\n");
	}

	public void			closeSections()
	{
		XmlFormat.formatStream(out, callerContext, "\t</sections>\n");
	}

	protected OutputStream		out;
	protected CallerContext		callerContext;
}
