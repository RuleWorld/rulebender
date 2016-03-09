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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.IWorkbenchPart;






import za.co.quirk.layout.LatticeData;
import za.co.quirk.layout.LatticeLayout;
import rulebender.preferences.PreferencesClerk;
import rulebender.core.workspace.ProjectFilter;
import rulebender.core.workspace.BNGFilter;
import rulebender.core.workspace.DirFilter;
import rulebender.Activator;
import rulebender.logging.Logger;

/**
 * Dialog that lets/forces a user to enter/select a workspace that will be used when saving all configuration files and
 * settings. This dialog is shown at startup of the GUI just after the splash screen has shown.
 * 
 * @author Emil Crumhorn
 */
public class LoadSample extends TitleAreaDialog {


    // various dialog messages
    private static final String _StrMsg               = "Please select a sample script by selecting from the dropdown menu, and then select a project name if you wish.";
    private static final String _StrError             = "You must set a directory";

    // our controls
    private Text                 _projectName;
    private Combo                _BNGSampleListCombo;
    private List<String>         _BNGSampleList;
    
    /**
     * Creates a new workspace dialog with a specific image as title-area image.
     * 
     * @param switchWorkspace true if we're using this dialog as a switch workspace dialog
     * @param wizardImage Image to show
     */
    public LoadSample(Image wizardImage) {
        super(Display.getDefault().getActiveShell());
        if (wizardImage != null) {
            setTitleImage(wizardImage);
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Create Project");
    }



    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle("Select BioNetGen Sample");
        setMessage(_StrMsg); 

        try {
            Composite inner = new Composite(parent, SWT.NONE);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 2;
            inner.setLayout(gridLayout);

            // label on left
            CLabel label = new CLabel(inner, SWT.NONE);
            label.setText("Select a Sample");
            label.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));

            // combo in middle
            _BNGSampleListCombo = new Combo(inner, SWT.BORDER);  // was Combo
            _BNGSampleListCombo.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
            
            
            String models2_dir =   PreferencesClerk.getDefaultBNGPath() 
                                 + System.getProperty("file.separator") 
                                 + "Models2";
            
            
            _BNGSampleList = new ArrayList<String>();
            File dir = new File(models2_dir);
            // list the files using our FileFilter
            File[] files = dir.listFiles(new BNGFilter());
            for (File f : files)
            {  
      	       //  System.out.println("Listing file: " + f.getName());
            	if (!f.getName().equals("blank_file.bngl")) {
                	if (!f.getName().equals("template.bngl")) {
                        _BNGSampleList.add(f.getName());
                	}
            	}
            }
            _BNGSampleListCombo.add("template.bngl");
            _BNGSampleListCombo.add("blank_file.bngl");
             for (String last : _BNGSampleList)
                _BNGSampleListCombo.add(last);

            // Combo box on top for name of sample 
            String wsRoot = "Click Down Arrow To Select Sample";
            _BNGSampleListCombo.setText(wsRoot == null ? "" : wsRoot);

            
            // When a sample is selected, copy the name of the sample
            // into the Text box for the project name.
            _BNGSampleListCombo.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                     String xfer = _BNGSampleListCombo.getText();
                     String xfer_truncated = xfer.substring(0,xfer.length()-5);
                        _projectName.setText(xfer_truncated);
                }

            });

            
            
            
            
            // ---------------  Project Name ---------------------------

            // label on left
            CLabel label2 = new CLabel(inner, SWT.NONE);
            label2.setText("Type Project Name");
            label2.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.GRAB_HORIZONTAL));


            _projectName = new Text(inner, SWT.BORDER);  
            _projectName.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL));
            String wsProj = "Default_Project_Name";
            _projectName.setText(wsRoot == null ? "" : wsProj);

            
            Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(),
              " Creating sample:  ");
            
            return inner;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * 
     * 
     * 
     */

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

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

    /*
     * 
     * 
     * 
     * 
     */
    
    @Override
    protected void okPressed() {
        String project_name    = _projectName.getText().trim();
        if (project_name.length() == 0) {
            setMessage("Please put a project name in the text box.", 
            		   IMessageProvider.ERROR);
            return;
        }
        // System.out.println("The project name is: " + project_name);

        
        String selected_sample    = _BNGSampleListCombo.getText().trim();
        if (selected_sample.length() < 6) {
            setMessage("Please select a sample file and include the .bngl extension.",
                		IMessageProvider.ERROR);
            return;
        }
        // System.out.println("The sample name is: " + selected_sample);

        
        /* This is where the project is added into the Navigator's project tree. */
        IProject project = null;
        try {
          IProgressMonitor progressMonitor = new NullProgressMonitor();
          IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	      project = root.getProject(project_name);
	      if (1 == 0) {
	        project.delete(true,true,progressMonitor);
	      } else {
	        project.create(progressMonitor);
	        project.open(progressMonitor);
	      }
        
        
        String sample_src_full_path = PreferencesClerk.getDefaultBNGPath() 
                                    + System.getProperty("file.separator") 
                                    + "Models2"
                                    + System.getProperty("file.separator") 
                                    + selected_sample;

        // System.out.println("Sample will be copied from: " + sample_src_full_path);

        
        String sample_dst_full_path = PreferencesClerk.getWorkspace() 
      	  	                        + System.getProperty("file.separator") 
      		                        + project_name      
      		                        + System.getProperty("file.separator") 
                                    + selected_sample;
        
        // System.out.println("Sample will be copied to: " + sample_dst_full_path);

        File src_file = new File(sample_src_full_path);
        File dst_dir  = new File(sample_dst_full_path);
        try {
            PickWorkspaceDialog.copyFiles(src_file, dst_dir);
        } catch (Exception e) {
          MessageDialog.openError(Display.getDefault().getActiveShell(),"Error",
	        "There was a failure during the copy of the sample. ");
            // System.out.println("Failure during sample copy.");
          return;
        }

        //  This will refresh the navigator tree structure, to reflect the
        //  addition of the sample.
        project.refreshLocal(IResource.DEPTH_INFINITE, null);
        
      } catch (CoreException e) {
        MessageDialog.openError(Display.getDefault().getActiveShell(),"Error",
          "A problem was encountered during the creation of the project.\n"
        + "Possibly a duplicate name has been selected.");
        //   System.out.println("Problem encountered when creating project. ");
        return;
      }
        
        
        
      //  ----------------- OPEN EDITOR -----------------------------------
        
        String relative_name = null;
               relative_name = "test/gen_expr.bngl";
               relative_name = project_name        
   		                     + System.getProperty("file.separator") 
                             + selected_sample;
        
      // Get an IPath for the relative file location. (Relative to the workspace.)
	  IPath path = new Path(relative_name);
		
	  // Get an IFile given the IPath
	  IFile fileToBeOpened = ResourcesPlugin.getWorkspace().getRoot()
		    .getFile(path);

      // Get the editor input
	  IEditorInput editorInput = new FileEditorInput(fileToBeOpened);

	  //  These output statements are pretty important.  The name should
	  //  be what you expect, and exists() should be true.  If exists()
	  //  is not true, then the file has not been properly entered into the
	  //  Navigator tree structure.
	  if (1 == 0) {
        System.out.println("editorInput is:     " + editorInput.getName());
        System.out.println("editorInput exists: " + editorInput.exists());
	  }

      // Get the active window and page.
      IWorkbenchWindow window = PlatformUI.getWorkbench()
		    .getActiveWorkbenchWindow();
	  IWorkbenchPage page = window.getActivePage();
	  
	  
	  //  Hilight this item in the Navigator  	  
	  final IWorkbenchPart activePart = page.getActivePart();
      //  System.out.println("Active part is: " + activePart.getTitle());
      final ISelection targetSelection = new StructuredSelection(fileToBeOpened);
      final IViewPart view = page.findView("rulebender.cnf.CommonNavigator");
      // Since our selection list consists of only one item, it's probably not
      // necessary to do the update in a separate thread.
	  getShell().getDisplay().asyncExec(new Runnable()
	    {
	      public void run() 
	      {
	                 IWorkbenchPart newPart = (IWorkbenchPart)view;
	                                newPart.setFocus();
	          ((ISetSelectionTarget)newPart).selectReveal(targetSelection);
	      }
	    });
	  

	  // Open the file in the BNGL Editor.
	  try {
			page.openEditor(editorInput, "rulebender.editors.bngl");
	  } catch (PartInitException e) {
	        MessageDialog.openError(Display.getDefault().getActiveShell(),"Error",
	                "A problem was encountered during the opening of the editor."); 
			e.printStackTrace();
	  }
        
      super.okPressed();
   }
}
