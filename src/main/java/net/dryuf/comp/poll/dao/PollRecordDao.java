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

package net.dryuf.comp.poll.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.poll.PollRecord;
import net.dryuf.core.EntityHolder;


public interface PollRecordDao extends net.dryuf.dao.DynamicDao<PollRecord, net.dryuf.comp.poll.PollRecord.Pk>
{
	public PollRecord		loadByPk(net.dryuf.comp.poll.PollRecord.Pk pk);
	public List<PollRecord>		listAll();
	public void			insert(PollRecord obj);
	public void			insertTxNew(PollRecord obj);
	public PollRecord		update(PollRecord obj);
	public void			remove(PollRecord obj);
	public boolean			removeByPk(net.dryuf.comp.poll.PollRecord.Pk pk);
	public List<PollRecord>		listByCompos(Long compos);
	public long			removeByCompos(Long compos);

	public net.dryuf.comp.poll.PollRecord.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<PollRecord> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<PollRecord> holder);
	public PollRecord		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<PollRecord>	retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.poll.PollRecord.Pk pk);
	public PollRecord		updateDynamic(EntityHolder<PollRecord> roleObject, net.dryuf.comp.poll.PollRecord.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.poll.PollRecord.Pk pk);
	public long			listDynamic(List<EntityHolder<PollRecord>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;

	public void			addPollVote(long pollId, long userId, int voteOption);
}
