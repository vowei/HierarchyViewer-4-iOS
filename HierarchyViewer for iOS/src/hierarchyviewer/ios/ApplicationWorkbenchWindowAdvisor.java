package hierarchyviewer.ios;

import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        //configurer.setInitialSize(new Point(600, 400));
        
        PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, 
        		false);
        PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.SHOW_PROGRESS_ON_STARTUP,
        		true);
        PlatformUI.getPreferenceStore().setDefault(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR,
        		IWorkbenchPreferenceConstants.TOP_LEFT);
        
       configurer.setShowMenuBar(true);
       configurer.setShowCoolBar(true);
       configurer.setShowFastViewBars(true);
       configurer.setShowPerspectiveBar(false);

    }
    

	@Override
	public void postWindowCreate() {
		// TODO Auto-generated method stub
		super.postWindowCreate();
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
	}
    
}
