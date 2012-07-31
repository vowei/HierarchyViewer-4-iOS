package hierarchyviewerlib.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dd.plist.NSDictionary;

import hierarchyviewerlib.common.ViewNode.Property;
import hierarchyviewerlib.uiutilities.DrawableViewNode;

public abstract class TreeViewParserBase {
	
	abstract public DrawableViewNode ParseTreeNode(NSDictionary rootDict, String imagePath);
	
	public abstract ViewNode CreateViewNodeFromString(ViewNode parent, String data);
	
	protected Set<String> aliasNameSet = new HashSet<String>();
	
	protected int getInt(Map<String, Property> namedProperties, String name, int defaultValue) {
        Property p = namedProperties.get(name);
        if (p != null) {
            try {
                return Integer.parseInt(p.value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
	
	protected boolean getBoolean(Map<String, Property> namedProperties, String name, boolean defaultValue) {
        Property p = namedProperties.get(name);
        if (p != null) {
            try {
                return Boolean.parseBoolean(p.value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
	
	protected void setNodePath(ViewNode viewNode)
	    {
			if(viewNode.parent!=null)
			{
				viewNode.path+=viewNode.parent.path+" > ";
			}
			
			viewNode.path+= String.format("%s :eq(%d)", viewNode.type,viewNode.index);

			ViewNode.Property property=new Property();
			property.name=":path";
			property.value=viewNode.path;
			viewNode.properties.add(0, property);
			viewNode.namedProperties.put(property.name, property);
			
		    final int N = viewNode.children.size();
		    for (int i = 0; i < N; i++) {
		        ViewNode child = viewNode.children.get(i);
		        setNodePath(child);
		    }
	    }
}
