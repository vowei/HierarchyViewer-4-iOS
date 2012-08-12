
/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.common;

public class iQueryElement {
	public String mName;
	public String mDescription;
	public boolean mSupported;
	
	public iQueryElement(String name,String description,boolean supported)
	{
		this(name,description);
		mSupported=supported;
	}
	
	public iQueryElement(String name,String description)
	{
		this.mName=name;
		this.mDescription=description;
	}
	
}
