package rulebender.core.workspace;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

// import org.eclipse.ui;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import za.co.quirk.layout.LatticeData;
import za.co.quirk.layout.LatticeLayout;
import rulebender.preferences.PreferencesClerk;
import rulebender.core.workspace.ProjectFilter;
import rulebender.core.workspace.DirFilter;
import rulebender.Activator;
import rulebender.logging.Logger;


/**
 * Dialog that lets/forces a user to enter/select a workspace that will be used when saving all configuration files and
 * settings. This dialog is shown at startup of the GUI just after the splash screen has shown.
 * 
 * @author Emil Crumhorn
 */
public class PickWorkspaceDialog extends TitleAreaDialog {

    // the name of the file that tells us that the workspace directory belongs to our application
    public static final String  WS_IDENTIFIER          = ".our_rcp_workspace";

    // you would probably normally define these somewhere in your Preference Constants
    private static final String _KeyWorkspaceRootDir   = "wsRootDir";
    private static final String _KeyRememberWorkspace  = "wsRemember";
    private static final String _KeyLastUsedWorkspaces = "wsLastUsedWorkspaces";
    
    private static final String _KeyBioNetGenRootDir   = "bngRootDir";
    private static final String _KeyRememberBioNetGen  = "bngRemember";
    private static final String _KeyLastUsedBioNetGen  = "bngLastUsedWorkspaces";

    private static final String _KeyDidSwitchRestart = "wsSwitchRestart";
    private static final String _KeyCleanWorkspace   = "wsCleanWorkspace";

    // this are our preferences we will be using as the IPreferenceStore is not available yet
    private static Preferences  _preferences           = Preferences.userNodeForPackage(PickWorkspaceDialog.class);

    // various dialog messages
    private static final String _StrMsg               = "Your workspace is where settings and various important files will be stored.  BioNetGen is a simulator that's installed separately.";
    private static final String _StrMsgClean          = "Your workspace is where settings and various important files will be stored.";
    private static final String _StrInfo              = "Please select a directory that will be the workspace root";
    private static final String _StrError             = "You must set a directory";

    // our controls
    private Combo               _workspacePathCombo;
    private List<String>        _lastUsedWorkspaces;
    private Button              _RememberWorkspaceButton;

    private Combo               _BioNetGenPathCombo;
    private List<String>        _lastUsedBioNetGen;
    private Button              _RememberBioNetGenButton;

    // used as separator when we save the last used workspace locations
    private static final String _SplitChar             = "#";
    // max number of entries in the history box
    private static final int    _MaxHistory            = 20;

    private boolean             _switchWorkspace;

    // whatever the user picks ends up on this variable
    private String              _selectedWorkspaceRootLocation;

    /**
     * Creates a new workspace dialog with a specific image as title-area image.
     * 
     * @param switchWorkspace true if we're using this dialog as a switch workspace dialog
     * @param wizardImage Image to show
     */
    public PickWorkspaceDialog(boolean switchWorkspace, Image wizardImage) {
        super(Display.getDefault().getActiveShell());
        this._switchWorkspace = switchWorkspace;
        if (wizardImage != null) {
            setTitleImage(wizardImage);
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        if (_switchWorkspace) {
        	if (isCleanWorkspace()) {
              newShell.setText("Recover Workspace");
        	} else {
              newShell.setText("Switch Workspace");
        	}
        } else {
            newShell.setText("Workspace Selection");
        }
    }

        
    /**
     * Returns whether the user selected "clean workspace" 
     * 
     * @return
     */
    public static boolean isCleanWorkspace() {
        return _preferences.getBoolean(_KeyCleanWorkspace, false);
    }
    
    public static void willCleanWorkspace(boolean true_or_false)
    {  
    	_preferences.putBoolean(_KeyCleanWorkspace, true_or_false);
    }

    
    /**
     * Returns whether the user selected "remember workspace" in the preferences
     * 
     * @return
     */
    public static boolean isRememberWorkspace() {
        return _preferences.getBoolean(_KeyRememberWorkspace, false);
    }
    
    public static void willSwitchRestart()
    {
    	_preferences.putBoolean(_KeyDidSwitchRestart, true);
    }
    
    /**
     * Called to see if we just switched the workspace and have restarted because of it.  
     */
    public static boolean didSwitchRestart()
    {
    	// Check to see if we did restart from a switch.
    	// The getBoolean will use the second parameter as the default
    	// if the key is not used yet.
    	if(_preferences.getBoolean(_KeyDidSwitchRestart, false))
    	{
    		// If we did restart, then set the value to false
    		// and return true.
    		_preferences.putBoolean(_KeyDidSwitchRestart, false);
    		return true;
    	}
    	
    	// if we did not restart after a switch then return false.
    	return false;
    }
    
    /**
     * Returns the last set workspace directory from the preferences
     * 
     * @return null if none
     */
    public static String getLastSetWorkspaceDirectory() {
        return _preferences.get(_KeyWorkspaceRootDir, null);
    }
    public static String getLastSetBioNetGenDirectory() {
        return _preferences.get(_KeyBioNetGenRootDir, null);
    }
    public static String setLastSetBioNetGenDirectory(String keyString) {
        _preferences.put(_KeyBioNetGenRootDir, keyString);
        return keyString; 
    }

    
    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle("Pick Workspace");
        if (!isCleanWorkspace()) { setMessage(_StrMsg); } 
        else                     { setMessage(_StrMsgClean); }

        try {
            Composite inner = new Composite(parent, SWT.NONE);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            inner.setLayout(gridLayout);

            // label on left
            CLabel label = new CLabel(inner, SWT.NONE);
            label.setText("Workspace Root Path");
            label.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));

            // combo in middle
            _workspacePathCombo = new Combo(inner, SWT.BORDER);
            _workspacePathCombo.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
            String wsRoot = _preferences.get(_KeyWorkspaceRootDir, "");
            if (wsRoot == null || wsRoot.length() == 0) {
                wsRoot = getWorkspacePathSuggestion();
            }
            _workspacePathCombo.setText(wsRoot == null ? "" : wsRoot);


            String lastUsed = _preferences.get(_KeyLastUsedWorkspaces, "");
            _lastUsedWorkspaces = new ArrayList<String>();
            if (lastUsed != null) {
                String[] all = lastUsed.split(_SplitChar);
                for (String str : all)
                    _lastUsedWorkspaces.add(str);
            }
            for (String last : _lastUsedWorkspaces)
                _workspacePathCombo.add(last);

            // browse button on right
            Button browse = new Button(inner, SWT.PUSH);
            browse.setText("Browse...");

                                                                                        
            browse.setSelection(_preferences.getBoolean(_KeyRememberWorkspace, false));

            browse.addListener(SWT.Selection, new Listener() { 
                public void handleEvent(Event event) {
                    DirectoryDialog dd = new DirectoryDialog(getParentShell());
                    dd.setText("Select Workspace Root");
                    dd.setMessage(_StrInfo);
                    dd.setFilterPath(_workspacePathCombo.getText());
                    String pick = dd.open();
                    if (pick == null && _workspacePathCombo.getText().length() == 0) {
                        setMessage(_StrError, IMessageProvider.ERROR);
                    } else {
                        setMessage(_StrMsg);
                        _workspacePathCombo.setText(pick);
                    }
                }

            });

            
            // checkbox below
            CLabel label2 = new CLabel(inner, SWT.NONE);
            label2.setText("");
            _RememberWorkspaceButton = new Button(inner, SWT.CHECK);
            _RememberWorkspaceButton.setText("Remember All Workspace Directories");
            _RememberWorkspaceButton.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
            _RememberWorkspaceButton.setSelection(_preferences.getBoolean(_KeyRememberWorkspace, false));

            
            
            Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(),
              " Creating PickWorkspaceDialog  isCleanWorkspace = " 
              + isCleanWorkspace());
            
            
            if (!isCleanWorkspace()) {
            
            // BioNetGen Location            
            // label on left
            CLabel label4 = new CLabel(inner, SWT.NONE);  label4.setText("");  
            CLabel label3 = new CLabel(inner, SWT.NONE);
            label3.setText("BioNetGen Root Path");
            label3.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));

            // combo in middle
            _BioNetGenPathCombo = new Combo(inner, SWT.BORDER);
            _BioNetGenPathCombo.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
            String bngRoot = _preferences.get(_KeyBioNetGenRootDir, "");
            
            bngRoot = PreferencesClerk.getBNGPath();
            _BioNetGenPathCombo.setText(bngRoot == null ? "" : bngRoot);


            
            String lastBNGUsed = _preferences.get(_KeyLastUsedBioNetGen, "");
            _lastUsedBioNetGen = new ArrayList<String>();
            if (lastBNGUsed != null) {
                String[] bngall = lastBNGUsed.split(_SplitChar);
                for (String str : bngall)
                    _lastUsedBioNetGen.add(str);
            }
            for (String last : _lastUsedBioNetGen)
                _BioNetGenPathCombo.add(last);

            
            
            
            // browse button on right
            Button browse2 = new Button(inner, SWT.PUSH);
            browse2.setText("Browse...");

                                                                                        
            browse2.setSelection(_preferences.getBoolean(_KeyBioNetGenRootDir, false));

            browse2.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    DirectoryDialog dd = new DirectoryDialog(getParentShell());
                    dd.setText("Select BioNetGen Directory");
                    dd.setMessage(_StrInfo);
                    dd.setFilterPath(_BioNetGenPathCombo.getText());
                    String pick = dd.open();
                    if (pick == null && _BioNetGenPathCombo.getText().length() == 0) {
                        setMessage(_StrError, IMessageProvider.ERROR);
                    } else {
                        setMessage(_StrMsg);
                        _BioNetGenPathCombo.setText(pick);
                    }
                }

            });


            
            // checkbox below
            CLabel label5 = new CLabel(inner, SWT.NONE);
            label5.setText("");
            _RememberBioNetGenButton = new Button(inner, SWT.CHECK);
            _RememberBioNetGenButton.setText("Remember All BioNetGen Directories");
            _RememberBioNetGenButton.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));
            _RememberBioNetGenButton.setSelection(_preferences.getBoolean(_KeyRememberBioNetGen, false));

            
			Activator.getDefault().getPreferenceStore().setDefault("SIM_PATH",PickWorkspaceDialog.getLastSetBioNetGenDirectory());
                                                       
            
            }
            
            return inner;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }
    
    /**
     * Returns whatever path the user selected in the dialog.
     * 
     * @return Path
     */
    
    
    public String getSelectedWorkspaceLocation() {
        return _selectedWorkspaceRootLocation;
    }

    // suggests a path based on the user.home/temp directory location
    private String getWorkspacePathSuggestion() {
        StringBuffer buf = new StringBuffer();

        String uHome = System.getProperty("user.home");
        if (uHome == null) {
            uHome = "c:" + File.separator + "temp";
        }

        buf.append(uHome);
        buf.append(File.separator);
        buf.append("RuleBender-workspace");

        return buf.toString();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        // clone workspace needs a lot of checks
        Button clone = createButton(parent, IDialogConstants.IGNORE_ID, "Clone", false);
        clone.addListener(SWT.Selection, new Listener() {

        	public void handleEvent(Event arg0) {
                try {
                    String txt = _workspacePathCombo.getText();
                    File workspaceDirectory = new File(txt);
                    if (!workspaceDirectory.exists()) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
                                "The currently entered workspace path does not exist. Please enter a valid path.");
                        return;
                    }

                    if (!workspaceDirectory.canRead()) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
                                "The currently entered workspace path is not readable. Please check file system permissions.");
                        return;
                    }

                    // check for workspace file (empty indicator that it's a workspace)
                    File wsFile = new File(txt + File.separator + WS_IDENTIFIER);
                    if (!wsFile.exists()) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
                                "The currently entered workspace path does not contain a valid workspace.");
                        return;
                    }

                    DirectoryDialog dd = new DirectoryDialog(Display.getDefault().getActiveShell());
                    dd.setFilterPath(txt);
                    String directory = dd.open();
                    if (directory == null) { return; }

                    File targetDirectory = new File(directory);
                    if (targetDirectory.getAbsolutePath().equals(workspaceDirectory.getAbsolutePath())) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Source and target workspaces are the same");
                        return;
                    }

                    // recursive check, if new directory is a subdirectory of our workspace, that's a big no-no or we'll
                    // create directories forever
                    if (isTargetSubdirOfDir(workspaceDirectory, targetDirectory)) {
                        MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Target folder is a subdirectory of the current workspace");
                        return;
                    }

                    try {
                        copyFiles(workspaceDirectory, targetDirectory);
                    } catch (Exception err) {
                        MessageDialog
                                .openError(Display.getDefault().getActiveShell(), "Error", "There was an error cloning the workspace: " + err.getMessage());
                        return;
                    }

                    boolean setActive = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Workspace Cloned",
                            "Would you like to set the newly cloned workspace to be the active one?");
                    if (setActive) {
                        _workspacePathCombo.setText(directory);
                    }
                } catch (Exception err) {
                           MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "There was an internal error, please check the logs");
                    err.printStackTrace();
                }
            }
        });
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    // checks whether a target directory is a subdirectory of ourselves
    private boolean isTargetSubdirOfDir(File source, File target) {
        List<File> subdirs = new ArrayList<File>();
        getAllSubdirectoriesOf(source, subdirs);
        return subdirs.contains(target);
    }
   

    // helper for above
    private void getAllSubdirectoriesOf(File target, List<File> buffer) {
        File[] files = target.listFiles();
        if (files == null || files.length == 0) return;

        for (File f : files) {
            if (f.isDirectory()) {
                buffer.add(f);
                getAllSubdirectoriesOf(f, buffer);
            }
        }
    }

    /**
     * This function will copy files or directories from one location to another. note that the source and the
     * destination must be mutually exclusive. This function can not be used to copy a directory to a sub directory of
     * itself. The function will also have problems if the destination files already exist.
     * 
     * @param src -- A File object that represents the source for the copy
     * @param dest -- A File object that represents the destination for the copy.
     * @throws IOException if unable to copy.
     */
    public static void copyFiles(File src, File dest) throws IOException {
        // Check to ensure that the source is valid...
        if (!src.exists()) {
            throw new IOException("Can not find source: " + src.getAbsolutePath());
        } else if (!src.canRead()) { // check to ensure we have rights to the source...
            throw new IOException("Cannot read: " + src.getAbsolutePath() + ". Check file permissions.");
        }
        // is this a directory copy?
        if (src.isDirectory()) {
            if (!dest.exists()) { // does the destination already exist?
                // if not we need to make it exist if possible (note this is mkdirs not mkdir)
                if (!dest.mkdirs()) { throw new IOException("Could not create direcotry: " + dest.getAbsolutePath()); }
            }
            // get a listing of files...
            String list[] = src.list();
            // copy all the files in the list.
            for (int i = 0; i < list.length; i++) {
                File dest1 = new File(dest, list[i]);
                File src1 = new File(src, list[i]);
                copyFiles(src1, dest1);
            }
        } else {
            // This was not a directory, so lets just copy the file
            FileInputStream fin = null;
            FileOutputStream fout = null;
            byte[] buffer = new byte[4096]; // Buffer 4K at a time (you can change this).
            int bytesRead;
            try {
                // open the files for input and output
                fin = new FileInputStream(src);
                fout = new FileOutputStream(dest);
                // while bytesRead indicates a successful read, lets write...
                while ((bytesRead = fin.read(buffer)) >= 0) {
                    fout.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) { // Error copying file...
                IOException wrapper = new IOException("Unable to copy file: " + src.getAbsolutePath() + "to" + dest.getAbsolutePath());
                wrapper.initCause(e);
                wrapper.setStackTrace(e.getStackTrace());
                throw wrapper;
            } finally { // Ensure that the files are closed (if they were open).
                if (fin != null) {
                    fin.close();
                }
                if (fout != null) {
                    fin.close();
                }
            }
        }
    }

    @Override
    protected void okPressed() {
        String str    = _workspacePathCombo.getText();
        if (str.length() == 0) {
            setMessage(_StrError, IMessageProvider.ERROR);
            return;
        }

        String strBNG=null;
        if (!isCleanWorkspace()) {
          strBNG = _BioNetGenPathCombo.getText(); }

        String ret = checkWorkspaceDirectory(getParentShell(), str, true, true);
        if (ret != null) {
        	if (ret.equals("CANCEL")) { ret = "Click Cancel Again To Close This Dialog Box"; }
            setMessage(ret, IMessageProvider.ERROR);
            //  The behaviour here is a bit surprizing.  You click cancel once and you get 
            //  the message above inserted into the "chose workspace" dialog box.  Click 
            //  CANCEL again and RuleBender will exit. 
            return;
        }

        // save it so we can show it in combo later
        _lastUsedWorkspaces.remove(str);

        if (!_lastUsedWorkspaces.contains(str))    {  _lastUsedWorkspaces.add(0, str); }
       
        // deal with the max history
        if (_lastUsedWorkspaces.size() > _MaxHistory) {
            List<String> remove = new ArrayList<String>();
            for (int i = _MaxHistory; i < _lastUsedWorkspaces.size(); i++) {
                remove.add(_lastUsedWorkspaces.get(i));
            }

            _lastUsedWorkspaces.removeAll(remove);
        }

        
        // create a string concatenation of all our last used workspaces
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < _lastUsedWorkspaces.size(); i++) {
            buf.append(_lastUsedWorkspaces.get(i));
            if (i != _lastUsedWorkspaces.size() - 1) {
                buf.append(_SplitChar);
            }
        }

        
        
        // save them onto our preferences
        _preferences.putBoolean(_KeyRememberWorkspace, _RememberWorkspaceButton.getSelection());
        if (_RememberWorkspaceButton.getSelection()) {
          _preferences.put(_KeyLastUsedWorkspaces, buf.toString());
        } else {
          _preferences.put(_KeyLastUsedWorkspaces, "");        	
        }

        
        if (!isCleanWorkspace()) {        
          _lastUsedBioNetGen.remove(strBNG);
          if (!_lastUsedBioNetGen.contains(strBNG))  {  _lastUsedBioNetGen.add(0, strBNG); }
        
          // deal with the max history
          if (_lastUsedBioNetGen.size() > _MaxHistory) {
              List<String> remove = new ArrayList<String>();
              for (int i = _MaxHistory; i < _lastUsedBioNetGen.size(); i++) {
                  remove.add(_lastUsedBioNetGen.get(i));
              }

              _lastUsedBioNetGen.removeAll(remove);
          }

        
          // create a string concatenation of all our last used workspaces
          StringBuffer bufBNG = new StringBuffer();
          for (int i = 0; i < _lastUsedBioNetGen.size(); i++) {
              bufBNG.append(_lastUsedBioNetGen.get(i));
              if (i != _lastUsedBioNetGen.size() - 1) {
                  bufBNG.append(_SplitChar);
              }
          }
  

          // save them onto our preferences
          _preferences.putBoolean(_KeyRememberBioNetGen, _RememberBioNetGenButton.getSelection());
          if (_RememberBioNetGenButton.getSelection()) {
            _preferences.put(_KeyLastUsedBioNetGen, bufBNG.toString()); 
           } else {
            _preferences.put(_KeyLastUsedBioNetGen, ""); 
           }
        }

        // now create it (Note that if it already exists, this will be a no-op.)
        boolean ok = checkAndCreateWorkspaceRoot(str);
        if (!ok) {
            setMessage("The workspace could not be created, please check the error log");
            return;
        }

        // here we set the location so that we can later fetch it again        
        _selectedWorkspaceRootLocation = str;

        // and on our preferences as well
        _preferences.put(_KeyWorkspaceRootDir, str);
        if (!isCleanWorkspace()) {
          _preferences.put(_KeyBioNetGenRootDir, strBNG);
          Activator.getDefault().getPreferenceStore().setValue("SIM_PATH",PickWorkspaceDialog.getLastSetBioNetGenDirectory());
        }


        super.okPressed();
    }

    
    
    /**
     * This assumes that the WS_IDENTIFIER file exisits, and it writes the RuleBender version
     * into it.
     * 
     * @return null if everything is ok, or an error message if not.
     */
    public static String writeWorkspaceVersion(String workspaceLocation,int what_to_write) {
        String rtstring = null;
    	Writer   writer = null;
    	String   string_to_write = null;
    	String   where_to_write = workspaceLocation + File.separator + WS_IDENTIFIER;
    	
   	    if (what_to_write == 1) {
	       string_to_write = PreferencesClerk.getRuleBenderVersion() + "\n";    		
    	}
   	    // The purpose of having the "CleanWorkspace" tag, is to invoke a deletion of the 
   	    // .metadata directory after an ordinary start of RuleBender.
    	if (what_to_write == 2) {
          string_to_write = "CleanWorkspace\n";    		
     	}

    	try {
    	    writer = new BufferedWriter(new OutputStreamWriter(
      	             new FileOutputStream(where_to_write)));
    	    writer.write(string_to_write);
    	} catch (IOException ex) {
        	  rtstring = "There was a problem initializing this workspace.";
    	} finally {
    	   try {writer.close();} catch (Exception ex) {
  	      	  rtstring = "There was a problem initializing this workspace.";
           }
    	}
    	
    	return rtstring;
    }
    

    
    /**
     * This assumes that the WS_IDENTIFIER file exisits, and it looks inside to see what version
     * of RuleBender was used to create the workspace.  The workspace is ok, if the version of
     * the workspace, is consistent with the current instantiation of RuleBender. 
     * 
     * @return null if everything is ok, or error string if not
     */
    public static String checkWorkspaceVersion(String workspaceLocation) {
        String rtstring = null;        
        String file_contents = null;
   
        try { 
          BufferedReader br = new BufferedReader(new FileReader(workspaceLocation + File.separator + WS_IDENTIFIER));
          try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
              sb.append(line);
              sb.append(System.lineSeparator());
              line = br.readLine();
            }
            file_contents = sb.toString().trim();
          } finally {
           br.close();
          } 
        } catch (IOException ioe) {
          rtstring = "The selected directory seems like a corrupted workspace.";
          return rtstring;
        }

        return file_contents;        
  }



    
    /**
     * This assumes that the WS_IDENTIFIER file exisits, and it looks inside to see what version
     * of RuleBender was used to create the workspace.  The workspace is ok, if the version of
     * the workspace, is consistent with the current instantiation of RuleBender. 
     * 
     * @return null if everything is ok, or error string if not
     */
    public static String checkWorkspaceVersionAndReact(String workspaceLocation) {
        String rtstring = null;        
        String file_contents = null;
        
        file_contents = checkWorkspaceVersion(workspaceLocation);
       
        boolean need_to_upgrade = false;
        if (file_contents == null) {
          need_to_upgrade = true;
        } else {
          if (file_contents.length() == 0) {
              need_to_upgrade = true;
          } else {
          	if (file_contents.equals("CleanWorkspace")) {
          	 // This call to RemoveMetaData is executed after the restart()
                RemoveMetaData(workspaceLocation);
                // need_to_upgrade = true;               		  
        	} else { 
        	  if (!file_contents.equals(PreferencesClerk.getRuleBenderVersion())) {
                  need_to_upgrade = true;               		  
        	  } 
        	}
       	  }
        }
        
    if (need_to_upgrade || isCleanWorkspace()) {
    	rtstring = upgradeWorkspace(workspaceLocation);
    }
    
    return rtstring;
  }


        	
 /**
  * NULL means do an ordinary upgrade.
  * 
  * @return null if everything is ok, or an error string if not.
 */
 public static String upgradeWorkspace(String workspace_location) {
     String rtstring = null;
        	       	
     try {
       	if (!isCleanWorkspace()) {
           String mssgStr = "\nThis wizard will upgrade your workspace: \n" +
           workspace_location + "\n\n" +
           "The procedure has a small risk of failing and corrupting your\n" +
           "data, so you may wish to hit Cancel and make a copy of your \n" +
           "workspace before continuing.";
                
           MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
           "Your Workspace Was Created With A Previous Version Of RuleBender", null, mssgStr,
       	   MessageDialog.INFORMATION, new String[] { "Upgrade Workspace", "Cancel"}, 0);
       		                     	      	    
       	   int result = dialog.open();
       	   
       	   if (result == 0 ) { 
       		  String conMssg = "You have chosen to upgrade your workspace. This procedure gives\n" +
       		                   "you a way to eliminate unwanted tracebacks while preserving your\n" +
       		                   "projects.";
       		  boolean resultb = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), 
       		                   "Confirm The Upgrade Of Workspace", conMssg); 
       		  if (!resultb) { result = 1; }
           }
       		                     	  
           if (result == 0) {
              rtstring = "INVOKE_RECOVERY_OPTION_1";
           } else {
              rtstring = "CANCEL";
           }
     	} else{
           String mssgStr = "This wizard will recover your workspace. \n\n" +
                            "Click Recover to restore defaults and preserve your projects.\n";
           MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
              "Your Workspace Was Created With A Previous Version Of RuleBender", null, mssgStr,
           MessageDialog.INFORMATION, new String[] { "Recover", "Cancel"}, 0);
                        	      	    
           int result = dialog.open();
           
           if (result == 0) { 
               String conMssg = "Please confirm that you wish to recover your workspace. \n\n" +
                            "Click OK, and RuleBender will upgrade your workspace, while preserving \n" +
                            "your projects and restoring defaults. \n";
               boolean resultb = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), 
                            "Confirm Recover of Workspace", conMssg); 
               if (!resultb) { result = 1; }
           }
                        	  
           if (result == 0) {
              rtstring = "INVOKE_RECOVERY_OPTION_2";
           } else {
              rtstring = "CANCEL";
           }
        }

    } catch (Exception ioe) {
       rtstring = "RuleBender was not able to upgrade your workspace.";
    }
        
    return rtstring;
    }

    
    /**
     * Ensures a workspace directory is OK in regards of reading/writing, etc. This method will get called externally as well.
     * 
     * @param parentShell Shell parent shell
     * @param workspaceLocation Directory the user wants to use
     * @param askCreate Whether to ask if to create the workspace or not in this location if it does not exist already
     * @param fromDialog Whether this method was called from our dialog or from somewhere else just to check a location
     * @return null if everything is ok, or an error message if not
     */
    public static String checkWorkspaceDirectory(Shell parentShell, String workspaceLocation, boolean askCreate, boolean fromDialog) {
        File f = new File(workspaceLocation);
        if (!f.exists()) {
            if (askCreate) {
                boolean create = MessageDialog.openConfirm(parentShell, "New Directory", "The directory does not exist. Would you like to create it?");
                if (create) {
                    try {
                        f.mkdirs();
                        File wsDot = new File(workspaceLocation + File.separator + WS_IDENTIFIER);
                        wsDot.createNewFile();

                        String rtcode = writeWorkspaceVersion(workspaceLocation,1);
                        if (rtcode != null)       { return rtcode; }
                    
                    } catch (Exception err) {
                        return "Error creating directories, please check folder permissions";
                    }
                }

                if (!f.exists()) { return "The selected directory does not exist"; }
            }
        }
        
        if (!f.canRead()) { return "The selected directory is not readable"; }

        if (!f.isDirectory()) { return "The selected path is not a directory"; }

        
        String ret_string = checkWorkspaceVersionAndReact(workspaceLocation);
        
        if (ret_string != null) {
          if (ret_string.equals("INVOKE_RECOVERY_OPTION_1") || 
       		  ret_string.equals("INVOKE_RECOVERY_OPTION_2")) {
        	  // Note: This only makes sense if we have a directory to work with. If no valid
        	  // directory was named, RuleBender should have died above.
        	  if (!attempt_recovery(workspaceLocation,ret_string)) {
        		  return "Recovery failed. Please check the read/write permissions for the workspace.";   
        	  }
          } else {
        	  return ret_string;        	  
          }
        }
                          
        
        File wsTest = new File(workspaceLocation + File.separator + WS_IDENTIFIER);
        if (fromDialog) {
            if (!wsTest.exists()) {
                boolean create = MessageDialog
                        .openConfirm(
                                parentShell,
                                "New Workspace",
                                "The directory '"
                                        + wsTest.getAbsolutePath()
                                        + "' is not set to be a workspace. Do note that files will be created directly under the specified directory and it is suggested you create a directory that has a name that represents your workspace. \n\n" + ""
                                        		+ "Would you like to create a workspace in the selected location?");
                if (create) {
                    try {
                        f.mkdirs();
                        File wsDot = new File(workspaceLocation + File.separator + WS_IDENTIFIER);
                        wsDot.createNewFile();
                    } catch (Exception err) {
                        return "Error creating directories, please check folder permissions";
                    }
                } else {
                    return "Please select a directory for your workspace";
                }

                if (!wsTest.exists()) { return "The selected directory does not exist"; }

                return null;
            }
        } else {
            if (!wsTest.exists()) { return "The selected directory is not a workspace directory"; }
        }

        return null;
    }

    /**
     * Checks to see if a workspace exists at a given directory string, and if not, creates it. Also puts our
     * identifying file inside that workspace.
     * 
     * @param wsRoot Workspace root directory as string
     * @return true if all checks and creations succeeded, false if there was a problem
     */
    public static boolean checkAndCreateWorkspaceRoot(String wsRoot) {
        try {
            File fRoot = new File(wsRoot);
            if (!fRoot.exists()) return false;

            File dotFile = new File(wsRoot + File.separator + PickWorkspaceDialog.WS_IDENTIFIER);
            if (!dotFile.exists() && !dotFile.createNewFile()) return false;

            String   rtcode = writeWorkspaceVersion(wsRoot,1);
            if (rtcode != null)       { return false; }
                    
            return true;
        } catch (Exception err) {
            // as it might need to go to some other error log too
            err.printStackTrace();
            return false;
        }
    }


    /**
     * This tries to recover a corrupted workspace. 
     * 
     * @param wsRoot Workspace root directory as string
     * @return true if all checks and creations succeeded, false if there was a problem
     */
    public static boolean attempt_recovery(String wsRoot,String optionStr) {
       boolean rtcode = true;
        
       if (optionStr.equals("INVOKE_RECOVERY_OPTION_1")) {
           if (!RemoveSnapFiles(wsRoot)) { return false; }
       }
       if (optionStr.equals("INVOKE_RECOVERY_OPTION_2")) {
           if (!RemoveMetaData(wsRoot)) { return false; }
       }
       renameProjects(wsRoot);
                     
       return rtcode;
    }
    

    
    
    
    /*
     * This version of RemoveSnapFiles assumes that only the .snap files will be
     * deleted.
     */
    
    static public boolean RemoveSnapFiles(String workspace_directory)
    {
    	boolean rtcode = true;

    	
        String ResDir = workspace_directory + System.getProperty("file.separator") +
		           ".metadata" + System.getProperty("file.separator") +
		           ".plugins" + System.getProperty("file.separator") +
		           "org.eclipse.core.resources";

        
        File ResFile = new File(ResDir);
        if (ResFile.exists()) {
            File[] files = ResFile.listFiles(new DirFilter());            
            for (File f : files) {
                if(f.delete()){
        		}else{
        	    	rtcode = false;
        		}
            }
        }
        return rtcode;
    }

    
/*
 * This version of RemoveMetaData() assumes that the whole metadata directory will 
 * be deleted.
 */
    static public boolean RemoveMetaData(String workspace_directory)
    {
    	boolean rtcode = true;    	
    	
    	String ResDir = workspace_directory + System.getProperty("file.separator") + ".metadata";
        
        File ResFile = new File(ResDir);
        if (ResFile.exists()) {
        	rtcode = deleteDirectory(ResFile);
        }
        return rtcode;
    }

    
    /*
     * This is a generic utility function that recursively deletes
     * a directory, along with all contained files and subdirectories.
     * It's not tailored to a specific purpose, so it could be used
     * elsewhere in RuleBender.
     */
    public static boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }
    
    
    
    static public void renameProjects(String workspace_directory)
    {   String rtcode = null;
     String markerStr = "_recovery_" + PreferencesClerk.getRuleBenderVersion();	
    	
     if (isCleanWorkspace()) {
         rtcode = writeWorkspaceVersion(workspace_directory,2);                	  
         RemoveMetaData(workspace_directory);
     }
    	
     if (rtcode == null) {    	
    	 
    	 
    	 
    	 
         // This should probably return a boolean to indicate whether it was successful or not.
         File dir = new File(workspace_directory);
         // list the files using our FileFilter
         File[] files = dir.listFiles(new ProjectFilter());
         for (File f : files)
         {  
//   	         System.out.println("Renaming file: " + f.toString());
        	 
 	    	if (!f.getName().endsWith(markerStr)) {
              //  This should never happen, but let's check for it anyway.
 	          //  It could be that an interupted RuleBender session might
 	    	  //  leave some directories renamed, while others still have
 	    	  //  their original name.  So for the second go around, let's
 	          //  be utra safe and check to see if any of the project
 	    	  //  directories have already been renamed, and leave those
 	          //  names alone.  (Otherwise, you could end up with a project 
              //  with a name like f.toString() + markerStr + markerStr)
        	 
               File tempDir = new File(f.toString() + markerStr);
               // System.out.println("temp: " + tempDir.toString());
               f.renameTo(tempDir);
 	    	 }
         }
     } 
      
      
    }
}
