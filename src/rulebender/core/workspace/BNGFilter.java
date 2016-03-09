package rulebender.core.workspace;

import java.io.*;
 
/**
 * A class that implements the Java FileFilter interface.
 */

public class BNGFilter implements FileFilter
{
    public boolean accept(File file)
    {
    	if (file.getName().startsWith(".")) {
            return false;    		
    	} else {
    		if (file.isDirectory()) {
                    return false;    		
    		} else {
                if (file.getName().endsWith(".bngl")) {
    		        if (file.exists()) {    		    	
                        return true;
                    } else {
                        return false;
    		        }
                }  else {
                   return false;
                }
    		}
    	}
    }
}