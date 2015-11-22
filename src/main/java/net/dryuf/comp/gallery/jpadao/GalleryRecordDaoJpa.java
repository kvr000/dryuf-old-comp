package net.dryuf.comp.gallery.jpadao;

import net.dryuf.comp.gallery.GalleryRecord;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GalleryRecordDaoJpa extends net.dryuf.dao.DryufDaoContext<GalleryRecord, net.dryuf.comp.gallery.GalleryRecord.Pk> implements net.dryuf.comp.gallery.dao.GalleryRecordDao
{

	public				GalleryRecordDaoJpa()
	{
		super(GalleryRecord.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<GalleryRecord>	listByCompos(net.dryuf.comp.gallery.GallerySection.Pk compos)
	{
		return (List<GalleryRecord>)entityManager.createQuery("FROM GalleryRecord WHERE pk.gallerySection = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(net.dryuf.comp.gallery.GallerySection.Pk compos)
	{
		return entityManager.createQuery("DELETE FROM GalleryRecord obj WHERE obj.pk.gallerySection = ?1").setParameter(1, compos).executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GalleryRecord		loadByDisplay(net.dryuf.comp.gallery.GallerySection.Pk gallerySectionPk, String displayName)
	{
		@SuppressWarnings("unchecked")
		List<GalleryRecord> result = entityManager.createQuery("SELECT ent FROM GalleryRecord ent WHERE ent.pk.gallerySection = ?1 AND ent.displayName = ?2").setParameter(1, gallerySectionPk).setParameter(2, displayName).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public Long			getMaxRecordCounter(net.dryuf.comp.gallery.GallerySection.Pk gallerySectionPk)
	{
		@SuppressWarnings("rawtypes")
		List result = entityManager.createQuery("SELECT MAX(gr.pk.recordCounter) FROM GalleryRecord gr WHERE gr.pk.gallerySection = ?1").setParameter(1, gallerySectionPk).getResultList();
		return result.isEmpty() ? null : (Long)result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GalleryRecord		loadSectionedPrevious(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk)
	{
		@SuppressWarnings("unchecked")
		List<GalleryRecord> result = entityManager.createQuery("SELECT r FROM GalleryRecord r WHERE r.pk.gallerySection = ?1 AND r.pk.recordCounter < ?2 ORDER BY r.pk.recordCounter DESC").setParameter(1, recordPk.getGallerySection()).setParameter(2, recordPk.getRecordCounter()).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GalleryRecord		loadSectionedNext(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk)
	{
		@SuppressWarnings("unchecked")
		List<GalleryRecord> result = entityManager.createQuery("SELECT r FROM GalleryRecord r WHERE r.pk.gallerySection = ?1 AND r.pk.recordCounter > ?2 ORDER BY r.pk.recordCounter ASC").setParameter(1, recordPk.getGallerySection()).setParameter(2, recordPk.getRecordCounter()).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GalleryRecord		loadFullPrevious(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk)
	{
		@SuppressWarnings("unchecked")
		List<GalleryRecord> result = entityManager.createQuery("SELECT r FROM GalleryRecord r WHERE (r.pk.gallerySection = ?1 AND r.pk.recordCounter < ?2) OR (r.pk.gallerySection.galleryId = ?3 AND r.pk.gallerySection.sectionCounter < ?4) ORDER BY r.pk DESC").setParameter(1, recordPk.getGallerySection()).setParameter(2, recordPk.getRecordCounter()).setParameter(3, recordPk.getGallerySection().getGalleryId()).setParameter(4, recordPk.getGallerySection().getSectionCounter()).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GalleryRecord		loadFullNext(net.dryuf.comp.gallery.GalleryRecord.Pk recordPk)
	{
		@SuppressWarnings("unchecked")
		List<GalleryRecord> result = entityManager.createQuery("SELECT r FROM GalleryRecord r WHERE (pk.gallerySection = ?1 AND pk.recordCounter > ?2) OR (pk.gallerySection.galleryId = ?3 AND pk.gallerySection.sectionCounter > ?4) ORDER BY pk.gallerySection ASC, pk.recordCounter ASC").setParameter(1, recordPk.getGallerySection()).setParameter(2, recordPk.getRecordCounter()).setParameter(3, recordPk.getGallerySection().getGalleryId()).setParameter(4, recordPk.getGallerySection().getSectionCounter()).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

}
