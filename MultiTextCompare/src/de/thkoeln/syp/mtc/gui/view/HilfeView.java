package de.thkoeln.syp.mtc.gui.view;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;

public class HilfeView extends JFrame {

	private JPanel panel;
	private FileDialog fd;
	private File[] auswahl;

	public HilfeView() throws MalformedURLException, IOException {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 1));

		fd = new FileDialog(this, "Dateiauswahl", FileDialog.LOAD);
		fd.setMultipleMode(true);
		fd.setDirectory(".");
		fd.setFile("*.txt");
		fd.setVisible(true);

		auswahl = fd.getFiles();

		IDiffHelper diff = new IDiffHelperImpl();

//		JEditorPane editorpane = new JEditorPane();
//		JScrollPane editorScrollPane = new JScrollPane(editorpane);
//		editorScrollPane
//				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		File file = new File(System.getProperty("user.dir") + File.separator
//				+ "/out/displayDiff.html");
//		editorpane.setPage(file.toURI().toURL());
//		editorpane.setEditable(true);
//		JFrame frame2 = new JFrame();
//		frame2.getContentPane().add(editorScrollPane);
//		frame2.setSize(400,400);
//		frame2.setVisible(true);
		
		// XHTMLPanel htmlPanel = new XHTMLPanel();
		// try {
		// String anzeigeModus = "BOTH"; // Optionen: BOTH, MID, RIGHT
		// // diff.computeDisplayDiff(auswahl, anzeigeModus);
		// File out = new File(System.getProperty("user.dir")
		// + File.separator + "/out/displayDiff.html");
		//
		// htmlPanel.setDocument(out);
		// FSScrollPane scroll = new FSScrollPane(htmlPanel);
		// JFrame frame = new JFrame("Flying Saucer Single Page Demo");
		// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.getContentPane().add(scroll);
		// frame.pack();
		// frame.setSize(1024, 768);
		// frame.setVisible(true);
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		 try {
		 String anzeigeModus = "BOTH"; //Optionen: BOTH, MID, RIGHT
		 diff.computeDisplayDiff(auswahl, anzeigeModus);
//		 String url = System.getProperty("user.dir")
//		 + File.separator + "/out/displayDiff.html";
//		 File htmlFile = new File(url);
//		 Desktop.getDesktop().browse(htmlFile.toURI());
		 } catch (IOException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		 }

		// this.setLocationRelativeTo(null);
	}

}
