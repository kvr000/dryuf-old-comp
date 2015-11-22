package net.dryuf.comp.wedding.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.wedding.WeddingGiftsHeader;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface WeddingGiftsHeaderDao extends net.dryuf.dao.DynamicDao<WeddingGiftsHeader, Long>
{
	public WeddingGiftsHeader	refresh(WeddingGiftsHeader obj);
	public WeddingGiftsHeader	loadByPk(Long pk);
	public List<WeddingGiftsHeader>	listAll();
	public void			insert(WeddingGiftsHeader obj);
	public void			insertTxNew(WeddingGiftsHeader obj);
	public WeddingGiftsHeader	update(WeddingGiftsHeader obj);
	public void			remove(WeddingGiftsHeader obj);
	public boolean			removeByPk(Long pk);

	public Long			importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<WeddingGiftsHeader> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<WeddingGiftsHeader> holder);
	public WeddingGiftsHeader	createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<WeddingGiftsHeader> retrieveDynamic(EntityHolder<?> composition, Long pk);
	public WeddingGiftsHeader	updateDynamic(EntityHolder<WeddingGiftsHeader> roleObject, Long pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, Long pk);
	public long			listDynamic(List<EntityHolder<WeddingGiftsHeader>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

}
