package hierarchyviewerlib.actions;

import hierarchyviewerlib.common.CustomString;
import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.devicebridge.DeviceBridge;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

public class OpenAppAction extends Action {
	private final IWorkbenchWindow window;
	
	public OpenAppAction(IWorkbenchWindow window)
	{
		setText(CustomString.getString("ACTION_OPEN_APP"));
		this.window = window;
		setId(ICommandIds.CMD_OPEN_APP);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_OPEN_APP);
        setImageDescriptor(ImageDescriptor.createFromImage(IConManager.OPEN_APP));
	}
	
	@Override
	public void run() {
		Shell shell=window.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setText(CustomString.getString("ACTION_SELECT_APP_FILE"));
		fileDialog.setFilterExtensions(new String[] { "*.app" });
		fileDialog.setFilterNames(new String[] { "AppFiles(*.app)" });
		String selected = fileDialog.open();
		if(selected==null)
			return;
		
		DeviceBridge.getBridge().start(selected);
		DeviceBridge.getBridge().requestElementTree();
	}
}
