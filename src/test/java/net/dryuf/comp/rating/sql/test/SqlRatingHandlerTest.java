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

package net.dryuf.comp.rating.sql.test;

import javax.validation.constraints.NotNull;

import net.dryuf.core.Dryuf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.dryuf.comp.rating.RatingHandler;
import net.dryuf.comp.rating.sql.SqlRatingBo;
import net.dryuf.core.AppContainer;
import net.dryuf.core.AppContainerAware;
import net.dryuf.core.CallerContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testContext.xml")
public class SqlRatingHandlerTest extends net.dryuf.tenv.AppTenvObject implements AppContainerAware
{
	@Override
	public void			afterAppContainer(@NotNull AppContainer appContainer)
	{
		super.afterAppContainer(appContainer);
		sqlRatingBo = appContainer.postProcessBean(new SqlRatingBo(), "sqlRatingBo", null);
	}

	public RatingHandler		initRating(CallerContext callerContext, String methodName)
	{
		RatingHandler ratingHandler = sqlRatingBo.openCreateRatingRef(callerContext, Dryuf.dotClassname(SqlRatingHandlerTest.class), methodName, 5);
		ratingHandler.cleanRating();
		return ratingHandler;
	}

	@Test
	public void			testHandler()
	{
		RatingHandler ratingHandler = initRating(this.createCallerContext(), "testHandler");
		ratingHandler.refresh();
		Assert.assertEquals(0, (long)ratingHandler.getRatingDetail().getRating());
		ratingHandler.addRating(1, 1);
		ratingHandler.refresh();
		Assert.assertEquals(1, (long)ratingHandler.getRatingDetail().getRating());
		ratingHandler.addRating(2, 5);
		ratingHandler.refresh();
		Assert.assertEquals(3, (long)ratingHandler.getRatingDetail().getRating());
		ratingHandler.addRating(1, 3);
		ratingHandler.refresh();
		Assert.assertEquals(4, (long)ratingHandler.getRatingDetail().getRating());
	}

	SqlRatingBo			sqlRatingBo;
}


