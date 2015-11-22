package net.dryuf.comp.gallery.dao;

import java.util.Map;
import java.util.List;
import net.dryuf.comp.gallery.GallerySection;
import net.dryuf.core.EntityHolder;
import net.dryuf.core.CallerContext;
import net.dryuf.transaction.TransactionHandler;


public interface GallerySectionDao extends net.dryuf.dao.DynamicDao<GallerySection, net.dryuf.comp.gallery.GallerySection.Pk>
{
	public GallerySection		refresh(GallerySection obj);
	public GallerySection		loadByPk(net.dryuf.comp.gallery.GallerySection.Pk pk);
	public List<GallerySection>	listAll();
	public void			insert(GallerySection obj);
	public void			insertTxNew(GallerySection obj);
	public GallerySection		update(GallerySection obj);
	public void			remove(GallerySection obj);
	public boolean			removeByPk(net.dryuf.comp.gallery.GallerySection.Pk pk);
	public List<GallerySection>	listByCompos(Long compos);
	public long			removeByCompos(Long compos);

	public net.dryuf.comp.gallery.GallerySection.Pk importDynamicKey(Map<String, Object> data);
	public Map<String, Object>	exportDynamicData(EntityHolder<GallerySection> holder);
	public Map<String, Object>	exportEntityData(EntityHolder<GallerySection> holder);
	public GallerySection		createDynamic(EntityHolder<?> composition, Map<String, Object> data);
	public EntityHolder<GallerySection> retrieveDynamic(EntityHolder<?> composition, net.dryuf.comp.gallery.GallerySection.Pk pk);
	public GallerySection		updateDynamic(EntityHolder<GallerySection> roleObject, net.dryuf.comp.gallery.GallerySection.Pk pk, Map<String, Object> updates);
	public boolean			deleteDynamic(EntityHolder<?> composition, net.dryuf.comp.gallery.GallerySection.Pk pk);
	public long			listDynamic(List<EntityHolder<GallerySection>> results, EntityHolder<?> composition, Map<String, Object> filter, List<String> sorts, Long start, Long limit);
	public TransactionHandler	keepContextTransaction(CallerContext callerContext);
	public <R> R			runTransactioned(java.util.concurrent.Callable<R> code) throws Exception;
	public <R> R			runTransactionedNew(java.util.concurrent.Callable<R> code) throws Exception;

	public GallerySection		loadByDisplay(Long galleryId, String displayName);

	public void			updateSectionStats(net.dryuf.comp.gallery.GallerySection.Pk gallerySectionPk);

	public Long			getMaxSectionCounter(Long galleryId);

}
