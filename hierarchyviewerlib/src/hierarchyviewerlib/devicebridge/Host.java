package hierarchyviewerlib.devicebridge;

import hierarchyviewerlib.devicebridge.DeviceMessage.IMessageReceiveListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
	int mActaulPort=8049;
	ServerSocket mServerSocket;
	DeviceMessage mDeviceMessage;
	IMessageReceiveListener mMessageReceiveListener;
	
	public Host(DeviceMessage deviceMessage)
	{
		mDeviceMessage = deviceMessage;
		for(;mActaulPort<8060;mActaulPort++)
		{
			try {
				mServerSocket=new ServerSocket(mActaulPort);
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(mServerSocket==null)
		{
			throw new RuntimeException("Cannot create socket host");
		}
		
	}
	
	public void setMessageReceiveListener(IMessageReceiveListener messageReceiveListener)
	{
		mMessageReceiveListener = messageReceiveListener;
	}
	
	
	public void start()
	{
		Runnable socketListener = new Runnable()
		{

			@Override
			public void run() {
				try
				{
					Thread workThread=null;
					while(!mServerSocket.isClosed())
					{
						Socket client = mServerSocket.accept();
						ClientHandler clientHandle=new ClientHandler(client);
						if(workThread!=null&&workThread.isAlive())
						{
							workThread.interrupt();
						}
						workThread = new Thread(clientHandle);
						workThread.start();
					}
				}
				catch(Exception e)
				{
				}
			}
		};
		new Thread(socketListener).start();
	}
	
	public int getPort()
	{
		return mActaulPort;
	}
	
	public boolean isClosed()
	{
		return mServerSocket.isClosed();
	}
	
	public void close()
	{
		try {
			mServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class ClientHandler implements Runnable
	{
		private Socket client;
		
		public ClientHandler(Socket client)
		{
			this.client=client;
		}
		
		@Override
		public void run() {
			try
			{
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
				//for test
				//mDeviceMessage.pushOutputMessage("getelement");
				while(true)
				{
					String str = in.readLine();
					if(str==null)
						break;
					if(str.equalsIgnoreCase("{\"type\":\"getcommand\"}"))
					{
						String message = mDeviceMessage.popOutputMessage();
						if(message==null)
						{
							out.write("null");
						}
						else
						{
							out.write(message);
						}
						out.newLine();
						out.flush();
					}
					else
					{
						mDeviceMessage.pushInputMessage(str);
						mMessageReceiveListener.messageReceived();
					}
				}				
			}
			catch(Exception e)
			{		
			}
		}
		
	}
}
