 /*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.common;

import java.util.Hashtable;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ViewManager {
	
	static Hashtable<String,String> sViewMap=new Hashtable<String,String>();
	static public void showView(String alias)
	{
		if(!sViewMap.containsKey(alias))
		{
			return;
		}

		String id=sViewMap.get(alias);
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        try {
			window.getActivePage().showView(id);
			} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}
	static public void addView(String alias, String viewId)
	{
		sViewMap.put(alias, viewId);
	}
	
}
