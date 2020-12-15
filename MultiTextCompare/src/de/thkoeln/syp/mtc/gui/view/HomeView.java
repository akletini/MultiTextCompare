package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomeView extends JFrame{	
	private JButton buttonDateiauswahl;
	JButton buttonDateiauswahl2;
	JButton buttonKonfiguration;
	JButton buttonHilfe;
	JPanel panel;
	JButton buttonMatrix = new JButton ("Matrix (Test)"); // ZUM TESTEN
	
	public HomeView() {
		buttonDateiauswahl = new JButton("Textvergleich");
		buttonDateiauswahl2 = new JButton("Textvergleich nach Muster");
		buttonKonfiguration = new JButton("Konfiguration");
		buttonHilfe = new JButton("Hilfe");
		
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 1));
		
		panel.add(buttonDateiauswahl);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(buttonDateiauswahl2);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(buttonKonfiguration);
		panel.add(Box.createRigidArea(new Dimension(5, 0)));
		panel.add(buttonHilfe);
		
		// ZUM TESTEN
//		panel.add(Box.createRigidArea(new Dimension(5, 0)));
//		panel.add(buttonMatrix); 
		
		this.add(panel, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("MultiTextCompare");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	public void addDateiauswahlListener(ActionListener e){
		buttonDateiauswahl.addActionListener(e);
	}
	
	public void addDateiauswahl2Listener(ActionListener e){
		buttonDateiauswahl2.addActionListener(e);
	}
	
	public void addKonfigurationListener(ActionListener e){
		buttonKonfiguration.addActionListener(e);
	}
	
	public void addHilfeListener(ActionListener e){
		buttonHilfe.addActionListener(e);
	}
	
	// ZUM TESTEN
	public void addMatrixListenerTEST(ActionListener e){
		buttonMatrix.addActionListener(e);
	}
}
