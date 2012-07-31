package hierarchyviewerlib.uicomponents;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;

import hierarchyviewerlib.controllers.iQueryController;
import hierarchyviewerlib.controllers.iQueryController.IIQueryInsertListener;
import hierarchyviewerlib.models.ConfigurationModel;
import hierarchyviewerlib.common.ClipboardHelper;
import hierarchyviewerlib.common.iQueryElement;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionListener;

public class iQueryCreatorDialog extends Dialog implements IIQueryInsertListener{

	private StyledText mIQueryStyledText;
	private StyledText mTestResultStyledText;
	private FormToolkit toolkit;
	ListViewer mExistIQueryListViewer;
	List mExistIQueryList;
	ListViewer mIQueryElementListViewer;
	List mIQueryElementList;
	Button mTestButton;
	Button mVerificationButton;
	Shell mShell;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public iQueryCreatorDialog(Shell parentShell) {
		super(parentShell);
		mShell=this.getShell();
		setShellStyle(SWT.DIALOG_TRIM | SWT.MIN);
		setBlockOnOpen(false);
	}
	
	@Override
	protected int getShellStyle()
	{
		return super.getShellStyle()|SWT.MIN;
	}

	
	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		this.getShell().setText("iQuery生成测试器");
		
		final Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns=2;
		layout.marginLeft=8;
	    layout.marginRight=8;
	    layout.verticalSpacing=10;
		container.setLayout(layout);

		mIQueryStyledText = new StyledText(container, SWT.BORDER);
		mIQueryStyledText.addModifyListener(mIQueryStyledText_ModifyListener);
		GridData data=new GridData(SWT.FILL, SWT.LEFT, false, false, 2, 1);
		data.heightHint=50;
		data.widthHint=500;
		mIQueryStyledText.setLayoutData(data);
		
		mTestButton = new Button(container, SWT.NONE);
		mTestButton.addSelectionListener(mTestButton_SelectionAdapter);
		mTestButton.setText("测试");
		data=new GridData();
		data.widthHint=65;
		mTestButton.setLayoutData(data);
		
		mVerificationButton = new Button(container, SWT.NONE);
		mVerificationButton.addSelectionListener(mVerificationButton_SelectionAdapter);
		mVerificationButton.setText("语法检查");
		data=new GridData();
		data.widthHint=75;
		mVerificationButton.setLayoutData(data);
		
		mTestResultStyledText = new StyledText(container, SWT.BORDER | SWT.READ_ONLY|SWT.H_SCROLL);
		data=new GridData(SWT.FILL, SWT.LEFT, false, false, 2, 1);
		data.heightHint=100;
		mTestResultStyledText.setLayoutData(data);
		
		toolkit = new FormToolkit(container.getDisplay());
		
		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		data=new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 1);
		label.setLayoutData(data);
  
		Section section1 =
			      toolkit.createSection(
			    		  container,
			    		  // form.getBody(),
			    		  Section.TREE_NODE);
		section1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		data = new GridData(SWT.FILL, SWT.LEFT, true, false, 2, 1);
		section1.setLayoutData(data);
		section1.addExpansionListener(new ExpansionAdapter() {
	        public void expansionStateChanged(ExpansionEvent e) {
	        	getShell().pack(true);
	        }
	      });
		section1.setText("iQuery元素");
		
		Composite subContainer1 = toolkit.createComposite(section1);
		subContainer1.setLayout(new GridLayout());
		
		mIQueryElementListViewer = new ListViewer(subContainer1, SWT.BORDER | SWT.V_SCROLL|SWT.H_SCROLL);
		mIQueryElementList = mIQueryElementListViewer.getList();
		mIQueryElementListViewer.setContentProvider(new iQueryElementListContentProvider());
		mIQueryElementListViewer.setLabelProvider(new iQueryElementListLabelProvider());
		mIQueryElementListViewer.setInput(ConfigurationModel.getModel().mIQueryElements);
		mIQueryElementList.addMouseListener(mIQueryElementList_MouseAdapternew);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint=150;
		mIQueryElementList.setLayoutData(data);
		section1.setClient(subContainer1);
		createContextMenu();
		return container;
	}
	
	private void createContextMenu(){
		
		Menu menu = new Menu(mIQueryStyledText);
		MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH); 
    	copyMenuItem.addSelectionListener(copyMenuItem_SelectionListener); 
    	copyMenuItem.setText("复制");
    	
    	MenuItem selectallMenuItem = new MenuItem(menu, SWT.PUSH); 
		selectallMenuItem.addSelectionListener(selectallMenuItem_SelectionListener); 
    	selectallMenuItem.setText("全选"); 
    	mIQueryStyledText.setMenu(menu);
    }
	
	SelectionListener selectallMenuItem_SelectionListener = new SelectionListener ()
	{

		@Override
		public void widgetSelected(SelectionEvent e) {
			mIQueryStyledText.setSelection(0, mIQueryStyledText.getText().length());
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	SelectionListener copyMenuItem_SelectionListener = new SelectionListener ()
	{

		@Override
		public void widgetSelected(SelectionEvent e) {
			String text = mIQueryStyledText.getSelectionText();
			text = text.replaceAll("\r", "");
			text = text.replaceAll("\n", " ");
			ClipboardHelper.setClipboard(text);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		//createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
		//		true);
		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		cancelButton.setText("关闭");
	}
	
	private String getInputIQuery()
	{
		String iquery= mIQueryStyledText.getText();
		iquery= iquery.replaceAll("\r", "");
		iquery= iquery.replace('\n', ' ');
		return iquery;
	}
	
	MouseAdapter mExistIQueryList_MouseAdapter = new MouseAdapter() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			String text = mExistIQueryList.getItem(mExistIQueryList.getSelectionIndex());
			iQueryController.notifyIQueryInserted(text);
		}
	};

	MouseAdapter mIQueryElementList_MouseAdapternew = new MouseAdapter() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			IStructuredSelection selection = (IStructuredSelection)mIQueryElementListViewer.getSelection(); 
			if (selection != null) { 
				iQueryElement element = (iQueryElement)selection.getFirstElement();
				iQueryController.notifyIQueryInserted(element.mName);
			}
		}
	};
	
	@Override
	public void insertIQuery(final String element) {
		getShell().getDisplay().syncExec(new Runnable() {
			
	        public void run() {
	        	String text = mIQueryStyledText.getText();
	    		if(!text.endsWith(" "))
	    		{
	    			text+=" ";
	    		}
	    		text+=element;
	    		mIQueryStyledText.setText(text);
	        };
			});
	}

	@Override
	public boolean close() {
		iQueryController.removeIQueryInsertListener(this);
		return super.close();
	}
	
	public class iQueryElementListContentProvider implements IStructuredContentProvider {
	    public Object[] getElements(Object inputElement) {
	    	java.util.List<iQueryElement> iQueryElementList=(java.util.List<iQueryElement>)inputElement;
	    	iQueryElement[] iQueryElements=new iQueryElement[iQueryElementList.size()];
	    	return iQueryElementList.toArray(iQueryElements);
	    }
	    public void dispose() {
	    }
	    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    }
	}
	
	public class iQueryElementListLabelProvider extends LabelProvider {
	    public String getText(Object element) {
	    	iQueryElement iQueryElement = (iQueryElement)element;
	        return iQueryElement.mName+"   -   "+iQueryElement.mDescription;
	    }
	    
	    public Image getImage(Object element) {
	        return null;
	    }
	}
	
	ModifyListener mIQueryStyledText_ModifyListener =new ModifyListener() {
		public void modifyText(ModifyEvent e) {
		}
	};
	
	SelectionAdapter mVerificationButton_SelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			java.util.List<String> errors = iQueryController.getSyntaxErrors(getInputIQuery());
			String text="";
			if(errors.size()==0)
			{
				mTestResultStyledText_setText("正确",false);
			}
			else
			{
				for(String error:errors)
				{
					text+=error+"\r\n";
				}
				mTestResultStyledText_setText(text,true);
			}
		}
	};
	
	public void mTestResultStyledText_setText(String message,boolean isError)
	{
		StyleRange style = new StyleRange();
		if(isError)
		{
			style.start=0;
			style.length=message.length();
			style.foreground = Display.getDefault().getSystemColor(
					SWT.COLOR_RED);
		}
		else
		{
			style.start=0;
			style.length=message.length();
			style.foreground = Display.getDefault().getSystemColor(
					SWT.COLOR_BLUE);
		}
		mTestResultStyledText.setText(message);
		mTestResultStyledText.setStyleRange(style);
	}
	
	SelectionAdapter mTestButton_SelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
		}
	};
	
	public void Test(String iquery)
	{
	}
	
	private void displayIQueryTestResult()
	{

	}

	@Override
	public int open() {
		iQueryController.addIQueryInsertListener(this);
		return super.open();
	}
}
