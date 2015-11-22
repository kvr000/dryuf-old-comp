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

package net.dryuf.comp.rating.mvp;

import net.dryuf.comp.rating.RatingHandler;
import net.dryuf.core.Dryuf;
import net.dryuf.srvui.PageUrl;
import net.dryuf.mvp.Presenter;


public class RatingPresenter extends net.dryuf.mvp.ChildPresenter
{
	public			RatingPresenter(Presenter parentPresenter, net.dryuf.core.Options options, net.dryuf.comp.rating.RatingHandler ratingHandler)
	{
		super(parentPresenter, options);

		this.ratingHandler = ratingHandler;
		this.presenterPath = options.getOptionDefault("presenterPath", "rating");
		this.updatedEvent = options.getOptionDefault("updatedEvent", null);
		if ((this.cssClass = options.getOptionDefault("css", (String)null)) == null) {
			this.cssClass = options.getOptionDefault("cssClass", "net.dryuf.comp.rating.RatingPresenter");
			this.cssFile = options.getOptionDefault("cssFile", net.dryuf.core.Dryuf.pathClassname(this.cssClass));
		}
	}

	public void			prepare()
	{
		if (this.cssFile != null)
			this.getRootPresenter().addLinkedFile("css", PageUrl.createResource(this.cssFile));
	}

	public boolean			processCommon()
	{
		String rating;
		if ((rating = this.getRequest().getParam("rating")) != null) {
			if (ratingHandler.getCallerContext().checkRole("Rating.rate")) {
				this.ratingHandler.addRating(((Number)this.getCallerContext().getUserId()).longValue(), Integer.valueOf(rating));
				if (updatedEvent != null)
					Dryuf.invokeMethodString0(getParentPresenter(), updatedEvent);
				getRootPresenter().getResponse().redirect("../");
				return false;
			}
			else {
				this.createDeniedPresenter();
			}
		}
		return true;
	}

	public void			render()
	{
		this.outputFormat("<span class=\"%S\" title=\"%W\">", this.cssClass, RatingPresenter.class, ratingHandler.getCallerContext().checkRole("Rating.rate") ? "Rate here" : "Login to rate");
		this.outputFormat("<span class='positive'>");
		int i;
		for (i = 1; i <= this.ratingHandler.getRatingValue(); i++)
			this.renderRatingStar(i);
		this.outputFormat("</span><span class='none'>");
		for (; i <= this.ratingHandler.getMaxRating(); i++)
			this.renderRatingStar(i);
		this.outputFormat("</span>");
		this.outputFormat("</span>");
	}

	public void			renderRatingStar(int i)
	{
		if (!ratingHandler.getCallerContext().checkRole("Rating.rate")) {
			this.output("★");
		}
		else {
			this.outputFormat("<a href=\"%S/?rating=%S\">★</a>", presenterPath, String.valueOf(i));
		}
	}

	protected RatingHandler		ratingHandler;

	public RatingHandler		getRatingHandler()
	{
		return this.ratingHandler;
	}

	protected String		presenterPath;

	protected String		updatedEvent;

	protected String		cssClass;

	protected String		cssFile;
}
