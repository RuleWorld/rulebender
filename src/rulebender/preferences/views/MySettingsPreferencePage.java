package rulebender.preferences.views;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench; 
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import rulebender.preferences.PreferencesClerk;
import rulebender.core.workspace.PickWorkspaceDialog;
import rulebender.Activator;

public class MySettingsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	
	public MySettingsPreferencePage() 
	{
		super(GRID);
	}

	public void createFieldEditors() {
		addField(new RadioGroupFieldEditor( 
                             "OUTPUT_SETTING","Chose Output Level",1,           
                             new String[][] {
                               {"Minimal Output", "minimal"},
                               {"Medium  Output",  "medium"},
                               {"Maximal Output", "maximal"}}, 
                             getFieldEditorParent()));      
		
		/*
		addField(new BooleanFieldEditor("BOOLEAN_VALUE", "&An example of a boolean preference", getFieldEditorParent()));

		addField(new RadioGroupFieldEditor("CHOICE", "An example of a multiple-choice preference", 1,
				new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		
		addField(new StringFieldEditor("MySTRING1", "A &text preference:",
				getFieldEditorParent()));
		
		addField(new StringFieldEditor("MySTRING2", "A &text preference:",
				getFieldEditorParent()));		
		*/
		
	}
	

	public void init(IWorkbench workbench) {
          setPreferenceStore(Activator.getDefault().getPreferenceStore());
          setDescription("RuleBender Settings");
		
        // Activator.getDefault().getPreferenceStore().setValue("OUTPUT_SETTING","minimal");
        // Activator.getDefault().getPreferenceStore().setValue("SIM_PATH",PickWorkspaceDialog.getLastSetBioNetGenDirectory());

	}

}
