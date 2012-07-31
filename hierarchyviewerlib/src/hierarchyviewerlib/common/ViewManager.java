/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
