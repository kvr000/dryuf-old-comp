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

package net.dryuf.comp.poll.jpadao;

import net.dryuf.comp.poll.PollRecord;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PollRecordDaoJpa extends net.dryuf.dao.DryufDaoContext<PollRecord, net.dryuf.comp.poll.PollRecord.Pk> implements net.dryuf.comp.poll.dao.PollRecordDao
{
	public				PollRecordDaoJpa()
	{
		super(PollRecord.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<PollRecord>		listByCompos(Long compos)
	{
		return (List<PollRecord>)entityManager.createQuery("FROM PollRecord WHERE pollId = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(Long compos)
	{
		return entityManager.createQuery("DELETE FROM PollRecord obj WHERE obj.pk.pollId = ?1").setParameter(1, compos).executeUpdate();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void			addPollVote(long pollId, long userId, int voteOption)
	{
		PollRecord.Pk pk = new PollRecord.Pk(pollId, userId);
		PollRecord pollRecord = loadByPk(pk);
		if (pollRecord == null) {
			pollRecord = new PollRecord();
			pollRecord.setPk(pk);
			pollRecord.setVoteOption((long)voteOption);
			insert(pollRecord);
		}
		else {
			pollRecord.setVoteOption((long)voteOption);
		}
	}
}
