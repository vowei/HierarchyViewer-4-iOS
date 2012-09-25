/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewer.ios;

import hierarchyviewerlib.actions.OpenAppAction;
import hierarchyviewerlib.actions.OpenLogFileAction;
import hierarchyviewerlib.actions.OpenViewAction;
import hierarchyviewerlib.actions.RefreshAppElementAction;
import hierarchyviewerlib.actions.RefreshLogFileAction;
import hierarchyviewerlib.actions.SaveHierarchyViewPictureAction;
import hierarchyviewerlib.actions.TerminateAppAction;
import hierarchyviewerlib.actions.iQueryCreatorDialogAction;
import hierarchyviewerlib.uicomponents.StatusBar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of the
 * actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use them
    // in the fill methods.  This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private Action openLogFileAction; 
    private Action refreshLogFileAction;
    private Action openScreenShotViewAction;
    private Action openHierarchyViewAction;
    private Action openPropertiesViewAction;
    private Action openHierarchyOverViewAction;
    private Action TreeViewListViewAction;
    private Action iqueryCreatorDialogAction;
    private Action saveHierarchyViewPictureAction;
    private Action openAppAction;
    private Action refreshAppElementAction;
    private Action terminateAppAction;
    
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        
        openLogFileAction=new OpenLogFileAction(window);
        register(openLogFileAction);
        
        refreshLogFileAction=new RefreshLogFileAction();
        register(refreshLogFileAction);
        
        openAppAction = new OpenAppAction(window);
        register(openAppAction);
        
        refreshAppElementAction = new RefreshAppElementAction();
        register(refreshAppElementAction);
        
        terminateAppAction = new TerminateAppAction();
        register(terminateAppAction);
        
        saveHierarchyViewPictureAction = new SaveHierarchyViewPictureAction(window);
        register(saveHierarchyViewPictureAction);
        
        //Tools
        iqueryCreatorDialogAction=new iQueryCreatorDialogAction(window);
        
        //Windows
        openScreenShotViewAction = new OpenViewAction("截屏视图","ScreenShot");
        register(openScreenShotViewAction);
        
        openHierarchyViewAction = new OpenViewAction("控件树视图","Hierarchy");
        register(openHierarchyViewAction);
        
        openPropertiesViewAction= new OpenViewAction("控件属性视图","Properties");
        register(openPropertiesViewAction);
        
        openHierarchyOverViewAction= new OpenViewAction("控件树缩略视图","HierarchyOverView");
        register(openHierarchyOverViewAction);
        
        TreeViewListViewAction= new OpenViewAction("控件树列表视图","TreeViewList");
        register(TreeViewListViewAction);
        
        
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        
    }
    
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager fileMenu = new MenuManager("&文件", IWorkbenchActionConstants.M_FILE);
        MenuManager toolMenu = new MenuManager("&工具", IWorkbenchActionConstants.M_NAVIGATE);
        MenuManager windowMenu = new MenuManager("&窗口", IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&帮助", IWorkbenchActionConstants.M_HELP);
        
        menuBar.add(fileMenu);
        menuBar.add(toolMenu);
        menuBar.add(windowMenu);
        // Add a group marker indicating where action set menus will appear.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);
        
        // File
        fileMenu.add(openLogFileAction);
        fileMenu.add(refreshLogFileAction);
        fileMenu.add(new Separator());
        
        fileMenu.add(openAppAction);
        fileMenu.add(refreshAppElementAction);
        fileMenu.add(terminateAppAction);
        fileMenu.add(new Separator());
        
        fileMenu.add(saveHierarchyViewPictureAction);
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);
        
        //Tools
        toolMenu.add(iqueryCreatorDialogAction);
        
        //Window
        windowMenu.add(openScreenShotViewAction);
        windowMenu.add(openHierarchyViewAction);
        windowMenu.add(openPropertiesViewAction);
        windowMenu.add(openHierarchyOverViewAction);
        windowMenu.add(TreeViewListViewAction);
        
        // Help
        helpMenu.add(aboutAction);
    }
    
    protected void fillCoolBar(ICoolBarManager coolBar) {
        IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "files"));   
        toolbar.add(openLogFileAction);
        toolbar.add(refreshLogFileAction);
        
        toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "app"));   
        toolbar.add(openAppAction);
        toolbar.add(refreshAppElementAction);
        toolbar.add(terminateAppAction);
        
        toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        coolBar.add(new ToolBarContributionItem(toolbar, "tools"));
        toolbar.add(saveHierarchyViewPictureAction);
        toolbar.add(iqueryCreatorDialogAction);
    }
    
	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(new StatusBar("status bar",statusLine));
		super.fillStatusLine(statusLine);
	}
}
