package net.dryuf.comp.wedding.jpadao;

import net.dryuf.comp.wedding.WeddingGiftsHeader;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class WeddingGiftsHeaderDaoJpa extends net.dryuf.dao.DryufDaoContext<WeddingGiftsHeader, Long> implements net.dryuf.comp.wedding.dao.WeddingGiftsHeaderDao
{

	public				WeddingGiftsHeaderDaoJpa()
	{
		super(WeddingGiftsHeader.class);
	}

}
