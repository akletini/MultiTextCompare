package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;

/**
 * Visualisierung des Fehlerliste
 * @author Allen Kletinitch
 *
 */
public class ErrorListView extends JFrame {

	private static final long serialVersionUID = -7789052817218164171L;
	private DefaultListModel<String> errorList;
	private JList<String> listFilePath;
	private JPanel panel;
	private JLabel labelTitle, labelErrorCount;
	private JScrollPane scrollPane;
	private Management management;
	private Logger logger;

	public ErrorListView() {
		
		panel = new JPanel();
		errorList = new DefaultListModel<String>();

		panel.setLayout(new MigLayout("", "[grow,fill]",
				"[30px][grow,fill][30px]"));

		labelTitle = new JLabel("Select an affected file");
		labelTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(labelTitle, "cell 0 0");

		scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		listFilePath = new JList<String>(errorList);
		listFilePath.setBackground(Color.WHITE);
		listFilePath.addMouseListener(new ErrorViewListener());
		
		labelErrorCount = new JLabel("Current error count: " + listFilePath.getModel().getSize());
		labelErrorCount.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(labelErrorCount, "flowx,cell 0 2");
		
		scrollPane = new JScrollPane(listFilePath);
		panel.add(scrollPane, "flowx,cell 0 1");


		add(panel);
		setMinimumSize(new Dimension(700, 400));
		setSize(new Dimension(400, 500));
		setLocationRelativeTo(null);
		setTitle("Parse Error Overview");
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
	}

	public void updateList() {
		management = Management.getInstance();
		errorList.clear();
		List<IParseError> errorFiles = management.getCurrentErrorList();
		for (IParseError error : errorFiles) {
			errorList.addElement(error.getFile().getAbsolutePath());
		}
		labelErrorCount.setText("Current error count: " + errorFiles.size());
	}

	public DefaultListModel<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(DefaultListModel<String> errorList) {
		this.errorList = errorList;
	}

}

class ErrorViewListener extends MouseAdapter {
	Management management;

	public void mouseClicked(MouseEvent evt) {
		management = Management.getInstance();
		JList<?> list = (JList<?>) evt.getSource();

		if (evt.getClickCount() == 2) {

			int index = list.locationToIndex(evt.getPoint());
			String fileName = management.getErrorListPane().getErrorList()
					.get(index);
			File selectedFile = new File(fileName);

			List<IParseError> currentErrorList = management
					.getCurrentErrorList();
			IParseError clickedError = null;
			for (IParseError error : currentErrorList) {
				if (error.getFile().getAbsolutePath()
						.equals(selectedFile.getAbsolutePath())) {
					clickedError = error;
				}
			}
			management.setParseErrorView(new ParseErrorView(selectedFile,
						clickedError));

		}
	}
	

}

