package net.dryuf.comp.forum.jpadao;

import net.dryuf.comp.forum.ForumRecord;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ForumRecordDaoJpa extends net.dryuf.dao.DryufDaoContext<ForumRecord, net.dryuf.comp.forum.ForumRecord.Pk> implements net.dryuf.comp.forum.dao.ForumRecordDao
{

	public				ForumRecordDaoJpa()
	{
		super(ForumRecord.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ForumRecord>	listByCompos(Long compos)
	{
		return (List<ForumRecord>)entityManager.createQuery("FROM ForumRecord WHERE pk.forumId = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(Long compos)
	{
		return entityManager.createQuery("DELETE FROM ForumRecord obj WHERE obj.pk.forumId = ?1").setParameter(1, compos).executeUpdate();
	}

}
