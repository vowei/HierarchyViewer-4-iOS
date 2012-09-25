package hierarchyviewerlib.actions;

import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.devicebridge.DeviceBridge;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class RefreshAppElementAction extends Action {

	public RefreshAppElementAction()
	{
		setText("刷新App控件树");
		setId(ICommandIds.CMD_REFRESH_APP_ELEMENT);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_REFRESH_APP_ELEMENT);
        setImageDescriptor(ImageDescriptor.createFromImage(IConManager.REFRESH_GREEN));
	}

	@Override
	public void run() {	
		DeviceBridge.getBridge().requestElementTree();
	}
}
