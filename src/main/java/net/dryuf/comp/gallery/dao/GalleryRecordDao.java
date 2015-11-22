package net.dryuf.comp.gallery.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.gallery.GalleryRecord;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface GalleryRecordDao extends net.dryuf.dao.DynamicDao<GalleryRecord, net.dryuf.comp.gallery.GalleryRecord.Pk>
{
	public GalleryRecord		refresh(GalleryRecord obj);
	public GalleryRecord		loadByPk(net.dryuf.comp.gallery.GalleryRecord.Pk pk);
	public List<GalleryRecord>	listAll();
	public void			insert(GalleryRecord obj);
	public void			insertTxNew(GalleryRecord obj);
	public GalleryRecord		update(GalleryRecord obj);
	public void			remove(GalleryRecord obj);
	public boolean			removeByPk(net.dryuf.comp.gallery.GalleryRecord.Pk pk);
	public List<GalleryRecord>	listByCompos(net.dryuf.comp.gallery.GallerySection.Pk compos);
	public long			removeByCompos(net.dryuf.comp.gallery.GallerySection.Pk compos);

	public net.dryuf.comp.gallery.GalleryRecord.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<GalleryRecord> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<GalleryRecord> holder);
	public GalleryRecord		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<GalleryRecord> retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.gallery.GalleryRecord.Pk pk);
	public GalleryRecord		updateDynamic(EntityHolder<GalleryRecord> roleObject, net.dryuf.comp.gallery.GalleryRecord.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.gallery.GalleryRecord.Pk pk);
	public long			listDynamic(List<EntityHolder<GalleryRecord>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

	public GalleryRecord		loadByDisplay(net.dryuf.comp.gallery.GallerySection.Pk gallerySectionPk, String displayName);

	public Long			getMaxRecordCounter(net.dryuf.comp.gallery.GallerySection.Pk gallerySectionPk);

	public GalleryRecord		loadSectionedPrevious(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk);

	public GalleryRecord		loadSectionedNext(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk);

	public GalleryRecord		loadFullPrevious(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk);

	public GalleryRecord		loadFullNext(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk);

}
