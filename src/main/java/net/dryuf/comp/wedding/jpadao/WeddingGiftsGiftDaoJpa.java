package net.dryuf.comp.wedding.jpadao;

import net.dryuf.comp.wedding.WeddingGiftsGift;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WeddingGiftsGiftDaoJpa extends net.dryuf.dao.DryufDaoContext<WeddingGiftsGift, net.dryuf.comp.wedding.WeddingGiftsGift.Pk> implements net.dryuf.comp.wedding.dao.WeddingGiftsGiftDao
{

	public				WeddingGiftsGiftDaoJpa()
	{
		super(WeddingGiftsGift.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<WeddingGiftsGift>	listByCompos(Long compos)
	{
		return (List<WeddingGiftsGift>)entityManager.createQuery("FROM WeddingGiftsGift WHERE pk.weddingGiftsId = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(Long compos)
	{
		return entityManager.createQuery("DELETE FROM WeddingGiftsGift obj WHERE obj.pk.weddingGiftsId = ?1").setParameter(1, compos).executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional("dryuf")
	public boolean			setReservedCode(Long weddingGiftsId, String displayName, String reservedCode)
	{
		return entityManager.createQuery("UPDATE WeddingGiftsGift SET reservedCode = ?1 WHERE pk.weddingGiftsId = ?2 AND pk.displayName = ?3 AND reservedCode IS NULL").setParameter(1, reservedCode).setParameter(2, weddingGiftsId).setParameter(3, displayName).executeUpdate() != 0;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional("dryuf")
	public boolean			revertReservedCode(Long weddingGiftsId, String displayName, String reservedCode)
	{
		return entityManager.createQuery("UPDATE WeddingGiftsGift SET reservedCode = NULL WHERE pk.weddingGiftsId = ?1 AND pk.displayName = ?2 AND reservedCode = ?3").setParameter(1, weddingGiftsId).setParameter(2, displayName).setParameter(3, reservedCode).executeUpdate() != 0;
	}

}
