package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

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
	private JLabel labelSpace;
	private JButton btnOk;

	public PopupView(String name, String error) {
		panel = new JPanel();
		label = new JLabel(error);
		labelSpace = new JLabel();
		btnOk = new JButton("OK");
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		panel.setLayout(new GridLayout(0, 1));
		panel.add(label);
		panel.add(labelSpace);
		panel.add(btnOk);
		this.add(panel, BorderLayout.CENTER);
		this.setTitle(name);
		this.pack();
		this.setLocationRelativeTo(null);
		this.getRootPane().setDefaultButton(btnOk);
		this.setVisible(true);
		popupController = new PopupController(this);
	}

	public void addOkListener(ActionListener e) {
		btnOk.addActionListener(e);
	}

}
