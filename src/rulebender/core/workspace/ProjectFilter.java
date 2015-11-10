package rulebender.core.workspace;

import java.io.*;
 
/**
 * A class that implements the Java FileFilter interface.
 */

public class ProjectFilter implements FileFilter
{
    public boolean accept(File file)
    {
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