package net.dryuf.comp.forum.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.forum.ForumRecord;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface ForumRecordDao extends net.dryuf.dao.DynamicDao<ForumRecord, net.dryuf.comp.forum.ForumRecord.Pk>
{
	public ForumRecord		refresh(ForumRecord obj);
	public ForumRecord		loadByPk(net.dryuf.comp.forum.ForumRecord.Pk pk);
	public List<ForumRecord>	listAll();
	public void			insert(ForumRecord obj);
	public void			insertTxNew(ForumRecord obj);
	public ForumRecord		update(ForumRecord obj);
	public void			remove(ForumRecord obj);
	public boolean			removeByPk(net.dryuf.comp.forum.ForumRecord.Pk pk);
	public List<ForumRecord>	listByCompos(Long compos);
	public long			removeByCompos(Long compos);

	public net.dryuf.comp.forum.ForumRecord.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<ForumRecord> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<ForumRecord> holder);
	public ForumRecord		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<ForumRecord> retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.forum.ForumRecord.Pk pk);
	public ForumRecord		updateDynamic(EntityHolder<ForumRecord> roleObject, net.dryuf.comp.forum.ForumRecord.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.forum.ForumRecord.Pk pk);
	public long			listDynamic(List<EntityHolder<ForumRecord>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

}
