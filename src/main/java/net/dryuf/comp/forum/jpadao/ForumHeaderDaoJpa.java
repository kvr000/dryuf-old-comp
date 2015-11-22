package net.dryuf.comp.forum.jpadao;

import net.dryuf.comp.forum.ForumHeader;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional("dryuf")
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ForumHeaderDaoJpa extends net.dryuf.dao.DryufDaoContext<ForumHeader, Long> implements net.dryuf.comp.forum.dao.ForumHeaderDao
{

	public				ForumHeaderDaoJpa()
	{
		super(ForumHeader.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Transactional("dryuf")
	public Long			getMaxCounter(Long forumId)
	{
		@SuppressWarnings("rawtypes")
		List result = entityManager.createQuery("SELECT MAX(pk.counter) FROM ForumRecord WHERE pk.forumId = ?1").setParameter(1, forumId).getResultList();
		return result.isEmpty() ? null : (Long)result.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Transactional("dryuf")
	public void			updateRecordStats(Long forumId)
	{
		entityManager.createQuery("UPDATE\tForumHeader h\nSET\n\th.lastAdded = IFNULL((SELECT MAX(created) FROM ForumRecord WHERE forumId = h.forumId), unix_timestamp()),\n\th.recordCount = (SELECT COUNT(*) FROM ForumRecord r WHERE r.pk.forumId = h.forumId)\nWHERE\n\th.forumId = ?1").setParameter(1, forumId).executeUpdate();
	}

}
