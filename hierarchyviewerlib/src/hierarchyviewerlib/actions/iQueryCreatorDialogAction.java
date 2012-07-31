package hierarchyviewerlib.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.uicomponents.iQueryCreatorDialog;

public class iQueryCreatorDialogAction extends Action {
	
private final IWorkbenchWindow window;
static private iQueryCreatorDialog iqueryCreatorDialog=null;

	public iQueryCreatorDialogAction(IWorkbenchWindow window)
	{
		setText("iQuery生成测试器");
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
		Shell shell =new Shell(parent);
		
		iQueryCreatorDialog dialog =new iQueryCreatorDialog(shell);
		dialog.open();
		iqueryCreatorDialog=dialog;
	}

}
