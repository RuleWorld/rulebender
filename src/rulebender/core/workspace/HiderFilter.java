package rulebender.core.workspace;

import java.io.*;
import rulebender.preferences.PreferencesClerk;
 
/**
 * A class that implements the Java FileFilter interface.
 */

public class HiderFilter implements FileFilter
{
    public boolean accept(File file)
    {   
      String hiderStr = "_recovery_" + PreferencesClerk.getRuleBenderVersion();
      // System.out.println("HiderFilter looking at file: " + file.toString());
    	if (!file.getName().endsWith(hiderStr)) {
            return false;    		
    	} else {
    	  if (file.getName().startsWith(".")) {
              return false;    		
        	} else {
    		  if (!file.isDirectory()) {
                  return false;    		
    		  } else {
    			String ProjStr = file.toString() + System.getProperty("file.separator") + ".project";
    		    // System.out.println(" Considering:  " + ProjFile);
    		      File ProjFile = new File(ProjStr);
    		      if (!ProjFile.exists()) {    		    	
                    return false;
    		      } else {
                    return true;
    		      }
    		  }
    	    }
    	}
   }
}