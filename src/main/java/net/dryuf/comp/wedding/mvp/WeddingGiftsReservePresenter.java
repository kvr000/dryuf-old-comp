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

import java.util.Map;

import net.dryuf.comp.wedding.dao.WeddingGiftsGiftDao;
import net.dryuf.comp.wedding.form.WeddingGiftsReserveForm;
import net.dryuf.meta.ActionDef;
import net.dryuf.service.mail.EmailSender;
import net.dryuf.mvp.Presenter;
import net.dryuf.text.util.TextUtil;
import net.dryuf.mvp.BeanFormPresenter;


public class WeddingGiftsReservePresenter extends BeanFormPresenter<WeddingGiftsReserveForm>
{
	public				WeddingGiftsReservePresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options.cloneAddingListed("formClass", "net.dryuf.wedding.WeddingGiftsReserveForm"));

		giftPresenter = (WeddingGiftsGiftPresenter)this.parentPresenter;
		weddingGiftsGiftDao = giftPresenter.getWeddingGiftsGiftDao();
		giftPresenter.setMode(WeddingGiftsPresenter.MODE_RESERVE);
	}

	@Override
	protected WeddingGiftsReserveForm createBackingObject()
	{
		return new WeddingGiftsReserveForm();
	}

	public boolean			retrieve(Map<String, String> errors, ActionDef action)
	{
		WeddingGiftsReserveForm reserveForm = getBackingObject();
		if (!super.retrieve(errors, action))
			return false;
		if (!reserveForm.getEmail().equals(reserveForm.getEmailConfirm()))
			errors.put("emailConfirm", this.localize(WeddingGiftsReservePresenter.class, "E-mail doesn't match"));
		return errors.isEmpty();
	}

	public boolean			performReserve(ActionDef action)
	{
		WeddingGiftsReserveForm reserveForm = getBackingObject();
		String reservedCode = TextUtil.generateCode(8);

		if (!weddingGiftsGiftDao.setReservedCode(giftPresenter.getWeddingGiftsHeader().getWeddingGiftsId(), giftPresenter.getGift().getDisplayName(), reservedCode)) {
			this.addMessageLocalized(Presenter.MSG_Error, WeddingGiftsReservePresenter.class, "Your has gift reservation failed, probably someone reserved in the meantime");
			return true;
		}
		else {
			EmailSender emailSender = getCallerContext().getBeanTyped("emailSender", EmailSender.class);
			emailSender.mailUtf8(
				reserveForm.getEmail(),
				this.localize(WeddingGiftsReservePresenter.class, "Wedding Gift Reservation"),
				this.localize(WeddingGiftsReservePresenter.class, "Your Gift has been successfully reserved.")+"\n"+
				this.localizeArgs(WeddingGiftsReservePresenter.class, "Your reservation code is {0}.", new Object[]{ reservedCode })+
				"\n\n"+
				giftPresenter.getGift().getName()+":\n"+
				giftPresenter.getGift().getDescription(),
				giftPresenter.getWeddingGiftsHeader().getContactEmail()
			);
			this.confirmed = true;
			giftPresenter.setMode(WeddingGiftsPresenter.MODE_RESERVE_DONE);
			this.addMessageLocalized(Presenter.MSG_Info, WeddingGiftsReservePresenter.class, "Your gift has been successfully reserved, we're looking forward to it :-)");
			this.addMessage(Presenter.MSG_Info, this.getUiContext().localizeArgs(WeddingGiftsReservePresenter.class, "Your reservation code is {0}", new Object[]{ reservedCode }));
			return true;
		}
	}

	public void			render()
	{
		if (this.confirmed) {
			this.output(this.localize(WeddingGiftsReservePresenter.class, "Please go back to <a href=\"..\">wedding gifts list</a> and reserve one more :-)"));
		}
		else {
			super.render();
			this.outputFormat("%W", WeddingGiftsReservePresenter.class, "We use your E-mail address only for sending the reservation code, we like surprise :-)");
		}
	}

	protected WeddingGiftsGiftPresenter giftPresenter;

	protected WeddingGiftsGiftDao	weddingGiftsGiftDao;

	protected boolean		confirmed = false;
}
