package hierarchyviewerlib.actions;

import hierarchyviewerlib.common.CustomString;
import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.devicebridge.DeviceBridge;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class TerminateAppAction extends Action {
	public TerminateAppAction()
	{
		setText(CustomString.getString("ACTION_TERMINATE_APP"));
		setId(ICommandIds.CMD_TERMINATE_APP);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_TERMINATE_APP);
        setImageDescriptor(ImageDescriptor.createFromImage(IConManager.TERMINATE));
	}

	@Override
	public void run() {	
		DeviceBridge.getBridge().close();
	}
}
