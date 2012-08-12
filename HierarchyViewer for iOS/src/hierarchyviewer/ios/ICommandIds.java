/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewer.ios;

/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {

    public static final String CMD_OPEN = "HierarchyViewer_for_iOS.open";
    public static final String CMD_OPEN_MESSAGE = "HierarchyViewer_for_iOS.openMessage";
    public static final String CMD_SAVE_PICTURE = "HierarchyViewer_for_iOS.SaveHierarchyViewPicture";
    
}
