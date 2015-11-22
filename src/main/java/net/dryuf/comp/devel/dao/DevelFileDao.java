package net.dryuf.comp.devel.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.devel.DevelFile;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface DevelFileDao extends net.dryuf.dao.DynamicDao<DevelFile, Long>
{
	public DevelFile		refresh(DevelFile obj);
	public DevelFile		loadByPk(Long pk);
	public List<DevelFile>		listAll();
	public void			insert(DevelFile obj);
	public void			insertTxNew(DevelFile obj);
	public DevelFile		update(DevelFile obj);
	public void			remove(DevelFile obj);
	public boolean			removeByPk(Long pk);

	public Long			importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<DevelFile> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<DevelFile> holder);
	public DevelFile		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<DevelFile>	retrieveDynamic(EntityHolder<?> composition, Long pk);
	public DevelFile		updateDynamic(EntityHolder<DevelFile> roleObject, Long pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, Long pk);
	public long			listDynamic(List<EntityHolder<DevelFile>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

}
