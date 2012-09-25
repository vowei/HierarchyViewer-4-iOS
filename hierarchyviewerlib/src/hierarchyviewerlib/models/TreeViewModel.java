/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 * A Part of source code come from "Android Open Source Project" 
 */


package hierarchyviewerlib.models;

import java.util.ArrayList;
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
    
	private final ArrayList<ITreeChangeListener> mTreeChangeListeners =
            new ArrayList<ITreeChangeListener>();
	
    public void setData(DrawableViewNode tree) {
        synchronized (this) {
        	mTree=tree;
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
    }

    public void setTimeout(int interval)
    {
    		
    }
  }
