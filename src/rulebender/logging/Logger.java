package rulebender.logging;
import rulebender.preferences.PreferencesClerk;

public class Logger
{
  public static enum LOG_LEVELS
    {
      INFO,
      WARNING,
      ERROR,
      SEVERE
    };
  
    /**
     * Just writing formatted logs to the console for now. Should update
     * with writing to a log file later.   
     * 
     * @param level
     * @param className
     * @param message
     */
    
    
    
  public static void log(LOG_LEVELS level, Class className, String message)
  {
	 if ("minimal" != PreferencesClerk.getOutputSetting()) {
       String[] split = className.getName().split("\\."); 
       System.out.println("[" + level +" | ..." + split[split.length-2] + "." + 
                      split[split.length - 1]+ "]: " + message);
     }
   }
}