/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.actions;


import org.eclipse.jface.action.Action;

import hierarchyviewerlib.common.ViewManager;

public class OpenViewAction extends Action {
	//private final IWorkbenchWindow window;
	private final String mViewName;
	
	public OpenViewAction(String title,String viewName)
	{
		setText(title);
		mViewName=viewName;
		//this.window = window;
		setId(ICommandIds.CMD_OPEN_VIEWS);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_OPEN_VIEWS);
        //setImageDescriptor(ImageDescriptor.createFromImage(IConManager.REFRESH_BLUE));
	}

	@Override
	public void run() {	
		ViewManager.showView(mViewName);
	}
}
