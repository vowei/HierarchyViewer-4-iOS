/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.common;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class IConManager {
	
	public static Image MAP_PAGE;
	public static Image INFO;
	public static Image REFRESH_BLUE;
	public static Image REFRESH_GREEN;
	public static Image NEW;
	public static Image OPEN_FOLDER;
	public static Image EDIT_IQUERY;
	public static Image SAVE_HIERARCHY;
	public static Image OPEN_APP;
	public static Image TERMINATE;
	
	static 
	{
		//MAP_PAGE = ImageLoader.getDdmUiLibLoader().loadImage("tree-view.png", Display.getDefault());
		//INFO = ImageLoader.getDdmUiLibLoader().loadImage("info_obj.png", Display.getDefault());
		REFRESH_BLUE = ImageLoader.getDdmUiLibLoader().loadImage("RefreshArrow_Blue.png", Display.getDefault());
		REFRESH_GREEN = ImageLoader.getDdmUiLibLoader().loadImage("RefreshArrow_Green.png", Display.getDefault());
		//NEW = ImageLoader.getDdmUiLibLoader().loadImage("new_con.png", Display.getDefault());
		OPEN_FOLDER = ImageLoader.getDdmUiLibLoader().loadImage("openfolderHS.png", Display.getDefault());
		EDIT_IQUERY=ImageLoader.getDdmUiLibLoader().loadImage("EditIQuery.png", Display.getDefault());
		SAVE_HIERARCHY=ImageLoader.getDdmUiLibLoader().loadImage("saveHS.png", Display.getDefault());
		OPEN_APP=ImageLoader.getDdmUiLibLoader().loadImage("run_exc.gif", Display.getDefault());
		TERMINATE=ImageLoader.getDdmUiLibLoader().loadImage("terminate.png", Display.getDefault());
	}
}
