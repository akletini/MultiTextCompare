package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.thkoeln.syp.mtc.gui.control.KonfigurationController;

public class KonfigurationView extends JFrame {
	JPanel panel;
	JLabel textFieldLeerzeichen;
	JLabel textFieldSatzzeichen;
	JLabel textFieldGrossschreibung;
	JLabel textFieldWurzelverzeichnis;
	JCheckBox checkBoxLeerzeichen;
	JCheckBox checkBoxGrossschreibung;
	JCheckBox checkBoxSatzzeichen;
	JButton buttonWurzelverzeichnis;
	
	public KonfigurationView() {
		textFieldLeerzeichen = new JLabel("Beachte Leezeichen ");
		textFieldSatzzeichen = new JLabel("Beachte Satzzeichen ");
		textFieldGrossschreibung = new JLabel("Beachte Groﬂschreibung ");
		textFieldWurzelverzeichnis = new JLabel("Wurzelverzeichnis ");
		checkBoxLeerzeichen = new JCheckBox();
		checkBoxSatzzeichen = new JCheckBox();
		checkBoxGrossschreibung = new JCheckBox();
		buttonWurzelverzeichnis = new JButton("Ausw‰hlen");
		
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 2));
		
		panel.add(textFieldLeerzeichen);
		panel.add(checkBoxLeerzeichen);

		panel.add(textFieldSatzzeichen);
		panel.add(checkBoxSatzzeichen);

		panel.add(textFieldGrossschreibung);
		panel.add(checkBoxGrossschreibung);

		panel.add(textFieldWurzelverzeichnis);
		panel.add(buttonWurzelverzeichnis);
		
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Konfiguration");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	public void addWurzelverzeichnisListener(ActionListener action){
		buttonWurzelverzeichnis.addActionListener(action);
	}
}


