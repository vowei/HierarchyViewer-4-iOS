/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewer.ios.views;

import hierarchyviewerlib.common.CustomString;
import hierarchyviewerlib.uicomponents.TreeViewListViewer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class TreeViewListView extends ViewPart {

	public static final String ID = "hierarchyviewer.ios.views.TreeViewListView"; //$NON-NLS-1$

	private TreeViewListViewer mTreeViewListViewer;
	
	public TreeViewListView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		container.setLayout(new FillLayout());
		
		mTreeViewListViewer =new  TreeViewListViewer(container);
		
		createActions();
		initializeToolBar();
		initializeMenu();
		
		this.setPartName(CustomString.getString("VIEW_TREEVIEW_LIST"));
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
