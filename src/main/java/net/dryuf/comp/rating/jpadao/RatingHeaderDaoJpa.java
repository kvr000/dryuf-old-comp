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

package net.dryuf.comp.rating.jpadao;

import net.dryuf.comp.rating.RatingHeader;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RatingHeaderDaoJpa extends net.dryuf.dao.DryufDaoContext<RatingHeader, Long> implements net.dryuf.comp.rating.dao.RatingHeaderDao
{

	public				RatingHeaderDaoJpa()
	{
		super(RatingHeader.class);
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void			updateStatistics(long ratingId)
	{
		entityManager.flush();
		entityManager.createQuery("UPDATE RatingHeader rh SET counts = COALESCE((SELECT COUNT(*) FROM RatingRecord rr WHERE rr.pk.ratingId = rh.ratingId), 0), total = COALESCE((SELECT SUM(value) FROM RatingRecord rr WHERE rr.pk.ratingId = rh.ratingId), 0) WHERE rh.ratingId = :ratingId")
			.setParameter("ratingId", ratingId)
			.executeUpdate();
		entityManager.createQuery("UPDATE RatingHeader rh SET rating = COALESCE(rh.total/COALESCE(CASE WHEN rh.counts = 0 THEN 1 ELSE rh.counts END, 0), 0) WHERE rh.ratingId = :ratingId")
			.setParameter("ratingId", ratingId)
			.executeUpdate();
	}
}
