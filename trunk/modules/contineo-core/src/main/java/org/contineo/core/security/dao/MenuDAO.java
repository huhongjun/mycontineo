package org.contineo.core.security.dao;

import java.util.Collection;
import java.util.Set;

import org.contineo.core.security.ExtMenu;
import org.contineo.core.security.Menu;

/**
 * Instances of this class is a DAO-service for menu objects.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public interface MenuDAO {

	/**
	 * This method persists the menu object.
	 * 
	 * @param menu Menu to be stored.
	 * @return True if successful stored in a database.
	 */
	public boolean store(Menu menu);

	/**
	 * This method deletes a menu in database.
	 * 
	 * @param menuId Menu to be deleted.
	 * @return True if successful deleted.
	 */
	public boolean delete(int menuId);

	/**
	 * Finds a menu by primarykey.
	 * 
	 * @param menuId Primarykey of wanted menu.
	 * @return Wanted menu or null.
	 */
	public Menu findByPrimaryKey(int menuId);

	/**
	 * Finds all menus by menutext.
	 * 
	 * @param menutext
	 * @return Collection of menus with given menutext.
	 */
	public Collection<Menu> findByMenuText(String menutext);

	/**
	 * Finds authorized menus for a user.
	 * 
	 * @param username Name of the user.
	 * @return Collection of found menus.
	 */
	public Collection<Menu> findByUserName(String username);

	/**
	 * Finds authorized menus for a user having a specified keyword.
	 * 
	 * @param username Name of the user.
	 * @param keyword Keyword of the document bind with the menu.
	 * @return Collection of found menus.
	 */
	public Collection<Menu> findByUserNameAndKeyword(String username, String keyword);

	/**
	 * Finds direct children of a menu.
	 * 
	 * @param parentId MenuId of the menu which children are wanted.
	 * @return Collection of found menus.
	 */
	public Collection<Menu> findByUserName(String username, int parentId);

	/**
	 * Finds all children(direct and indirect) by parentId
	 * 
	 * @param parentId
	 * @return
	 */
	public Collection<Menu> findByParentId(int parentId);

	
    /**
     * Finds direct children of a menu.
     * 
     * @param parentId MenuId of the menu which children are wanted.
     * @return Collection of found menus.
     */
    public Collection<Menu> findChildren(int parentId);
	
	/**
	 * This method is looking up for writing rights for a menu and an user.
	 * 
	 * @param menuId ID of the menu.
	 * @param username Name of the user.
	 */
	public boolean isWriteEnable(int menuId, String username);

	public boolean isReadEnable(int menuId, String username);

	/**
	 * This method selects only the menutext from a menu.
	 * 
	 * @param menuId Id of the menu.
	 * @return Selected menutext.
	 */
	public String findMenuTextByMenuId(int menuId);

	/**
	 * This method selects only the menuId from the menus for which a user is
	 * authorized.
	 * 
	 * @param username Name of the user.
	 * @return Collection of selected menuId's.
	 */
	public Set<Integer> findMenuIdByUserName(String username);

	/**
	 * returns a collection with sub-menus contained in menu with the given id
	 * 
	 * @param menuid return all menus in this menu
	 * @param userName only return those menus the user has at least read access
	 *        to
	 * @return a collection containing elements of type {@link ExtMenu}
	 */
	public Collection<ExtMenu> getContainedMenus(int menuId, String userName);

	/**
	 * returns if a menu is writeable for a user
	 * 
	 * @param menuid check this menu
	 * @param userName privileges for this should be checked
	 * @return a 0 if false, a 1 if true
	 */
	public Integer isMenuWriteable(int menuId, String userName);

	/**
	 * checks that the user has access to the menu and all its sub-items
	 */
	public boolean hasWriteAccess(Menu menu, String p_userName);

	/**
	 * Finds all menues associated to the passed group
	 * 
	 * @param groupName The group name
	 * @return The collection of menues
	 */
	public Collection<Menu> findByGroupName(String groupName);

}