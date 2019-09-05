package rulebender.contactmap.view;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IEditorPart;

import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

import rulebender.contactmap.models.CMapModelBuilder;
import rulebender.contactmap.models.ContactMapModel;
import rulebender.contactmap.prefuse.ContactMapVisual;
import rulebender.contactmap.properties.StatePropertySource;
import rulebender.contactmap.properties.MoleculePropertySource;
import rulebender.editors.bngl.BNGLEditor;
import rulebender.editors.bngl.model.BNGASTReader;
import rulebender.editors.bngl.model.BNGLModel;
import rulebender.logging.Logger;
import rulebender.logging.Logger.LOG_LEVELS;

/**
 * This class is the selection and part listener for the contact map
 * 
 * Implements ISelectionListener so that it sees all selections that are
 * registered through the SelectionService.
 * 
 * Implements the IPartListener2 so that it can see when parts and views are
 * opened and closed. This is how the contact map is updated when a new editor
 * is opened/closed.
 * 
 * @author adammatthewsmith
 * 
 */
public class ContactMapSelectionListener implements ISelectionListener,
    IPartListener2 {
	// The contact map visualization.
	private ContactMapView m_view;

	// The model that is being visualized.
	private BNGLModel m_currentModel;

	// A HashMap that holds any pre-existing but not currently displayed
	// contact map prefuse visualizations. This is so that a new contact map
	// is not created every time a user selects a new bngl editor tab.
	private final HashMap<String, prefuse.Display> m_contactMapRegistry;

	/**
	 * Constructor sets up the data structures given the
	 * EclipseRCP/ContactMapView.
	 * 
	 * @param view
	 */
	public ContactMapSelectionListener(ContactMapView view) {
		setView(view);

		// Create the registry
		m_contactMapRegistry = new HashMap<String, prefuse.Display>();

		// Register the view as a listener for workbench selections.
		m_view.getSite().getPage().addPostSelectionListener(this);

		// Register the view as a part listener.
		m_view.getSite().getPage().addPartListener(this);
	}

	/**
	 * Called on a selection event.
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// If it's from the BNGLEditor then we want to set the contact map.
		System.out.println("selectionChanged: " + part + selection);
		if (part instanceof BNGLEditor) {
			bnglEditorSelection((BNGLEditor) part, selection);
		}
		// If it's not a bngl file
		else {
			// TODO clear the contact map.
		}
	}


	/**
	 * Given a new model, set it to the current model. Also looks for an existing
	 * prefuse.Display object and sets it as visible.
	 */
	private void setCurrentModel(BNGLModel model) {
		m_currentModel = model;

		// Get an existing map.
		prefuse.Display toShow = lookupDisplay(m_currentModel);

		// Set the correct contact map.
		m_view.setCMap(toShow);

		// Activate the part. This is so the property view will add listeners
		// to the view.
		m_view.getViewSite().getPage().activate(m_view.getViewSite().getPart());

	}

	/**
	 * Internal method that is called when a bngl file is selected in the editor.
	 * 
	 * @param part
	 * @param selection
	 */
	private void bnglEditorSelection(BNGLEditor part, ISelection selection) {
		// Get the string that represents the current file.
		String osString = ((FileEditorInput) part.getEditorInput()).getPath()
		    .toOSString();

		// If it's the same file
		if (m_currentModel != null && osString.equals(m_currentModel.getPathID())) {
			// TODO it could be a text selection. This is where the selected
			// text would be parsed and a visual element would be selected
			// if there was a match with an element.
	        prefuse.Display curDisp = lookupDisplay(m_currentModel);
	        // TODO: Actually curDisp can ALSO be null but trying to put a check 
	        // here caused me more issues. Fix that somehow
	        prefuse.Visualization curVis = curDisp.getVisualization();
			// gotta make sure we have a visualization to begin with
			if (curVis != null) {
				// TODO First MAKE SURE it's a text selection before casting it into one 
				// I don't actually know if it can be something else but just in case
				TextSelection textSel = (TextSelection) selection;
				// System.out.println("get text: " + textSel.getText());
				// Get the string to compare to
				String selStr = textSel.getText();  
				// Make sure we have an actual string or let's not do anything
				if (selStr.length()>0) {
					// Parse a tiny bit, remove comments
					int offset = selStr.indexOf("#");
		      	    if (offset!=-1) {
		      		  selStr = selStr.substring(0, offset);
		      	    } 
		      	    // remove whitespace
					selStr = selStr.replaceAll("\\s+", "");
					//System.out.println("Parsed text: " + selStr);
									
					// Let's take the aggregate visual items
					Iterator items = curVis.getVisualGroup("aggregates").tuples();
					
					// loop over all and see if any of it matches
					while (items.hasNext()) {
						VisualItem item = (VisualItem) items.next();
						
						// pull molecule_expression out of the item
						String molExp = item.get("molecule_expression").toString();
						String molName = item.get("molecule").toString();
						
						// compare to text in selection, 
						if (molExp.equals(selStr) || molName.equals(selStr)) {
							//System.out.println("item: " + item);
							// Using selection pathway from CMapClickControlDelegate.java
							m_view.getSite().getSelectionProvider().setSelection(
								new StructuredSelection(new MoleculePropertySource(
										item, m_currentModel.getPathID())));
							TupleSet focused = curVis.getFocusGroup("selected");
							if (focused != null) {
								focused.clear();
								focused.addTuple(item);
							}
							curVis.run("color");
						} 
				    }
				} else {
					m_view.getSite().getSelectionProvider().setSelection(new StructuredSelection());
					TupleSet focused = curVis.getFocusGroup("selected");
					if (focused != null) {
						focused.clear();
						curVis.run("color");	
					}
				}
			}
		}
		// If it's a different file, then call the local private method that
		// handles it.
		else {
			setCurrentModel(part.getModel());
		}
	}

	/**
	 * Can be null if there is no Display (bngl compile error)
	 * 
	 * @param model
	 * @return
	 */
	private prefuse.Display lookupDisplay(BNGLModel model) {
		return m_contactMapRegistry.get(model.getPathID());
	}

	/**
	 * Generates a contact map using the abstract syntax tree from the bngl
	 * parser.
	 * 
	 * @param sourcePath
	 *          The path of the resource. This is used for selection later.
	 * @param ast
	 *          - The abstract syntax tree for the bngl model.
	 * @return
	 */
	private prefuse.Display generateContactMap(String sourcePath, File ast) {
		// If the ast has not been generated for this model, then
		// just return null so there is not contact map displayed.
		// Also, an empty file produces a complete ast, so the length
		// requirement catches that.

		if (ast == null || ast.toString() == null) {
			return null;
		}

		// Create the builder for the cmap
		CMapModelBuilder cmapModelBuilder = new CMapModelBuilder();

		// Create the astReader and register the cmapModelBuilder
		BNGASTReader astReader = new BNGASTReader(cmapModelBuilder);

		// Use the reader to construct the model for the given ast.
		// Sometimes an ast is not null, but is not complete due to errors.
		// This try/catch block catches those situations.
		try {
			astReader.buildModel(ast);
		} catch (NullPointerException e) {
			Logger
			    .log(
			        LOG_LEVELS.SEVERE,
			        this.getClass(),
			        "Failed to produce CMapModel on file:\n"
			            + m_currentModel.getPathID());

			e.printStackTrace();
			// Stop doing everything
			return null;
		}

		// Get the model from the builder.
		ContactMapModel cModel = cmapModelBuilder.getCMapModel();

		// FIXME
		// This should never happen. If it does then an error should have been
		// thrown
		// by the parser. I'm only 95% sure about that statement, so I am
		// leaving this here. If there is a legitimate way for this to happen, then
		// an Exception should be thrown here.
		if (cModel == null) {
			Logger.log(LOG_LEVELS.SEVERE, this.getClass(),
			    "The CMapModel is null. Something went wrong "
			        + "while trying to parse the Abstract Syntax Tree for the "
			        + "BNGL File.  \nExiting");

			return null;
		}

		// Set the source path for the contact map model.
		cModel.setSourcePath(sourcePath);

		// Set a dimension
		Dimension dim = m_view.getSize();

		// Get the CMapVisual object for the CMapModel
		ContactMapVisual cVisual = new ContactMapVisual(m_view, cModel, dim);

		return cVisual.getDisplay();
	}

	/**
	 * Used to set the view.
	 * 
	 * @param view
	 */
	private void setView(ContactMapView view) {
		m_view = view;
	}

	/**
	 * Change the shown CMap Display given the path of a bngl file and an ast.
	 * 
	 * @param path
	 * @param ast
	 */
	private void updateDisplayForPathAndAST(String path, File ast) {
		// Clear the current entry.
		m_contactMapRegistry.remove(path);

		// Generate a new display
		prefuse.Display display = generateContactMap(path, ast);

		// Add the new representation.
		m_contactMapRegistry.put(path, display);

		// If it is the model that is currently displayed,
		// then send the Display object to the view.
		if (path.equals(m_currentModel.getPathID())) {
			m_view.setCMap(display);
		}
	}

	/**
	 * Called by Eclipse RCP when a part is opened. (we care if it is an editor)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference in_partRef) {		
    IWorkbenchPartReference partRef = in_partRef;
		 
		
		IEditorPart my_active_editor = PlatformUI.getWorkbench()
	               .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		 if (my_active_editor != null) {
			 // This if-block covers the case where the species graph when RuleBender
			 // is started.  To see the Contact Map, the user will then click on the
			 // Contact Map tab, which will bring the UI thread in here.  
	
			 IWorkbenchPart              wpart     = (IWorkbenchPart) my_active_editor;
			 IWorkbenchPartSite       wpartSite    = wpart.getSite();
			 IWorkbenchPage           wpartPage    = wpartSite.getPage();
			 IWorkbenchPartReference  wpartRef     = wpartPage.getReference(wpart);
			 
			if ((wpartRef.getId().equals("rulebender.editors.bngl")
				    && wpartRef.getPart(false) instanceof BNGLEditor)) {
			       partRef = wpartRef;
			}		 
		 }

		// If it's a bngl editor. (Cannot remember why there is id and instanceof
		// checking but I would not change it without serious testing.)
		if (partRef.getId().equals("rulebender.editors.bngl")
		    && partRef.getPart(false) instanceof BNGLEditor) {
			// Set the current file.
			setCurrentModel(((BNGLEditor) partRef.getPart(false)).getModel());

			// Create a property changed listener for when files are saved and
			// models are updated.
			PropertyChangeListener pcl = new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					String filePath = ((BNGLModel) propertyChangeEvent.getSource())
					    .getPathID();
					String propertyName = propertyChangeEvent.getPropertyName();

					if (propertyName.equals(BNGLModel.AST)) {
						// Update the display object that is associated with the path and
						// ast.
						updateDisplayForPathAndAST(filePath,
						    (File) propertyChangeEvent.getNewValue());
					} else if (propertyName.equals(BNGLModel.ERRORS)) {
						// Don't care.
					}
				}
			};

			m_currentModel.addPropertyChangeListener(pcl);

			// generate the display and add the initial cmap to the registry.
			m_contactMapRegistry.put(
			    m_currentModel.getPathID(),
			    generateContactMap(m_currentModel.getPathID(),
			        m_currentModel.getAST()));

			m_view.setCMap(lookupDisplay(m_currentModel));

		} // end if it's an editor block
	}

	/**
	 * private convenience method for when an editor is closed.
	 * 
	 * @param path
	 */
	private void bnglFileClosed(String path) {
		m_contactMapRegistry.remove(path);
		if (m_contactMapRegistry.size() == 0) {
			m_view.setCMap(null);
		}
		// The selection of any remaining files occurs before
		// the close event, don't worry about changing the
		// m_currentModel
	}

	/**
	 * Called by Eclipse RCP when a part is closed (again, we only care if it is
	 * an editor).
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		// If it is a bngl error.
		if (partRef.getId().equals("rulebender.editors.bngl")) { // (Cannot remember
			                                                       // why there is id
			                                                       // and instanceof
			                                                       // checking
			                                                       // but I would not
			                                                       // change it
			                                                       // without serious
			                                                       // testing.)
			if (partRef.getPart(false) instanceof BNGLEditor) {
				BNGLEditor editor = ((BNGLEditor) partRef.getPart(false));

				String osString = ((FileEditorInput) editor.getEditorInput()).getPath()
				    .toOSString();
				bnglFileClosed(osString);
			}
		}

	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// FIXME This where we should be checkingn to see if a new model is
		// opened instead of using the selection service.
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {

	}

}
