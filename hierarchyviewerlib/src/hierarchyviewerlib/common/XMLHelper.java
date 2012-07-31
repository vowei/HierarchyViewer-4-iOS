package hierarchyviewerlib.common;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHelper {

	 public static Node selectSingleNode(String express, Object source) {//查找节点，并返回第一个符合条件节点
	        Node result=null;
	        XPathFactory xpathFactory=XPathFactory.newInstance();
	        XPath xpath=xpathFactory.newXPath();
	        try {
	            result=(Node) xpath.evaluate(express, source, XPathConstants.NODE);
	        } catch (XPathExpressionException e) {
	            // TODO e.printStackTrace();
	        }
	        
	        return result;
	    }
	    
	    public static NodeList selectNodes(String express, Object source) {//查找节点，返回符合条件的节点集。
	        NodeList result=null;
	        XPathFactory xpathFactory=XPathFactory.newInstance();
	        XPath xpath=xpathFactory.newXPath();
	        try {
	            result=(NodeList) xpath.evaluate(express, source, XPathConstants.NODESET);
	        } catch (XPathExpressionException e) {
	            e.printStackTrace();
	        }
	        
	        return result;
	    }	
}
