package rulebender.simulate.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import rulebender.core.utility.Console;
import rulebender.preferences.PreferencesClerk;
import rulebender.simulate.BioNetGenUtility;
import rulebender.simulate.ResultsFileUtility;
import rulebender.simulate.parameterscan.ParameterScanComposite;

public class SimulateView extends ViewPart {
	private Composite main, actionSelect, stackedComposite,
	    parameterScanComposite, runFileComposite;
	public static Object mostRecentSimulateView = null;

	private StackLayout stackLayout;

	private ScrolledComposite scroll;

	private SimulateViewSelectionListener listener;

	private IFile m_selectedFile;

	private Text fileText;

	public SimulateView() {
		// Save a pointer to the most recently constructed SimulateView
		SimulateView.mostRecentSimulateView = this;		
	}

	@Override
	public void createPartControl(final Composite parent) {
		/*
		 * Need the all encompassing composite.
		 */
		parent.setLayout(new FillLayout());
		// parent.setBackground(new Color(Display.getCurrent(), 0,0,255));

		scroll = new ScrolledComposite(parent, SWT.V_SCROLL);
		scroll.setBackground(new Color(Display.getCurrent(), 0, 0, 0));

		main = new Composite(scroll, SWT.NULL);

		scroll.setContent(main);

		// main.setBackground(new Color(Display.getCurrent(), 0, 255,0));
		main.setLayout(new FillLayout());

		FormData mainFormData = new FormData();
		mainFormData.top = new FormAttachment(0, 0);
		mainFormData.bottom = new FormAttachment(100, 0);
		mainFormData.left = new FormAttachment(0, 0);
		mainFormData.right = new FormAttachment(100, 0);

		main.setLayoutData(mainFormData);
		main.setLayout(new FormLayout());

		main.setSize(500, 300);
		/*
		 * Create the composite that will hold the filename and action select
		 * widgets
		 */

		actionSelect = new Composite(main, SWT.NULL);
		// actionSelect.setBackground(new Color(Display.getCurrent(), 255,0,0));

		// Create the default formdata which puts the composite in the upper left of
		// the parent.
		FormData actionSelectFormData = new FormData();
		actionSelectFormData.top = new FormAttachment(0, 10);
		actionSelectFormData.left = new FormAttachment(0, 10);
		actionSelectFormData.right = new FormAttachment(100, 0);
		actionSelect.setLayoutData(actionSelectFormData);

		// Set the layout of the actionSelect Composite.
		actionSelect.setLayout(new FormLayout());

		// Create the filename Text widget
		Label helpLabel = new Label(actionSelect, SWT.NULL);
		// fileLabel.setSize(actionSelect.getBorderWidth()-50, 10);
		helpLabel.setText("Select a BNGL file in the Navigator.");

		// Put this in the upper left corner of the actionSelect Composite.
		FormData helpLabelFormData = new FormData();
		helpLabelFormData.top = new FormAttachment(0, 0);
		helpLabelFormData.left = new FormAttachment(0, 0);
		helpLabel.setLayoutData(helpLabelFormData);

		// Create the filename Text widget
		Label fileLabel = new Label(actionSelect, SWT.NULL);
		fileLabel.setText("File:");

		// Put this below the help text.
		FormData fileLabelFormData = new FormData();
		fileLabelFormData.top = new FormAttachment(helpLabel, 10);
		fileLabelFormData.left = new FormAttachment(0, 0);
		fileLabel.setLayoutData(fileLabelFormData);

		// Create the Text Box
		fileText = new Text(actionSelect, SWT.BORDER);
		if (Console.mostRecentIFile != null) {
			setSelectedResource(Console.mostRecentIFile);
		}
		fileText.setEditable(false);

		FormData fileTextFormData = new FormData();
		fileTextFormData.top = new FormAttachment(helpLabel, 10);
		fileTextFormData.left = new FormAttachment(fileLabel, 10);
		fileTextFormData.right = new FormAttachment(100, -10);
		fileText.setLayoutData(fileTextFormData);

		// Add a verifier that makes sure that the text forms a correct file path.
		/*
		 * fileText.addListener (SWT.Verify, new Listener () { public void
		 * handleEvent (Event e) { String string = e.text;
		 * 
		 * if(!string.endsWith(".bngl") || !(new File(string)).exists()) { e.doit =
		 * false; } else { setSelectedFile(string); } } }); //
		 */

		// The model will be selected by either writing the path into the text box,
		// or selecting the node in the navigator.

		// Need a way to select the method (either run model or parameter scan for
		// now)
		final Combo combo = new Combo(actionSelect, SWT.READ_ONLY);

		FormData comboFormData = new FormData();
		comboFormData.top = new FormAttachment(fileText, 10);
		comboFormData.left = new FormAttachment(0, 10);
		comboFormData.right = new FormAttachment(100, -10);
		combo.setLayoutData(comboFormData);

		combo.setItems(new String[] { "Run File", "Parameter Scan" });
		combo.select(0);

		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (combo.getText().equals("Run File")) {
					stackLayout.topControl = runFileComposite;
					stackedComposite.layout();
				} else if (combo.getText().equals("Parameter Scan")) {
					stackLayout.topControl = parameterScanComposite;
					stackedComposite.layout();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		/*
		 * The StackedLayout for the actual Actions
		 */
		stackedComposite = new Composite(main, SWT.NULL);

		FormData stackedCompositeFormData = new FormData();
		stackedCompositeFormData.top = new FormAttachment(actionSelect, 10);
		stackedCompositeFormData.left = new FormAttachment(0, 10);
		stackedCompositeFormData.right = new FormAttachment(100, 10);

		stackedComposite.setLayoutData(stackedCompositeFormData);

		stackLayout = new StackLayout();
		stackedComposite.setLayout(stackLayout);

		parameterScanComposite = new ParameterScanComposite(stackedComposite, this);

		runFileComposite = new Composite(stackedComposite, SWT.NULL);

		runFileComposite.setLayout(new FormLayout());

		Button runButton = new Button(runFileComposite, SWT.PUSH);
		runButton.setText("Run");

		FormData buttonData = new FormData();
		buttonData.left = new FormAttachment(0, 10);
		buttonData.right = new FormAttachment(100, -10);
		runButton.setLayoutData(buttonData);

		runButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Console.displayOutput(getSelectedFile().getRawLocation().toOSString(),
				    Console.getConsoleLineDelimeter() + "Running File...");

				Console.showConsole(getSelectedFile().getRawLocation().toOSString());

				// Run the simulation. This returns a boolean,
				// but for now I am ignoring it.
				
				String finalPath = PreferencesClerk.getFullBNGPath();
				
//				System.out.println(" Finally using the path " + finalPath);
				
				BioNetGenUtility.runBNGLFile(getSelectedFile(), PreferencesClerk
				    .getFullBNGPath(), ResultsFileUtility
				    .getSimulationResultsDirectoryForIFile(getSelectedFile()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		stackLayout.topControl = runFileComposite;
		stackedComposite.layout();

		main.pack();

		// Add a listener that updates the views when the
		// parent object is resized.
		parent.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				main.setSize(parent.getSize().x, parent.getSize().y);
			}
		});

		listener = new SimulateViewSelectionListener(this);
	}

	@Override
	public void setFocus() {
	}

	public IFile getSelectedFile() {
		return m_selectedFile;
	}

	public void setSelectedResource(IFile selectedObject) {
		m_selectedFile = selectedObject;
		fileText.setText(selectedObject.getFullPath().toOSString());
		
		// System.out.println(" We seem to be selecting file: " + selectedObject.getFullPath().toOSString() +
		// 		           " IFile: " + selectedObject.toString());
	}
}
