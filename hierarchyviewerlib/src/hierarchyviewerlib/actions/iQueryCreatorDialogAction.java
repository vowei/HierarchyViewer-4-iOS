package hierarchyviewerlib.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import hierarchyviewerlib.common.CustomString;
import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.uicomponents.iQueryCreatorDialog;

public class iQueryCreatorDialogAction extends Action {
/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */	

private final IWorkbenchWindow window;
static private iQueryCreatorDialog iqueryCreatorDialog=null;

	public iQueryCreatorDialogAction(IWorkbenchWindow window)
	{
		setText(CustomString.getString("ACTION_IQUERY_GENERATOR"));
		this.window = window;
		setId(ICommandIds.CMD_OPEN_IQUERY_DIALOG);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_OPEN_IQUERY_DIALOG);
        setImageDescriptor(ImageDescriptor.createFromImage(IConManager.EDIT_IQUERY));
	}
	
	@Override
	public void run() {
		
		if(iqueryCreatorDialog!=null)
		{
			iqueryCreatorDialog.open();
			return;
		}
		
		Shell parent=window.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		iQueryCreatorDialog dialog =new iQueryCreatorDialog(parent);
		iqueryCreatorDialog=dialog;
		dialog.open();
	}

}
