/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */


package hierarchyviewerlib.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

//import cc.iqa.studio.tracks.Logger;

public class MessageBoxManager {
	
	private static Shell sModelShell=null;
	
	static 
	{
		sModelShell=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
	}

	public static void openInformation(boolean modal, String title, String message, Shell shell)
	{
		if(shell==null)
		{
			shell=sModelShell;
		}
		//Logger.actionLog("MessageBox弹出，错误级别:信息，model:%b,title:%s,Message:%s.", modal,title,message);
		MessageDialog.openInformation(getShell(shell,modal), title, message);
	}
	
	public static void openInformation(boolean modal, String title, String message)
	{
		openInformation(modal, title, message, null);
	}
	
	public static void openError(boolean modal, String title, String message, Shell shell)
	{
		if(shell==null)
		{
			shell=sModelShell;
		}
		//Logger.actionLog("MessageBox弹出，错误级别:错误，model:%b,title:%s,Message:%s.", modal,title,message);
		MessageDialog.openError(getShell(shell,modal), title, message);
	}
	
	public static void openError(boolean modal, String title, String message)
	{
		openError(modal, title, message, null);
	}
	
	public static void openWarning(boolean modal, String title,String message,Shell shell)
	{
		if(shell==null)
		{
			shell=sModelShell;
		}
		//Logger.actionLog("MessageBox弹出，错误级别:警告，model:%b,title:%s,Message:%s.", modal,title,message);
		MessageDialog.openWarning(getShell(shell,modal), title, message);
	}
	
	public static void openWarning(boolean modal, String title, String message)
	{
		openWarning(modal, title, message, null);
	}
	
	private static Shell getShell(Shell shell, boolean modal)
	{
		if(modal)
		{
			return shell;
		}
		else
		{
			Shell nonModelShell=new Shell(shell);
			return nonModelShell;
		}
	}
}
