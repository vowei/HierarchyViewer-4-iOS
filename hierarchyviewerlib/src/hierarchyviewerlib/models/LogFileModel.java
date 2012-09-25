package hierarchyviewerlib.models;

import hierarchyviewerlib.common.TreeViewParserBase;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.common.iOSLogFileNodeParser;
import hierarchyviewerlib.uiutilities.DrawableViewNode;

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

public class LogFileModel {
	
	private TreeViewParserBase mTreeViewPraser;
	private String mLogFilePath="";
	private String mLogFileDirectory="";
	private List<LogNode> mLogNodeList;
	private static LogFileModel sModel;
	
	private final ArrayList<ILogFileChangeListener> mLogFileChangeListeners =
            new ArrayList<ILogFileChangeListener>();
	
	 public void addLogFileChangeListener(ILogFileChangeListener listener) {
	        synchronized (mLogFileChangeListeners) {
	        	mLogFileChangeListeners.add(listener);
	        }
	    }

	    public void removeLogFileChangeListener(ILogFileChangeListener listener) {
	        synchronized (mLogFileChangeListeners) {
	        	mLogFileChangeListeners.remove(listener);
	        }
	    }
	    
	// TODO public static void Initialize(String address)
		public static void Initialize()
		{
			//TODO build web service connection;
			
			sModel = new LogFileModel();
			sModel.mTreeViewPraser=new iOSLogFileNodeParser();
		}
		
	    public static LogFileModel getModel() {
	        if (sModel == null) {
	        	Initialize();
	            //TODO throw exception to ask use build web service connection first
	        }
	        return sModel;
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

		if (name == null || name.isEmpty()) {
			TreeViewModel.getModel().setData(null);
			return;
		}

		for (LogNode logNode : this.mLogNodeList) {
			if (logNode.name.equalsIgnoreCase(name)) {
				TreeViewModel.getModel().setData(logNode.drawableViewNode);
				return;
			}
		}
	}
	
	public ViewNode getViewNodeByLogName(String name) {

		if (name == null || name.isEmpty()) {
			TreeViewModel.getModel().setData(null);
			return null;
		}

		for (LogNode logNode : this.mLogNodeList) {
			if (logNode.name.equalsIgnoreCase(name)) {
				return logNode.drawableViewNode.viewNode;
			}
		}
		return null;
	}
	
	public void setViewTreeParser(TreeViewParserBase TreeViewParserBase)
	{
		this.mTreeViewPraser=TreeViewParserBase;
	}
	
	public TreeViewParserBase getViewTreeParser()
	{
		return mTreeViewPraser;
	}
	
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
	
	private ILogFileChangeListener[] getLogFileChangeListenerList() {
		ILogFileChangeListener[] listeners = null;
        synchronized (mLogFileChangeListeners) {
            if (mLogFileChangeListeners.size() == 0) {
                return null;
            }
            listeners =
            		mLogFileChangeListeners.toArray(new ILogFileChangeListener[mLogFileChangeListeners.size()]);
        }
        return listeners;
    }
	
	public void notifyLogFileChanged() {
		ILogFileChangeListener[] listeners = getLogFileChangeListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].logfileChanged();
            }
        }
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
	
	public class LogNode
	{
		String name;
		NSDictionary nsDict;
		DrawableViewNode drawableViewNode;
		String nodeName;
	}
	
	public static interface ILogFileChangeListener {    
        public void logfileChanged();
    }
}
