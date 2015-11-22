package net.dryuf.comp.gallery.jpadao;

import net.dryuf.comp.gallery.GalleryHeader;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GalleryHeaderDaoJpa extends net.dryuf.dao.DryufDaoContext<GalleryHeader, Long> implements net.dryuf.comp.gallery.dao.GalleryHeaderDao
{

	public				GalleryHeaderDaoJpa()
	{
		super(GalleryHeader.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public GalleryHeader		loadByRef(String refBase, String refKey)
	{
		@SuppressWarnings("unchecked")
		List<GalleryHeader> result = entityManager.createQuery("SELECT ent FROM GalleryHeader ent WHERE ent.refBase = ?1 AND ent.refKey = ?2").setParameter(1, refBase).setParameter(2, refKey).getResultList();
		if (result.isEmpty())
			return null;
		return result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional("dryuf")
	public void			updateHeaderStats(Long galleryId)
	{
		entityManager.createQuery("UPDATE GalleryHeader h SET h.lastAdded = IFNULL((SELECT MAX(r.created) FROM GalleryRecord r WHERE r.pk.gallerySection.galleryId = h.galleryId), unix_timestamp()*1000), recordCount = (SELECT COUNT(*) FROM GalleryRecord r WHERE r.pk.gallerySection.galleryId = h.galleryId) WHERE h.galleryId = ?1").setParameter(1, galleryId).executeUpdate();
	}

}
