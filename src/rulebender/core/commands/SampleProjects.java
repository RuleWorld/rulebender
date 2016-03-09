package rulebender.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import rulebender.core.workspace.LoadSample;

/**
 * This is an AbstractHandler that is called when the user wants to 
 * change the workspace.  This command is registered in the 
 * org.eclipse.ui.commands extension point of plugin.xml.
 * @author adammatthewsmith
 *
 */
public class SampleProjects extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), 
        "Create Sample","The next dialogue box will prompt you to select a "
        + "sample BioNetGen script, " 
        + "and it will allow you to select a project name if you wish. \n\n"
        + "Then it will load the complete project into your workspace."); 
		
		LoadSample pwd = new LoadSample(null); 
        int pick = pwd.open(); 
        if (pick == Dialog.CANCEL) 
            return null; 
         
		return null;
	}
}
