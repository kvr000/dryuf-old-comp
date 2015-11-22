package net.dryuf.comp.poll.jpadao;

import net.dryuf.comp.poll.PollOption;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PollOptionDaoJpa extends net.dryuf.dao.DryufDaoContext<PollOption, net.dryuf.comp.poll.PollOption.Pk> implements net.dryuf.comp.poll.dao.PollOptionDao
{

	public				PollOptionDaoJpa()
	{
		super(PollOption.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<PollOption>		listByCompos(Long compos)
	{
		return (List<PollOption>)entityManager.createQuery("FROM PollOption WHERE pk.pollId = ?1 ORDER BY pk").setParameter(1, compos).getResultList();
	}

	@Override
	@Transactional("dryuf")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public long			removeByCompos(Long compos)
	{
		return entityManager.createQuery("DELETE FROM PollOption obj WHERE obj.pk.pollId = ?1").setParameter(1, compos).executeUpdate();
	}

}
