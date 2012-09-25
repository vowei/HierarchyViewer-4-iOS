/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.models.LogFileModel;

public class OpenLogFileAction extends Action {
	private final IWorkbenchWindow window;
	
	public OpenLogFileAction(IWorkbenchWindow window)
	{
		setText("打开plist日志");
		this.window = window;
		setId(ICommandIds.CMD_OPEN_LOG_FILE);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_OPEN_LOG_FILE);
        setImageDescriptor(ImageDescriptor.createFromImage(IConManager.OPEN_FOLDER));
	}

	@Override
	public void run() {
		Shell shell=window.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setText("选择要打开的日志文件");
		fileDialog.setFilterExtensions(new String[] { "*.plist" });
		fileDialog.setFilterNames(new String[] { "Textfiles(*.plist)" });
		String selected = fileDialog.open();
		if(selected==null)
			return;
		
		LogFileModel.getModel().loadLogFile(selected);
		
		//load project
	}
}
