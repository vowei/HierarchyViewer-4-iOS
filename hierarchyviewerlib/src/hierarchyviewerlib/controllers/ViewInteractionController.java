package hierarchyviewerlib.controllers;

import java.util.ArrayList;

public class ViewInteractionController {
	
	static private ViewInteractionController sController=null;
	
	private ArrayList<IViewInteractionListener> mViewInteractionListeners =
            new ArrayList<IViewInteractionListener>();
	
	static public ViewInteractionController getController()
	{
		if(sController==null)
		{
			sController = new ViewInteractionController();
		}
		
		return sController;
	}
	
	 private IViewInteractionListener[] getViewInteractionListenerList() {
		 IViewInteractionListener[] listeners = null;
	        synchronized (mViewInteractionListeners) {
	            if (mViewInteractionListeners.size() == 0) {
	                return null;
	            }
	            listeners =
	            		mViewInteractionListeners.toArray(new IViewInteractionListener[mViewInteractionListeners.size()]);
	        }
	        return listeners;
	    }
	 
	public void SubmitInteractionTask(String taskName,Object value)
	{
		IViewInteractionListener[] listeners = getViewInteractionListenerList();
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                listeners[i].interactionTaskSubmited(taskName,value);
            }
        }
	}
	
	public void addViewInteractionListener(IViewInteractionListener listener) {
        synchronized (mViewInteractionListeners) {
        	mViewInteractionListeners.add(listener);
        }
    }

    public void removeViewInteractionListener(IViewInteractionListener listener) {
        synchronized (mViewInteractionListeners) {
        	mViewInteractionListeners.remove(listener);
        }
    }
	
	public interface IViewInteractionListener
	{
		public void interactionTaskSubmited(String taskName,Object value);
	}
}
