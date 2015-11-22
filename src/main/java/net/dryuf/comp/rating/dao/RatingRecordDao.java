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

package net.dryuf.comp.rating.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.rating.RatingRecord;
import net.dryuf.core.EntityHolder;


public interface RatingRecordDao extends net.dryuf.dao.DynamicDao<RatingRecord, net.dryuf.comp.rating.RatingRecord.Pk>
{
	public RatingRecord		loadByPk(net.dryuf.comp.rating.RatingRecord.Pk pk);
	public List<RatingRecord>	listAll();
	public void			insert(RatingRecord obj);
	public void			insertTxNew(RatingRecord obj);
	public RatingRecord		update(RatingRecord obj);
	public void			remove(RatingRecord obj);
	public boolean			removeByPk(net.dryuf.comp.rating.RatingRecord.Pk pk);
	public List<RatingRecord>	listByCompos(Long compos);
	public long			removeByCompos(Long compos);

	public net.dryuf.comp.rating.RatingRecord.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<RatingRecord> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<RatingRecord> holder);
	public RatingRecord		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<RatingRecord> retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.rating.RatingRecord.Pk pk);
	public RatingRecord		updateDynamic(EntityHolder<RatingRecord> roleObject, net.dryuf.comp.rating.RatingRecord.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.rating.RatingRecord.Pk pk);
	public long			listDynamic(List<EntityHolder<RatingRecord>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;

	public void			addRatingValue(long pollId, long userId, int value);
}
