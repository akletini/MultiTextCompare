package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.thkoeln.syp.mtc.datenhaltung.api.IDiffChar;
import de.thkoeln.syp.mtc.datenhaltung.api.IDiffLine;
import de.thkoeln.syp.mtc.steuerung.impl.IDiffHelperImpl;
import de.thkoeln.syp.mtc.steuerung.services.IDiffHelper;

public class HilfeView extends JFrame {

	private JPanel panel;
	private File[] auswahl = new File[3];
	private JTextPane tPane;

	public HilfeView() throws IOException {
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 1));

		auswahl[0] = new File("F://a.txt");
		auswahl[1] = new File("F://b.txt");
		auswahl[2] = new File("F://c.txt");

		IDiffHelper diff = new IDiffHelperImpl();

		try {

			String anzeigeModus = "BOTH"; // Optionen: BOTH, MID, RIGHT
			diff.computeDisplayDiff(auswahl, anzeigeModus);
			EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));

			tPane = new JTextPane();
			tPane.setBorder(eb);
			// tPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
			tPane.setMargin(new Insets(5, 5, 5, 5));
			tPane.setBackground(new Color(192,192,192));
			panel.add(tPane);

			for(IDiffLine diffLine : diff.getLeftLines()){
				for(IDiffChar diffChar : diffLine.getDiffedLine()){
					appendToPane(tPane, diffChar.getCurrentChar().toString(), stringToColor(diffChar.getCharColor()));
				}
			}

			getContentPane().add(panel);

			pack();
			setVisible(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
	
	private Color stringToColor(String string){
		if(string.equals("WHITE")){
			return Color.WHITE;
		}
		else if(string.equals("RED")){
			return Color.RED;
		}
		else if(string.equals("GREEN")){
			return Color.GREEN;
		}
		return null;
	}

}
