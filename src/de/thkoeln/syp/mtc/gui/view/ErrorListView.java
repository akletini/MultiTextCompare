package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

/**
 * Visualisierung des Fehlerliste
 * 
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
	private JButton delButton;
	private Management management;
	private Logger logger;

	public ErrorListView() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
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

		labelErrorCount = new JLabel("Current error count: "
				+ listFilePath.getModel().getSize());
		labelErrorCount.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(labelErrorCount, "flowx,cell 0 2");
		delButton = new JButton("Delete from selection");
		delButton.addActionListener(new DeleteFromImports());
		panel.add(delButton, "flowx,cell 0 2");

		scrollPane = new JScrollPane(listFilePath);
		panel.add(scrollPane, "flowx,cell 0 1");

		add(panel);
		setMinimumSize(new Dimension(700, 400));
		setSize(new Dimension(400, 500));
		setLocationRelativeTo(null);
		setTitle("Parse Error Overview");
		try {
			this.setIconImage(ImageIO.read(classLoader
					.getResourceAsStream("icon.png")));
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

	class DeleteFromImports implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			management = Management.getInstance();
			IFileImporter fileImporter = management.getFileImporter();

			List<IParseError> errorFiles = management.getCurrentErrorList();
			if (!errorFiles.isEmpty()) {
				for (IParseError error : errorFiles) {
					fileImporter.deleteImport(error.getFile());
				}
				management.setNewSelection(true);
				errorList.clear();
				labelErrorCount.setText("Current error count: 0");

				management.getFileSelectionController().updateListFilePath();
			}
		}

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
