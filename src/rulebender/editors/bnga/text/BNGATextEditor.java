package rulebender.editors.bnga.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import de.ralfebert.rcp.tools.preferredperspective.IPrefersPerspective;
import rulebender.editors.bnga.model.SimulationProtocol;
import rulebender.editors.bngl.BNGLColorManager;
import rulebender.editors.bngl.IBNGLLinkedElement;
import rulebender.editors.bngl.IBNGLLinkedElementCollection;

public class BNGATextEditor extends TextEditor 
                            implements ISelectionListener,
                                       IResourceChangeListener
{
	// The model for the text
	private SimulationProtocol m_model;
	// The color manager for the syntax highlighting.
	private BNGLColorManager m_colorManager;
	
	//private String m_path;
	
	public BNGATextEditor()  
	{
		// Call the TextEditor constructor
		super();
		
		// Create the colormanager.
		m_colorManager = new BNGLColorManager();
		
		// Set the SourceViewerConfiguration which takes care of many different
		// types of configs and decoration.
		setSourceViewerConfiguration(new BNGAConfiguration(m_colorManager));
		
		// Set the DocumentProvider which handles the representation of the file
		// and how the text is partitioned.
		setDocumentProvider(new BNGADocumentProvider());
		
		// Can't create the m_model here because we need the part name,
		// and the part name is not set until after the constructor.  
		// So i used a lazy load in the getter.
	
		// Register with the ISelectionService
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPostSelectionListener(this);
		
		// Register as a resource change listener.
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
  	}
	
	@Override
	public void editorSaved()
	{	
		
	}	
	
	public void dispose() 
	{		
		super.dispose();
	}
	
	@Override
	public boolean isEditable()
	{
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
	public void selectionChanged(IWorkbenchPart part, ISelection selection) 
	{
		System.out.println("Part: " + part.getTitle());
		System.out.println("selection: " + selection.toString());
		System.out.println("empty selection? " + selection.isEmpty());
		System.out.println("structured selection? " + (selection instanceof IStructuredSelection));
		System.out.println("text selection? " + (selection instanceof ITextSelection));
		
		// If it is an IStructuredSelection
		if(selection instanceof IStructuredSelection)
		{
			if(!selection.isEmpty())
			{
				// Get the object that was selected
				IStructuredSelection iSSelection = (IStructuredSelection) selection;
				Object item = iSSelection.getFirstElement();
			
				// If it's the object implements IBNLLinkedElement, ie if it 
				// has methods to get the path of the source file and a regular 
				// expression for text search.
				if (item instanceof IBNGLLinkedElement)
				{
					// Get the current path that is listening. 
					String thisPath = ((FileEditorInput) ((IEditorInput) getEditorInput())).getPath().toOSString();
					
					// If it is for this file.
					if(((IBNGLLinkedElement) item).getLinkedBNGLPath().equals(thisPath))
					{
						searchableTextObjectSelected((IBNGLLinkedElement) item);
					}
				}
				else if(item instanceof IBNGLLinkedElementCollection)
				{
					// Get the current path that is listening. 
					String thisPath = ((FileEditorInput) ((IEditorInput) getEditorInput())).getPath().toOSString();
					
					// If it is for this file.
					if(((IBNGLLinkedElementCollection) item).getLinkedBNGLPath().equals(thisPath))
					{
						searchableTextObjectCollectionSelected((IBNGLLinkedElementCollection) item);
					}
				}
				else
				{
					//clearMarkers("rulebender.markers.textinstance");
				}
			}	
			
			else
			{
				//clearMarkers("rulebender.markers.textinstance");
			}
			
		}
		else if (selection instanceof ITextSelection)
		{
			System.out.println(((ITextSelection) selection).toString());
		}
		else
		{
			//clearMarkers("rulebender.markers.textinstance");
		}
	}
	
	private void searchableTextObjectCollectionSelected(IBNGLLinkedElementCollection collection)
	{
		//clearMarkers("rulebender.markers.textinstance");
		
		for(IBNGLLinkedElement ele : collection.getCollection())
		{
			selectFromRegExp(ele.getRegex());
		}
	}
	
	private void searchableTextObjectSelected(IBNGLLinkedElement source) 
	{	
		//clearMarkers("rulebender.markers.textinstance");
		selectFromRegExp(source.getRegex());
	}
	
	private void selectFromRegExp(String regExp)
	{
		System.out.println("Search for regex: " + regExp);
		
		//Get the ifile reference for this editor input.
		IFile file = ((FileEditorInput) ((IEditorInput) getEditorInput())).getFile();
				
		ITextEditor editor = (ITextEditor) this.getAdapter(ITextEditor.class);
				
		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(this.getEditorInput());
			
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(document.get());		
		
		IMarker marker = null;
		
		while(m.find()) 
        {	
			try
			{
				marker = file.createMarker("rulebender.markers.textinstance");
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.CHAR_START, m.start());
				marker.setAttribute(IMarker.CHAR_END, m.end());
				
				System.out.println("Made the text marker!");
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
			}
		} 
	}
		
	//TODO this is not being called. 
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event)
	{
		System.out.println("Resource Changed Event: " + event.getType());
		
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE)
		{
			System.out.println("Closing");
			
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput) getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
}
