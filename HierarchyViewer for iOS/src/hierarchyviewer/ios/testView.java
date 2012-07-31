package hierarchyviewer.ios;

import hierarchyviewerlib.models.TreeViewModel;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class testView extends ViewPart {

	public static final String ID = "hierarchyviewer.ios.testView"; //$NON-NLS-1$

	public testView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeViewModel.getModel().loadLogFile("/home/shiyimin/Result1/Automation Results.plist");
			}
		});
		btnNewButton.setBounds(21, 32, 88, 29);
		btnNewButton.setText("New Button");
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
		label.setBounds(68, 105, 2, 64);
		
		Link link = new Link(container, SWT.NONE);
		link.setBounds(115, 104, 61, 17);
		link.setText("<a> href=\"/p/e-pomodoro/w/list\"New Link</a>");

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
