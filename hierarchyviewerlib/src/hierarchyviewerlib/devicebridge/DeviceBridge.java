package hierarchyviewerlib.devicebridge;

import hierarchyviewerlib.common.FileHelper;
import hierarchyviewerlib.common.TreeViewParserBase;
import hierarchyviewerlib.common.iOSJsonFileNodeParser;
import hierarchyviewerlib.devicebridge.DeviceMessage.IMessageReceiveListener;
import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.uiutilities.DrawableViewNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DeviceBridge implements IMessageReceiveListener {
	
	final public String scriptFolder="";
	final public String jsFile="";
	final public String batmanFile="";
	public String resultPath="";
	public String appPath="";
	public DeviceMessage mDeviceMessage=null;
	private static DeviceBridge sBridge;
	private boolean initialized = false;
	private Host mHost;
	private String hierarchyviewerdataFolder="";
	private String mResultPath="";
	private TreeViewParserBase treeViewParser;
	
	static public DeviceBridge getBridge()
	{
		if(sBridge==null)
		{
			sBridge = new DeviceBridge();
		}
		
		return sBridge;
	}
	
	public void requestElementTree()
	{
		mDeviceMessage.pushOutputMessage("getelement");
	}
	
	public DeviceBridge()
	{
		//prepare hierarchyviewerdata folder
		checkCreateHierarchyviewerdataFolder();
		
		//create Message queue
		mDeviceMessage=new DeviceMessage();
		
		//create parser
		treeViewParser = new iOSJsonFileNodeParser();
	}
	
	private void checkCreateHierarchyviewerdataFolder() 
	{
		String homeStr = System.getProperty("user.home");
		hierarchyviewerdataFolder = homeStr+File.separator+"hierarchyviewerdata";
		File f = new File(hierarchyviewerdataFolder);
		if(!f.exists())
		{
			f.mkdirs();
		}
		
		//source
		Bundle bundle = Platform.getBundle("hierarchyviewerlib"); 
		URL url = bundle.getResource("hierarchyviewerdata");
		URL fileURL=null;
		try {
			fileURL = FileLocator.toFileURL(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String sourceDataFolder = fileURL.getPath();
		
		File batmanFile=new File(hierarchyviewerdataFolder+File.separator+"batman.app");
		if(!batmanFile.exists())
		{
			FileHelper.copyDirectory(sourceDataFolder+File.separator+"batman.app", hierarchyviewerdataFolder+File.separator+"batman.app");
			FileHelper.setFolderPermission(hierarchyviewerdataFolder+File.separator+"batman.app", "764");
		}
		
		File runtimejsFile = new File(hierarchyviewerdataFolder+File.separator+"runtime.js");
		if(!runtimejsFile.exists())
		{
			FileHelper.copyFile(sourceDataFolder+File.separator+"runtime.js", hierarchyviewerdataFolder+File.separator+"runtime.js");
		}
		
		//create result folder
		File resultFolder =new File(hierarchyviewerdataFolder+File.separator+"result");
		if(!resultFolder.exists())
		{
			resultFolder.mkdirs();
		}
	}
	
	public void close() 
	{
		try
		{
			if(initialized)
			{
				if(!mHost.isClosed())
				{
					mHost.close();
				}
				
				stopInstruments();
			}
			
			initialized=false;
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void start(String appPath)
	{
		try
		{
			//close the old app and host
			this.close();
			
			//start host first
			 mHost=new Host(mDeviceMessage);
			 mHost.setMessageReceiveListener(this);
			 mHost.start();
			 
			 int port = mHost.getPort();
			 
			 //write the port into ~/hierarchyviewerdata/port file
			 notifySocketPort(port,hierarchyviewerdataFolder+File.separator+"port");
			 
			 //start instruments
			 //1,get result folder
			 mResultPath = getResultFolder();
			 String scriptPath = hierarchyviewerdataFolder+File.separator+"runtime.js";
			 startInstruments(appPath,mResultPath,scriptPath);
			 
			 initialized=true;
		}
		catch(IOException e)
		{
			
		}
	}
	
	private void startInstruments(String appPath,String resultPath, String scriptPath) throws IOException
	{
		final String TestScriptFormat = "/usr/bin/instruments -t /Developer/Platforms/iPhoneOS.platform/Developer/Library/Instruments/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate " 
				+ "\"%1s\" -e UIASCRIPT \"%2s\" -e UIARESULTSPATH \"%3s\"";
		String startAppCmd = String.format(TestScriptFormat,appPath, scriptPath, resultPath);
		String[]  cmd = {"/usr/bin/instruments","-t","/Developer/Platforms/iPhoneOS.platform/Developer/Library/Instruments/PlugIns/AutomationInstrument.bundle/Contents/Resources/Automation.tracetemplate",
				appPath,"-e","UIASCRIPT",scriptPath,"-e", "UIARESULTSPATH",resultPath};
		Runtime.getRuntime().exec(cmd);
		//Runtime.getRuntime().exec(startAppCmd);
	}
	
	private void stopInstruments() throws IOException
	{
		Runtime.getRuntime().exec("killall -9 instruments");
	}
	
	private String getResultFolder()
	{
		java.io.File currentDir = new java.io.File("");
		String path = currentDir.getAbsolutePath()+File.separator+"Log";
		Date date = new Date();
		String year=String.valueOf(date.getYear()+1900);
		String month=String.valueOf(date.getMonth()+1);
		month=month.length()==1?"0"+month:month;
		String day=String.valueOf(date.getDate());
		day=day.length()==1?"0"+day:day;
		String hours = String.valueOf(date.getHours());
		hours=hours.length()==1?"0"+hours:hours;
		String minutes = String.valueOf(date.getMinutes());
		minutes=minutes.length()==1?"0"+minutes:minutes;
		String seconds = String.valueOf(date.getSeconds());
		seconds=seconds.length()==1?"0"+seconds:seconds;
		
		String folder= String.format("%s%s%s%s%s%s", 
				year,month,day,hours,minutes,seconds);
		String resultFolder = hierarchyviewerdataFolder+File.separator+"result"+File.separator+folder;
		File resultFolderFile = new File(resultFolder);
		resultFolderFile.mkdirs();
		return resultFolder;
	}
	
	//write the socket port 
	private void notifySocketPort(int port,String path) throws IOException
	{
		File f = new File(path);
		if(!f.exists())
		{
			f.createNewFile();
		}
		BufferedWriter bufferedWriter =new BufferedWriter(new FileWriter(f,false));
		bufferedWriter.write(String.valueOf(port));
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	@Override
	public void messageReceived() {
		// TODO Auto-generated method stub
		String message= mDeviceMessage.popInputMessage();
		if(message==null||message.isEmpty())
		{
			return;
		}
		
		JsonParser parser = new JsonParser();
		JsonObject o =(JsonObject)parser.parse(message);
		String imageName = o.get("screenshot").getAsString()+".png";
		String imagePath = mResultPath + File.separator + "Run 1"+File.separator+imageName;
		JsonObject treeJsonObject = o.getAsJsonObject("Tree");
		DrawableViewNode tree = this.treeViewParser.ParseTreeNode(treeJsonObject,imagePath);
		TreeViewModel.getModel().setData(tree);
	}
	
}
