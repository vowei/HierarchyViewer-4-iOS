/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 * A Part of source code come from "Android Open Source Project" 
 */


package hierarchyviewerlib.models;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

import hierarchyviewerlib.common.*;
import hierarchyviewerlib.uiutilities.DrawableViewNode;
import hierarchyviewerlib.uiutilities.DrawableViewNode.Point;
import hierarchyviewerlib.uiutilities.DrawableViewNode.Rectangle;


public class TreeViewModel {
	
	private static TreeViewModel sModel;
	
	public static final double MAX_ZOOM = 2;

    public static final double MIN_ZOOM = 0.2;

    private DrawableViewNode mTree;

    private DrawableViewNode mSelectedNode;

    private Rectangle mViewport;

    private double mZoom;

    private TreeViewParserBase mTreeViewPraser;
	
    private List<LogNode> mLogNodeList;
    
	private final ArrayList<ITreeChangeListener> mTreeChangeListeners =
            new ArrayList<ITreeChangeListener>();
	
	private String mLogFilePath="";
	
	private String mLogFileDirectory="";
	
	public void refreshLogFile()
	{
		if(mLogFilePath.isEmpty())
			return;
		loadLogFile(mLogFilePath);
	}
	
	
	public String getLogFilePath()
	{
		return mLogFilePath;
	}
	
	public void loadLogFile(String path)
	{
		File logFile =new File(path);
		//获取日志绝对路径和所在目录
		mLogFilePath=logFile.getAbsolutePath();
		mLogFileDirectory=logFile.getParentFile().getAbsolutePath();
		
		try {
			NSDictionary rootDict = (NSDictionary)PropertyListParser.parse(logFile);
			NSObject[] children = ((NSArray)rootDict.objectForKey("All Samples")).getArray();
			List<LogNode> logNodeList =new ArrayList<LogNode>();
			for(NSObject child:children)
			{
				if(!(child instanceof NSDictionary))
				{
					continue;
				}
				NSDictionary dict = (NSDictionary)child;
				String message=dict.objectForKey("Message").toString();
				//todo add comments
				if(message.startsWith("UIATarget: name:"))
				{
					LogNode logNode=new LogNode();
					logNode.nsDict=dict;
					logNode.nodeName=message;
					logNodeList.add(logNode);
				}
			}
			
			Hashtable<String,List<String>> fileNameTable =new Hashtable<String,List<String>>();
			
			//获取每个节点对应的图片和图片目录名
			for(int i=logNodeList.size()-1;i>=0;i--)
			{
				LogNode logNode=logNodeList.get(i);
				if(!fileNameTable.containsKey(logNode.nodeName))
				{
					String filePrefix=logNode.nodeName.replace(':', '-');
					List<String> sameNameFiles=getFilesWithPrefix(filePrefix,mLogFileDirectory);
					fileNameTable.put(logNode.nodeName, sameNameFiles);
				}
				
				List<String> sameNameFiles = fileNameTable.get(logNode.nodeName);
				if(sameNameFiles.size()==0)
				{
					logNode.name=null;
				}
				else
				{
					//keypoint:
					//由于我们是从最后一个节点开始遍历
					//所以节点对应的图片名的后缀应该是数字最大的
					//(sameNameFiles里面的文件名是按照后缀的数字从小到大排序的)
					//所以我们总是取sameNameFiles中最后一个元素
					String name = sameNameFiles.remove(sameNameFiles.size()-1);
					logNode.name=name;
				}

			}
			
			//把plist节点翻译为ViewNode节点
			for(LogNode logNode:logNodeList)
			{
				String imagePath= mLogFileDirectory+File.separator+logNode.name+".png";

				logNode.drawableViewNode = mTreeViewPraser.ParseTreeNode(logNode.nsDict, imagePath);
			}
			this.mLogNodeList=logNodeList;
			setCurrentData(null);
			notifyLogFileChanged();
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	
	//get files with prefix
	//in the result, all file's extension have been removed
	private List<String> getFilesWithPrefix(final String fileNamePrefix,String directory)
	{
		File file =new File(directory);
		String[] files = file.list(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				//取.png文件
				if(!name.endsWith(".png"))
				{
					return false;
				}
				//去后缀
				String name1= name.substring(0, name.length()-4);
				if( !name1.startsWith(fileNamePrefix))
				{
					return false;
				}	
				//如果文件名后面没有加数字后缀,true
				if(fileNamePrefix.equalsIgnoreCase(name1))
				{
					return true;
				}
				
				//如果文件名后缀是数字,true; 不是数字,false
				String numStr=name1.substring(fileNamePrefix.length()).trim();
				try
				{
					Integer.parseInt(numStr);
					return true;
				}
				catch(NumberFormatException e)
				{
					return false;
				}
			}
			});
		
		List<String> fileList =new ArrayList<String>();
		for(String f:files)
		{
			//去除.png后缀
			fileList.add(f.substring(0, f.length()-4));
		}
		
		
		Collections.sort(fileList, new Comparator<String>(){

			@Override
			public int compare(String o1, String o2) {
				
				if(o1.equalsIgnoreCase(fileNamePrefix))
				{
					return -1;
				}
				
				if(o2.equalsIgnoreCase(fileNamePrefix))
				{
					return 1;
				}
				String numStr1=o1.substring(fileNamePrefix.length()).trim();
				String numStr2=o2.substring(fileNamePrefix.length()).trim();
				int num1= Integer.parseInt(numStr1);
				int num2= Integer.parseInt(numStr2);
				return num1-num2;
			}});
		return fileList;
	}
	
	public List<String> getViewTreeNames()
	{
		List<String> names=new ArrayList<String>();
		 synchronized (this) {
			 for(LogNode logNode:this.mLogNodeList)
			 {
				 names.add(logNode.name);
			 }
		 }
		 return names;
	}
	
    public void setCurrentData(String name) {
        synchronized (this) {
        	
        	if(name==null||name.isEmpty())
        	{
        		mTree=null;
        	}
        	
        	for(LogNode logNode: this.mLogNodeList)
        	{
        		if(logNode.name.equalsIgnoreCase(name))
        		{
        			mTree=logNode.drawableViewNode;
        			break;
        		}
        	}
        	
            mViewport = null;
            mZoom = 1;
            mSelectedNode = null;
        }
        notifyTreeChanged();
    }
    
	// TODO public static void Initialize(String address)
	public static void Initialize()
	{
		//TODO build web service connection;
		
		sModel = new TreeViewModel();
		sModel.mTreeViewPraser=new iOSTreeViewParser();
	}

	public void setViewTreeParser(TreeViewParserBase TreeViewParserBase)
	{
		this.mTreeViewPraser=TreeViewParserBase;
	}
	
	public TreeViewParserBase getViewTreeParser()
	{
		return mTreeViewPraser;
	}
	
    public static TreeViewModel getModel() {
        if (sModel == null) {
        	Initialize();
            //TODO throw exception to ask use build web service connection first
        }
        return sModel;
    }

    public void setSelection(DrawableViewNode selectedNode) {
        synchronized (this) {
            this.mSelectedNode = selectedNode;
        }
        notifySelectionChanged();
    }
    
    public void setViewport(Rectangle viewport) {
        synchronized (this) {
            this.mViewport = viewport;
        }
        notifyViewportChanged();
    }
    
    public void setZoom(double newZoom) {
        Point zoomPoint = null;
        synchronized (this) {
            if (mTree != null && mViewport != null) {
                zoomPoint =
                        new Point(mViewport.x + mViewport.width / 2, mViewport.y + mViewport.height / 2);
            }
        }
        zoomOnPoint(newZoom, zoomPoint);
    }

    public void zoomOnPoint(double newZoom, Point zoomPoint) {
        synchronized (this) {
            if (mTree != null && this.mViewport != null) {
                if (newZoom < MIN_ZOOM) {
                    newZoom = MIN_ZOOM;
                }
                if (newZoom > MAX_ZOOM) {
                    newZoom = MAX_ZOOM;
                }
                mViewport.x = zoomPoint.x - (zoomPoint.x - mViewport.x) * mZoom / newZoom;
                mViewport.y = zoomPoint.y - (zoomPoint.y - mViewport.y) * mZoom / newZoom;
                mViewport.width = mViewport.width * mZoom / newZoom;
                mViewport.height = mViewport.height * mZoom / newZoom;
                mZoom = newZoom;
            }
        }
        notifyZoomChanged();
    }
    
    public DrawableViewNode getTree() {
        synchronized (this) {
            return mTree;
        }
    }
    
    public Rectangle getViewport() {
        synchronized (this) {
            return mViewport;
        }
    }

    public double getZoom() {
        synchronized (this) {
            return mZoom;
        }
    }

    public DrawableViewNode getSelection() {
        synchronized (this) {
            return mSelectedNode;
        }
    }
    
    private ITreeChangeListener[] getTreeChangeListenerList() {
        ITreeChangeListener[] listeners = null;
        synchronized (mTreeChangeListeners) {
            if (mTreeChangeListeners.size() == 0) {
                return null;
            }
            listeners =
                    mTreeChangeListeners.toArray(new ITreeChangeListener[mTreeChangeListeners.size()]);
        }
        return listeners;
    }
    
    public void notifyTreeChanged() {
        ITreeChangeListener[] listeners = getTreeChangeListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].treeChanged();
            }
        }
    }

    public void notifySelectionChanged() {
        ITreeChangeListener[] listeners = getTreeChangeListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].selectionChanged();
            }
        }
    }
    
    public void notifyViewportChanged() {
        ITreeChangeListener[] listeners = getTreeChangeListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].viewportChanged();
            }
        }
    }

    public void notifyZoomChanged() {
        ITreeChangeListener[] listeners = getTreeChangeListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].zoomChanged();
            }
        }
    }
    
    public void notifyLogFileChanged() {
        ITreeChangeListener[] listeners = getTreeChangeListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].logfileChanged();
            }
        }
    }

    
    public void addTreeChangeListener(ITreeChangeListener listener) {
        synchronized (mTreeChangeListeners) {
            mTreeChangeListeners.add(listener);
        }
    }

    public void removeTreeChangeListener(ITreeChangeListener listener) {
        synchronized (mTreeChangeListeners) {
            mTreeChangeListeners.remove(listener);
        }
    }
    
    public static interface ITreeChangeListener {
        public void treeChanged();

        public void selectionChanged();

        public void viewportChanged();

        public void zoomChanged();
        
        public void logfileChanged();
    }

    public void setTimeout(int interval)
    {
    		
    }
    
	public class LogNode
	{
		String name;
		NSDictionary nsDict;
		DrawableViewNode drawableViewNode;
		String nodeName;
	}
  }
