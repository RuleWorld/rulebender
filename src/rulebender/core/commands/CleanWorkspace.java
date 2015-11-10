package rulebender.core.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import rulebender.core.workspace.PickWorkspaceDialog;

/**
 * This is an AbstractHandler that is called when the user wants to 
 * change the workspace.  This command is registered in the 
 * org.eclipse.ui.commands extension point of plugin.xml.
 * @author adammatthewsmith
 *
 */
public class CleanWorkspace extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
        // This seems like an ugly and unmodular way to change the
        // behaviour of PickWorkspaceDialog(), but this seemed like
        // the easiest and fastest way to pass data into the okPressed
        // method, which has no parameters.
		System.out.println("\n\n-------------------------------------------------");
		System.out.println("Entering CleanWorkspace");
        PickWorkspaceDialog.willCleanWorkspace(true);
		PickWorkspaceDialog pwd = new PickWorkspaceDialog(true, null); 
		System.out.println("Just finished PickWorkspaceDialog");
        int pick = pwd.open(); 
        if (pick == Dialog.CANCEL) 
            return null; 
        PickWorkspaceDialog.willCleanWorkspace(false);
 
        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Recover Workspace", 
     	"This will restart RuleBender with default settings and it will preserve your projects."); 

        // These next two commands accomplish the same task, but I don't trust the first one,
        PickWorkspaceDialog.willSwitchRestart();        
        String rtcode = PickWorkspaceDialog.writeWorkspaceVersion(
        		        PickWorkspaceDialog.getLastSetWorkspaceDirectory(),2);               	  
        
        // restart client 
        PlatformUI.getWorkbench().restart(); 
		System.out.println("-------------------------------------------------\n\n");
        
		return null;
	}
}
