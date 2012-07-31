package hierarchyviewerlib.actions;

import hierarchyviewerlib.common.ClipboardHelper;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.controllers.TreeViewController;

import org.eclipse.jface.action.Action;

public class CopyFunctionCallByIndexAction extends Action {

public ViewNode mSelectedViewNode;
	
	public CopyFunctionCallByIndexAction(ViewNode selectedViewNode)
	{
		this.setText("拷贝函数调用路径(索引)");
		mSelectedViewNode=selectedViewNode;
	}
	
	@Override
	public void run() {
		String functionCallStr = 
				TreeViewController.getController().getFunctionCallByIndex(mSelectedViewNode);
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