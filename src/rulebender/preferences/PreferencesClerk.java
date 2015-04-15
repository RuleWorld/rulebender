/**
 * This class is a static way to reference the preferences and other
 */
package rulebender.preferences;

import java.io.File;

import rulebender.Activator;
import rulebender.core.workspace.PickWorkspaceDialog;
import rulebender.preferences.views.MyFieldEditorPreferencePage;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rulebender.preferences.PreferencesClerk;
import rulebender.Activator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import rulebender.preferences.PreferencesClerk;
import rulebender.simulate.BioNetGenUtility;
import rulebender.Activator;

public class PreferencesClerk 
{
  
  static
  {
    String dir = "";
        //Activator
        //.getDefault()
        //.getPreferenceStore()
        //.getString(MyFieldEditorPreferencePage.PREF_SIM_PATH);

    if (null == dir || "".equals(dir))
    {
//      Activator
//      .getDefault()
//      .getPreferenceStore()
//      .setDefault(MyFieldEditorPreferencePage.PREF_SIM_PATH, 
//          System.getProperty("user.dir") + MyFieldEditorPreferencePage.DEF_SIM_DIR);
      
      // TODO Check the os and set the default or set the actual value appropriately
    }
  }
	// The name of the main BNG file.
	private static String BNGName = "BNG2.pl";

	// The path from the root directory to the main BNG file.
	private static String BNGPathFromRoot = "BioNetGen-2.2.6";

	// Private constructor for static access only.
	private PreferencesClerk() {
		throw new AssertionError();
	}

	/**
	 * This method returns the path to the 'BNGName' file, but not including that
	 * file.
	 * 
	 * @return String path to 'BNGName'
	 */

	public static String getDefaultBNGPath() 
	{
//           return Activator.getDefault().getPreferenceStore().getString("SIM_PATH")
//	   + System.getProperty("file.separator") + BNGPathFromRoot;
		return  System.getProperty("user.dir")
		    + System.getProperty("file.separator")
                    + BNGPathFromRoot
		    + System.getProperty("file.separator");
	}
	/**
	 * Returns the name of the main BNG file in 'BNGName'.
	 * 
	 * @return String name of main bng file.
	 */
	public static String getBNGName() {
		return BNGName;
	}

	/**
	 * Returns the user supplied root path to the BioNetGen directory. This does
	 * not include the path from the root to 'BNGName' or 'BNGName' itself.
	 * 
	 * @return String root path to BNG directory.
	 */
	public static String getBNGRoot() {
//		return Activator.getDefault().getPreferenceStore().getString("SIM_PATH")
//		    + System.getProperty("file.separator");
		// System.out.println("calling getBNGRoot()");
		return  System.getProperty("user.dir");
	}

	/**
	 * Returns the entire path from the root to the 'BNGName' file.
	 * 
	 * @return String path to main bng file.
	 */
	
	public static String getUserBNGPath() {
		return Activator.getDefault().getPreferenceStore().getString("SIM_PATH")
				+ System.getProperty("file.separator");
	}
	
	public static String getFullUserBNGPath() {
		return getUserBNGPath() + BNGName;
	}
	public static String getFullDefaultBNGPath() {
		return getDefaultBNGPath() + BNGName;
	}
	public static String getBNGPath() {

		boolean prereq = BioNetGenUtility.checkPreReq();
		if (prereq) {
			 String     bngPath2  = PreferencesClerk.getFullUserBNGPath();		 
			 // System.out.println(" clerk, bngPath2 " + bngPath2);
	   		 boolean bng2 = validateBNGPath(bngPath2);
			 if (bng2) { return PreferencesClerk.getUserBNGPath(); }

	  	     String     bngPath   = PreferencesClerk.getFullDefaultBNGPath();
		     boolean bng  = validateBNGPath(bngPath);
		     // System.out.println(" clerk, bngPath " + bngPath);
		     if (bng) { return PreferencesClerk.getDefaultBNGPath(); }
		}
		
		return "No_Valid_Path_";  //  This is not a good way to handle the situation, but it's
		               //  better than what we had before.
	}
	public static String getFullBNGPath() {
		return getBNGPath() + BNGName;
	}

	
	private static boolean validateBNGPath(String path) {
		if ((new File(path)).exists()) {
			return true;
		}
		return false;
	}

	
	
	public static OS getOS() {
		String stemp = System.getProperty("os.name");

		if (stemp.contains("Windows") || stemp.contains("WINDOWS")
		    || stemp.contains("windows")) {
			return OS.WINDOWS;
		} else if (stemp.contains("Mac") || stemp.contains("MAC")
		    || stemp.contains("mac")) {
			return OS.OSX;
		} else {
			return OS.LINUX;
		}

	}

	public static String getWorkspace() {
		return PickWorkspaceDialog.getLastSetWorkspaceDirectory();
	}
}
