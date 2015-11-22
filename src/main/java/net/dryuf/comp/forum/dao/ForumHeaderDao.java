package net.dryuf.comp.forum.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.forum.ForumHeader;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface ForumHeaderDao extends net.dryuf.dao.DynamicDao<ForumHeader, Long>
{
	public ForumHeader		refresh(ForumHeader obj);
	public ForumHeader		loadByPk(Long pk);
	public List<ForumHeader>	listAll();
	public void			insert(ForumHeader obj);
	public void			insertTxNew(ForumHeader obj);
	public ForumHeader		update(ForumHeader obj);
	public void			remove(ForumHeader obj);
	public boolean			removeByPk(Long pk);

	public Long			importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<ForumHeader> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<ForumHeader> holder);
	public ForumHeader		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<ForumHeader> retrieveDynamic(EntityHolder<?> composition, Long pk);
	public ForumHeader		updateDynamic(EntityHolder<ForumHeader> roleObject, Long pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, Long pk);
	public long			listDynamic(List<EntityHolder<ForumHeader>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

	public Long			getMaxCounter(Long forumId);

	public void			updateRecordStats(Long forumId);

}
