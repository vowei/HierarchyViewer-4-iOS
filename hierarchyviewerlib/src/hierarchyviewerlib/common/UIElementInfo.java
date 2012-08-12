/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html) 
 */


package hierarchyviewerlib.common;

public class UIElementInfo {
	public String name;
	public String jsFunctionName;
	//"Object" means the js function return Object, 
	//"Array" means the js function return Array 
	public String type;
	
	public UIElementInfo(String n,String f, String t)
	{
		name=n;
		jsFunctionName=f;
		type=t;
	}
}
