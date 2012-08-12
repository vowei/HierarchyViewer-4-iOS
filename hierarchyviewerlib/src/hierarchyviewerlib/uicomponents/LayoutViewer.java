/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 * A Part of source code come from "Android Open Source Project" 
 */

package hierarchyviewerlib.uicomponents;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;

import hierarchyviewerlib.actions.CopyFunctionCallByIndexAction;
import hierarchyviewerlib.actions.CopyFunctionCallByNameAction;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.controllers.ViewInteractionController;
import hierarchyviewerlib.controllers.ViewInteractionController.IViewInteractionListener;
import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.models.TreeViewModel.ITreeChangeListener;
import hierarchyviewerlib.uiutilities.DrawableViewNode;
import hierarchyviewerlib.uiutilities.DrawableViewNode.Point;

import java.util.ArrayList;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.ui.PlatformUI;

public class LayoutViewer extends Canvas implements ITreeChangeListener,IViewInteractionListener {

    private TreeViewModel mModel;

    private DrawableViewNode mTree;

    private DrawableViewNode mSelectedNode;
    
    private Point mSelectedPoint=new Point();

    private Transform mTransform;

    private Transform mInverse;

    private double mScale;

    private boolean mShowExtras = false;

    private boolean mOnBlack = true;
    
    private ToolTip mToolTip;
    
    private MenuManager menuManager =new MenuManager("#PopUpMenu");

    public LayoutViewer(Composite parent) {
        super(parent, SWT.NONE);
        mModel = TreeViewModel.getModel();
        mModel.addTreeChangeListener(this);
        ViewInteractionController.getController().addViewInteractionListener(this);

        addDisposeListener(mDisposeListener);
        addPaintListener(mPaintListener);
        addListener(SWT.Resize, mResizeListener);
        addMouseListener(mMouseListener);
        addMouseMoveListener(mMouseMoveListener);

        mTransform = new Transform(Display.getDefault());
        mInverse = new Transform(Display.getDefault());

        treeChanged();
        
        createContextMenu();
        
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        mToolTip = new ToolTip(shell,
                SWT.BALLOON );//SWT.ICON_INFORMATION
        mToolTip.setAutoHide(true); //自动隐藏气泡式提示文本
    }

    public void setShowExtras(boolean show) {
        mShowExtras = show;
        doRedraw();
    }

   /* public void setOnBlack(boolean value) {
        mOnBlack = value;
        doRedraw();
    }

    public boolean getOnBlack() {
        return mOnBlack;
    }*/

    private DisposeListener mDisposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
            mModel.removeTreeChangeListener(LayoutViewer.this);
            mTransform.dispose();
            mInverse.dispose();
            if (mSelectedNode != null) {
                mSelectedNode.viewNode.dereferenceImage();
            }
        }
    };

    private Listener mResizeListener = new Listener() {
        public void handleEvent(Event e) {
            synchronized (this) {
                setTransform();
            }
        }
    };
    
    private void createContextMenu(){
    	menuManager.addMenuListener(mMenuListener);
    	Menu menu=menuManager.createContextMenu(this);
    	this.setMenu(menu); 
    }
    
    private IMenuListener mMenuListener=new IMenuListener()
    {
    	public void menuAboutToShow(IMenuManager manager){
    		menuManager.setRemoveAllWhenShown(true);
    		//menuManager.add(new Separator("edit"));
    		ViewNode selectedViewNode=null;
    		if(mSelectedNode!=null)
    		{
    			selectedViewNode=mSelectedNode.viewNode;
    		}
    		
    		menuManager.add(new CopyFunctionCallByIndexAction(selectedViewNode));
    		menuManager.add(new CopyFunctionCallByNameAction(selectedViewNode));
    	}
    };
    
    private MouseMoveListener mMouseMoveListener= new MouseMoveListener() {
    	public void mouseMove(MouseEvent e) {
    		synchronized (LayoutViewer.this) {
                if (mTree != null) {
                    float[] pt = {
                            e.x, e.y
                    };
                    Point mouseOnPoint= new Point();
                    
                    mInverse.transform(pt);
                    mouseOnPoint.x=pt[0];
                    mouseOnPoint.y=pt[1];
                    
                    ViewInteractionController.getController().SubmitInteractionTask("CoordUpdate", mouseOnPoint);
                }
    	}
    }
    };
    
    private MouseListener mMouseListener = new MouseListener() {

        public void mouseDoubleClick(MouseEvent e) {
            if (mSelectedNode != null) {
/*                HierarchyViewerDirector.getDirector()
                        .showCapture(getShell(), mSelectedNode.viewNode);*/
            }
        }

        public void mouseDown(MouseEvent e) {
            boolean selectionChanged = false;
            DrawableViewNode newSelection = null;
            synchronized (LayoutViewer.this) {
                if (mTree != null) {
                    float[] pt = {
                            e.x, e.y
                    };
                    mInverse.transform(pt);
                    mSelectedPoint.x=pt[0];
                    mSelectedPoint.y=pt[1];
                    newSelection =
                            updateSelection(mTree, pt[0], pt[1], 0, 0, 0, 0, mTree.viewNode.width,
                                    mTree.viewNode.height);
                    if (mSelectedNode != newSelection) {
                        selectionChanged = true;
                    }
                }
            }
            if (selectionChanged) {
                mModel.setSelection(newSelection);
            }
        }

        public void mouseUp(MouseEvent e) {
        	if(e.button==3)
        	{
        	}
        }
    };

    private DrawableViewNode updateSelection(DrawableViewNode node, float x, float y, int left,
            int top, int clipX, int clipY, int clipWidth, int clipHeight) {
        if (!node.treeDrawn) {
            return null;
        }
        // Update the clip
        int x1 = Math.max(left, clipX);
        int x2 = Math.min(left + node.viewNode.width, clipX + clipWidth);
        int y1 = Math.max(top, clipY);
        int y2 = Math.min(top + node.viewNode.height, clipY + clipHeight);
        clipX = x1;
        clipY = y1;
        clipWidth = x2 - x1;
        clipHeight = y2 - y1;
        if (x < clipX || x > clipX + clipWidth || y < clipY || y > clipY + clipHeight) {
            return null;
        }
        final int N = node.children.size();
        for (int i = N - 1; i >= 0; i--) {
            DrawableViewNode child = node.children.get(i);
            DrawableViewNode ret =
                    updateSelection(child, x, y,
                            left + child.viewNode.left - node.viewNode.scrollX, top
                                    + child.viewNode.top - node.viewNode.scrollY, clipX, clipY,
                            clipWidth, clipHeight);
            if (ret != null) {
                return ret;
            }
        }
        return node;
    }

    private PaintListener mPaintListener = new PaintListener() {
        public void paintControl(PaintEvent e) {
            synchronized (LayoutViewer.this) {
                //if (mOnBlack) {
                //    e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                //} else {
                    e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                //}
             
                e.gc.fillRectangle(0, 0, getBounds().width, getBounds().height);
                if (mTree != null) {
                	
                    e.gc.setLineWidth((int) Math.ceil(0.3 / mScale));
                    e.gc.setTransform(mTransform);
                    //if (mOnBlack) {
                    //    e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                    //} else {
                        e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                    //}
                    Rectangle parentClipping = e.gc.getClipping();
                    e.gc.setClipping(0, 0, mTree.viewNode.width + (int) Math.ceil(0.3 / mScale),
                            mTree.viewNode.height + (int) Math.ceil(0.3 / mScale));
                    //add by binhua
                    if(mTree.mScreenShot!=null)
                    {
                    	//e.gc.drawImage(mTree.mScreenShot, 0, 0);
                    	e.gc.drawImage(mTree.mScreenShot, 0, 0, mTree.mScreenShot.getImageData().width, mTree.mScreenShot.getImageData().height,
                    			0, 0, mTree.viewNode.width, mTree.viewNode.height);
                    }
                    /////////
                    
                    paintRecursive(e.gc, mTree, 0, 0, true);
     
                    if (mSelectedNode != null) {
                        e.gc.setClipping(parentClipping);

                        // w00t, let's be nice and display the whole path in
                        // light red and the selected node in dark red.
                        ArrayList<Point> rightLeftDistances = new ArrayList<Point>();
                        int left = 0;
                        int top = 0;
                        DrawableViewNode currentNode = mSelectedNode;
                        while (currentNode != mTree) {
                            left += currentNode.viewNode.left;
                            top += currentNode.viewNode.top;
                            currentNode = currentNode.parent;
                            left -= currentNode.viewNode.scrollX;
                            top -= currentNode.viewNode.scrollY;
                            rightLeftDistances.add(new Point(left, top));
                        }
                        e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
                        currentNode = mSelectedNode.parent;
                        final int N = rightLeftDistances.size();
                        for (int i = 0; i < N; i++) {
                            e.gc.drawRectangle((int) (left - rightLeftDistances.get(i).x),
                                    (int) (top - rightLeftDistances.get(i).y),
                                    currentNode.viewNode.width, currentNode.viewNode.height);
                            currentNode = currentNode.parent;
                        }

                        if (mShowExtras && mSelectedNode.viewNode.image != null) {
                            e.gc.drawImage(mSelectedNode.viewNode.image, left, top);
                            if (mOnBlack) {
                                e.gc.setForeground(Display.getDefault().getSystemColor(
                                        SWT.COLOR_WHITE));
                            } else {
                                e.gc.setForeground(Display.getDefault().getSystemColor(
                                        SWT.COLOR_BLACK));
                            }
                            paintRecursive(e.gc, mSelectedNode, left, top, true);

                        }

                        e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                        e.gc.setLineWidth((int) Math.ceil(2 / mScale));
                        e.gc.drawRectangle(left, top, mSelectedNode.viewNode.width,
                                mSelectedNode.viewNode.height);
                    }
                }
            }
        }
    };

    private void paintRecursive(GC gc, DrawableViewNode node, int left, int top, boolean root) {
        if (!node.treeDrawn) {
            return;
        }
        // Don't shift the root
        if (!root) {
            left += node.viewNode.left;
            top += node.viewNode.top;
        }
        Rectangle parentClipping = gc.getClipping();
        int x1 = Math.max(parentClipping.x, left);
        int x2 =
                Math.min(parentClipping.x + parentClipping.width, left + node.viewNode.width
                        + (int) Math.ceil(0.3 / mScale));
        int y1 = Math.max(parentClipping.y, top);
        int y2 =
                Math.min(parentClipping.y + parentClipping.height, top + node.viewNode.height
                        + (int) Math.ceil(0.3 / mScale));

        // Clipping is weird... You set it to -5 and it comes out 17 or
        // something.
        if (x2 <= x1 || y2 <= y1) {
            return;
        }
        gc.setClipping(x1, y1, x2 - x1, y2 - y1);
        final int N = node.children.size();
        for (int i = 0; i < N; i++) {
            paintRecursive(gc, node.children.get(i), left - node.viewNode.scrollX, top
                    - node.viewNode.scrollY, false);
        }
        gc.setClipping(parentClipping);
        if (!node.viewNode.willNotDraw) {
            gc.drawRectangle(left, top, node.viewNode.width, node.viewNode.height);
        }

    }

    private void doRedraw() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                redraw();
            }
        });
    }

    private void setTransform() {
        if (mTree != null) {
            Rectangle bounds = getBounds();
            int leftRightPadding = bounds.width <= 30 ? 0 : 5;
            int topBottomPadding = bounds.height <= 30 ? 0 : 5;
            mScale =
                    Math.min(1.0 * (bounds.width - leftRightPadding * 2) / mTree.viewNode.width, 1.0
                            * (bounds.height - topBottomPadding * 2) / mTree.viewNode.height);
            int scaledWidth = (int) Math.ceil(mTree.viewNode.width * mScale);
            int scaledHeight = (int) Math.ceil(mTree.viewNode.height * mScale);

            mTransform.identity();
            mInverse.identity();
            mTransform.translate((bounds.width - scaledWidth) / 2.0f,
                    (bounds.height - scaledHeight) / 2.0f);
            mInverse.translate((bounds.width - scaledWidth) / 2.0f,
                    (bounds.height - scaledHeight) / 2.0f);
            mTransform.scale((float) mScale, (float) mScale);
            mInverse.scale((float) mScale, (float) mScale);
            if (bounds.width != 0 && bounds.height != 0) {
                mInverse.invert();
            }
        }
    }

    public void selectionChanged() {
        synchronized (this) {
            if (mSelectedNode != null) {
                mSelectedNode.viewNode.dereferenceImage();
            }
            mSelectedNode = mModel.getSelection();
            if (mSelectedNode != null) {
                mSelectedNode.viewNode.referenceImage();
            }
        }
        doRedraw();
    }

    // Note the syncExec and then synchronized... It avoids deadlock
    public void treeChanged() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                synchronized (this) {
                    if (mSelectedNode != null) {
                        mSelectedNode.viewNode.dereferenceImage();
                    }
                    mTree = mModel.getTree();
                    mSelectedNode = mModel.getSelection();
                    if (mSelectedNode != null) {
                        mSelectedNode.viewNode.referenceImage();
                    }
                    setTransform();
                }
            }
        });
        doRedraw();
    }

    public void viewportChanged() {
        // pass
    }

    public void zoomChanged() {
        // pass
    }

	@Override
	public void logfileChanged() {
		//pass
	}

	@Override
	public void interactionTaskSubmited(String taskName, Object value) {
		final Control thisControl=this;
		if(taskName.equalsIgnoreCase("NodeViewTooltip"))
		{
			final ViewNode viewNode=(ViewNode)value;
			Display.getDefault().syncExec(new Runnable() {
	            public void run() {
	                synchronized (this) {
	                	String message=viewNode.descriptionStr;
	                	float centerX=viewNode.left+viewNode.width/2;
	                	float centerY=viewNode.top+viewNode.height/2;
	                	float[] pt=new float[]{centerX,centerY};
	                	mTransform.transform(pt);
	                	org.eclipse.swt.graphics.Point nodePoint=new org.eclipse.swt.graphics.Point((int)pt[0], (int)pt[1]);
	                	org.eclipse.swt.graphics.Point controlPoint = thisControl.toDisplay(0, 0);
	                	org.eclipse.swt.graphics.Point displayPoint=
	                			new org.eclipse.swt.graphics.Point(nodePoint.x+controlPoint.x,nodePoint.y+controlPoint.y);
	                	
	                       //设置提示信息
	                       mToolTip.setMessage(message);
	                       mToolTip.setLocation(displayPoint);
	                       mToolTip.setVisible(true);
	                }
	            }
	        });
		}
		
	}
}
