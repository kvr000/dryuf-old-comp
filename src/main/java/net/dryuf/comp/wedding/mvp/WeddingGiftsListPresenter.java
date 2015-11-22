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

package net.dryuf.comp.wedding.mvp;

import java.util.LinkedList;

import net.dryuf.comp.wedding.WeddingGiftsGift;
import net.dryuf.comp.wedding.WeddingGiftsHeader;
import net.dryuf.comp.wedding.dao.WeddingGiftsGiftDao;
import net.dryuf.comp.wedding.dao.WeddingGiftsHeaderDao;
import net.dryuf.core.EntityHolder;
import net.dryuf.net.util.UrlUtil;
import net.dryuf.xml.util.XmlFormat;


public class WeddingGiftsListPresenter extends net.dryuf.mvp.ChildPresenter
{
	public				WeddingGiftsListPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options);

		giftsPresenter = (WeddingGiftsPresenter)parentPresenter;
		this.weddingGiftsHeaderDao = giftsPresenter.getWeddingGiftsHeaderDao();
		this.weddingGiftsGiftDao = giftsPresenter.getWeddingGiftsGiftDao();
		this.weddingGiftsHeader = giftsPresenter.getWeddingGiftsHeader();
		this.weddingGiftsId = giftsPresenter.getWeddingGiftsId();

		giftsPresenter.setMode(WeddingGiftsPresenter.MODE_LIST);
	}

	public void			render()
	{
		LinkedList<EntityHolder<WeddingGiftsGift>> gifts = new LinkedList<EntityHolder<WeddingGiftsGift>>();
		this.weddingGiftsGiftDao.listDynamic(gifts, new EntityHolder<WeddingGiftsHeader>(this.weddingGiftsHeader, this.getCallerContext()), null, null, null, null);
		if (weddingGiftsHeader.getCoordinatorEmail() != null) {
			this.output("<p>");
			this.output(this.localize(WeddingGiftsListPresenter.class, "In case of questions please contact our <a href=\"coordinator.html\">gift coordinator</a>."));
			this.output("</p>");
		}
		this.outputFormat("<table border=\"1\">\n<tr><th>%W</th><th>%W</th><th>%W</th><th>%W</th></tr>\n", WeddingGiftsListPresenter.class, "Inspiration", getClass(), "Name", getClass(), "Description", getClass(), "State");
		for (EntityHolder<WeddingGiftsGift> holder: gifts) {
			WeddingGiftsGift gift = holder.getEntity();
			this.outputFormat("<tr><td><img width='198' src=%A /></td><td>%S</td><td>%S</td><td>", gift.getInspirationUrl(), gift.getName(), gift.getDescription());
			if (gift.getReservedCode() == null) {
				outputFormat("%s, <a href=\"%s/reserve.html\">%s</a>", XmlFormat.escapeXml(this.localize(WeddingGiftsListPresenter.class, "Available")), XmlFormat.escapeXml(UrlUtil.encodeUrl(gift.getDisplayName())), XmlFormat.escapeXml(this.localize(WeddingGiftsListPresenter.class, "Reserve")));
			}
			else {
				outputFormat("%s, <a href=\"%s/cancel.html\">%s</a>", XmlFormat.escapeXml(this.localize(WeddingGiftsListPresenter.class, "Reserved")), XmlFormat.escapeXml(UrlUtil.encodeUrl(gift.getDisplayName())), XmlFormat.escapeXml(this.localize(WeddingGiftsListPresenter.class, "Cancel")));
			}
			this.output("</td></tr>\n");
		}
		if (weddingGiftsHeader.getProposalEmail() != null) {
			this.outputFormat("<tr><td align='center'><b><font size=\"+5\">?</font></b></td><td>%W</td><td>%W</td><td><a href=\"propose.html\">%W</a></td></tr>\n", WeddingGiftsListPresenter.class, "Proposal", getClass(), "Proposal according to your ideas.", getClass(), "Propose");
		}
		this.output("</table>\n");
	}

	protected WeddingGiftsPresenter	giftsPresenter;

	protected WeddingGiftsHeaderDao	weddingGiftsHeaderDao;

	protected WeddingGiftsGiftDao	weddingGiftsGiftDao;

	protected WeddingGiftsHeader	weddingGiftsHeader;

	protected long			weddingGiftsId;
}
