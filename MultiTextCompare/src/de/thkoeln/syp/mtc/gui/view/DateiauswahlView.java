package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DateiauswahlView extends JFrame{	
	JLabel testLabel;
	JPanel panel;
	
	public DateiauswahlView() {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 1));

		testLabel = new JLabel("Test");
		
		panel.add(testLabel);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Dateiauswahl");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
}
