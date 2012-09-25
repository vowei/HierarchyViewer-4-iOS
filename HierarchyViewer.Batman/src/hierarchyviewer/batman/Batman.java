package hierarchyviewer.batman;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Batman {
	
	public static void main(String args[]) throws IOException, InterruptedException
	{
		String homeStr = System.getProperty("user.home");
		String dataFolder = homeStr+File.separator+"hierarchyviewerdata";
		/*File f = new File(dataFolder+File.separator+"logfile");	
		if(!f.exists())
		{
			f.createNewFile();
		}
		BufferedWriter bufferedWriter =new BufferedWriter(new FileWriter(f,true));
		for(String s:args)
		{
			bufferedWriter.write(s);
			bufferedWriter.write("\r\n");
		}
		bufferedWriter.flush();
		bufferedWriter.close();*/
		
		if(args[0].equalsIgnoreCase("getport"))
		{
			String portFileStr = dataFolder+File.separator+"port";
			File portFile=new File(portFileStr);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(portFile));
			String portStr = bufferedReader.readLine();
			System.out.print(portStr);
			return ;
		}
		else if(args[0].equalsIgnoreCase("getcommand"))
		{
			String portStr = args[1];
			Socket socket = new Socket();
		    int port = Integer.parseInt(portStr);
		    socket.connect(new InetSocketAddress("127.0.0.1", port), 9 * 1000);
		    
		    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		    for(int i=0;i<90;i++)
		    {
		    	out.write("{\"type\":\"getcommand\"}");
		    	out.newLine();
		    	out.flush();
		    	String line = in.readLine();
		    	if(line.equalsIgnoreCase("getelement"))
		    	{
		    		System.out.print("getelement");
		    		break;
		    	}
		    	Thread.sleep(100);
		    }
		    //out.close();
		    //in.close();
		    socket.close();
			return ;
		}
		else if (args[0].equalsIgnoreCase("sendelement"))
		{
			String portStr = args[2];
			Socket socket = new Socket();
		    int port = Integer.parseInt(portStr);
		    socket.connect(new InetSocketAddress("127.0.0.1", port), 9 * 1000);
		    //System.out.print("getelement");
		    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		    //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
		    out.write(args[1]);
		    out.newLine();
		    out.flush();
		    //out.close();
		    socket.close();
		    return;
		}
	}
}
