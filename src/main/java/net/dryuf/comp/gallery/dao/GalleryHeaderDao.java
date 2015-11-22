package net.dryuf.comp.gallery.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.gallery.GalleryHeader;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface GalleryHeaderDao extends net.dryuf.dao.DynamicDao<GalleryHeader, Long>
{
	public GalleryHeader		refresh(GalleryHeader obj);
	public GalleryHeader		loadByPk(Long pk);
	public List<GalleryHeader>	listAll();
	public void			insert(GalleryHeader obj);
	public void			insertTxNew(GalleryHeader obj);
	public GalleryHeader		update(GalleryHeader obj);
	public void			remove(GalleryHeader obj);
	public boolean			removeByPk(Long pk);

	public Long			importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<GalleryHeader> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<GalleryHeader> holder);
	public GalleryHeader		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<GalleryHeader> retrieveDynamic(EntityHolder<?> composition, Long pk);
	public GalleryHeader		updateDynamic(EntityHolder<GalleryHeader> roleObject, Long pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, Long pk);
	public long			listDynamic(List<EntityHolder<GalleryHeader>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

	public GalleryHeader		loadByRef(String refBase, String refKey);

	public void			updateHeaderStats(Long galleryId);

}
