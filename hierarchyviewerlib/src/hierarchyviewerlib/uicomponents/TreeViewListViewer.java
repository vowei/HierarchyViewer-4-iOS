package hierarchyviewerlib.uicomponents;

import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.models.TreeViewModel.ITreeChangeListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class TreeViewListViewer extends ListViewer implements ITreeChangeListener{

	public TreeViewListViewer(Composite parent) {
		super(parent,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new LabelProvider() );
		TreeViewModel.getModel().addTreeChangeListener(this);
		this.addSelectionChangedListener(new ISelectionChangedListener() 
		{

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				 ISelection selection = event.getSelection();
			        if (selection !=null ) {
			            String value = (String) ((IStructuredSelection) selection).getFirstElement();
			            TreeViewModel.getModel().setCurrentData(value);
			        }
			}});
	}

	@Override
	public void treeChanged() {
		// pass
	}

	@Override
	public void selectionChanged() {
		// pass
	}

	@Override
	public void viewportChanged() {
		// pass
	}

	@Override
	public void zoomChanged() {
		// pass
	}

	@Override
	public void logfileChanged() {
		setInput(TreeViewModel.getModel().getViewTreeNames());
	}

}
