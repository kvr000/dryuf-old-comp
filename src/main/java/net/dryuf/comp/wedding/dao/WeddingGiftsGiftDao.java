package net.dryuf.comp.wedding.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.wedding.WeddingGiftsGift;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface WeddingGiftsGiftDao extends net.dryuf.dao.DynamicDao<WeddingGiftsGift, net.dryuf.comp.wedding.WeddingGiftsGift.Pk>
{
	public WeddingGiftsGift		refresh(WeddingGiftsGift obj);
	public WeddingGiftsGift		loadByPk(net.dryuf.comp.wedding.WeddingGiftsGift.Pk pk);
	public List<WeddingGiftsGift>	listAll();
	public void			insert(WeddingGiftsGift obj);
	public void			insertTxNew(WeddingGiftsGift obj);
	public WeddingGiftsGift		update(WeddingGiftsGift obj);
	public void			remove(WeddingGiftsGift obj);
	public boolean			removeByPk(net.dryuf.comp.wedding.WeddingGiftsGift.Pk pk);
	public List<WeddingGiftsGift>	listByCompos(Long compos);
	public long			removeByCompos(Long compos);

	public net.dryuf.comp.wedding.WeddingGiftsGift.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<WeddingGiftsGift> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<WeddingGiftsGift> holder);
	public WeddingGiftsGift		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<WeddingGiftsGift> retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.wedding.WeddingGiftsGift.Pk pk);
	public WeddingGiftsGift		updateDynamic(EntityHolder<WeddingGiftsGift> roleObject, net.dryuf.comp.wedding.WeddingGiftsGift.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.wedding.WeddingGiftsGift.Pk pk);
	public long			listDynamic(List<EntityHolder<WeddingGiftsGift>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

	public boolean			setReservedCode(Long weddingGiftsId, String displayName, String reservedCode);

	public boolean			revertReservedCode(Long weddingGiftsId, String displayName, String reservedCode);

}
