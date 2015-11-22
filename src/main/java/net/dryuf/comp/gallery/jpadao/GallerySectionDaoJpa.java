package net.dryuf.comp.gallery.jpadao;

import net.dryuf.comp.gallery.GallerySection;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GallerySectionDaoJpa extends net.dryuf.dao.DryufDaoContext<GallerySection, net.dryuf.comp.gallery.GallerySection.Pk> implements net.dryuf.comp.gallery.dao.GallerySectionDao
{

	public				GallerySectionDaoJpa()
	{
		super(GallerySection.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<GallerySection>	listByCompos(Long compos)
	{
		return (List<GallerySection>)entityManager.createQuery("FROM GallerySection WHERE pk.galleryId = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(Long compos)
	{
		return entityManager.createQuery("DELETE FROM GallerySection obj WHERE obj.pk.galleryId = ?1").setParameter(1, compos).executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GallerySection		loadByDisplay(Long galleryId, String displayName)
	{
		@SuppressWarnings("unchecked")
		List<GallerySection> result = entityManager.createQuery("SELECT ent FROM GallerySection ent WHERE ent.pk.galleryId = ?1 AND ent.displayName = ?2").setParameter(1, galleryId).setParameter(2, displayName).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional("dryuf")
	public void			updateSectionStats(net.dryuf.comp.gallery.GallerySection.Pk gallerySectionPk)
	{
		entityManager.createQuery("UPDATE GallerySection s SET s.lastAdded = IFNULL((SELECT MAX(r.created) FROM GalleryRecord r WHERE r.pk.gallerySection = s.pk), unix_timestamp()*1000), recordCount = (SELECT COUNT(*) FROM GalleryRecord r WHERE r.pk.gallerySection = s.pk) WHERE s.pk = ?1").setParameter(1, gallerySectionPk).executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public Long			getMaxSectionCounter(Long galleryId)
	{
		@SuppressWarnings("rawtypes")
		List result = entityManager.createQuery("SELECT MAX(gs.pk.sectionCounter) FROM GallerySection gs WHERE gs.pk.galleryId = ?1").setParameter(1, galleryId).getResultList();
		return result.isEmpty() ? null : (Long)result.get(0);
	}

}
