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

package net.dryuf.comp.poll.mvp;

import net.dryuf.comp.poll.PollHandler;
import net.dryuf.comp.poll.PollHeader;
import net.dryuf.comp.poll.PollOption;
import net.dryuf.core.Dryuf;
import net.dryuf.core.Options;
import net.dryuf.mvp.Presenter;
import net.dryuf.mvp.NeedLoginPresenter;


public class PollPresenter extends net.dryuf.mvp.ChildPresenter
{
	public				PollPresenter(Presenter parentPresenter, Options options, PollHandler pollHandler)
	{
		super(parentPresenter, options);
		this.urlPath = (String)options.getOptionMandatory("urlPath");
		this.pollHandler = pollHandler;
		this.cssClass = Dryuf.dashClassname(options.getOptionDefault("cssClass", Dryuf.dotClassname(PollPresenter.class)));
	}

	public boolean			processCommon()
	{
		if (this.getCallerContext().isLoggedIn())
			this.pollHandler.addPollVote((Long)this.getCallerContext().getUserId(), Integer.valueOf(this.getRequest().getParamMandatory("pollVote")));
		this.getRootPresenter().getResponse().redirect("../");
		return false;
	}

	public void			prepare()
	{
	}

	public void			render()
	{
		boolean canVote = this.pollHandler.getCallerContext().checkRole("Poll.vote");
		PollHeader detail = this.pollHandler.getPollDetail();
		this.outputFormat("<table class='net-dryuf-comp-poll-web-PollPresenter'>\n");
		this.outputFormat("<tr class='header'><th colspan='3'>%S</th></tr>\n", detail.getDescription());
		long totalVotes;
		if ((totalVotes = detail.getTotalVotes()) == 0)
			totalVotes = 1;
		for (PollOption option: this.pollHandler.getPollOptions()) {
			this.outputFormat("<tr><td class='option'>");
			if (canVote) {
				this.outputFormat("<a href=\"%S?pollVote=%S\">%S</a>", this.urlPath, String.valueOf(option.getOptionId()), option.getDescription());
			}
			else {
				this.outputFormat("%S", option.getDescription());
			}
			this.outputFormat("</td><td class='barcolumn'><div class='bar' style='width: %S%%;'></div></td><td class='percent'>%S%%</td></tr>\n", String.valueOf(Math.floor(100*option.getVoteCount()/totalVotes)), String.valueOf(Math.floor(100*option.getVoteCount()/totalVotes)));
		}
		this.outputFormat("<tr class='footer'><td colspan='2'>%W</td><td class='total'>%S</td></tr>\n", PollPresenter.class, "Total votes", String.valueOf(detail.getTotalVotes()));
		if (!canVote) {
			this.outputFormat("<tr><td colspan='3'>");
			new NeedLoginPresenter(this, net.dryuf.mvp.NoLeadChildPresenter.NOLEAD_OPTIONS.cloneAddingListed("messageClass", Dryuf.dotClassname(PollPresenter.class), "message", "For polling you need to --login--.")).render();
			this.outputFormat("</td></tr>\n");
		}
		this.outputFormat("</table>\n");
	}

	protected String		urlPath;
	protected String		cssClass;

	protected PollHandler		pollHandler;
}
