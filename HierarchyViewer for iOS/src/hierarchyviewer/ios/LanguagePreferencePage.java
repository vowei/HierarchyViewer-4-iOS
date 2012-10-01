package hierarchyviewer.ios;


import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class LanguagePreferencePage extends FieldEditorPreferencePage 
implements IWorkbenchPreferencePage {

	public LanguagePreferencePage()
	{
		super(GRID);
		setTitle("General");
	}
	
	@Override
	protected void createFieldEditors() {
		String[][] choose = new String[][]{
			new String[]{"English","en"},
			new String[]{"中文","cn"}
		};
		
		FieldEditor languageFieldEditor = 
				new RadioGroupFieldEditor("General.Language",
						"Language",1,choose,getFieldEditorParent(),true);
		addField(languageFieldEditor);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

}
