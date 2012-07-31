package hierarchyviewerlib.controllers;

import java.util.ArrayList;
import java.util.List;

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
			iQueryParser parser = iQueryIdeParser.parse(text);
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
	
	static public List<String> getSyntaxErrors(String iquery)
	{
		try {
			iQueryParser parser = iQueryIdeParser.parse(iquery);
			List<String> errors= parser.getErrors();
			return errors;
		} catch (Exception e) {
			//TODO exception type
			throw new RuntimeException(e.toString());
		}
	}
	
	static public String getSyntaxError(String iquery)
	{
		try {
			iQueryParser parser = iQueryIdeParser.parse(iquery);
			List<String> errors= parser.getErrors();
			if(errors.size()==0)
			{
				return "正确";
			}
			else
			{
				String message="";
				for(String error:errors)
				{
					message+=error;
				}
				return message;
			}
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
