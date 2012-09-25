package hierarchyviewerlib.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

import hierarchyviewerlib.uiutilities.DrawableViewNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class iOSJsonFileNodeParser extends TreeViewParserBase {
	
	public static final String MISCELLANIOUS = "miscellaneous";
	private int protocolVersion=2;
	
	@Override
	public DrawableViewNode ParseTreeNode(Object treeObject,
			String imagePath) {
		 JsonObject rootJsonObject =(JsonObject)treeObject;
		 ViewNode currentNode=null;
		 
		HashMap<ViewNode,JsonObject> nodeMap=new HashMap<ViewNode,JsonObject> ();
		Stack<ViewNode> nodeStack=new Stack<ViewNode>();
		
		JsonObject propertiesJsonObject = (JsonObject)rootJsonObject.get("element");
		currentNode = CreateViewNodeFromString(null,propertiesJsonObject);
		nodeMap.put(currentNode, rootJsonObject);
		nodeStack.push(currentNode);
		
		while(nodeStack.size()>0)
		{
			currentNode = nodeStack.pop();
			JsonObject currentJsonObject = nodeMap.get(currentNode);
			JsonArray children = currentJsonObject.getAsJsonArray("children");
			ViewNode parent = currentNode;
			if(children!=null)
			{
				for(JsonElement element:children)
				{
					if(element==JsonNull.INSTANCE)
						continue;
					JsonObject jsonObject = (JsonObject)element;
					propertiesJsonObject=jsonObject.getAsJsonObject("element");
					currentNode = CreateViewNodeFromString(parent,propertiesJsonObject);
					nodeMap.put(currentNode, jsonObject);
					nodeStack.push(currentNode);
				}
			}
		}
			
		//find the root
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
	}

	@Override
	public ViewNode CreateViewNodeFromString(ViewNode parent, Object data1) {
		JsonObject propertiesJsonObject =(JsonObject)data1;
		
		ViewNode currentNode=new ViewNode();
		currentNode.descriptionStr=propertiesJsonObject.toString();
		currentNode.parent = parent;
        if (currentNode.parent != null) {
        	currentNode.parent.children.add(currentNode);
        }
        
        for(Entry<String,JsonElement> entry : propertiesJsonObject.entrySet())
        {
        	String key = entry.getKey();
        	String value = entry.getValue().getAsString();
        	ViewNode.Property property = new ViewNode.Property();
            property.name=key;
            property.value=value;
            currentNode.properties.add(property);
            currentNode.namedProperties.put(property.name, property);
        }
        currentNode.type=currentNode.namedProperties.get("type").value;
        
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
		
		ViewNode.Property property = new ViewNode.Property();
        property.name=":index";
        property.value=Integer.toString(currentNode.index);
        currentNode.properties.add(property);
        currentNode.namedProperties.put(property.name, property);
        
        property = new ViewNode.Property();
        property.name=":elementIndex";
        property.value=Integer.toString(currentNode.elementIndex);
        currentNode.properties.add(property);
        currentNode.namedProperties.put(property.name, property);
		
		return currentNode;
	}

}
