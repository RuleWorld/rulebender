package rulebender;

import java.io.*;
import java.io.File;
import java.io.FileFilter;

import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import rulebender.preferences.PreferencesClerk;
import rulebender.core.workspace.PickWorkspaceDialog;
import rulebender.core.workspace.HiderFilter;

/**
 * Eclipse RCP Generated Class.  This class manages the windows of the 
 * workbench.  You can interact with the various configurer objects that
 * are passed in to the initialization methods. Some manual settings
 * have been changed here: see comments in the code.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) 
	{
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() 
	{	
		// The IWorkbenchWindowConfigurer instancse is used to set 
		// many options for startup.
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		
		// Set the size of the window.
		configurer.setInitialSize(new Point(1024, 768));
		
		// Use the coolbar toolbar?  The coolbar let the user move the buttons around.
		// It is what is used by eclipse by default I think.
		configurer.setShowCoolBar(false);
		
		// Use the status line?  The status line is in the bottom left of the window
		// and can be used by any view to display information.
		configurer.setShowStatusLine(true);
		
		// Show the progress indicator for jobs registered
		// through the jobs api?  This shows a progress indicator on the window
		// when the Jobs API is used.  
		configurer.setShowProgressIndicator(true);
		
		// Show the perspectives bar? The Perspectives bar displays
		// perspectives that the user can switch to. 
		configurer.setShowPerspectiveBar(true);
				
		// Set the name of the application.
		configurer.setTitle("RuleBender " + PreferencesClerk.getRuleBenderVersionNumber());
		// was configurer.setTitle("RuleBender");
		
		// The Preference Store manages program options that are stored
		// as key value pairs.
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		// Set the buttons that appear in the perspective switcher.  These
		// id values are for the modeling, simulation, and analysis perspectives.
		prefStore.setValue(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, 
		    "rulebender.perspective");
		    		
		// Do not use the traditionaly style tabs.  I think that this is just a cosmetic option.
		prefStore.setValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, false);

		// If there were any projects that were renamed before startup, in order to get past
		// corruption of the .snap files or other files, those projects will now be recreated,
		// and the original names of the project directories will be restored.
        restoreProjects();		

	}
	
/*
 * Restore any projects that were saved before startup, in order to work around corruption
 * of the .snap files or other files. 
 * 
 * Right now, this routine identifies project directories, renames them so that they have their
 * original name and then creates an Eclipse project with the same name.  So right now, we're
 * relying on the fact that Eclipse does not wipe a new project directory clean at the time
 * that the project is created.  If this changes in the future, we may need to reverse the 
 * order of operations below.  We may need to create the project first, and then recursively
 * copy the original project files into the new project directory. 
 */
	private void restoreProjects() {
		 String workspace_directory = PickWorkspaceDialog.getLastSetWorkspaceDirectory();
	     String markerStr = "_recovery_" + PreferencesClerk.getRuleBenderVersion();	
	     int marklen = markerStr.length();
	    	
	     System.out.println("restoring projects   workspace = " + workspace_directory);
	     
	     // Put the correct version stamp into this workspace.   
	     String file_contents = PickWorkspaceDialog.checkWorkspaceVersion(workspace_directory);
	     System.out.println("999 " + file_contents);
	     if (file_contents.equals("CleanWorkspace")) {
           String rtcode = PickWorkspaceDialog.writeWorkspaceVersion(workspace_directory,1);
           if (rtcode != null) {
        	   System.out.println(" Unable to upgrade your workspace.  Please check the read/write");
        	   System.out.println("authority for the workspace.");
           }
	     }
	     System.out.println("999.1");
	    	
	      // This should probably return a boolean to indicate whether it was successful or not.
	      File dir = new File(workspace_directory);
	      // list the files using our FileFilter
	      File[] files = dir.listFiles(new HiderFilter());
	      for (File f : files)
	      {
	          System.out.println("Restoring project: " + f.getName());
	          int fnamelen = f.toString().length();
	          String newStr = f.toString().substring(0,fnamelen-marklen);
	          // System.out.println("new name: " + newStr);
	          File tempDir = new File(newStr);
		      System.out.println(" Deleting directory " + tempDir.getName());
		      if (!tempDir.exists()) {
		    	  //  This next line is a little bit of a fudge to get things to work cleanly.
		    	  //  Ideally, you would first create the project, then delete the project 
		    	  //  directory and replace it with the original.  This way the "create" command
		    	  //  can never corrupt anything in the original directory.  Unfortunately,
		    	  //  if you do it this way, you get no files listed in the project, and you 
		    	  //  need to do yet another restart.  So renaming the directory here, rather 
		    	  //  than after the project creation, allows us to avoid the second restart.
		          f.renameTo(tempDir);          			    	  
		      } else {
		    	  System.out.println(" Unable to recover " + tempDir.getName() + " If files ");
		    	  System.out.println(" are missing, please check " + f.toString());
		      }

		
      		  try {
			
			    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			    IProject project = root.getProject(tempDir.getName());
			
			    if (project.exists()) {
				  System.out.println(" Project " + tempDir.getName() + " already exists. It will not be recreated. ");
			    } else {
			      project.create(null);
			      project.open(null);
			    }
			  } catch(CoreException ce) {
				System.out.println(" Core exception while recreating project " + tempDir.getName());
			  }
	      }
	      System.out.println(" Done processing files ");
	}
	
	
	public void postWindowCreate()
	{
		// The PreferenceManager manages the preference panes in the program that
		// are accessible by clicking "RuleBender" (OSX) or "Window" in the menu and then
		// selecting "Preferences...".
		PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();
		
		// Remove the team preferences.  Any plugins that are included in the rulebender product
		// can contribute to the PreferenceManager, so we must manually remove the ones that we do 
		// not want to include.
		pm.remove("org.eclipse.team.ui.TeamPreferences");
		
		// Activate the Contact Map so that it's not concealed by the Spieces Graph 
		// or the Simulation View.
	    try {
		  IWorkbenchPart   wpart     = (IWorkbenchPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("rulebender.contactmap.view.ContactMapView");
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(wpart);
		} catch (Exception e2) {
          System.out.printf("There was an error while attempting to activate the Contact Map.");		
          e2.printStackTrace();
        }
	}
}
