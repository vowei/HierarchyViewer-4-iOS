/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewer.ios.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import hierarchyviewerlib.common.CustomString;
import hierarchyviewerlib.uicomponents.TreeViewOverview;

public class HierarchyOverView extends ViewPart {

	public static final String ID = "hierarchyviewer.ios.views.HierarchyOverView"; //$NON-NLS-1$

	private TreeViewOverview mTreeViewOverview;
	
	public HierarchyOverView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		mTreeViewOverview=new TreeViewOverview(container);
		
		createActions();
		initializeToolBar();
		initializeMenu();
		
		this.setPartName(CustomString.getString("VIEW_HIERARCHY_OVERVIEW"));
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
