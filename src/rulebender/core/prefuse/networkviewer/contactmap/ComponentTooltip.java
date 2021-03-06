package rulebender.core.prefuse.networkviewer.contactmap;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;

import rulebender.core.prefuse.networkviewer.PrefuseTooltip;


public class ComponentTooltip extends PrefuseTooltip {
	protected javax.swing.JLabel nameLabel;
	protected javax.swing.JLabel moleLabel;
	protected javax.swing.JPanel contentsPanel;
	protected String gender;
	
	public ComponentTooltip(javax.swing.JComponent owner, String name, String states) {
			super(owner, 0 , 1000);
			
			contentsPanel = new javax.swing.JPanel();
			contentsPanel.setLayout(new BorderLayout());
			
			nameLabel = new javax.swing.JLabel();
			moleLabel = new javax.swing.JLabel();
			
			nameLabel.setText(name);
			moleLabel.setText(states);
			
			contentsPanel.add(nameLabel, BorderLayout.PAGE_START);
			contentsPanel.add(moleLabel, BorderLayout.PAGE_END);
			

			contentsPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
			contentsPanel.setBackground(new Color(255, 255, 255));
		}
		
		public java.awt.Component getContents() {
			return contentsPanel;
		}
	}