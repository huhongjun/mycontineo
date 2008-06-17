package org.contineo.core.document.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.document.DownloadTicket;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>DownloadTicketDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateDownloadTicketDAO.java,v 1.1 2007/06/29 06:28:28 marco Exp $
 * @since 3.0
 */
public class HibernateDownloadTicketDAO extends HibernateDaoSupport implements DownloadTicketDAO {

	protected static Log log = LogFactory.getLog(HibernateDownloadTicketDAO.class);

	private HibernateDownloadTicketDAO() {
	}

	/**
	 * @see org.contineo.core.document.dao.DownloadTicketDAO#delete(java.lang.String)
	 */
	public boolean delete(String ticketid) {
		boolean result = true;

		try {
			DownloadTicket ticket = findByPrimaryKey(ticketid);
			if (ticket != null)
				getHibernateTemplate().delete(ticket);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(),e);
			result = false;
		}

		return result;
	}

	/**
	 * @see org.contineo.core.document.dao.DownloadTicketDAO#findByPrimaryKey(java.lang.String)
	 */
	public DownloadTicket findByPrimaryKey(String ticketid) {
		DownloadTicket ticket = null;

		try {
			ticket = (DownloadTicket) getHibernateTemplate().get(DownloadTicket.class, ticketid);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(),e);
		}
		return ticket;
	}

	/**
	 * @see org.contineo.core.document.dao.DownloadTicketDAO#store(org.contineo.core.document.DownloadTicket)
	 */
	public boolean store(DownloadTicket ticket) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(ticket);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(),e);
			result = false;
		}

		return result;
	}

	/**
	 * @see org.contineo.core.document.dao.DownloadTicketDAO#deleteByMenuId(int)
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteByMenuId(int menuId) {
		boolean result = true;

		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) getHibernateTemplate().find(
					"from org.contineo.core.document.DownloadTicket _ticket where _ticket.menuId = ?",
					new Object[] { new Integer(menuId) });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(),e);
			result = false;
		}

		return result;
	}
}