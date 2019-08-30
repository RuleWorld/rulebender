/**
 * ContactMapLayeredPane.java
 * @author Adam M. Smith
 * April 29, 2011
 * 
 * This class contains the main contact map visualization and its
 * context view.  The size of the context view is static, but the 
 * overall size of the window is set in the constructor and can
 * be changed with a method call after instantiation. 
 */
package rulebender.core.prefuse;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import prefuse.Display;
import rulebender.core.prefuse.overview.Overview;

//Prateek Adurty
import javax.swing.JButton;
import prefuse.Visualization;
import rulebender.core.prefuse.networkviewer.contactmap.CMAPNetworkViewer;
import rulebender.core.prefuse.networkviewer.contactmap.ContactMapPosition;
import rulebender.core.prefuse.networkviewer.contactmap.ForceSimulator;
import rulebender.core.prefuse.networkviewer.contactmap.NodeItem;
import rulebender.core.prefuse.networkviewer.contactmap.VisualItem;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.core.runtime.Status.CANCEL_STATUS;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

// trying to center view
import prefuse.action.layout.Layout;
import prefuse.data.Graph;
import prefuse.util.display.DisplayLib; 
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;

/**
 * This class defines the pane that contains a prefuse.Display object
 * and an overview for that Display.  It is a subclass of the AWT 
 * JLayeredPane.
 * @author adammatthewsmith
 *
 */
public class LayeredPane extends JLayeredPane 
{	
	// The overview window size
	private final float OVERVIEW_WIDTH = 0.2f;
	private final float OVERVIEW_HEIGHT = 0.2f;
	
	// The width of the borders in pixels
	private final int BORDER_WIDTH = 1;

	// The size of the main visualization window.
	private Dimension m_currentSize;
	
	// The JPanels that hold the visualization and the overview
	private JPanel mainJPanel;
	private JPanel overviewJPanel;
	
	//Prateek Adurty Button that reruns FDLM
	private JPanel buttonJPanel;
	private JButton button;
	
	// The border object that the two JPanels share.
	private Border border;
	
	// FD layout button variables
	private CMAPNetworkViewer networkViewer;
	private Job fdlmJob;
	private boolean fdlmRunning = false;
	private String fname; 
	private String nPosPath;
	
	/**
	 * Constructor
	 * 
	 * @param size - The Dimension object describing the size (in pixels)
	 * 				 of the main visualization.
	 */
	public LayeredPane(Dimension size)
	{
		// Set the value of the local size variable.
		m_currentSize = size;
		
		// Instantiate the border object.
		border = new LineBorder(Color.GRAY, BORDER_WIDTH);
		
		// Instantiate the JPanel for the overview and set its border
		overviewJPanel = new JPanel(); 
	    overviewJPanel.setBorder(border);
	    
	    overviewJPanel.setBackground(Color.WHITE);
	    
	    // Instantiate the JPanel for the main visualization and set its border.
	    mainJPanel = new JPanel();
		mainJPanel.setBorder(border);
		mainJPanel.setBackground(Color.WHITE);
		
		//Instantiate Button for the JPanel and set its border
		buttonJPanel = new JPanel();
		buttonJPanel.setBorder(border);
		buttonJPanel.setBackground(Color.WHITE);
		
		
		// Add the JPanels to the JLayeredPane (this object)
		this.add(mainJPanel, new Integer(0));		
		this.add(overviewJPanel, new Integer(1));
		
		//Prateek Adurty
		this.add(buttonJPanel, new Integer(1));
		
		// Update the sizes of the JPanels and Displays
		myResize(size);
	}
	
	/**
	 * Use this to set the Display object to use.
	 * 
	 * @param display - The prefuse.Display object for the visualization. 
	 */
	public void setDisplay(Display display)
	{
		
		
		// Remove any component children
		if(mainJPanel.getComponentCount() > 0)
		{
			mainJPanel.removeAll();
		}
		if(overviewJPanel.getComponentCount() > 0)
		{
			overviewJPanel.removeAll();
		}	
		
		//Prateek Adurty
		if(buttonJPanel.getComponentCount() > 0)
		{
			buttonJPanel.removeAll();
		}
		
		// If the passed in display is not null.
		if(display != null)
		{
			// Add the display to the main panel
			mainJPanel.add(display);
						
	     	// add overview display to panel
			overviewJPanel.add(new Overview(display));
		}
		
		//Prateek Adurty
		button = new JButton("Run FDLM");
		button.setPreferredSize(new Dimension(150,20));
		buttonJPanel.setLayout(null);
		buttonJPanel.add(button);
		setButtonForFDLM();
		
		myResize();
	}

	/**
	 * There is a native resize method, but I needed to do more so I created
	 * this one.  This default version calls the parameterized version with
	 * the m_currentSize as input.
	 */
	public void myResize()
	{
		myResize(m_currentSize);
	}
	
	/**
	 * There is a native resize method, but I needed to do more so I created
	 * this one.
	 * 
	 * Sets the m_currentSize.
	 * 
	 * Updates the size of the main pane.
	 * Updates the size of the overview pane.
	 * 
	 * @param size
	 */
	public void myResize(Dimension size) 
	{	
		m_currentSize = size;
		
		int overviewWidth = (int) (m_currentSize.getWidth() * OVERVIEW_WIDTH);
		int overviewHeight = (int) (m_currentSize.getHeight() * OVERVIEW_HEIGHT);
		
		
		if(mainJPanel != null && overviewJPanel != null)
		{
			mainJPanel.setBounds(0, 0, m_currentSize.width, m_currentSize.height);
			
			overviewJPanel.setBounds(0, size.height-overviewHeight, overviewWidth-BORDER_WIDTH, overviewHeight-BORDER_WIDTH);
			
			//Prateek Adurty
			buttonJPanel.setBounds(10, 10, 150, 20);

			if(mainJPanel.getComponentCount() == 1 && overviewJPanel.getComponentCount() == 1)
			{
				((Display) mainJPanel.getComponent(0)).setSize(new Dimension(m_currentSize.width-BORDER_WIDTH*2, m_currentSize.height-BORDER_WIDTH*2));				
				((Display) mainJPanel.getComponent(0)).setBounds(BORDER_WIDTH, BORDER_WIDTH, m_currentSize.width-BORDER_WIDTH*2, m_currentSize.height-BORDER_WIDTH*2);
				((Display) overviewJPanel.getComponent(0)).setBounds(BORDER_WIDTH, BORDER_WIDTH, overviewWidth-BORDER_WIDTH*3, overviewHeight-BORDER_WIDTH*2);
				((Display) overviewJPanel.getComponent(0)).setSize(new Dimension(overviewWidth-BORDER_WIDTH*3, overviewHeight-BORDER_WIDTH*2));
				
				//Prateek Adurty
				button.setBounds(0, 0, 150, 20);
				
			}
		}
	}
	
	public void setCMAPNetworkViewer(CMAPNetworkViewer networkViewerInput)
	{
		// we need the networkViewer here
		networkViewer = networkViewerInput;
	}
	
	private void setButtonForFDLM()
	{
		// TODO: This setup needs to be re-ran once files are changed
		// if you have two files open it will only run on the one that was
		// opened last!!
		// TODO: Do we need to use asyncExec at all here? I don't see the point
		// since it is already running in paralell? 
		
		// setting up a job for the concurrency framework of eclipse
		fdlmJob = new Job("Running FDLM") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// fdlmRunning is going to be true when this job get submitted
					// I _think_ this is better practice than while(true)? 
					while (fdlmRunning) {
						// call the run command, it runs layouting for a small step
						// and then updates the visual
						networkViewer.visualizationRun(nPosPath);
						// really fast w/o this sleep, there could be a way better solution
						// e.g. run the FD engine and visual updating separately like gaming loops
						// but I don't know how to implement one right now
						try {
							Thread.sleep(50);
						} catch (Exception e) { 
							System.out.println(e);
						}
						// Not sure if this is necessary but it should be useful for
						// canceling the job in case of a sudden crash/quit
						if (monitor.isCanceled()) {
							return OK_STATUS;
						}
					}
				} catch (Exception e) {
					System.out.println(e);
					// Should never return OK to be honest, we keep running unless canceled
					return CANCEL_STATUS;
				}
				return CANCEL_STATUS;
			}
		};
		// now make the button submit the job and cancel when pressed again
		button.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  StringBuilder positionFilePath = new StringBuilder();
				  fname = networkViewer.getFilepath();
				  positionFilePath.append(fname.substring(0, fname.length()-5));
				  positionFilePath.append(".pos");
				  nPosPath = positionFilePath.toString();
				  // Switch running boolean on and off and then submit the layout 
				  // job to the eclipse concurrency framework
				  fdlmRunning = !fdlmRunning;
				  if (fdlmRunning) {
					  centerView();
					  // this means we want to be running, set button text
					  button.setText("Stop FDLM");
					  // Submit to concurrency framework
					  // to be entirely honest, I actually don't know what the first two 
					  // really does, last command actually starts the job on a separate
					  // thread
					  ContactMapPosition.writeNodeLocations(nPosPath, networkViewer.getVisualization());
					  fdlmJob.setUser(true);
					  fdlmJob.setPriority(Job.LONG);
					  fdlmJob.schedule();  
				  } else {
					  // this means we want to stop, let's change text and cancel the job
					  button.setText("Run FDLM");
					  // TODO: This is not guaranteed upon sudden exit or 
					  // in case of a crash, gotta catch this and guarantee the cancel
					  fdlmJob.cancel();
					  
					  // TODO: This is not guaranteed to happen post-cancellation
					  // .join doesn't work either. We need to wait until this is done 
					  // and _then_ save the positions.

					  // Let's dump back into the original file now that we are done 
					  // re-running the force directed layouting
					  ContactMapPosition.writeNodeLocations(nPosPath, networkViewer.getVisualization());
				  }
			  }});
	}
	private void centerView() {
		String nodeGroup = PrefuseLib.getGroupName(networkViewer.getCompGraph(), Graph.NODES);
		// update positions
		Iterator<?> iter = networkViewer.getVisualization().items(nodeGroup);
		double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
		while (iter.hasNext()) {
			VisualItem item = (VisualItem) iter.next();
			
			double x = item.getX();
			double y = item.getY();		
				// find max x
			if (x > x1) {
				x1 = x;
			}

			// find min x
			if (x < x2) {
				x2 = x;
			}

			// find max y
			if (y > y1) {
				y1 = y;
			}

			// find min y
			if (y < y2) {
				y2 = y;
			}
		}
		// center position of current graph
		double center_x = (x1 + x2) / 2;
		double center_y = (y1 + y2) / 2;		
			
		// ASS: We gotta move the window and not the items themselves
		// because this results in massive jitter and constant moving 
		// of the items around when clicked on
		double w = x1-x2 + (x1-x2)*0.2;
		double h = y1-y2 + (y1-y2)*0.2;

		Rectangle2D offsetBox = new Rectangle2D.Double(-w/1.8, -h/1.8, w, h);
		DisplayLib.fitViewToBounds(networkViewer.getVisualization().getDisplay(0), offsetBox, 0);
	}
}
