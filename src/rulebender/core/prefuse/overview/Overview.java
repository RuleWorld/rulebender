package rulebender.core.prefuse.overview;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.util.display.DisplayLib;

/**
 *
 * @author Juanjo Vega
 */
public class Overview extends Display 
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Overview(Display display) {
        super(display.getVisualization());

        setBackground(Color.WHITE);
        //setBorder(BorderFactory.createTitledBorder("Overview"));

        DisplayLib.fitViewToBounds(this, getVisualization().getBounds(Visualization.ALL_ITEMS), 0);
        addItemBoundsListener(new FitOverviewListener());

        OverviewControl zoomToFitRectangleControl = new OverviewControl(display, this);
        addControlListener(zoomToFitRectangleControl);
        addPaintListener(zoomToFitRectangleControl);
    }
}