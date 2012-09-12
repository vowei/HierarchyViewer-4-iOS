/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 */

package hierarchyviewerlib.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import hierarchyviewerlib.common.IConManager;
import hierarchyviewerlib.models.TreeViewModel;
import hierarchyviewerlib.uicomponents.TreeView;
import hierarchyviewerlib.uiutilities.DrawableViewNode;

public class SaveHierarchyViewPictureAction extends Action {
	private final IWorkbenchWindow window;

	
	public SaveHierarchyViewPictureAction(IWorkbenchWindow window)
	{
		setText("保存控件树视图");
		this.window = window;
		setId(ICommandIds.CMD_SAVE_PICTURE);
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId(ICommandIds.CMD_SAVE_PICTURE);
        setImageDescriptor(ImageDescriptor.createFromImage(IConManager.SAVE_HIERARCHY));
	}

	@Override
	public void run() {
	  	
		Shell shell=window.getWorkbench().getActiveWorkbenchWindow().getShell();
		FileDialog fileDialog = new FileDialog(shell,SWT.SAVE);
		fileDialog.setText("保存控件层次图");
		fileDialog.setFilterExtensions(new String[] { "*.png","*.bmp","*.jpg","*.gif"});
		fileDialog.setFilterNames(new String[] { "PNG(*.png)","Bitmap(*.bmp)","JPEG(*.jpg),", "GIF(*.gif)" });
		fileDialog.setFileName("hierarchyview.png");
		String selected = fileDialog.open();
		if(selected==null)
			return;
	  	
	  	save(selected);
	}
	
	  
	  public void save(String path)
	  {
		  int dotIndex= path.lastIndexOf('.');
		  int imageType =  SWT.IMAGE_BMP;
		  if(dotIndex<0)
		  {
			  imageType = SWT.IMAGE_BMP;
		  }
		  else
		  {
			  String extension = path.substring(dotIndex);
			  if(extension.equalsIgnoreCase(".png"))
			  {
				  imageType=SWT.IMAGE_PNG;
			  }
			  else if (extension.equalsIgnoreCase(".bmp"))
			  {
				  imageType=SWT.IMAGE_BMP;
			  }
			  else if (extension.equalsIgnoreCase(".jpg")||
					  extension.equalsIgnoreCase(".jpeg")||
					  extension.equalsIgnoreCase(".jpe"))
			  {
				  imageType=SWT.IMAGE_JPEG;
			  }
			  else if (extension.equalsIgnoreCase(".gif"))
			  {
				  imageType=SWT.IMAGE_GIF;
			  }
		  }
		  
		  //Shell parent=window.getWorkbench().getActiveWorkbenchWindow().getShell();
		  
		  DrawableViewNode viewNode = TreeViewModel.getModel().getTree();
		  Image image = TreeView.paintToImage(viewNode);
		  
		  ImageLoader imageLoader = new ImageLoader();
          imageLoader.data = new ImageData[] {
              image.getImageData()
          };
          imageLoader.save(path, imageType);
	  }
}
