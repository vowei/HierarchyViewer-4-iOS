package hierarchyviewer.ios.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import hierarchyviewerlib.uicomponents.LayoutViewer;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ScreenShotView extends ViewPart {

	public static final String ID = "hierarchyviewer.ios.views.ScreenShotView"; //$NON-NLS-1$
	public static LayoutViewer mLayoutViewer;

	
	public ScreenShotView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
	    layout.marginWidth=0;
	    layout.marginHeight=0;
	    layout.numColumns = 1;
	    container.setLayout(layout);
	    
		mLayoutViewer=new LayoutViewer(container);
		mLayoutViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
}
