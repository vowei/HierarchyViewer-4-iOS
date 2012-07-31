package hierarchyviewer.ios;

import hierarchyviewer.ios.views.ControlPropertiesView;
import hierarchyviewer.ios.views.HierarchyOverView;
import hierarchyviewer.ios.views.HierarchyView;
import hierarchyviewer.ios.views.ScreenShotView;
import hierarchyviewer.ios.views.TreeViewListView;
import hierarchyviewerlib.common.ViewManager;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	/**
	 * The ID of the perspective as specified in the extension.
	 */
	public static final String ID = "HierarchyViewer_for_iOS.perspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		
		IFolderLayout folder1 = layout.createFolder("LeftLayout", IPageLayout.LEFT, 0.13f, editorArea);
		folder1.addPlaceholder(TreeViewListView.ID + ":*");
		folder1.addView(TreeViewListView.ID);
		
		
		//layout.addStandaloneView(NavigationView.ID,  false, IPageLayout.LEFT, 0.25f, editorArea);
		IFolderLayout folder2 = layout.createFolder("MainLayout", IPageLayout.LEFT, 0.60f, editorArea);
		folder2.addPlaceholder(HierarchyView.ID + ":*");
		folder2.addView(HierarchyView.ID);
		
		IFolderLayout folder3 = layout.createFolder("RightLayout1", IPageLayout.LEFT, 0.60f, editorArea);
		folder3.addPlaceholder(HierarchyOverView.ID + ":*");
		folder3.addView(HierarchyOverView.ID);
		
		IFolderLayout folder4 = layout.createFolder("RightbottomLayout", IPageLayout.BOTTOM, 0.35f,"RightLayout1");
		folder4.addPlaceholder(ScreenShotView.ID + ":*");
		folder4.addView(ScreenShotView.ID);
		
		IFolderLayout folder5 = layout.createFolder("RightLayout2", IPageLayout.LEFT, 0.95f, editorArea);
		folder5.addPlaceholder(ControlPropertiesView.ID + ":*");
		folder5.addView(ControlPropertiesView.ID);
		
		//IFolderLayout folder6 = layout.createFolder("RightLayout3", IPageLayout.BOTTOM, 0.2f, editorArea);
		//folder6.addPlaceholder(testView.ID + ":*");
		//folder6.addView(testView.ID);
		
		ViewManager.addView("ScreenShot", ScreenShotView.ID);
		ViewManager.addView("Hierarchy",HierarchyView.ID);
		ViewManager.addView("Properties", ControlPropertiesView.ID);
		ViewManager.addView("HierarchyOverView", HierarchyOverView.ID);
		ViewManager.addView("TreeViewList", TreeViewListView.ID);
	}
}
