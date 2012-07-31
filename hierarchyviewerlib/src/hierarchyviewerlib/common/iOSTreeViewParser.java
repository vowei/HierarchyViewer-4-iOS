package hierarchyviewerlib.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;

import hierarchyviewerlib.uiutilities.DrawableViewNode;

import com.dd.plist.*;

public class iOSTreeViewParser extends TreeViewParserBase {

	public static final String MISCELLANIOUS = "miscellaneous";
	private int protocolVersion=2;
	
	@Override
	public DrawableViewNode ParseTreeNode(NSDictionary rootDict,String imagePath) {

		ViewNode currentNode=null;
		HashMap<ViewNode,NSDictionary> nodeMap=new HashMap<ViewNode,NSDictionary> ();
		Stack<ViewNode> nodeStack=new Stack<ViewNode>();
		String message=null;
		try {
			//skip UIATarget node;
			rootDict  =(NSDictionary)((NSArray)rootDict.objectForKey("children")).objectAtIndex(0);
			rootDict  =(NSDictionary)((NSArray)rootDict.objectForKey("children")).objectAtIndex(0);
			message=rootDict.objectForKey("Message").toString();
			currentNode=CreateViewNodeFromString(currentNode,message);
			nodeMap.put(currentNode, rootDict);
			nodeStack.push(currentNode);
			while(nodeStack.size()>0)
			{
				currentNode=nodeStack.pop();
				NSDictionary currentNSDict=nodeMap.get(currentNode);
				
				NSObject[] children = ((NSArray)currentNSDict.objectForKey("children")).getArray();
				ViewNode parent=currentNode;
				
				for(NSObject nsObject:children)
				{
					NSDictionary nsDictionary=(NSDictionary)nsObject;
					message=nsDictionary.objectForKey("Message").toString();
					currentNode=CreateViewNodeFromString(parent,message);
					nodeMap.put(currentNode, nsDictionary);
					nodeStack.push(currentNode);
				}
			}
			
			while (currentNode.parent != null) {
	            currentNode = currentNode.parent;
	        }
	        
	        currentNode.protocolVersion = protocolVersion;
	       
	        currentNode.setViewCount();
	        
	      //set alias for the node id="NO_ID" (infact, for iOS, all id="NO_ID")
	        setNodePath(currentNode);
	        
	        DrawableViewNode root = new DrawableViewNode(currentNode);
	        root.setScreenShot(imagePath);
	        root.setLeft();
            root.placeRoot();
            
	        return root;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	//UIAStaticText: name:More value:More rect:{{135, 28}, {49, 27}}
	@Override
	public ViewNode CreateViewNodeFromString(ViewNode parent, String data)
	{ 
		//
		ViewNode currentNode=new ViewNode();
		currentNode.parent = parent;
        if (currentNode.parent != null) {
        	currentNode.parent.children.add(currentNode);
        }
		
		int index = data.indexOf(':');
        currentNode.type = data.substring(0, index);
        
        currentNode.index=0;
        if(currentNode.parent != null){
        	//skip currentNode itself
        	for(int i=currentNode.parent.children.size()-2;i>=0;i--){
        		ViewNode brother = currentNode.parent.children.get(i);
        		if(currentNode.type.equalsIgnoreCase(brother.type)){
        			currentNode.index=brother.index+1;
        			break;
        		}
        	}
        }
		currentNode.elementIndex = currentNode.parent == null ? 0 : currentNode.parent.children.size()-1;
        
        //add index, element index and type to properties
        ViewNode.Property property = new ViewNode.Property();
        property.name="type";
        property.value=currentNode.type;
        currentNode.properties.add(property);
        currentNode.namedProperties.put(property.name, property);
        
        property = new ViewNode.Property();
        property.name=":index";
        property.value=Integer.toString(currentNode.index);
        currentNode.properties.add(property);
        currentNode.namedProperties.put(property.name, property);
        
        property = new ViewNode.Property();
        property.name=":elementIndex";
        property.value=Integer.toString(currentNode.elementIndex);
        currentNode.properties.add(property);
        currentNode.namedProperties.put(property.name, property);
		
		int start = index+2;
		String key="";
		String value="";
		index=data.indexOf(':', start);
		while(index>0)
		{
			key=data.substring(start,index);
			start=index+1;
			//get second ':' position
			index=data.indexOf(':', start);
			//if have next section, use ' ' to get value 
			if(index>0)
			{
				index=data.lastIndexOf(' ', index);
				value=data.substring(start,index);
				loadProperties(currentNode,key,value);
			}
			else
			{
				value=data.substring(start);
				loadProperties(currentNode,key,value);
				break;
			}
			start=index+1;
			index=data.indexOf(':', start);
		}
		
		currentNode.left=getInt(currentNode.namedProperties,":left",0);
		currentNode.top=getInt(currentNode.namedProperties,":top",0);
		currentNode.width=getInt(currentNode.namedProperties,":width",0);
		currentNode.height=getInt(currentNode.namedProperties,":height",0);
		
		if(currentNode.parent!=null)
		{
			int parentLeft = getInt(currentNode.parent.namedProperties,":left",0);
			int parentTop = getInt(currentNode.parent.namedProperties,":top",0);
			currentNode.left-=parentLeft;
			currentNode.top-=parentTop;
		}
		
		Collections.sort(currentNode.properties, new Comparator<ViewNode.Property>() {
            public int compare(ViewNode.Property source, ViewNode.Property destination) {
                return source.name.compareTo(destination.name);
            }
        });
		
		return currentNode;
	}
	
	private void loadProperties(ViewNode currentNode, String key, String value) {
		
		ViewNode.Property property=null;
		if(key.equalsIgnoreCase("rect"))
		{
			int start=2;
			int index=value.indexOf(',');
			String left=value.substring(start,index).trim();
			property = new ViewNode.Property();
			property.name=":left";
			property.value=left;
			currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
			
			start=index+1;
			index=value.indexOf('}',start);
			String top=value.substring(start,index).trim();
			property = new ViewNode.Property();
			property.name=":top";
			property.value=top;
			currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
			
			start=value.indexOf('{', index)+1;
			index=value.indexOf(',',start);
			String width=value.substring(start,index).trim();
			property = new ViewNode.Property();
			property.name=":width";
			property.value=width;
			currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
			
			start=index+1;
			index=value.indexOf('}',start);
			String height=value.substring(start,index).trim();
			property = new ViewNode.Property();
			property.name=":height";
			property.value=height;
			currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
            
            property = new ViewNode.Property();
			property.name=":bottom";
			property.value=top+height;
			currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
            
            property = new ViewNode.Property();
			property.name=":right";
			property.value=left+width;
			currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
		}

		property = new ViewNode.Property();
		property.name=key;
		property.value=value;
		currentNode.properties.add(property);
        currentNode.namedProperties.put(property.name, property);

	}
	
	
}
