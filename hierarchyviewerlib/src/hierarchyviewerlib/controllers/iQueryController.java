/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */


package hierarchyviewerlib.controllers;

import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.uiutilities.DrawableViewNode;

import java.util.ArrayList;
import java.util.List;

import cc.iqa.iquery.IPseudoAttribute;
import cc.iqa.iquery.ITreeNode;
import cc.iqa.iquery.iQueryIdeParser;
import cc.iqa.iquery.iQueryParser;

public class iQueryController {
	
	private static ArrayList<IIQueryInsertListener> mIQueryInsertListeners =
            new ArrayList<IIQueryInsertListener>();
	
	private static IIQueryInsertListener[] getIQueryInsertListenerList() {
		IIQueryInsertListener[] listeners = null;
        synchronized (mIQueryInsertListeners) {
            if (mIQueryInsertListeners.size() == 0) {
                return null;
            }
            listeners =
            		mIQueryInsertListeners.toArray(new IIQueryInsertListener[mIQueryInsertListeners.size()]);
        }
        return listeners;
    }
	
	public static void notifyIQueryInserted(String iQueryElement) {
		IIQueryInsertListener[] listeners = getIQueryInsertListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].insertIQuery(iQueryElement);
            }
        }
    }
	
	public static void addIQueryInsertListener(IIQueryInsertListener listener) {
        synchronized (mIQueryInsertListeners) {
        	mIQueryInsertListeners.add(listener);
        }
    }

    public static void removeIQueryInsertListener(IIQueryInsertListener listener) {
        synchronized (mIQueryInsertListeners) {
        	mIQueryInsertListeners.remove(listener);
        }
    }
	
	static public boolean isLegal(String text)
	{
		try {
			iQueryParser parser = iQueryIdeParser.createParser(text);
			if(parser.getErrors().size()==0)
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch (Exception e) {
			//TODO exception type
			throw new RuntimeException(e.toString());
		} 
	}
	
	private static void registerPseudoAttributes(iQueryParser parser) {
		parser.registerPseudoAttribute("top", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":top").getValue();
			}
		});
		parser.registerPseudoAttribute("left", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":left").getValue();
			}
		});
		parser.registerPseudoAttribute("bottom", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":bottom").getValue();
			}
		});
		parser.registerPseudoAttribute("right", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":right").getValue();
			}
		});
		parser.registerPseudoAttribute("width", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":width").getValue();
			}
		});
		parser.registerPseudoAttribute("height", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":height").getValue();
			}
		});
	}
	
	static public List<String> query(String iquery,boolean fromSelectedNode, List<ITreeNode> outTreeNodeList)
	{
		List<String> errors;
		try {
			DrawableViewNode tree = TreeViewModel.getModel().getTree();
			if(tree==null)
			{
				errors = new ArrayList<String>();
				errors.add("当前没有加载控件树");
				return errors;
			}
			
			ITreeNode startNode=TreeViewModel.getModel().getTree().viewNode;
			if(fromSelectedNode&&TreeViewModel.getModel().getSelection()!=null)
			{
				startNode=TreeViewModel.getModel().getSelection().viewNode;
			}
			
			List<ITreeNode> candidates =new ArrayList<ITreeNode>();
			candidates.add(startNode);
			iQueryParser parser = iQueryIdeParser.createParser(iquery,false);
			registerPseudoAttributes(parser);
			outTreeNodeList.addAll(parser.query(candidates));
			errors= parser.getErrors();
			return errors;
		} catch (Exception e) {
			//TODO exception type
			throw new RuntimeException(e.toString());
		}
	}
	
	public interface IIQueryInsertListener
	{
		void insertIQuery(String elment);
	}
	
}
