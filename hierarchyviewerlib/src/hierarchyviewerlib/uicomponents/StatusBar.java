/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.uicomponents;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import hierarchyviewerlib.controllers.ViewInteractionController;
import hierarchyviewerlib.controllers.ViewInteractionController.IViewInteractionListener;
import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.models.TreeViewModel.ITreeChangeListener;
import hierarchyviewerlib.uiutilities.DrawableViewNode.Point;

public class StatusBar extends ControlContribution implements ITreeChangeListener,IViewInteractionListener {

	private Label mMessageLabel;
	private IStatusLineManager mIStatusLineManager;
	private Link mCompanyLink;
	
	/** 
	 * @wbp.parser.entryPoint
	 */
	public StatusBar(String id, IStatusLineManager statusLineManager) {
		super(id);
		mIStatusLineManager=statusLineManager;
		TreeViewModel.getModel().addTreeChangeListener(this);
		ViewInteractionController.getController().addViewInteractionListener(this);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected Control createControl(Composite parent) {
		Composite composite=new Composite(parent, SWT.NONE);
		FormLayout statusBarLayout = new FormLayout();
        statusBarLayout.marginHeight = statusBarLayout.marginWidth = 0;
        composite.setLayout(statusBarLayout);
        
        //
        mCompanyLink =new Link(composite, SWT.NONE);
        
        FormData companyLinkFormData = new FormData();
        companyLinkFormData.width=100;
        companyLinkFormData.height=40;
        companyLinkFormData.right = new FormAttachment(100, 0);
        companyLinkFormData.top = new FormAttachment(50,-3);
        mCompanyLink.setLayoutData(companyLinkFormData);
        mCompanyLink.setText("<a href=\"http://www.vowei.com\">知平软件</a>");
        mCompanyLink.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                   //System.out.println("You have selected: "+e.text);
                   try {
                    //  Open default external browser 
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
                  } 
                 catch (PartInitException ex) {
                    // TODO Auto-generated catch block
                     ex.printStackTrace();
                } 
                catch (MalformedURLException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
        });
        
        Label label = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
        FormData labelFormData = new FormData();
        labelFormData.right = new FormAttachment(mCompanyLink, -20);
        labelFormData.top= new FormAttachment(50,-3);
        label.setLayoutData(labelFormData);
        
     // Progress stuff
        mMessageLabel = new Label(composite, SWT.LEFT);

        FormData messageLabelFormData = new FormData();
        messageLabelFormData.width=100;
        messageLabelFormData.height=40;
        messageLabelFormData.right = new FormAttachment(label, -20);
        messageLabelFormData.top= new FormAttachment(50,-3);
        //progressBarFormData.top = new FormAttachment(mTreeViewButton, 0, SWT.CENTER);
        mMessageLabel.setLayoutData(messageLabelFormData);
        mMessageLabel.setText("坐标:");
        
        
        //Label label = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
        
		return composite;
	}

	@Override
	public void treeChanged() {
		//pass
	}

	@Override
	public void selectionChanged() {
		//pass
	}

	@Override
	public void viewportChanged() {
		//pass
	}

	@Override
	public void zoomChanged() {
		//pass
	}

	@Override
	public void logfileChanged() {
		Display.getDefault().syncExec(new Runnable() {
            public void run() {
                synchronized (this) {
                	mIStatusLineManager.setMessage(null, TreeViewModel.getModel().getLogFilePath());
                }
            }
        });
	}
	
	@Override
	public void interactionTaskSubmited(String taskName, Object value) {
		
		//event is send by LayoutViewer.mMouseMoveListener event.
		if(taskName.equalsIgnoreCase("CoordUpdate"))
		{
			Point pt =(Point)value;
			mMessageLabel.setText("坐标:"+(int)pt.x+","+(int)pt.y);
		}
	}
}
