/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.uicomponents;

import hierarchyviewerlib.models.LogFileModel;
import hierarchyviewerlib.models.LogFileModel.ILogFileChangeListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TreeViewListViewer extends ListViewer implements ILogFileChangeListener{

	public TreeViewListViewer(Composite parent) {
		super(parent,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new LabelProvider() );
		LogFileModel.getModel().addLogFileChangeListener(this);
		this.addSelectionChangedListener(new ISelectionChangedListener() 
		{

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				 ISelection selection = event.getSelection();
			        if (selection !=null ) {
			            String value = (String) ((IStructuredSelection) selection).getFirstElement();
			            LogFileModel.getModel().setCurrentData(value);
			        }
			}});
	}

	@Override
	public void logfileChanged() {
		setInput(LogFileModel.getModel().getViewTreeNames());
	}

}
