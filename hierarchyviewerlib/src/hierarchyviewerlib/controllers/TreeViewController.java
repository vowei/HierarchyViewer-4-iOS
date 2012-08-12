/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.controllers;

import java.util.Map;

import hierarchyviewerlib.common.UIElementInfo;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.models.ConfigurationModel;

public class TreeViewController {
	
	static TreeViewController sController=null;
	
	static public TreeViewController getController()
	{
		if(sController==null)
		{
			sController = new TreeViewController();
		}
		return sController;
	}
	
	public String getFunctionCallByIndex(ViewNode viewNode)
	{
		String functionCallStr="";
		Map<String,UIElementInfo> uiElementInfoMap = 
				ConfigurationModel.getModel().getUIElementInfoMap();
		
		ViewNode currentNode=viewNode;
		while(currentNode!=null)
		{
			String currentFunctionCall="";
			if(uiElementInfoMap.containsKey(currentNode.type))
			{
				UIElementInfo elementInfo=uiElementInfoMap.get(currentNode.type);
				if(elementInfo.type.equalsIgnoreCase("Object"))
				{
					currentFunctionCall=elementInfo.jsFunctionName+"()";
				}
				else
				{
					currentFunctionCall=elementInfo.jsFunctionName+"()["+currentNode.index+"]";
				}
			}
			//映射表里面没有的，按元素名作为函数进行调用，事实是错误的调用。
			else
			{
				currentFunctionCall=currentNode.type+"()["+currentNode.index+"]";
			}
			
			functionCallStr="."+currentFunctionCall+functionCallStr;
			
			currentNode=currentNode.parent;
		
		}
		return "target.frontMostApp()"+functionCallStr;
	}
	
	public String getFunctionCallByName(ViewNode viewNode)
	{
		String functionCallStr="";
		Map<String,UIElementInfo> uiElementInfoMap = 
				ConfigurationModel.getModel().getUIElementInfoMap();
		
		ViewNode currentNode=viewNode;
		while(currentNode!=null)
		{
			String currentFunctionCall="";
			if(uiElementInfoMap.containsKey(currentNode.type))
			{
				UIElementInfo elementInfo=uiElementInfoMap.get(currentNode.type);
				if(elementInfo.type.equalsIgnoreCase("Object"))
				{
					currentFunctionCall=elementInfo.jsFunctionName+"()";
				}
				else
				{
					if(currentNode.namedProperties.containsKey("name"))
					{
						currentFunctionCall=elementInfo.jsFunctionName+"()[\""+currentNode.namedProperties.get("name").value+"\"]";
					}
					else
					{
						currentFunctionCall=elementInfo.jsFunctionName+"()["+currentNode.index+"]";
					}
				}
			}
			//映射表里面没有的，按元素名作为函数进行调用，事实是错误的调用。
			else
			{
				currentFunctionCall=currentNode.type+"()["+currentNode.index+"]";
			}
			
			functionCallStr="."+currentFunctionCall+functionCallStr;
			
			currentNode=currentNode.parent;
		
		}
		return "target.frontMostApp()"+functionCallStr;
	}
}
