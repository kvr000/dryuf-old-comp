package net.dryuf.comp.gallery.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.gallery.GallerySource;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface GallerySourceDao extends net.dryuf.dao.DynamicDao<GallerySource, net.dryuf.comp.gallery.GallerySource.Pk>
{
	public GallerySource		refresh(GallerySource obj);
	public GallerySource		loadByPk(net.dryuf.comp.gallery.GallerySource.Pk pk);
	public List<GallerySource>	listAll();
	public void			insert(GallerySource obj);
	public void			insertTxNew(GallerySource obj);
	public GallerySource		update(GallerySource obj);
	public void			remove(GallerySource obj);
	public boolean			removeByPk(net.dryuf.comp.gallery.GallerySource.Pk pk);
	public List<GallerySource>	listByCompos(net.dryuf.comp.gallery.GalleryRecord.Pk compos);
	public long			removeByCompos(net.dryuf.comp.gallery.GalleryRecord.Pk compos);

	public net.dryuf.comp.gallery.GallerySource.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<GallerySource> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<GallerySource> holder);
	public GallerySource		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<GallerySource> retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.gallery.GallerySource.Pk pk);
	public GallerySource		updateDynamic(EntityHolder<GallerySource> roleObject, net.dryuf.comp.gallery.GallerySource.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.gallery.GallerySource.Pk pk);
	public long			listDynamic(List<EntityHolder<GallerySource>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

}
