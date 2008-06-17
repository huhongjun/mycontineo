package org.contineo.core.communication.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.communication.EMail;
import org.contineo.core.communication.EMailAccount;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>EMailAccount</code>
 * 
 * @author Alessandro Gasparini
 * @version $Id: HibernateEMailAccountDAO.java,v 1.1 2007/06/29 06:28:30 marco Exp $
 * @since 3.0
 */
public class HibernateEMailAccountDAO extends HibernateDaoSupport implements EMailAccountDAO {

    protected static Log log = LogFactory.getLog(HibernateEMailAccountDAO.class);

    private HibernateEMailAccountDAO() {
    }

    /**
     * @see org.contineo.core.communication.dao.EMailAccountDAO#store(org.contineo.core.communication.EMailAccount)
     */
    public boolean store(EMailAccount account) {
        boolean result = true;

        try {
            getHibernateTemplate().saveOrUpdate(account);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage());
            result = false;
        }

        return result;
    }

    /**
     * @see org.contineo.core.communication.dao.EMailAccountDAO#delete(int)
     */
    public boolean delete(int accountId) {
        boolean result = true;

        try {
            DetachedCriteria dt = DetachedCriteria.forClass(EMail.class);
            dt.add(Property.forName("accountId").eq(new Integer(accountId)));
            getHibernateTemplate().deleteAll(getHibernateTemplate().findByCriteria(dt));

            EMailAccount emAccount = (EMailAccount) getHibernateTemplate().get(EMailAccount.class, accountId);
            if (emAccount != null)
                getHibernateTemplate().delete(emAccount);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage(), e);
            result = false;
        }

        return result;
    }

    /**
     * @see org.contineo.core.communication.dao.EMailAccountDAO#findByPrimaryKey(int)
     */
    public EMailAccount findByPrimaryKey(int accountId) {
        EMailAccount emAccount = null;

        try {
            emAccount = (EMailAccount) getHibernateTemplate().get(EMailAccount.class, new Integer(accountId));
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage(), e);
        }

        return emAccount;
    }

    /**
     * @see org.contineo.core.communication.dao.EMailAccountDAO#findAll()
     */
    @SuppressWarnings("unchecked")
    public Collection<EMailAccount> findAll() {
        Collection<EMailAccount> result = new ArrayList<EMailAccount>();

        try {
            DetachedCriteria dt = DetachedCriteria.forClass(EMailAccount.class);
            result = (Collection<EMailAccount>) getHibernateTemplate().findByCriteria(dt);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * @see org.contineo.core.communication.dao.EMailAccountDAO#deleteByUsername(java.lang.String)
     */
    public boolean deleteByUsername(String username) {
        boolean result = true;

        try {
            Collection<EMailAccount> coll = findByUserName(username);
            for (EMailAccount account : coll) {
                delete(account.getAccountId());
            }
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage());
            result = false;
        }

        return result;
    }

    /**
     * @see org.contineo.core.communication.dao.EMailAccountDAO#findByUserName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Collection<EMailAccount> findByUserName(String username) {
        Collection<EMailAccount> result = new ArrayList<EMailAccount>();

        try {
            DetachedCriteria dt = DetachedCriteria.forClass(EMailAccount.class);
            dt.add(Property.forName("userName").eq(username));

            result = (Collection<EMailAccount>) getHibernateTemplate().findByCriteria(dt);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage(), e);
        }

        return result;
    }
}
