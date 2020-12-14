package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopupView extends JFrame {
	JPanel panel;
	JLabel label;
	
	public PopupView(){
		panel = new JPanel();
		label = new JLabel("Error");
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setLayout(new GridLayout(0, 1));
		
		panel.add(label);
		
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Konfiguration");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

}
