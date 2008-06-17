package org.contineo.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.security.dao.GroupDAO;
import org.contineo.core.security.dao.UserDAO;

/**
 * Basic implementation of <code>SecurityManager</code>
 * 
 * @author Marco Meschieri
 * @version $Id: SecurityManagerImpl.java,v 1.1 2007/06/29 06:28:30 marco Exp $
 * @since 3.0
 */
public class SecurityManagerImpl implements SecurityManager {

	protected static Log log = LogFactory.getLog(SecurityManagerImpl.class);

	protected UserDAO userDAO;

	protected GroupDAO groupDAO;

	private SecurityManagerImpl() {
	}

	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#assignUsersToGroup(java.util.Collection,
	 *      org.contineo.core.security.Group)
	 */
	public void assignUsersToGroup(Collection<User> users, Group group) {
		for (Iterator iter = users.iterator(); iter.hasNext();) {
			User user = (User) iter.next();
        
			if (!group.getUsers().contains(user)) {
				group.getUsers().add(user);
			}
			if (!user.getGroups().contains(group))
				user.getGroups().add(group);
		}
		groupDAO.store(group);

		if (log.isDebugEnabled())
			log.debug("Assigned users " + users + " to group " + group);
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#assignUserToGroups(org.contineo.core.security.User,
	 *      java.util.Collection)
	 */
	public void assignUserToGroups(User user, Collection<Group> groups) {
		for (Group group : groups)
			assignUserToGroup(user, group);
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#assignUserToGroup(org.contineo.core.security.User,
	 *      org.contineo.core.security.Group)
	 */
	public void assignUserToGroup(User user, Group group) {
		Collection<User> users = new ArrayList<User>();
		users.add(user);
		assignUsersToGroup(users, group);
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#removeUsersFromGroup(java.util.Collection,
	 *      org.contineo.core.security.Group)
	 */
	public void removeUsersFromGroup(Collection<User> users, Group group) {
		for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
			User user = iter.next();
			if (group.getUsers().contains(user)){
				group.getUsers().remove(user);
				user.getGroups().remove(group);
			}
		}

		groupDAO.store(group);

		if (log.isDebugEnabled())
			log.debug("Removed users " + users + " from group " + group);
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#removeUserFromGroup(org.contineo.core.security.User,
	 *      org.contineo.core.security.Group)
	 */
	public void removeUserFromGroup(User user, Group group) {
		Collection<User> users = new ArrayList<User>();
		users.add(user);
		removeUsersFromGroup(users, group);
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#removeUserFromAllGroups(org.contineo.core.security.User)
	 */
	public void removeUserFromAllGroups(User user) {
		Collection<Group> groups = groupDAO.findAll();
		for (Iterator<Group> iter = groups.iterator(); iter.hasNext();) {
			Group group = iter.next();
			if (group.getUsers().contains(user))
				removeUserFromGroup(user, group);
		}
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#removeAllUsersFromGroup(org.contineo.core.security.Group)
	 */
	public void removeAllUsersFromGroup(Group group) {
		removeUsersFromGroup(group.getUsers(), group);
	}

	/**
	 * @see org.contineo.core.security.SecurityManager#assignUserToGroups(org.contineo.core.security.User,
	 *      java.lang.String[])
	 */
	public void assignUserToGroups(User user, String[] groupNames) {
		if (groupNames == null)
			return;
		ArrayList<Group> groups = new ArrayList<Group>();
		for (int i = 0; i < groupNames.length; i++)
			groups.add(groupDAO.findByPrimaryKey(groupNames[i]));
		assignUserToGroups(user, groups);
	}
}