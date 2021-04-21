package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.control.ConfigController;
import de.thkoeln.syp.mtc.gui.control.Management;

public class ConfigView extends JFrame {

	private Management management;
	private IConfig config;
	private int matchAtSimilarity;
	private JPanel panel, panelGeneral, panelXML, panelDiff, panelJSON;
	private JScrollPane scrollGeneral, scrollDiff, scrollXML, scrollJSON;
	private JCheckBox whitespaceCheck, blanklinesCheck,
			checkBoxKeepPunctuation, checkBoxKeepCapitalization,
			checkBoxSortElements, checkBoxSortAttibutes,
			checkBoxDeleteAttributes, checkBoxDeleteComments, checkBoxOnlyTags,
			checkBoxMatchLines, checkBoxBestMatch, checkBoxSortKeys,
			checkBoxDeleteValues;
	private JLabel labelBlankLines, lblKeepPunctuation, lblKeepCapitalization,
			comparisonModeLabel, lblRootPath, lblComparisonParameters,
			lblValidation, lblPrint, lblSortElements, lblSortAttributes,
			lblDeleteAttributes, lblDeleteComments, lblOnlyTags, lblMatching,
			lblMatchLines, lblLookForBest, lblMatchAt, lblSortKeys,
			lblDeleteValues, lblXmlParameters, lblJsonParameters, lblLookahead;

	private JComboBox comboBoxComparisonModes, comboBoxXMLValidation, comboBoxXMLPrint;
	private JButton btnResetToDefault, btnSetRootPath, btnOk, btnCancel, btnSaveAs;
	private JSlider matchAtSlider;
	private JTextField textFieldLookahead;

	/**
	 * Create the frame.
	 */
	public ConfigView() {
		management = Management.getInstance();
		config = management.getFileImporter().getConfig();

		Color white = Color.WHITE;
		panel = new JPanel();
		panelGeneral = new JPanel();
		panelDiff = new JPanel();
		panelXML = new JPanel();
		panelJSON = new JPanel();

		scrollGeneral = new JScrollPane();
		scrollDiff = new JScrollPane();
		scrollXML = new JScrollPane();
		scrollJSON = new JScrollPane();

		scrollGeneral.setViewportView(panelGeneral);
		scrollDiff.setViewportView(panelDiff);
		scrollXML.setViewportView(panelXML);
		scrollJSON.setViewportView(panelJSON);

		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new MigLayout("", "[grow]", "[25px][grow,fill][]"));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(panel.getBounds());

		// //////////////////////GENERAL/////////////////////////////////
		panelGeneral.setLayout(new MigLayout("",
				"[][164.00][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][77.00]"));

		lblComparisonParameters = new JLabel("Comparison parameters");
		lblComparisonParameters.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelGeneral.add(lblComparisonParameters, "cell 1 1");

		whitespaceCheck = new JCheckBox("");
		whitespaceCheck.setBackground(white);
		whitespaceCheck.setSelected(config.getKeepWhitespaces());
		panelGeneral.add(whitespaceCheck, "cell 1 3,alignx left,aligny top");

		JLabel labelWhitespace = new JLabel("Keep whitespaces");
		panelGeneral.add(labelWhitespace, "cell 5 3,alignx left,aligny center");

		blanklinesCheck = new JCheckBox("");
		blanklinesCheck.setBackground(white);
		blanklinesCheck.setSelected(config.getKeepBlankLines());
		panelGeneral.add(blanklinesCheck, "cell 1 4");

		labelBlankLines = new JLabel("Keep blank lines");
		panelGeneral.add(labelBlankLines, "cell 5 4,alignx left");

		checkBoxKeepPunctuation = new JCheckBox("");
		checkBoxKeepPunctuation.setBackground(white);
		checkBoxKeepPunctuation.setSelected(config.getKeepPuctuation());
		panelGeneral.add(checkBoxKeepPunctuation, "cell 1 5");

		lblKeepPunctuation = new JLabel("Keep punctuation");
		panelGeneral.add(lblKeepPunctuation, "cell 5 5,alignx left");

		checkBoxKeepCapitalization = new JCheckBox("");
		checkBoxKeepCapitalization.setBackground(white);
		checkBoxKeepCapitalization.setSelected(config.getKeepCapitalization());
		panelGeneral.add(checkBoxKeepCapitalization, "cell 1 6");

		lblKeepCapitalization = new JLabel("Keep capitalization");
		lblKeepCapitalization.setHorizontalAlignment(SwingConstants.LEFT);
		panelGeneral.add(lblKeepCapitalization, "cell 5 6,alignx left");

		String[] comparisonModes = { "Compare characters", "Compare lines" };
		comboBoxComparisonModes = new JComboBox(comparisonModes);
		comboBoxComparisonModes.setSelectedIndex(config.getCompareLines() ? 1
				: 0);

		panelGeneral.add(comboBoxComparisonModes, "cell 1 8,alignx left");

		comparisonModeLabel = new JLabel("Comparison mode");
		comparisonModeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		panelGeneral.add(comparisonModeLabel, "cell 5 8,alignx left");

		btnSetRootPath = new JButton();
		if (config.getRootDir() != null) {
			btnSetRootPath.setText(config.getRootDir());
		}
		panelGeneral.add(btnSetRootPath, "cell 5 10,alignx left");

		// ////////////////////////XML//////////////////////////////
		panelXML.setLayout(new MigLayout("",
				"[][164.00px][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][77.00]"));

		String[] validations = { "None", "Internal XSD", "External XSD", "DTD" };

		lblXmlParameters = new JLabel("XML parameters");
		lblXmlParameters.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelXML.add(lblXmlParameters, "cell 1 1");
		comboBoxXMLValidation = new JComboBox(validations);
		comboBoxXMLValidation.setSelectedItem(config.getXmlValidation());
		panelXML.add(comboBoxXMLValidation, "cell 1 3,alignx left, grow");

		lblValidation = new JLabel("Validation");
		panelXML.add(lblValidation, "cell 5 3");

		String[] prints = { "Pretty", "Raw", "Compact" };
		comboBoxXMLPrint = new JComboBox(prints);
		comboBoxXMLPrint.setSelectedItem(config.getXmlPrint());
		panelXML.add(comboBoxXMLPrint, "cell 1 4,alignx left, grow");

		lblPrint = new JLabel("Print");
		panelXML.add(lblPrint, "cell 5 4");

		checkBoxSortElements = new JCheckBox("");
		checkBoxSortElements.setBackground(white);
		checkBoxSortElements.setSelected(config.getXmlSortElements());
		panelXML.add(checkBoxSortElements, "cell 1 5");

		lblSortElements = new JLabel("Sort elements");
		panelXML.add(lblSortElements, "cell 5 5");

		checkBoxSortAttibutes = new JCheckBox("");
		checkBoxSortAttibutes.setBackground(white);
		checkBoxSortAttibutes.setSelected(config.getXmlSortAttributes());
		panelXML.add(checkBoxSortAttibutes, "cell 1 6");

		lblSortAttributes = new JLabel("Sort attributes");
		panelXML.add(lblSortAttributes, "cell 5 6");

		checkBoxDeleteAttributes = new JCheckBox("");
		checkBoxDeleteAttributes.setBackground(white);
		checkBoxDeleteAttributes.setSelected(config.getXmlDeleteAttributes());
		panelXML.add(checkBoxDeleteAttributes, "cell 1 7");

		lblDeleteAttributes = new JLabel("Delete attributes");
		panelXML.add(lblDeleteAttributes, "cell 5 7");

		checkBoxDeleteComments = new JCheckBox("");
		checkBoxDeleteComments.setBackground(white);
		checkBoxDeleteComments.setSelected(config.getXmlDeleteComments());
		panelXML.add(checkBoxDeleteComments, "cell 1 8");

		lblDeleteComments = new JLabel("Delete comments");
		panelXML.add(lblDeleteComments, "cell 5 8");

		checkBoxOnlyTags = new JCheckBox("");
		checkBoxOnlyTags.setBackground(white);
		checkBoxOnlyTags.setSelected(config.getXmlOnlyTags());
		panelXML.add(checkBoxOnlyTags, "cell 1 9");

		lblOnlyTags = new JLabel("Only Tags");
		panelXML.add(lblOnlyTags, "cell 5 9");

		lblRootPath = new JLabel("Root search path");
		lblRootPath.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelGeneral.add(lblRootPath, "cell 1 10");

		// ////////////////////////Diff//////////////////////////////
		panelDiff.setLayout(new MigLayout("",
				"[][164.00][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][77.00]"));

		lblMatching = new JLabel("Matching");
		lblMatching.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelDiff.add(lblMatching, "cell 1 1");

		checkBoxMatchLines = new JCheckBox("");
		checkBoxMatchLines.setBackground(white);
		checkBoxMatchLines.setSelected(config.getLineMatch());
		panelDiff.add(checkBoxMatchLines, "cell 1 3");

		lblMatchLines = new JLabel("Match lines");
		panelDiff.add(lblMatchLines, "cell 5 3");

		checkBoxBestMatch = new JCheckBox("");
		checkBoxBestMatch.setBackground(white);
		// checkBoxBestMatch.setSelected(config.getBestMatch());
		panelDiff.add(checkBoxBestMatch, "cell 1 4");

		lblLookForBest = new JLabel("Look for best match");
		panelDiff.add(lblLookForBest, "cell 5 4");
		matchAtSlider = new JSlider(JSlider.HORIZONTAL, 0, 100,
				(int) (config.getMatchAt() * 100));
		matchAtSlider.setBackground(white);
		matchAtSlider.setMinorTickSpacing(5);
		matchAtSlider.setMajorTickSpacing(25);
		matchAtSlider.setPaintTicks(true);
		matchAtSlider.setPaintLabels(true);
		matchAtSlider.setSnapToTicks(true);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(0, new JLabel("0"));
		labelTable.put(25, new JLabel("25"));
		labelTable.put(50, new JLabel("50"));
		labelTable.put(75, new JLabel("75"));
		labelTable.put(100, new JLabel("100"));
		matchAtSlider.setLabelTable(labelTable);

		panelDiff.add(matchAtSlider, "cell 1 5,alignx center,aligny center");

		lblMatchAt = new JLabel("Match lines at "
				+ (int) (config.getMatchAt() * 100) + "% similarity");
		panelDiff.add(lblMatchAt, "cell 5 5");

		matchAtSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				matchAtSimilarity = source.getValue();
				lblMatchAt.setText("Match lines at " + (int) matchAtSimilarity
						+ "% similarity");
			}
		});

		textFieldLookahead = new JTextField();
		textFieldLookahead.setText("" + config.getMatchingLookahead());
		panelDiff.add(textFieldLookahead,
				"cell 1 6,alignx center,aligny center, grow");

		lblLookahead = new JLabel("Matching lookahead ");
		lblLookahead
				.setToolTipText("<html>Number of lines that will be checked to write similar text on the same line.<br> Setting it to 0 will cause the matcher to search through the whole document</html>");
		panelDiff.add(lblLookahead, "cell 5 6");

		// ///////////////////////JSON//////////////////////////
		panelJSON.setLayout(new MigLayout("",
				"[][164.00][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][77.00]"));

		lblJsonParameters = new JLabel("JSON parameters");
		lblJsonParameters.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelJSON.add(lblJsonParameters, "cell 1 1");

		checkBoxSortKeys = new JCheckBox("");
		checkBoxSortKeys.setBackground(white);
		checkBoxSortKeys.setSelected(config.getJsonSortKeys());
		panelJSON.add(checkBoxSortKeys, "cell 1 3");

		lblSortKeys = new JLabel("Sort keys");
		panelJSON.add(lblSortKeys, "cell 5 3");

		checkBoxDeleteValues = new JCheckBox("");
		checkBoxDeleteValues.setBackground(white);
		checkBoxDeleteValues.setSelected(config.getJsonDeleteValues());
		panelJSON.add(checkBoxDeleteValues, "cell 1 4");

		lblDeleteValues = new JLabel("Delete values");
		panelJSON.add(lblDeleteValues, "cell 5 4");

		// //////////////////////////////////////////////////////////////
		tabbedPane.add(scrollGeneral, "    General    ");
		tabbedPane.add(scrollDiff, "    Diff    ");
		tabbedPane.add(scrollXML, "    XML    ");
		tabbedPane.add(scrollJSON, "    JSON    ");

		tabbedPane.setBackground(white);
		panelGeneral.setBackground(white);
		panelDiff.setBackground(white);
		panelXML.setBackground(white);
		panelJSON.setBackground(white);

		panel.add(tabbedPane, "cell 0 0 1 2,grow");
		setContentPane(panel);

		btnOk = new JButton("OK");
		panel.add(btnOk, "cell 0 2, trailing,growy");
		
		btnSaveAs = new JButton("Save as");
		panel.add(btnSaveAs, "cell 0 2");

		btnResetToDefault = new JButton("Defaults");
		panel.add(btnResetToDefault, "cell 0 2,alignx center,aligny top");

		btnCancel = new JButton("Cancel");
		panel.add(btnCancel, "cell 0 2");

		setTitle("Settings" + " using " + config.getPath());
		setResizable(true);
		setMinimumSize(new Dimension(800, 500));
		this.setBounds(100, 100, 800, 500);
		setSize(800, 500);
		setLocationRelativeTo(null);
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		management.setConfigController(new ConfigController(this));
	}

	public void setLookaheadText() {
		try {
			Integer lookahead = config.getMatchingLookahead();
			String lookeheadText = lookahead.toString();
			textFieldLookahead.setText(lookeheadText);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public JLabel getLblRootPath() {
		return lblRootPath;
	}

	public Management getManagement() {
		return management;
	}

	public IConfig getConfig() {
		return config;
	}

	public JPanel getPanel() {
		return panel;
	}

	public JPanel getPanelGeneral() {
		return panelGeneral;
	}

	public JPanel getPanelXML() {
		return panelXML;
	}

	public JPanel getPanelDiff() {
		return panelDiff;
	}

	public JPanel getPanelJSON() {
		return panelJSON;
	}

	public JScrollPane getScrollGeneral() {
		return scrollGeneral;
	}

	public JScrollPane getScrollDiff() {
		return scrollDiff;
	}

	public JScrollPane getScrollXML() {
		return scrollXML;
	}

	public JScrollPane getScrollJSON() {
		return scrollJSON;
	}

	public JCheckBox getCheckBoxWhitespaces() {
		return whitespaceCheck;
	}

	public JLabel getLabelBlankLines() {
		return labelBlankLines;
	}

	public JCheckBox getCheckBoxBlankLines() {
		return blanklinesCheck;
	}

	public JCheckBox getCheckBoxPunctuation() {
		return checkBoxKeepPunctuation;
	}

	public JCheckBox getCheckBoxCaps() {
		return checkBoxKeepCapitalization;
	}

	public JLabel getLblKeepPunctuation() {
		return lblKeepPunctuation;
	}

	public JLabel getLblKeepCapitalization() {
		return lblKeepCapitalization;
	}

	public JComboBox getComboBoxComparisonModes() {
		return comboBoxComparisonModes;
	}

	public JLabel getComparisonModeLabel() {
		return comparisonModeLabel;
	}

	public JButton getBtnResetToDefault() {
		return btnResetToDefault;
	}

	public JLabel getLblComparisonParameters() {
		return lblComparisonParameters;
	}

	public JComboBox getComboBoxXmlValidation() {
		return comboBoxXMLValidation;
	}

	public JLabel getLblValidation() {
		return lblValidation;
	}

	public JLabel getLblPrint() {
		return lblPrint;
	}

	public JComboBox getComboBoxXmlPrint() {
		return comboBoxXMLPrint;
	}

	public JLabel getLblSortElements() {
		return lblSortElements;
	}

	public JLabel getLblSortAttributes() {
		return lblSortAttributes;
	}

	public JLabel getLblDeleteAttributes() {
		return lblDeleteAttributes;
	}

	public JLabel getLblDeleteComments() {
		return lblDeleteComments;
	}

	public JLabel getLblOnlyTags() {
		return lblOnlyTags;
	}

	public JCheckBox getCheckBoxXmlSortElements() {
		return checkBoxSortElements;
	}

	public JCheckBox getCheckBoxXmlSortAttributes() {
		return checkBoxSortAttibutes;
	}

	public JCheckBox getCheckBoxXmlDeleteAttribute() {
		return checkBoxDeleteAttributes;
	}

	public JCheckBox getCheckBoxXmlDeleteComments() {
		return checkBoxDeleteComments;
	}

	public JCheckBox getCheckBoxXmlOnlyTags() {
		return checkBoxOnlyTags;
	}

	public JLabel getLblMatching() {
		return lblMatching;
	}

	public JCheckBox getCheckBoxLineMatch() {
		return checkBoxMatchLines;
	}

	public JLabel getLblMatchLines() {
		return lblMatchLines;
	}

	public JCheckBox getCheckBoxBestMatch() {
		return checkBoxBestMatch;
	}

	public JLabel getLblLookForBest() {
		return lblLookForBest;
	}

	public JSlider getMatchAtSlider() {
		return matchAtSlider;
	}

	public JLabel getLblMatchAt() {
		return lblMatchAt;
	}

	public JButton getBtnSetRootPath() {
		return btnSetRootPath;
	}

	public JCheckBox getCheckBoxJsonSortKeys() {
		return checkBoxSortKeys;
	}

	public JLabel getLblSortKeys() {
		return lblSortKeys;
	}

	public JCheckBox getCheckBoxJsonDeleteValues() {
		return checkBoxDeleteValues;
	}

	public JLabel getLblDeleteValues() {
		return lblDeleteValues;
	}

	public JButton getBtnOk() {
		return btnOk;
	}

	public JButton getBtnCancel() {
		return btnCancel;
	}

	public JLabel getLblXmlParameters() {
		return lblXmlParameters;
	}

	public JLabel getLblJsonParameters() {
		return lblJsonParameters;
	}

	public int getTextFieldLookaheadValue() {
		return Integer.parseInt(textFieldLookahead.getText());
	}

	public JTextField getTextFieldLookahead() {
		return textFieldLookahead;
	}

	// Button listeners

	public void addSetRootListener(ActionListener e) {
		btnSetRootPath.addActionListener(e);
	}

	public void addDefaultListener(ActionListener e) {
		btnResetToDefault.addActionListener(e);
	}

	public void addSaveListener(ActionListener e) {
		btnOk.addActionListener(e);
	}

	public void addCancelistener(ActionListener e) {
		btnCancel.addActionListener(e);
	}
	public void addSaveAsListener(ActionListener e) {
		btnSaveAs.addActionListener(e);
	}
	
	

}
