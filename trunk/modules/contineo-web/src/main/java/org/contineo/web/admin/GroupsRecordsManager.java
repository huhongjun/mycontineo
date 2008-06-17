package org.contineo.web.admin;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.security.Group;
import org.contineo.core.security.dao.GroupDAO;
import org.contineo.core.security.dao.MenuDAO;
import org.contineo.util.Context;
import org.contineo.web.SessionManagement;
import org.contineo.web.i18n.Messages;

/**
 * <p>
 * The <code>GroupsManager</code> class is responsible for constructing the
 * list of <code>Group</code> beans which will be bound to a ice:dataTable JSF
 * component. <p/>
 * <p>
 * Large data sets could be handle by adding a ice:dataPaginator. Alternatively
 * the dataTable could also be hidden and the dataTable could be added to
 * scrollable ice:panelGroup.
 * </p>
 * 
 * @author Marco Meschieri
 * @version $Id: DocumentsRecordsManager.java,v 1.1 2007/06/29 06:28:29 marco
 *          Exp $
 * @since 3.0
 */
public class GroupsRecordsManager {
	protected static Log log = LogFactory.getLog(GroupsRecordsManager.class);

	private Collection<Group> groups = new ArrayList<Group>();

	private String selectedPanel = "list";

	private String parentGroup = null;

	private Group selectedGroup = null;

	private UIComponent groupDesc;

	public GroupsRecordsManager() {
	}

	private void clear() {
		if (groupDesc != null)
			((UIInput) groupDesc).setSubmittedValue(null);
	}

	private void reload() {
		groups.clear();

		try {
			String username = SessionManagement.getUsername();
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

			if (mdao.isReadEnable(7, username)) {
				GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
				groups = dao.findAll();
			} else {
				Messages.addLocalizedError("errors.noaccess");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			Messages.addLocalizedError("errors.error");
		}
	}

	public String getSelectedPanel() {
		return selectedPanel;
	}

	public void setSelectedPanel(String panel) {
		this.selectedPanel = panel;
	}

	public String list() {
		selectedPanel = "list";
		reload();

		return null;
	}

	public String edit() {
		selectedGroup = (Group) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("group");

		String username = SessionManagement.getUsername();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		if (mdao.isReadEnable(7, username)) {
			clear();
		} else {
			Messages.addLocalizedError("errors.noaccess");
		}

		selectedPanel = "edit";
		return null;
	}

	public String addGroup() {
		parentGroup = null;

		String username = SessionManagement.getUsername();
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		if (mdao.isReadEnable(7, username)) {
			selectedGroup = new Group();
		} else {
			Messages.addLocalizedError("errors.noaccess");
		}

		selectedPanel = "create";

		return null;
	}

	public String delete() {
		String groupId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(
				"groupId");

		if (SessionManagement.isValid()) {
			try {
				String username = SessionManagement.getUsername();
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

				if (mdao.isReadEnable(7, username)) {
					// we do not allow to delete the initial "admin" group
					if (groupId.equals("admin")) {
						Messages.addLocalizedError("errors.action.groupdeleted.admin");
					} else {
						GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
						boolean deleted = dao.delete(groupId);

						if (!deleted) {
							Messages.addLocalizedError("errors.action.groupdeleted");
						} else {
							Messages.addLocalizedInfo("msg.action.groupdeleted");
						}
					}
				} else {
					Messages.addLocalizedError("errors.noaccess");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.groupdeleted");
			}

			reload();
		} else {
			return "login";
		}

		return null;
	}

	public String save() {
		if (SessionManagement.isValid()) {
			GroupDAO dao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);

			try {
				if (dao.exists(selectedGroup.getGroupName()) && "create".equals(selectedPanel)) {
					Messages.addLocalizedError("errors.action.groupexists");
				} else {
					boolean stored = false;

					if ("create".equals(selectedPanel)) {
						stored = dao.insert(selectedGroup, parentGroup);
					} else {
						stored = dao.store(selectedGroup);
					}

					if (!stored) {
						Messages.addLocalizedError("errors.action.savegroup.notstored");
					} else {
						Messages.addLocalizedInfo("msg.action.savegroup");
					}
				}
			} catch (Exception e) {
				Messages.addLocalizedError("errors.action.savegroup.notstored");
			}

			selectedPanel = "list";
			reload();

			return null;
		} else {
			return "login";
		}
	}

	/**
	 * Gets the list of Group which will be used by the ice:dataTable component.
	 */
	public Collection<Group> getGroups() {
		if (groups.size() == 0) {
			reload();
		}

		return groups;
	}

	public int getCount() {
		return getGroups().size();
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(Group selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	public String getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(String group) {
		this.parentGroup = group;
	}

	public UIComponent getGroupDesc() {
		return groupDesc;
	}

	public void setGroupDesc(UIComponent groupDesc) {
		this.groupDesc = groupDesc;
	}
}
