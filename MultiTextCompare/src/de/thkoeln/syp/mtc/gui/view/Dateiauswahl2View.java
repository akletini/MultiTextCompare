package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.thkoeln.syp.mtc.gui.control.Dateiauswahl2Controller;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class Dateiauswahl2View extends JFrame{
	JPanel panel;
	JLabel labelWurzelverzeichnis;
	JLabel labelWurzelpfad;
	JLabel labelName;
	JButton buttonWurzelverzeichnis;
	JButton buttonVergleichen;
	JTextField textFieldName;
	FileDialog fd;

	IFileImporter fileImport;
	ITextvergleicher textVergleicher;
	Dateiauswahl2Controller dateiauswahl2Controller;

	public Dateiauswahl2View(){
		panel = new JPanel();
		textVergleicher = new ITextvergleicherImpl();
		fileImport = new IFileImporterImpl();
		labelWurzelverzeichnis = new JLabel("Wurzelverzeichnis: ");
		labelWurzelpfad = new JLabel(fileImport.getConfig().getRootDir());
		labelName = new JLabel("Dateiname:");
		buttonWurzelverzeichnis = new JButton("Neu auswählen...");
		buttonVergleichen = new JButton("Vergleichen");
		textFieldName = new JTextField();
		
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 2));
		
		
		panel.add(labelWurzelverzeichnis);
		panel.add(labelWurzelpfad);
		panel.add(new JLabel(""));
		panel.add(buttonWurzelverzeichnis);
		panel.add(labelName);
		panel.add(new JLabel(""));
		panel.add(textFieldName);
		panel.add(buttonVergleichen);

		
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Dateivergleich");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		fd = new FileDialog(this, "Verzeichniswahl", FileDialog.LOAD);
		dateiauswahl2Controller =  new Dateiauswahl2Controller(this);
	}
	
	public void addWurzelverzeichnisListener(ActionListener e){
		buttonWurzelverzeichnis.addActionListener(e);
	}
	
	public void addVergleichenListener(ActionListener e){
		buttonVergleichen.addActionListener(e);
	}
	
	public IFileImporter getFileImport(){
		return fileImport;
	}
	
	public ITextvergleicher getTextvergleicher(){
		return textVergleicher;
	}
	
	public JTextField getTextFieldName(){
		return textFieldName;
	}
	
	public void updateWurzelpfad(){
		labelWurzelpfad.setText(fileImport.getConfig().getRootDir());
	}
}
