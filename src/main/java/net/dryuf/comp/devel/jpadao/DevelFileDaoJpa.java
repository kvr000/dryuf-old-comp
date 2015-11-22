package net.dryuf.comp.devel.jpadao;

import net.dryuf.comp.devel.DevelFile;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DevelFileDaoJpa extends net.dryuf.dao.DryufDaoContext<DevelFile, Long> implements net.dryuf.comp.devel.dao.DevelFileDao
{

	public				DevelFileDaoJpa()
	{
		super(DevelFile.class);
	}

}
