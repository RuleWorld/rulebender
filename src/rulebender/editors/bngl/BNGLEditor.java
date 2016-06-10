package rulebender.editors.bngl;


import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Integer;
import java.lang.NumberFormatException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;


import org.antlr.runtime.RecognitionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.jface.viewers.ISelectionProvider;


import rulebender.core.utility.ANTLRFilteredPrintStream;
import rulebender.core.utility.Console;
import rulebender.editors.bngl.model.BNGLModel;
import rulebender.errorview.model.BNGLError;
import rulebender.logging.Logger;
import rulebender.simulate.BioNetGenConsole;
import rulebender.contactmap.prefuse.CMapClickControlDelegate;
import rulebender.contactmap.properties.MoleculePropertySource;
import rulebender.contactmap.view.ContactMapSelectionListener;
import rulebender.contactmap.view.ContactMapView;
import bngparser.BNGParseData;
import bngparser.BNGParserUtility;

//import rulebender.simulate.BioNetGenConsole;

/**
 * This class defines the editor for bngl.
 * 
 * The ISelectionListener implementation listens for selections in the tool.
 * 
 * @author adammatthewsmith
 * 
 */

public class BNGLEditor extends TextEditor implements ISelectionListener,
    IResourceChangeListener, ISelectionChangedListener {
	// The model for the text
	private BNGLModel m_model;
	// The color manager for the syntax highlighting.
	private final BNGLColorManager m_colorManager;
	private ITextSelection rhctextSelection;

        // This is non-static.  Each instance has its own copy of
        // ListenerRegistrations so there should be one listener
        // per model.	
	private int ListenerRegistrations = 0;
	private int KeyListenerAdded = 0;
	

	// private String m_path;
	public BNGLEditor() {
		// Call the TextEditor constructor
		super();
		
		ListenerRegistrations = 0;
		
		// Create the colormanager.
		m_colorManager = new BNGLColorManager();


		// Set the SourceViewerConfiguration which takes care of many different
		// types of configs and decoration.
		setSourceViewerConfiguration(new BNGLConfiguration(m_colorManager));

		// Set the DocumentProvider which handles the representation of the file
		// and how the text is partitioned.
		setDocumentProvider(new ColoringDocumentProvider());

		// Can't create the m_model here because we need the part name,
		// and the part name is not set until after the constructor.
		// So i used a lazy load in the getter.

		// Register with the ISelectionService
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		    .addPostSelectionListener(this);

		// Register as a resource change listener.
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		
	}

	/*
	 * @Override public void initializeEditor() { super.initializeEditor(); }
	 */

	@Override
	public void editorSaved() {
		clearMarkers("rulebender.markers.bnglerrormarker");

		if (m_model == null) {
			m_model = new BNGLModel(((FileEditorInput) (getEditorInput())).getPath()
			    .toOSString());
		}


		
		m_model.setAST(getAST());
	}

	private void clearMarkers(String markerId) {
		// Get the ifile reference for this editor input.
		IFile file = ((FileEditorInput) (getEditorInput())).getFile();

		try {
			file.deleteMarkers(markerId, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private BNGParseData produceParseData() {
		// Get the text in the document.
		String text = this.getSourceViewer().getDocument().get();
		return BNGParserUtility.produceParserInfoForBNGLText(text);
	}

	/**
	 * Getter for the ast data structure. The
	 * 
	 * @return
	 */
	public BNGLModel getModel() {
		if (m_model == null) {
			m_model = new BNGLModel(((FileEditorInput) (getEditorInput())).getPath()
			    .toOSString());
			m_model.setAST(getAST());
		 }

		return m_model;
	}

	/**
	 * Returns a BNGL model given a specific source path
	 * 
	 * @param src
	 * @return
	 */
	public BNGLModel getModel(String src) {
		BNGLModel mdl = new BNGLModel(src);
		mdl.setAST(getAST());
		return mdl;
	} // getModel

	/**
	 * Returns the AST for the text in the editor, or null if there are errors.
	 * Also, the errors are reported to the console.
	 * 
	 * @return prog_return or NULL
	 * 
	 */
	private File getAST() {
		// The abstract syntax tree that will be returned.
		// On a failure, it will be null.
		File toReturn = null;

		// Save a link to the orinal error out.
		PrintStream old = System.err;

		// Set the error out to a new printstream that will only display the antlr
		// output.
		String path = ((FileEditorInput) (getEditorInput())).getPath().toOSString();
			
		Console.clearConsole(path);
		ANTLRFilteredPrintStream errorStream = new ANTLRFilteredPrintStream(
		    Console.getMessageConsoleStream(path),
		    ((FileEditorInput) (getEditorInput())).getPath().toOSString(), old,
		    ((FileEditorInput) (getEditorInput())).getPath().toOSString());
		System.setErr(errorStream);

		try {
			toReturn = BioNetGenConsole.generateXML(new File(
			    ((FileEditorInput) (getEditorInput())).getPath().toOSString()),
			    Console.getMessageConsoleStream(path));
			// Just for syntax analysis!!
			produceParseData().getParser().prog();
		} catch (NullPointerException e) {
			//e.printStackTrace();
			System.out.println("Caught in the getAST Method.");
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setErrors(errorStream.getErrorList());

		System.err.flush();
		System.setErr(old);

		return toReturn;
	}

	private void setErrors(ArrayList<BNGLError> errorList) {
		// Add the error list to the model
		m_model.setErrors(errorList);

		// Get the document.
		IDocument document = getDocumentProvider().getDocument(getEditorInput());

		// Get the ifile reference for this editor input.
		IFile file = ((FileEditorInput) (getEditorInput())).getFile();

		// Create a reference to a region that will be used to hold information
		// about the error location.
		IRegion region = null;

		// Set the annotations.
		for (BNGLError error : errorList) {
			// Get the information about the location.
			try {
				region = document.getLineInformation(error.getLineNumber() - 1);
			} catch (BadLocationException exception) {
				exception.printStackTrace();
			}

			// make a marker
			IMarker marker = null;
			try {
				marker = file.createMarker("rulebender.markers.bnglerrormarker");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.MESSAGE, error.getMessage());
				marker.setAttribute(IMarker.LINE_NUMBER, error.getLineNumber());
				marker.setAttribute(IMarker.CHAR_START, region.getOffset());
				marker.setAttribute(IMarker.CHAR_END,
				    region.getOffset() + region.getLength());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	@Override
	public void dispose() {
		clearMarkers("rulebender.markers.bnglerrormarker");
		clearMarkers("rulebender.markers.textinstance");

		super.dispose();
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public boolean isEditorInputModifiable() {
		return true;
	}

	@Override
	public boolean isEditorInputReadOnly() {
		return false;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(),
		    "Part: " + part.getTitle());
		Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(), "selection: "
		    + selection.toString());
		Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(), "empty selection? "
		    + selection.isEmpty());
		Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(),
		    "structured selection? " + (selection instanceof IStructuredSelection));
		Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(), "text selection? "
		    + (selection instanceof ITextSelection));


		
	  		  	  
		//  This listener needs to be added only once.  I'm adding it here, because the
		//  getActiveEditor() call returns null during the constructor.  
		if (KeyListenerAdded == 0) {
			KeyListenerAdded++;
		        
			BNGLListener bnglListener = new BNGLListener();		
			IEditorPart rhceditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	        ((StyledText)rhceditor.getAdapter(org.eclipse.swt.widgets.Control.class)).addKeyListener(bnglListener);	   
		}			
			
	
		
		// If it is an IStructuredSelection
		if (selection instanceof IStructuredSelection) {
			if (!selection.isEmpty()) {
				// Get the object that was selected
				IStructuredSelection iSSelection = (IStructuredSelection) selection;
				Object item = iSSelection.getFirstElement();

				// If it's the object implements IBNLLinkedElement, ie if it
				// has methods to get the path of the source file and a regular
				// expression for text search.
				if (item instanceof IBNGLLinkedElement) {
					// Get the current path that is listening.
					String thisPath = ((FileEditorInput) (getEditorInput())).getPath()
					    .toOSString();

					// If it is for this file.
					if (((IBNGLLinkedElement) item).getLinkedBNGLPath().equals(thisPath)) {
						searchableTextObjectSelected((IBNGLLinkedElement) item);
					}
				} else if (item instanceof IBNGLLinkedElementCollection) {
					// Get the current path that is listening.
					String thisPath = ((FileEditorInput) (getEditorInput())).getPath()
					    .toOSString();

					// If it is for this file.
					if (((IBNGLLinkedElementCollection) item).getLinkedBNGLPath().equals(
					    thisPath)) {
						searchableTextObjectCollectionSelected((IBNGLLinkedElementCollection) item);
					}
				} else {
					clearMarkers("rulebender.markers.textinstance");
				}
			}

			else {
				clearMarkers("rulebender.markers.textinstance");
			}

		} else if (selection instanceof ITextSelection) {
			// System.out.println(((ITextSelection) selection).toString());
		} else {
			clearMarkers("rulebender.markers.textinstance");
		}
		
	}
	
	

	private void searchableTextObjectCollectionSelected(
	    IBNGLLinkedElementCollection collection) {
		clearMarkers("rulebender.markers.textinstance");

		for (IBNGLLinkedElement ele : collection.getCollection()) {
			selectFromRegExp(ele.getRegex());
		}
	}

	private void searchableTextObjectSelected(IBNGLLinkedElement source) {
		clearMarkers("rulebender.markers.textinstance");
		selectFromRegExp(source.getRegex());
	}

	private void selectFromRegExp(String regExp) {
		Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(), "Search for regex: "
		    + regExp);

		char[] cArr = regExp.toCharArray();
		if ((cArr[0]!='\\') || (cArr[1]!='s') || (cArr[2]!='*') || (cArr[3]!='\\') ||  
		    (cArr[4]!='\\') || (cArr[5]!='?') || (cArr[6]!='\\') || (cArr[7]!='s') ||
		    (cArr[8]!='*')) {
		// Get the ifile reference for this editor input.
		IFile file = ((FileEditorInput) (getEditorInput())).getFile();

		ITextEditor editor = (ITextEditor) this.getAdapter(ITextEditor.class);

		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(this.getEditorInput());

		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(document.get());

		IMarker marker = null;

		while (m.find()) {
			try {
				marker = file.createMarker("rulebender.markers.textinstance");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.CHAR_START, m.start());
				marker.setAttribute(IMarker.CHAR_END, m.end());
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		}
	}

	public void setSelection(final int lineNumber) {
		doSetSelection(new ITextSelection() {

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public int getOffset() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getLength() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getStartLine() {
				return lineNumber;
			}

			@Override
			public int getEndLine() {
				return lineNumber;
			}

			@Override
			public String getText() {
				// TODO Auto-generated method stub
				return null;
			}
		});
	}

	/**
	 * Closes all project files on project close.
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {

		// Closing
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Logger.log(Logger.LOG_LEVELS.INFO, this.getClass(),
			    "Resource PRE_CLOSE Event: " + event.getType());

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (IWorkbenchPage page : pages) {
						if (((FileEditorInput) getEditorInput()).getFile().getProject()
						    .equals(event.getResource())) {
							IEditorPart editorPart = page.findEditor(getEditorInput());
							page.closeEditor(editorPart, true);
						}
					}
				}
			});
		}
		// // Rename
		// else if (event.getType() == IResourceChangeEvent.POST_CHANGE)
		// {
		// event.getDelta().getKind();
		// }
	}
	
	

//      Register this text editor as a listener to the Contact Map view.	
	public void timeToRegister(CMapClickControlDelegate  cmccd) {
		try {
			if (ListenerRegistrations < 1) {
				ListenerRegistrations++;
			  cmccd.addSelectionChangedListener(this);
			}
		} catch (Exception e) {
			// log.error(e.getMessage(), e);
		}
    }


	
	
	
	@Override
	public void selectionChanged(SelectionChangedEvent ee) {
		
	IStructuredSelection my_sSelection = 	(IStructuredSelection)(ee).getSelection();

		
		if (my_sSelection instanceof IStructuredSelection) {
			if (my_sSelection instanceof IStructuredSelection) {
                            // Get the object that was selected
		            IStructuredSelection iSSelection = (IStructuredSelection) my_sSelection;
		            Object item = iSSelection.getFirstElement();

		            // If it's the object implements IBNLLinkedElement, ie if it
                            // has methods to get the path of the source file and a regular
                            // expression for text search.
			    if (item instanceof IBNGLLinkedElement) {
			       // Get the current path that is listening.
			       String thisPath = 
                                 ((FileEditorInput) getEditorInput())
                                 .getPath().toOSString();

                               // If it is for this file.
                               if (((IBNGLLinkedElement) item).getLinkedBNGLPath().equals(thisPath)) {
				 searchableTextObjectSelected((IBNGLLinkedElement) item);
						}
                               } else if (item instanceof IBNGLLinkedElementCollection) {
			       // Get the current path that is listening.
			       String thisPath = 
                                 ((FileEditorInput) (getEditorInput()))
                                 .getPath().toOSString();

                               // If it is for this file.
	                       if (((IBNGLLinkedElementCollection) item).getLinkedBNGLPath().equals( thisPath))  {
                                  searchableTextObjectCollectionSelected((IBNGLLinkedElementCollection) item);
                                 }
                               } else {
			         clearMarkers("rulebender.markers.textinstance");
                                      }
                               }
		} else {
			
//			System.out.println("   ee  is not structured" + ee.toString());
		}
		
	}

	
	
}
