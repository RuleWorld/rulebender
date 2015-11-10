package rulebender.core.workspace;

import java.io.*;
 
/**
 * A class that implements the Java FileFilter interface.
 */

public class DirFilter implements FileFilter
{
    public boolean accept(File file)
    {   // System.out.println(" Considering:  " + file.getName());
    	if (file.getName().endsWith(".snap")) {
            return true;    		
    	} else {
            return false;    		
        }
    }
}
