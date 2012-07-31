package hierarchyviewerlib.actions;

import hierarchyviewerlib.common.ClipboardHelper;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.controllers.TreeViewController;

import org.eclipse.jface.action.Action;

public class CopyFunctionCallByNameAction extends Action {

public ViewNode mSelectedViewNode;
	
	public CopyFunctionCallByNameAction(ViewNode selectedViewNode)
	{
		this.setText("拷贝函数调用路径(控件名)");
		mSelectedViewNode=selectedViewNode;
	}
	
	@Override
	public void run() {
		String functionCallStr = 
				TreeViewController.getController().getFunctionCallByName(mSelectedViewNode);
		ClipboardHelper.setClipboard(functionCallStr);
	}
	
	@Override
	public boolean isEnabled() {
		if(mSelectedViewNode==null)
		{
			return false;
		}
		return true;
	}
}
