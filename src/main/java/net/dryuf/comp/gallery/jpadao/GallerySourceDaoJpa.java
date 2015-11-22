package net.dryuf.comp.gallery.jpadao;

import net.dryuf.comp.gallery.GallerySource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class GallerySourceDaoJpa extends net.dryuf.dao.DryufDaoContext<GallerySource, net.dryuf.comp.gallery.GallerySource.Pk> implements net.dryuf.comp.gallery.dao.GallerySourceDao
{

	public				GallerySourceDaoJpa()
	{
		super(GallerySource.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<GallerySource>	listByCompos(net.dryuf.comp.gallery.GalleryRecord.Pk compos)
	{
		return (List<GallerySource>)entityManager.createQuery("FROM GallerySource WHERE pk.record = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(net.dryuf.comp.gallery.GalleryRecord.Pk compos)
	{
		return entityManager.createQuery("DELETE FROM GallerySource obj WHERE obj.pk.record = ?1").setParameter(1, compos).executeUpdate();
	}

}
