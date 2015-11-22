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

import net.dryuf.comp.wedding.WeddingGiftsHeader;
import net.dryuf.comp.wedding.dao.WeddingGiftsGiftDao;
import net.dryuf.comp.wedding.form.WeddingGiftsCancelForm;
import net.dryuf.meta.ActionDef;
import net.dryuf.mvp.Presenter;


public class WeddingGiftsCancelPresenter extends net.dryuf.mvp.BeanFormPresenter<WeddingGiftsCancelForm>
{
	public				WeddingGiftsCancelPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options.cloneAddingListed("formClass", "net.dryuf.wedding.WeddingGiftsCancelForm"));

		giftPresenter = (WeddingGiftsGiftPresenter)parentPresenter;
		weddingGiftsGiftDao = giftPresenter.getWeddingGiftsGiftDao();
		weddingGiftsHeader = giftPresenter.getWeddingGiftsHeader();

		giftPresenter.setMode(WeddingGiftsPresenter.MODE_CANCEL);
	}

	@Override
	protected WeddingGiftsCancelForm createBackingObject()
	{
		return new WeddingGiftsCancelForm();
	}

	public boolean			performCancel(ActionDef action)
	{
		WeddingGiftsCancelForm cancelForm = getBackingObject();
		if (!weddingGiftsGiftDao.revertReservedCode(weddingGiftsHeader.getWeddingGiftsId(), giftPresenter.getGift().getDisplayName(), cancelForm.getReservedCode())) {
			this.addMessageLocalized(Presenter.MSG_Error, WeddingGiftsCancelPresenter.class, "Your gift reservation cancellation failed, probably wrong code specified");
			return true;
		}
		else {
			this.confirmed = true;
			giftPresenter.setMode(WeddingGiftsPresenter.MODE_CANCEL_DONE);
			this.addMessageLocalized(Presenter.MSG_Info, WeddingGiftsCancelPresenter.class, "Your gift reservation has been successfully cancelled :-(");
			return true;
		}
	}

	public void			render()
	{
		if (this.confirmed) {
			this.output(this.localize(WeddingGiftsCancelPresenter.class, "Please go back to <a href=\"..\">wedding gifts list</a> and reserve different one :-)"));
		}
		else {
			super.render();
		}
	}

	protected WeddingGiftsGiftPresenter giftPresenter;

	protected WeddingGiftsGiftDao	weddingGiftsGiftDao;

	protected WeddingGiftsHeader	weddingGiftsHeader;

	protected boolean		confirmed = false;
}
