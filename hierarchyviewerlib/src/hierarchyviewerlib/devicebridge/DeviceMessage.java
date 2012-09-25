package hierarchyviewerlib.devicebridge;


public class DeviceMessage {
	String mOutputMessage;
	String mInputMessage;
	IMessageReceiveListener messageReceiveListener;
	
	public DeviceMessage()
	{
	}
	
	public String popOutputMessage()
	{
		synchronized (this) {
			String ret=mOutputMessage;
			mOutputMessage=null;
			return ret;
		}
	}
	
	public void pushOutputMessage(String message)
	{
		synchronized (this) {
			mOutputMessage=message;
		}
	}
	
	public String popInputMessage()
	{
		synchronized (this) {
			String ret=mInputMessage;
			mInputMessage=null;
			return ret;
		}
	}
	
	public void pushInputMessage(String message)
	{
		synchronized (this) {
			mInputMessage=message;
			if(messageReceiveListener!=null)
			{
				messageReceiveListener.messageReceived();
			}
		}
	}
	
	public void clear()
	{
		mOutputMessage=null;
		mInputMessage=null;
	}
	
	public interface IMessageReceiveListener
	{
		void messageReceived();
	}
}
