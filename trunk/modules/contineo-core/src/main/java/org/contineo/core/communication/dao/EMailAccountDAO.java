package org.contineo.core.communication.dao;

import java.util.Collection;

import org.contineo.core.communication.EMailAccount;

/**
 * DAO for <code>EMailAccount</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini
 */
public interface EMailAccountDAO {

    /**
     * This method persists an emailaccount object.
     * 
     * @param account EMailAccount which should be store.
     * @return True if successfully stored in a database.
     */
    public boolean store(EMailAccount account); 

    /**
     * This method deletes an emailaccount.
     * 
     * @param accountId AccountId of the emailaccount which should be delete.
     */
    public boolean delete(int accountId); 

    /**
     * This method finds an emailaccount by its accountId.
     */
    public EMailAccount findByPrimaryKey(int accountId); 

    /**
     * Loads all accounts
     * 
     * @return
     */
    public Collection<EMailAccount> findAll(); 

    public Collection<EMailAccount> findByUserName(String username); 
    
        /**
     * This method deletes an emailaccount.
     * 
     * @param username Username of the emailaccount which should be delete.
     */
    public boolean deleteByUsername(String username); 
}