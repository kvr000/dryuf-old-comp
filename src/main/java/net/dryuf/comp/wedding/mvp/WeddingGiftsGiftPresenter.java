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
import net.dryuf.core.Dryuf;
import net.dryuf.core.EntityHolder;
import net.dryuf.mvp.ChildPresenter;
import net.dryuf.mvp.PresenterDivider;
import net.dryuf.mvp.PresenterElement;
import net.dryuf.mvp.StaticPresenterDivider;
import net.dryuf.util.MapUtil;


public class WeddingGiftsGiftPresenter extends ChildPresenter
{
	public				WeddingGiftsGiftPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options);

		giftsPresenter = (WeddingGiftsPresenter)getParentPresenter();
		this.weddingGiftsId = giftsPresenter.getWeddingGiftsId();
		this.weddingGiftsHeader = giftsPresenter.getWeddingGiftsHeader();
		this.weddingGiftsGiftDao = giftsPresenter.getWeddingGiftsGiftDao();
	}

	public void			setMode(int mode)
	{
		giftsPresenter.setMode(mode);
	}

	public boolean			process()
	{
		String displayName = this.getRootPresenter().getLastElementWithoutSlash();
		LinkedList<EntityHolder<WeddingGiftsGift>> gifts = new LinkedList<EntityHolder<WeddingGiftsGift>>();
		this.weddingGiftsGiftDao.listDynamic(gifts, giftsPresenter.getWeddingGiftsHeaderHolder(), MapUtil.createHashMap("pk.displayName", (Object)displayName), null, null, null);
		if (gifts.size() != 1) {
			this.parentPresenter.setLeadChild(null);
			this.createNotFoundPresenter();
			return true;
		}
		else {
			this.giftHolder = gifts.get(0);
			this.gift = giftHolder.getEntity();
		}
		return divider.process(this);
	}

	public void			render()
	{
		this.outputFormat("<p>%S: %S</p>\n", this.gift.getName(), this.gift.getDescription());
		super.render();
	}

	protected WeddingGiftsPresenter	giftsPresenter;

	public WeddingGiftsPresenter	getGiftsPresenter()
	{
		return this.giftsPresenter;
	}

	protected long			weddingGiftsId;

	public long			getWeddingGiftsId()
	{
		return this.weddingGiftsId;
	}

	protected WeddingGiftsHeader	weddingGiftsHeader;

	public WeddingGiftsHeader	getWeddingGiftsHeader()
	{
		return this.weddingGiftsHeader;
	}

	protected WeddingGiftsGiftDao	weddingGiftsGiftDao;

	public WeddingGiftsGiftDao	getWeddingGiftsGiftDao()
	{
		return this.weddingGiftsGiftDao;
	}

	protected EntityHolder<WeddingGiftsGift> giftHolder;

	public EntityHolder<WeddingGiftsGift> getGiftHolder()
	{
		return this.giftHolder;
	}

	protected WeddingGiftsGift	gift;

	public WeddingGiftsGift		getGift()
	{
		return this.gift;
	}

	public static PresenterDivider			divider = new StaticPresenterDivider(new PresenterElement[]{
		PresenterElement.createClassed("",		true,	"guest",	ChildPresenter.class,			net.dryuf.core.Options.NONE),
		PresenterElement.createClassed("reserve.html",	false,	"guest",	WeddingGiftsReservePresenter.class,	net.dryuf.core.Options.NONE),
		PresenterElement.createClassed("cancel.html",	false,	"guest",	WeddingGiftsCancelPresenter.class,	net.dryuf.core.Options.NONE),
	});
}
