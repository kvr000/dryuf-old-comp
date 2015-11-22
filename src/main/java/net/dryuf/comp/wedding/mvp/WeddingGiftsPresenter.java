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

import net.dryuf.comp.wedding.WeddingGiftsHeader;
import net.dryuf.comp.wedding.dao.WeddingGiftsGiftDao;
import net.dryuf.comp.wedding.dao.WeddingGiftsHeaderDao;
import net.dryuf.core.Dryuf;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.Options;
import net.dryuf.mvp.Presenter;
import net.dryuf.mvp.PresenterDivider;
import net.dryuf.mvp.PresenterElement;
import net.dryuf.mvp.StaticPresenterDivider;
import net.dryuf.util.MapUtil;


public class WeddingGiftsPresenter extends net.dryuf.mvp.ChildPresenter
{
	public static final int		MODE_LIST		= 0;
	public static final int		MODE_INFO		= 1;
	public static final int		MODE_RESERVE		= 2;
	public static final int		MODE_RESERVE_DONE	= 3;
	public static final int		MODE_CANCEL		= 4;
	public static final int		MODE_CANCEL_DONE	= 5;
	public static final int		MODE_PROPOSE		= 6;
	public static final int		MODE_PROPOSE_DONE	= 7;
	public static final int		MODE_COORDINATE		= 8;
	public static final int		MODE_COORDINATE_DONE	= 9;

	public				WeddingGiftsPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options);

		this.weddingGiftsReference = (String) options.getOptionMandatory("refKey");
		this.weddingGiftsHeaderDao = getCallerContext().getBeanTyped("weddingGiftsHeaderDao", WeddingGiftsHeaderDao.class);
		this.weddingGiftsGiftDao = getCallerContext().getBeanTyped("weddingGiftsGiftDao", WeddingGiftsGiftDao.class);

		LinkedList<EntityHolder<WeddingGiftsHeader>> headers = new LinkedList<EntityHolder<WeddingGiftsHeader>>();
		this.weddingGiftsHeaderDao.listDynamic(headers, EntityHolder.createRoleOnly(getCallerContext()), MapUtil.createHashMap("refBase", (Object)Dryuf.dotClassname(WeddingGiftsHeader.class), "refKey", (Object)weddingGiftsReference), null, null, null);
		if (headers.size() != 1)
			throw new RuntimeException("No WeddingGiftsHeader found for reference "+Dryuf.dotClassname(WeddingGiftsHeader.class)+" "+this.weddingGiftsReference);
		this.weddingGiftsHeaderHolder = headers.get(0);
		this.weddingGiftsHeader = weddingGiftsHeaderHolder.getEntity();
		this.weddingGiftsId = this.weddingGiftsHeader.getWeddingGiftsId();
	}

	public int			getMode()
	{
		return this.mode;
	}

	public void			setMode(int mode)
	{
		this.mode = mode;
	}

	public boolean			process()
	{
		Presenter subPresenter;
		if ((subPresenter = staticDivider.tryPage(this)) == null) {
			if (this.getRootPresenter().needPathSlash(true) == null)
				return false;
			subPresenter = new WeddingGiftsGiftPresenter(this, Options.NONE);
		}
		return subPresenter.process();
	}

	protected WeddingGiftsHeaderDao	weddingGiftsHeaderDao;

	public WeddingGiftsHeaderDao	getWeddingGiftsHeaderDao()
	{
		return this.weddingGiftsHeaderDao;
	}

	protected WeddingGiftsGiftDao	weddingGiftsGiftDao;

	public WeddingGiftsGiftDao	getWeddingGiftsGiftDao()
	{
		return this.weddingGiftsGiftDao;
	}

	protected String		weddingGiftsReference;

	protected EntityHolder<WeddingGiftsHeader> weddingGiftsHeaderHolder;

	public EntityHolder<WeddingGiftsHeader> getWeddingGiftsHeaderHolder()
	{
		return this.weddingGiftsHeaderHolder;
	}

	protected WeddingGiftsHeader	weddingGiftsHeader;

	public WeddingGiftsHeader	getWeddingGiftsHeader()
	{
		return this.weddingGiftsHeader;
	}

	protected long			weddingGiftsId;

	public long			getWeddingGiftsId()
	{
		return this.weddingGiftsId;
	}

	protected int			mode;

	public static PresenterDivider	staticDivider = new StaticPresenterDivider(new PresenterElement[]{
		PresenterElement.createClassed("",			true,	"guest",	WeddingGiftsListPresenter.class,		Options.NONE),
		PresenterElement.createClassed("propose.html",		false,	"guest",	WeddingGiftsProposePresenter.class,		Options.NONE),
		PresenterElement.createClassed("coordinator.html",	false,	"guest",	WeddingGiftsCoordinatorPresenter.class,		Options.NONE),
	});
}
