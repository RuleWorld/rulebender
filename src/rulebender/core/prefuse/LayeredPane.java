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
import rulebender.core.prefuse.networkviewer.contactmap.CMAPNetworkViewer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
	
	
	
	
	//Prateek Adurty
	private JPanel buttonJPanel;
	private JButton b;
	
	
	
	
	
	
	
	
	
	
	
	// The border object that the two JPanels share.
	private Border border;
	
	//Add this  
	private CMAPNetworkViewer newNetworkViewer;
	
	
	
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
		
		
		//Prateek Adurty
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
	
	
	
	
	//Prateek Adurty
	
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
			
			
			
			
			//Prateek Adurty
			b = new JButton("Run FDLM");
			buttonJPanel.add(b);
			
		
		myResize();
		}
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
				b.setSize(new Dimension(150, 20));
				b.setBounds(0, 0, 150, 20);
				
				
				
				
			}
		}
	}
		
	//Prateek Adurty
	/*
	 * sets up Force Directed Layout Button by adding ActionListener and hooking up to visualizationRun method which 
	 * reruns the contact map layout
	 */
		public void setupButton(CMAPNetworkViewer networkViewerInput)
		{
			
				newNetworkViewer = networkViewerInput;
				b.addActionListener(new ActionListener() { 
					  public void actionPerformed(ActionEvent e) { 
						  System.out.println("I was clicked!");
		                   newNetworkViewer.visualizationRun();					  } 
					} );
			
		}
					
			//layeredPane = new LayeredPane(new Dimension(400,600));
			
			//frame.getlayeredPane().setNetorkviewer(network)
}
