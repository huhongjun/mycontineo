package org.contineo.web.navigation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.contineo.core.security.Menu;
import org.contineo.core.security.dao.MenuDAO;
import org.contineo.util.Context;
import org.contineo.util.config.PropertiesBean;
import org.contineo.web.SessionManagement;
import org.contineo.web.StyleBean;
import org.contineo.web.i18n.Messages;

/**
 * <p>
 * The MenuBarBean class determines which menu item fired the ActionEvent and
 * stores the modified id information in a String. MenuBarBean also controls the
 * orientation of the Menu Bar.
 * </p>
 * 
 * @author Marco Meschieri
 * @version $Id: MenuBarBean.java,v 1.9 2007/10/11 16:33:56 marco Exp $
 * @since 3.0
 */
public class MenuBarBean {
	protected static Log logger = LogFactory.getLog(MenuBarBean.class);

	// records which menu item fired the event
	private String actionFired;

	// records the param value for the menu item which fired the event
	private String param;

	// orientation of the menubar ("horizontal" or "vertical")
	private String orientation = "horizontal";

	// list for the dynamic menus w/ getter & setter
	protected List<MenuItem> model = new ArrayList<MenuItem>();

	private NavigationBean navigation;

	/**
	 * Get the param value for the menu item which fired the event.
	 * 
	 * @return the param value
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Set the param value.
	 */
	public void setParam(String param) {
		this.param = param;
	}

	/**
	 * Get the modified ID of the fired action.
	 * 
	 * @return the modified ID
	 */
	public String getActionFired() {
		return actionFired;
	}

	public List<MenuItem> getModel() {
		if (model.isEmpty()) {
			createMenuItems();
		}

		return model;
	}

	public void setModel(List<MenuItem> model) {
		this.model = model;
	}

	/**
	 * Identify the ID of the element that fired the event and return it in a
	 * form suitable for display.
	 * 
	 * @param e the event that fired the listener
	 */
	public void primaryListener(ActionEvent e) {
		MenuItem menu = (MenuItem) e.getSource();

		if (StringUtils.isNotEmpty(menu.getContent().getTemplate())) {
			navigation.setSelectedPanel(menu.getContent());
		}
	}

	/**
	 * Get the orientation of the menu ("horizontal" or "vertical")
	 * 
	 * @return the orientation of the menu
	 */
	public String getOrientation() {
		return orientation;
	}

	/**
	 * Set the orientation of the menu ("horizontal" or "vertical").
	 * 
	 * @param orientation the new orientation of the menu
	 */
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	/**
	 * Finds all first-level menus accessible by the current user
	 * 
	 * @return
	 */
	protected void createMenuItems() {
		model.clear();

		String username = SessionManagement.getUsername();
		PageContentBean page = new PageContentBean("home", "home");
		page.setContentTitle(Messages.getMessage("home"));
		page.setDisplayText(Messages.getMessage("home"));
		page.setIcon(StyleBean.getImagePath("home.png"));

		MenuItem item = createMenuItem(Messages.getMessage("home"), null, "#{menuBar.primaryListener}", null, null, StyleBean
				.getImagePath("home.png"), false, null, page);
		model.add(item);

		try {
			if (username != null) {
				MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				Collection<Menu> menus = menuDao.findByUserName(username, 1);

				for (Menu menu : menus) {
					createMenuStructure(menu, null);
				}
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}

		MenuItem helpMenu = createMenuItem(Messages.getMessage("help"), "m-help", "#{menuBar.primaryListener}", null, null, StyleBean
				.getImagePath("help.png"), false, null, null);

		String lang=SessionManagement.getLanguage();
		//check if the help for the language exists, if not use the en version
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		File helpIndex=new File(servletContext.getRealPath(StyleBean.getPath("help") + "/" + SessionManagement.getLanguage() + "/index.html"));
		if(!helpIndex.exists())
			lang="en";
		String url = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()
				+ StyleBean.getPath("help") + "/" + lang + "/index.html";
		helpMenu.getChildren().add(
				createMenuItem(Messages.getMessage("help"), "m-helpcontents", null, null, url, StyleBean
						.getImagePath("help.png"), false, "_blank", null));

		PageContentBean infoPage = new PageContentBean("info", "info");
		infoPage.setContentTitle(Messages.getMessage("msg.jsp.info"));
		
		String product="";
		try {
			PropertiesBean pbean = new PropertiesBean(getClass().getClassLoader().getResource("context.properties"));
			product=pbean.getProperty("product.name");
		} catch (IOException e) {
			e.printStackTrace();
		}
		helpMenu.getChildren().add(
				createMenuItem(Messages.getMessage("about")+" "+product, "m-about", "#{menuBar.primaryListener}", null, null, StyleBean
						.getImagePath("about.png"), false, null, infoPage));
		model.add(helpMenu);
	}

	private void createMenuStructure(Menu menu, MenuItem parent) {
		
		PageContentBean page;
		MenuItem item;
		page = new PageContentBean("m-" + Integer.toString(menu.getMenuId()));

		if (StringUtils.isNotEmpty(menu.getMenuRef())) {
			page.setTemplate(menu.getMenuRef());
		}

		page.setContentTitle(Messages.getMessage(menu.getMenuText()));
		page.setIcon(StyleBean.getImagePath(menu.getMenuIcon()));

		item = createMenuItem(Messages.getMessage(menu.getMenuText()), "m-" + Integer.toString(menu.getMenuId()),
				"#{menuBar.primaryListener}", null, null, (menu.getMenuIcon() != null) ? StyleBean.getImagePath(menu
						.getMenuIcon()) : null, false, null, page);

		if (parent == null) {
			model.add(item);
		} else {
			parent.getChildren().add(item);
		}

		// For 'Documents' menu, skip children
		if (menu.getMenuId() != Menu.MENUID_DOCUMENTS) {
			MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			Collection<Menu> children = menuDao.findByUserName(SessionManagement.getUsername(), menu.getMenuId());

			for (Menu child : children) {
				createMenuStructure(child, item);
			}
		}
	}

	protected MenuItem createMenuItem(String label, String id, String actionListener, String action, String link,
			String icon, boolean immediate, String target, PageContentBean content) {
		MenuItem menuItem = new MenuItem();
		menuItem.setValue(label);

		if (id != null) {
			menuItem.setId(id);
		}

		if (actionListener != null) {
			menuItem.setActionListener(createActionListenerMethodBinding(actionListener));
		}

		if (action != null) {
			menuItem.setAction(createActionMethodBinding(action));
		}

		if (link != null) {
			menuItem.setLink(link);
		}

		if (icon != null) {
			menuItem.setIcon(icon);
		}

		menuItem.setImmediate(immediate);

		if (target != null) {
			menuItem.setTarget(target);
		}

		if (content != null) {
			menuItem.setContent(content);
		}

		return menuItem;
	}

	protected MethodBinding createActionListenerMethodBinding(String actionListenerString) {
		Class[] args = { ActionEvent.class };
		MethodBinding methodBinding = null;

		methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding(actionListenerString,
				args);

		return methodBinding;
	}

	protected MethodBinding createActionMethodBinding(String action) {
		Class[] args = {};
		MethodBinding methodBinding = null;

		methodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding(action, args);

		return methodBinding;
	}

	public void setNavigation(NavigationBean navigation) {
		this.navigation = navigation;
	}
}
