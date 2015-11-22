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

import net.dryuf.comp.wedding.form.WeddingGiftsCoordinatorForm;
import net.dryuf.meta.ActionDef;
import net.dryuf.service.mail.EmailSender;
import net.dryuf.mvp.Presenter;


public class WeddingGiftsCoordinatorPresenter extends net.dryuf.mvp.BeanFormPresenter<WeddingGiftsCoordinatorForm>
{
	public				WeddingGiftsCoordinatorPresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options);

		giftPresenter = (WeddingGiftsGiftPresenter) parentPresenter;
		giftPresenter.setMode(WeddingGiftsPresenter.MODE_COORDINATE);
	}

	@Override
	protected WeddingGiftsCoordinatorForm createBackingObject()
	{
		return new WeddingGiftsCoordinatorForm();
	}

	public boolean			performSubmit(ActionDef action)
	{
		WeddingGiftsCoordinatorForm coordinatorForm = getBackingObject();
		EmailSender emailSender = getCallerContext().getBeanTyped("emailSender", EmailSender.class);
		emailSender.mailUtf8(
			giftPresenter.weddingGiftsHeader.getCoordinatorEmail(),
			this.localize(WeddingGiftsCoordinatorPresenter.class, "Wedding Gift Question"),
			coordinatorForm.getYourEmail()+" "+
			this.localize(WeddingGiftsCoordinatorPresenter.class, "has a question regarding wedding gift:")+"\n\n"+
			coordinatorForm.getDescription(),
			coordinatorForm.getYourEmail()
		);
		this.confirmed = true;
		giftPresenter.setMode(WeddingGiftsPresenter.MODE_COORDINATE_DONE);
		this.addMessageLocalized(Presenter.MSG_Info, WeddingGiftsCoordinatorPresenter.class, "Your question has been sent to our coordinator. Thank you!");
		return true;
	}

	public void			render()
	{
		if (this.confirmed) {
			this.output(this.localize(WeddingGiftsCoordinatorPresenter.class, "Please go back to <a href=\".\">wedding gifts list</a>."));
		}
		else {
			super.render();
			this.outputFormat("%W", WeddingGiftsCoordinatorPresenter.class, "Your question will be sent to our coordinator and you will receive response to your e-mail.");
		}
	}

	protected WeddingGiftsGiftPresenter giftPresenter;

	protected boolean		confirmed = false;
}
