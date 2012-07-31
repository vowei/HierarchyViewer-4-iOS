/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hierarchyviewerlib.uicomponents;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import hierarchyviewerlib.common.ClipboardHelper;
import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.common.ViewNode.Property;
import hierarchyviewerlib.controllers.ViewInteractionController.IViewInteractionListener;
import hierarchyviewerlib.controllers.iQueryController;
import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.models.TreeViewModel.ITreeChangeListener;
import hierarchyviewerlib.uiutilities.DrawableViewNode;
import hierarchyviewerlib.uiutilities.TreeColumnResizer;

import java.util.ArrayList;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class PropertyViewer extends Composite implements ITreeChangeListener,IViewInteractionListener{
    private TreeViewModel mModel;

    private TreeViewer mTreeViewer;

    private Tree mTree;

    private DrawableViewNode mSelectedNode;

    private Font mSmallFont;
    
    
    private class ContentProvider implements ITreeContentProvider, ITableLabelProvider {

        public Object[] getChildren(Object parentElement) {
            synchronized (PropertyViewer.this) {
                if (mSelectedNode != null && parentElement instanceof String) {
                    String category = (String) parentElement;
                    ArrayList<Property> returnValue = new ArrayList<Property>();
                    for (Property property : mSelectedNode.viewNode.properties) {
                        if (category.equals(ViewNode.MISCELLANIOUS)) {
                            if (property.name.indexOf(':') == -1) {
                                returnValue.add(property);
                            }
                        } else {
                            if (property.name.startsWith(((String) parentElement) + ":")) {
                                returnValue.add(property);
                            }
                        }
                    }
                    return returnValue.toArray(new Property[returnValue.size()]);
                }
                return new Object[0];
            }
        }

        public Object getParent(Object element) {
            synchronized (PropertyViewer.this) {
                if (mSelectedNode != null && element instanceof Property) {
                    if (mSelectedNode.viewNode.categories.size() == 0) {
                        return null;
                    }
                    String name = ((Property) element).name;
                    int index = name.indexOf(':');
                    if (index == -1) {
                        return ViewNode.MISCELLANIOUS;
                    }
                    return name.substring(0, index);
                }
                return null;
            }
        }

        public boolean hasChildren(Object element) {
            synchronized (PropertyViewer.this) {
                if (mSelectedNode != null && element instanceof String) {
                    String category = (String) element;
                    for (String name : mSelectedNode.viewNode.namedProperties.keySet()) {
                        if (category.equals(ViewNode.MISCELLANIOUS)) {
                            if (name.indexOf(':') == -1) {
                                return true;
                            }
                        } else {
                            if (name.startsWith(((String) element) + ":")) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }

        public Object[] getElements(Object inputElement) {
            synchronized (PropertyViewer.this) {
                if (mSelectedNode != null && inputElement instanceof TreeViewModel) {
                    if (mSelectedNode.viewNode.categories.size() == 0) {
                        return mSelectedNode.viewNode.properties
                                .toArray(new Property[mSelectedNode.viewNode.properties.size()]);
                    } else {
                        return mSelectedNode.viewNode.categories
                                .toArray(new String[mSelectedNode.viewNode.categories.size()]);
                    }
                }
                return new Object[0];
            }
        }

        public void dispose() {
            // pass
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // pass
        }

        public Image getColumnImage(Object element, int column) {
            return null;
        }

        public String getColumnText(Object element, int column) {
            synchronized (PropertyViewer.this) {
                if (mSelectedNode != null) {
                    if (element instanceof String && column == 0) {
                        String category = (String) element;
                        return Character.toUpperCase(category.charAt(0)) + category.substring(1);
                    } else if (element instanceof Property) {
                        if (column == 0) {
                            String returnValue = ((Property) element).name;
                            //int index = returnValue.indexOf(':');
                            //if (index != -1) {
                            //    return returnValue.substring(index + 1);
                           // }
                            return returnValue;
                        } else if (column == 1) {
                            return ((Property) element).value;
                        }
                    }
                }
                return "";
            }
        }

        public void addListener(ILabelProviderListener listener) {
            // pass
        }

        public boolean isLabelProperty(Object element, String property) {
            // pass
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
            // pass
        }
    }

    public PropertyViewer(Composite parent) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout());
        mTreeViewer = new TreeViewer(this, SWT.NONE);

        mTree = mTreeViewer.getTree();
        mTree.addMouseListener(mTree_MouseAdapter);
        mTree.setLinesVisible(true);
        mTree.setHeaderVisible(true);

        TreeColumn propertyColumn = new TreeColumn(mTree, SWT.NONE);
        propertyColumn.setText("Property");
        TreeColumn valueColumn = new TreeColumn(mTree, SWT.NONE);
        valueColumn.setText("Value");
        
        mTreeViewer.setColumnProperties(new String[] { "Property","Value" });
        
        mModel = TreeViewModel.getModel();
        ContentProvider contentProvider = new ContentProvider();
        mTreeViewer.setContentProvider(contentProvider);
        mTreeViewer.setLabelProvider(contentProvider);
        mTreeViewer.setInput(mModel);
   
        mModel.addTreeChangeListener(this);

        loadResources();
        addDisposeListener(mDisposeListener);

        mTree.setFont(mSmallFont);

        new TreeColumnResizer(this, propertyColumn, valueColumn);
        addControlListener(mControlListener);

        treeChanged();
        createContextMenu();
    }

    public void loadResources() {
        Display display = Display.getDefault();
        Font systemFont = display.getSystemFont();
        FontData[] fontData = systemFont.getFontData();
        FontData[] newFontData = new FontData[fontData.length];
        for (int i = 0; i < fontData.length; i++) {
            newFontData[i] = new FontData(fontData[i].getName(), 14, fontData[i].getStyle());
        }
        mSmallFont = new Font(Display.getDefault(), newFontData);
    }
    
    public void updateFont(int size)
    {
        Display display = Display.getDefault();
        Font systemFont = display.getSystemFont();
        FontData[] fontData = systemFont.getFontData();
        FontData[] newFontData = new FontData[fontData.length];
        for (int i = 0; i < fontData.length; i++) {
            newFontData[i] = new FontData(fontData[i].getName(), size, fontData[i].getStyle());
        }
        mSmallFont = new Font(Display.getDefault(), newFontData);
        Font oldFont = mSmallFont;
        mTree.setFont(mSmallFont);
        if(oldFont!=null)
        {
        	oldFont.dispose();
        }
        	
    }

    private DisposeListener mDisposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
            mModel.removeTreeChangeListener(PropertyViewer.this);
            mSmallFont.dispose();
        }
    };

    // If the window gets too small, hide the data, otherwise SWT throws an
    // ERROR.

    private ControlListener mControlListener = new ControlAdapter() {
        private boolean noInput = false;

        private boolean noHeader = false;

        @Override
        public void controlResized(ControlEvent e) {
            if (getBounds().height <= 20) {
                mTree.setHeaderVisible(false);
                noHeader = true;
            } else if (noHeader) {
                mTree.setHeaderVisible(true);
                noHeader = false;
            }
            if (getBounds().height <= 38) {
                mTreeViewer.setInput(null);
                noInput = true;
            } else if (noInput) {
                mTreeViewer.setInput(mModel);
                noInput = false;
            }
        }
    };
    
	private void createContextMenu(){
		
		Menu menu = new Menu(mTreeViewer.getControl());
    	MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH); 
    	copyMenuItem.addSelectionListener(copyMenuItem_SelectionListener); 
    	copyMenuItem.setText("复制");
    	mTreeViewer.getTree().setMenu(menu);
    }
	
	private SelectionListener  copyMenuItem_SelectionListener=new SelectionListener ()
	{
		@Override
		public void widgetSelected(SelectionEvent e) {
			IStructuredSelection selection = (IStructuredSelection)mTreeViewer.getSelection(); 
			String str="";
			if (selection != null) { 
				Property p =(Property)selection.getFirstElement();
				if(p.name.equals(":path"))
				{
					str=p.value;
				}
				else
				{
					str=String.format("[%s = '%s']", p.name,p.value);
				}
				ClipboardHelper.setClipboard(str);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	

    private void doRefresh() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                mTreeViewer.refresh();
            }
        });
    }
    
    public void selectionChanged() {
        synchronized (this) {
            mSelectedNode = mModel.getSelection();
        }
        doRefresh();
    }

    public void treeChanged() {
        synchronized (this) {
            mSelectedNode = mModel.getSelection();
        }
        doRefresh();
    }
	
    public void viewportChanged() {
        // pass
    }

    public void zoomChanged() {
        // pass
    }
	
	MouseAdapter mTree_MouseAdapter = new MouseAdapter() {
    	@Override
    	public void mouseDoubleClick(MouseEvent e) {
			IStructuredSelection selection = (IStructuredSelection)mTreeViewer.getSelection(); 
			String str="";
			if (selection != null) { 
				Property p =(Property)selection.getFirstElement();
				if(p.name.equals(":path"))
				{
					str=p.value;
				}
				else
				{
					str=String.format("[%s = '%s']", p.name,p.value);
				}
				iQueryController.notifyIQueryInserted(str);
			}
    	}
    };

	@Override
	public void logfileChanged() {
		//pass 
	}

	@Override
	public void interactionTaskSubmited(String taskName, Object value) {
		if(taskName.equalsIgnoreCase("Font"));
		int font = (Integer) value;
		
	}
}
