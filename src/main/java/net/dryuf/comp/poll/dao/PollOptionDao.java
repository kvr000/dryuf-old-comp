package net.dryuf.comp.poll.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.poll.PollOption;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface PollOptionDao extends net.dryuf.dao.DynamicDao<PollOption, net.dryuf.comp.poll.PollOption.Pk>
{
	public PollOption		refresh(PollOption obj);
	public PollOption		loadByPk(net.dryuf.comp.poll.PollOption.Pk pk);
	public List<PollOption>		listAll();
	public void			insert(PollOption obj);
	public void			insertTxNew(PollOption obj);
	public PollOption		update(PollOption obj);
	public void			remove(PollOption obj);
	public boolean			removeByPk(net.dryuf.comp.poll.PollOption.Pk pk);
	public List<PollOption>		listByCompos(Long compos);
	public long			removeByCompos(Long compos);

	public net.dryuf.comp.poll.PollOption.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<PollOption> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<PollOption> holder);
	public PollOption		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<PollOption>	retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.poll.PollOption.Pk pk);
	public PollOption		updateDynamic(EntityHolder<PollOption> roleObject, net.dryuf.comp.poll.PollOption.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.poll.PollOption.Pk pk);
	public long			listDynamic(List<EntityHolder<PollOption>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

}
