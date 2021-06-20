package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.control.PopupController;
import de.thkoeln.syp.mtc.logging.Logger;

public class PopupView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2476545478556069504L;
	private PopupController popupController;
	private JPanel panel;
	private JLabel label;
	private JLabel labelSpace;
	private JButton btnOk;
	private Management management;
	private Logger logger;

	public PopupView(String name, String error) {
		management = Management.getInstance();
		logger = management.getLogger();
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
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
		popupController = new PopupController(this);
	}

	public void addOkListener(ActionListener e) {
		btnOk.addActionListener(e);
	}

}
