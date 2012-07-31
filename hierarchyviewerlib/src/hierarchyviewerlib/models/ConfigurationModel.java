package hierarchyviewerlib.models;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import hierarchyviewerlib.common.*;

import javax.xml.parsers.*;

public class ConfigurationModel {
	
	
	static public ConfigurationModel sModel;

	public List<iQueryElement> mIQueryElements = new ArrayList<iQueryElement>();
	public Map<String,UIElementInfo> mUIElementInfoMap=new HashMap<String,UIElementInfo>();
	
	
	public ConfigurationModel()
	{
		try {
			Initialize();
		} catch (Exception e) {
			//TODO
		}
	}

	public Map<String,UIElementInfo>  getUIElementInfoMap()
	{
		return mUIElementInfoMap;
	}

	public static ConfigurationModel getModel() {
        if (sModel == null) {
        	sModel = new ConfigurationModel();
            //TODO throw exception to ask use build web service connection first
        }
        return sModel;
    }

	
	//TODO handle exception here
	private void Initialize() throws SAXException, IOException, ParserConfigurationException, URISyntaxException
	{
		Bundle bundle = Platform.getBundle("hierarchyviewerlib"); 
		URL url = bundle.getResource("configuration/configuration.xml");
		URL fileURL =FileLocator.toFileURL(url);
		File file =new File(fileURL.getPath()); 
        if (!file.exists()) {
        	int i=1;
        	//TODO throw exception, cannot file configuration file.
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        Element root =doc.getDocumentElement();
        Node platformNode = XMLHelper.selectSingleNode("/Configuration/Platform[@name='Android']", (Object)root);
        
      //get all iquery elements
        NodeList iQueryElementNodeList=XMLHelper.selectNodes("./iQuery/Element",root);
        for(int i=0;i<iQueryElementNodeList.getLength();i++)
        {
        	Element iQueryElement=(Element)iQueryElementNodeList.item(i);
        	String name =iQueryElement.getAttribute("name");
        	String description =iQueryElement.getAttribute("description");
        	mIQueryElements.add(new iQueryElement(name,description));
        }
        
        //get all uielements
        NodeList uiElementsNodeList=XMLHelper.selectNodes("./UIElements/Element",root);
        for(int i=0;i<uiElementsNodeList.getLength();i++)
        {
        	Element uiElement=(Element)uiElementsNodeList.item(i);
        	String name =uiElement.getAttribute("name");
        	String function =uiElement.getAttribute("function");
        	String type =uiElement.getAttribute("type");
        	mUIElementInfoMap.put(name, new UIElementInfo(name,function,type));
        }
	}
}
