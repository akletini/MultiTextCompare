package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MatrixView extends JFrame {
	JPanel panel;
	JButton testButton;
	private static DecimalFormat df = new DecimalFormat("0.00");
	public MatrixView() {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(4, 4));

		for(int i=0; i<16; i++){
			double wert = Math.random();
			String wertString = df.format(wert);
			testButton = new JButton(""+ wertString);
			
			int r = (int) ((255* (100 - (wert*100))) / 100);
			int g = (int) ((255*(wert*100) / 100));
			Color  wertFarbe   = new Color(r, g,  0);
			
			testButton.setOpaque(true);
			testButton.setBackground(wertFarbe);
			testButton.setForeground(Color.BLACK);
		
			panel.add(testButton);
		}
		
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Matrix");
		this.setSize(400, 400);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
}
