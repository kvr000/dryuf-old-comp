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

import net.dryuf.comp.wedding.form.WeddingGiftsProposeForm;
import net.dryuf.meta.ActionDef;
import net.dryuf.service.mail.EmailSender;
import net.dryuf.mvp.Presenter;
import net.dryuf.mvp.BeanFormPresenter;


public class WeddingGiftsProposePresenter extends BeanFormPresenter<WeddingGiftsProposeForm>
{
	public				WeddingGiftsProposePresenter(net.dryuf.mvp.Presenter parentPresenter, net.dryuf.core.Options options)
	{
		super(parentPresenter, options.cloneAddingListed("formClass", "net.dryuf.wedding.WeddingGiftsProposeForm"));

		giftsPresenter = (WeddingGiftsPresenter)parentPresenter;
		giftsPresenter.setMode(WeddingGiftsPresenter.MODE_PROPOSE);
	}

	@Override
	protected WeddingGiftsProposeForm createBackingObject()
	{
		return new WeddingGiftsProposeForm();
	}

	public boolean			performPropose(ActionDef action)
	{
		WeddingGiftsProposeForm proposeForm = getBackingObject();
		EmailSender emailSender = getCallerContext().getBeanTyped("emailSender", EmailSender.class);
		emailSender.mailUtf8(
			giftsPresenter.getWeddingGiftsHeader().getProposalEmail(),
			this.localize(WeddingGiftsProposePresenter.class, "Wedding Gift Proposal"),
			proposeForm.getYourEmail()+" "+
			this.localize(WeddingGiftsProposePresenter.class, "gives a proposal for wedding gift:")+"\n\n"+
			proposeForm.getDescription(),
			proposeForm.getYourEmail()
		);
		this.confirmed = true;
		((WeddingGiftsPresenter) this.parentPresenter).setMode(WeddingGiftsPresenter.MODE_PROPOSE_DONE);
		this.addMessageLocalized(Presenter.MSG_Info, WeddingGiftsProposePresenter.class, "Your proposal has been sent to our coordinator. Thank you!");
		return true;
	}

	public void			render()
	{
		if (this.confirmed) {
			this.output(this.localize(WeddingGiftsProposePresenter.class, "Please go back to <a href=\".\">wedding gifts list</a>."));
		}
		else {
			super.render();
			this.outputFormat("%W", WeddingGiftsProposePresenter.class, "Your proposal will be sent to our coordinator and you will receive response to your e-mail.");
		}
	}

	protected WeddingGiftsPresenter	giftsPresenter;

	protected boolean		confirmed = false;
}
