package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.gui.control.PopupController;

public class PopupView extends JFrame {
	private PopupController popupController;
	private JPanel panel;
	private JLabel label;
	private JButton btnOk;
	
	public PopupView(String error) {
		panel = new JPanel();
		label = new JLabel(error);
		btnOk = new JButton("OK");
		this.getRootPane().setDefaultButton(btnOk);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setLayout(new GridLayout(0, 1));
		panel.add(label);
		panel.add(btnOk);
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Konfiguration");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		popupController = new PopupController(this);
	}
	
	public void addOkListener(ActionListener e) {
		btnOk.addActionListener(e);
	}

}
