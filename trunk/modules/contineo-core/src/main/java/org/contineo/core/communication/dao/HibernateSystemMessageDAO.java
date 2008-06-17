package org.contineo.core.communication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.communication.SystemMessage;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>EMailAccount</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateSystemMessageDAO.java,v 1.2 2006/08/29 16:33:46 marco Exp $
 * @since 3.0
 */
public class HibernateSystemMessageDAO extends HibernateDaoSupport implements SystemMessageDAO {

	protected static Log log = LogFactory.getLog(HibernateSystemMessageDAO.class);

	private HibernateSystemMessageDAO() {
	}

	/**
	 * @see org.contineo.core.communication.dao.SystemMessageDAO#delete(int)
	 */
	public boolean delete(int messageid) {
		boolean result = true;

		try {
			SystemMessage message = (SystemMessage) getHibernateTemplate().get(SystemMessage.class,
					new Integer(messageid));
			if (message != null)
				getHibernateTemplate().delete(message);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see org.contineo.core.communication.dao.SystemMessageDAO#findByPrimaryKey(int)
	 */
	public SystemMessage findByPrimaryKey(int messageid) {
		SystemMessage sysmess = null;

		try {
			sysmess = (SystemMessage) getHibernateTemplate().get(SystemMessage.class, new Integer(messageid));
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return sysmess;
	}

	/**
	 * @see org.contineo.core.communication.dao.SystemMessageDAO#findByRecipient(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMessage> findByRecipient(String recipient) {
		List<SystemMessage> coll = new ArrayList<SystemMessage>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(SystemMessage.class);
			dt.add(Property.forName("recipient").eq(recipient));
			dt.addOrder(Order.desc("sentDate"));

			coll = (List<SystemMessage>) getHibernateTemplate().findByCriteria(dt);
			coll = collectGarbage(coll, false);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see org.contineo.core.communication.dao.SystemMessageDAO#getCount(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getCount(String recipient) {
		int count = 0;

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(SystemMessage.class);
			dt.add(Property.forName("recipient").eq(recipient));
			dt.add(Property.forName("read").eq(new Integer(0)));

			Collection<SystemMessage> coll = (Collection<SystemMessage>) getHibernateTemplate().findByCriteria(dt);
			count = coll.size();
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return count;
	}

	/**
	 * @see org.contineo.core.communication.dao.SystemMessageDAO#store(org.contineo.core.communication.SystemMessage)
	 */
	public boolean store(SystemMessage sysmess) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(sysmess);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	public void deleteExpiredMessages(String recipient) {
		collectGarbage(findByRecipient(recipient), true);
	}

	/**
	 * Cleans from the passed collection all expired messages
	 * 
	 * @param coll The input messages
	 * @param removeExpired True if expired messages must be deleted
	 * @return The cleaned messages collection
	 */
	protected List<SystemMessage> collectGarbage(Collection<SystemMessage> coll, boolean removeExpired) {
		List<SystemMessage> out = new ArrayList<SystemMessage>();
		try {
			Iterator iter = coll.iterator();
			Date date = new Date();
			long time = date.getTime();

			while (iter.hasNext()) {
				SystemMessage sm = (SystemMessage) iter.next();
				long sentdate = new Date().getTime();
				long timespan = sm.getDateScope();
				timespan = timespan * 86400000;
				sentdate += timespan;

				if (time >= sentdate) {
					if (removeExpired)
						delete(sm.getMessageId());
				} else {
					out.add(sm);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return out;
	}
}
