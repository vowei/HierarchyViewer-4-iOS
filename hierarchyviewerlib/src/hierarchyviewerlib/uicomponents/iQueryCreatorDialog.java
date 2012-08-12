/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.uicomponents;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import hierarchyviewerlib.controllers.TreeViewController;
import hierarchyviewerlib.controllers.ViewInteractionController;
import hierarchyviewerlib.controllers.iQueryController;
import hierarchyviewerlib.controllers.iQueryController.IIQueryInsertListener;
import hierarchyviewerlib.models.ConfigurationModel;
import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.common.ClipboardHelper;
import hierarchyviewerlib.common.MessageBoxManager;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.common.iQueryElement;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionListener;

import cc.iqa.iquery.ITreeNode;

public class iQueryCreatorDialog extends Dialog implements IIQueryInsertListener{

	private StyledText mIQueryStyledText;
	private TableViewer mTestResultTableViewer;
	private Table mTestResultTable;
	private FormToolkit toolkit;
	private TableViewer mIQueryElementTableViewer;
	private org.eclipse.swt.widgets.Table mIQueryElementTable;
	Button mTestButton;
	Button mCopyButton;
	Shell mShell;
	Combo mStartNodeCombo;
	Label mResultLabel;
	
	private boolean opened=false;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public iQueryCreatorDialog(Shell parentShell) {
		super(parentShell);
		mShell=this.getShell();
		if(System.getProperty("os.name").equalsIgnoreCase("linux"))
		{
			setShellStyle(SWT.DIALOG_TRIM);
		}
		else
		{
			setShellStyle(SWT.DIALOG_TRIM |SWT.ON_TOP);
		}
		setBlockOnOpen(false);
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

		Label label = new Label(container, SWT.NONE);
		label.setText("查找开始位置:");
		GridData data=new GridData();
		label.setLayoutData(data);
		
		mStartNodeCombo=new Combo(container, SWT.BORDER);
		data=new GridData();
		data.widthHint=120;
		mStartNodeCombo.setLayoutData(data);
		mStartNodeCombo.add("根节点");
		mStartNodeCombo.add("选中节点");
		mStartNodeCombo.select(0);
		
		mIQueryStyledText = new StyledText(container, SWT.BORDER);
		mIQueryStyledText.addModifyListener(mIQueryStyledText_ModifyListener);
		data=new GridData(SWT.FILL, SWT.LEFT, false, false, 2, 1);
		data.heightHint=50;
		data.widthHint=500;
		mIQueryStyledText.setLayoutData(data);
		mIQueryStyledText.setFocus();
		
		mTestButton = new Button(container, SWT.NONE);
		mTestButton.addSelectionListener(mTestButton_SelectionAdapter);
		mTestButton.setText("测试");
		data=new GridData();
		data.widthHint=75;
		mTestButton.setLayoutData(data);
		
		mCopyButton = new Button(container, SWT.NONE);
		mCopyButton.addSelectionListener(mCopyButton_SelectionAdapter);
		mCopyButton.setText("复制");
		data=new GridData();
		data.widthHint=75;
		mCopyButton.setLayoutData(data);
		
		mTestResultTableViewer = new TableViewer(container, SWT.BORDER |SWT.H_SCROLL | SWT.V_SCROLL);
		mTestResultTableViewer.setContentProvider(new TestResultContentProvider());
		mTestResultTableViewer.addSelectionChangedListener(mTestResultTableViewer_SelectionChangedListener);
		
		data=new GridData(SWT.FILL, SWT.LEFT, false, false, 2, 1);
		data.heightHint=100;
		mTestResultTable=mTestResultTableViewer.getTable();
		mTestResultTable.setLayoutData(data);
		
		TableViewerColumn viewerColumn = new TableViewerColumn(mTestResultTableViewer,
				SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		//column.setText(title);
		//column.setWidth(bound);
		column.setWidth(500);
		column.setResizable(false);
		column.setMoveable(false);
		viewerColumn.setLabelProvider(new TestResultColumnLabelProvider());
		
		mResultLabel = new Label(container, SWT.RIGHT);
		data=new GridData(SWT.FILL, SWT.LEFT, false, false, 2, 1);
		mResultLabel.setLayoutData(data);
		
		toolkit = new FormToolkit(container.getDisplay());
		
		label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
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
		
		mIQueryElementTableViewer = new TableViewer(subContainer1, SWT.BORDER | SWT.V_SCROLL|SWT.H_SCROLL);
		mIQueryElementTable = mIQueryElementTableViewer.getTable();
		mIQueryElementTableViewer.setContentProvider(new iQueryElementTableContentProvider());
		
		TableViewerColumn iQueryViewerColumn = new TableViewerColumn(mIQueryElementTableViewer,
				SWT.NONE);
		TableColumn iQueryColumn = iQueryViewerColumn.getColumn();
		//column.setText(title);
		//column.setWidth(bound);
		iQueryColumn.setWidth(500);
		iQueryColumn.setResizable(false);
		iQueryColumn.setMoveable(false);
		iQueryViewerColumn.setLabelProvider(new iQueryElementTableLabelProvider());
		
		mIQueryElementTableViewer.setInput(ConfigurationModel.getModel().mIQueryElements);
		mIQueryElementTable.addMouseListener(mIQueryElementTable_MouseAdapternew);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint=150;
		mIQueryElementTable.setLayoutData(data);
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

	MouseAdapter mIQueryElementTable_MouseAdapternew = new MouseAdapter() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			IStructuredSelection selection = (IStructuredSelection)mIQueryElementTableViewer.getSelection(); 
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
		opened=false;
		return super.close();
	}
	
	public class iQueryElementTableContentProvider implements IStructuredContentProvider {
	    public Object[] getElements(Object inputElement) {
	    	List<?> iQueryElementList=(List<?>)inputElement;
	    	iQueryElement[] iQueryElements=new iQueryElement[iQueryElementList.size()];
	    	return iQueryElementList.toArray(iQueryElements);
	    }
	    public void dispose() {
	    }
	    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	    }
	}
	
	public class iQueryElementTableLabelProvider extends StyledCellLabelProvider {
		
		@Override
		public void update(ViewerCell cell) {
			iQueryElement element = (iQueryElement)cell.getElement();
				cell.setText(element.mName+"   -   "+element.mDescription);
				StyleRange range=new StyleRange();
				range.start=0;
				range.length=element.mName.length();
				if(element.mSupported)
				{
					range.foreground = Display.getDefault().getSystemColor(
						SWT.COLOR_DARK_BLUE);
				}
				else
				{
					range.foreground = Display.getDefault().getSystemColor(
							SWT.COLOR_DARK_GRAY);
				}
				cell.setStyleRanges(new StyleRange[]{range});
				super.update(cell);
		}
	}
	
	ModifyListener mIQueryStyledText_ModifyListener =new ModifyListener() {
		public void modifyText(ModifyEvent e) {
		}
	};
	
	SelectionAdapter mCopyButton_SelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			String iquery= mIQueryStyledText.getText();
			iquery= iquery.replaceAll("\r", "");
			iquery= iquery.replace('\n', ' ');
			iquery="$(\""+iquery+"\")[0]";
			
			//从根节点开始的查询
			if(mStartNodeCombo.getSelectionIndex()==0||TreeViewModel.getModel().getSelection()==null)
			{
				ClipboardHelper.setClipboard(iquery);
			}
			//从选中的节点开始的查询
			else
			{
				ViewNode viewNode = TreeViewModel.getModel().getSelection().viewNode;
				String callPath = TreeViewController.getController().getFunctionCallByName(viewNode);
				ClipboardHelper.setClipboard(callPath+"."+iquery);
			}
		}
	};
	
	ISelectionChangedListener mTestResultTableViewer_SelectionChangedListener =new ISelectionChangedListener()
	{

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection)event.getSelection(); 
			if (selection != null) { 
				Object selectedObject = selection.getFirstElement();
				if(selectedObject instanceof String)
				{
					return;
				}
				
				if(selectedObject instanceof ViewNode)
				{
					ViewNode viewNode=(ViewNode)selection.getFirstElement(); 
					ViewInteractionController.getController().SubmitInteractionTask("NodeViewTooltip", viewNode);
				}
			}
		}
		
	};
	
	SelectionAdapter mTestButton_SelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			String query =mIQueryStyledText.getText();
			
			//check if contains syntax that not supported be tested
			String unsupportedSyntax="";
			for(iQueryElement element:ConfigurationModel.getModel().mIQueryElements)
			{
				if(!element.mSupported&&query.toLowerCase().contains(element.mName.toLowerCase()))
				{
					unsupportedSyntax+=element.mName+",";
				}
			}
			if(!unsupportedSyntax.isEmpty())
			{
				MessageBoxManager.openWarning(true, "警告",
						String.format("查询中包含：%s 它们可以在iquery中使用，但是HierarchyViewer for iOS无法测试它们。", unsupportedSyntax),mShell);
				return;
			}
			
			//clear last result
			mTestResultTable.removeAll();
			
			boolean fromSelectedNode=false;
			if(mStartNodeCombo.getSelectionIndex()==1)
			{
				fromSelectedNode=true;
			}
			
			List<ITreeNode> outTreeNodeList = new ArrayList<ITreeNode>();
			List<String> errors = 
					iQueryController.query(mIQueryStyledText.getText(),fromSelectedNode,outTreeNodeList);
			
			if(errors.size()==0)
			{
				mResultLabel.setText(String.format("找到%s个控件", outTreeNodeList.size()));
				mTestResultTableViewer.setInput(outTreeNodeList);
			}
			else
			{
				mResultLabel.setText("查询错误");
				mTestResultTableViewer.setInput(errors);
			}
		}
	};

	@Override
	public int open() {
		if(opened)
		{
			mShell.forceActive();
			return 0;
		}
		iQueryController.addIQueryInsertListener(this);
		opened=true;
		return super.open();
	}
	
	class TestResultColumnLabelProvider extends  StyledCellLabelProvider {
		@Override
		public void update(ViewerCell cell) {
			if(cell.getElement() instanceof String)
			{
				//Error
				String errorString = (String)cell.getElement();
				cell.setText(errorString);
				StyleRange range=new StyleRange();
				range.start=0;
				range.length=errorString.length();
				range.foreground = Display.getDefault().getSystemColor(
						SWT.COLOR_RED);
				cell.setStyleRanges(new StyleRange[]{range});
				super.update(cell);
			}
			else {
				ViewNode node = (ViewNode)cell.getElement();
				String message=node.descriptionStr;
				cell.setText(message);
				StyleRange range=new StyleRange();
				range.start=0;
				range.length=message.length();
				range.foreground = Display.getDefault().getSystemColor(
						SWT.COLOR_BLUE);
				cell.setStyleRanges(new StyleRange[]{range});
				super.update(cell);
			}
		}
	};
	
	class TestResultContentProvider implements IStructuredContentProvider  
	{

		@Override
		public void dispose() {
			// pass
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// pass
		}

		@Override
		public Object[] getElements(Object inputElement) {
			List<?> elements = (List<?>)inputElement;
			Object[] objects=new Object[elements.size()];
			elements.toArray(objects);
			return objects;
		}
	}
}
