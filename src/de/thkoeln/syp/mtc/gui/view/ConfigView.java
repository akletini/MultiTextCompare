package de.thkoeln.syp.mtc.gui.view;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.control.ConfigController;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Visualisierung des Config-Panels
 * @author Allen Kletinitch
 *
 */
public class ConfigView extends JFrame {

	private static final long serialVersionUID = 7166501486094263080L;
	private Management management;
	private IConfig config;
	private Logger logger;
	private int matchAtSimilarity;
	private JPanel panel, panelGeneral, panelXML, panelMatching, panelJSON;
	private JScrollPane scrollGeneral, scrollMatching, scrollXML, scrollJSON;
	private JCheckBox whitespaceCheck, blanklinesCheck,
			checkBoxKeepPunctuation, checkBoxKeepCapitalization,
			checkBoxSortElements, checkBoxSortAttibutes,
			checkBoxDeleteAttributes, checkBoxDeleteComments, checkBoxOnlyTags,
			checkBoxMatchLines, checkBoxBestMatch, checkBoxSortKeys,
			checkBoxDeleteValues, checkBoxXMLSemantic, checkBoxJSONSemantic,
			checkBoxOpenLastComparison, checkBoxKeepJsonArrayOrder,
			checkBoxCompareXMLComments;

	private JLabel labelBlankLines, lblKeepPunctuation, lblKeepCapitalization,
			labelWhitespace, comparisonModeLabel, lblRootPath,
			lblComparisonParameters, lblValidation, lblPrint, lblSortElements,
			lblSortAttributes, lblDeleteAttributes, lblDeleteComments,
			lblOnlyTags, lblMatching, lblMatchLines, lblLookForBest,
			lblMatchAt, lblSortKeys, lblDeleteValues, lblXmlParameters,
			lblJsonParameters, lblLookahead, lblMaxLineLength, lblXmlSemantic,
			lblJsonSemantic, lblOpenLastComparison, lblKeepJsonArrayOrder,
			lblCompareXMLComments;

	private JComboBox<?> comboBoxComparisonModes, comboBoxXMLValidation,
			comboBoxXMLPrint;
	private JButton btnResetToDefault, btnSetRootPath, btnOk, btnCancel,
			btnSaveAs;
	private JSlider matchAtSlider;
	private JTextField textFieldLookahead, textFieldMaxLength;

	/**
	 * Create the frame.
	 */
	public ConfigView() {
		management = Management.getInstance();
		config = management.getFileImporter().getConfig();
		logger = management.getLogger();

		Color white = Color.WHITE;
		ToolTipManager.sharedInstance().setInitialDelay(500);
		ToolTipManager.sharedInstance().setDismissDelay(6000);
		panel = new JPanel();
		panelGeneral = new JPanel();
		panelMatching = new JPanel();
		panelXML = new JPanel();
		panelJSON = new JPanel();

		scrollGeneral = new JScrollPane();
		scrollMatching = new JScrollPane();
		scrollXML = new JScrollPane();
		scrollJSON = new JScrollPane();

		scrollGeneral.setViewportView(panelGeneral);
		scrollMatching.setViewportView(panelMatching);
		scrollXML.setViewportView(panelXML);
		scrollJSON.setViewportView(panelJSON);

		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new MigLayout("", "[grow]", "[25px][grow,fill][]"));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(panel.getBounds());

		// //////////////////////GENERAL/////////////////////////////////
		panelGeneral.setLayout(new MigLayout("",
				"[][164.00][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][][77.00]"));

		lblComparisonParameters = new JLabel("Comparison parameters");
		lblComparisonParameters.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelGeneral.add(lblComparisonParameters, "cell 1 1");

		whitespaceCheck = new JCheckBox("");
		whitespaceCheck.setBackground(white);
		whitespaceCheck.setSelected(config.getKeepWhitespaces());
		panelGeneral.add(whitespaceCheck, "cell 1 3,alignx left,aligny top");

		labelWhitespace = new JLabel("Keep whitespaces");
		labelWhitespace
				.setToolTipText("Decides if whitespaces will be deleted for any comparison");
		panelGeneral.add(labelWhitespace, "cell 5 3,alignx left,aligny center");

		blanklinesCheck = new JCheckBox("");
		blanklinesCheck.setBackground(white);
		blanklinesCheck.setSelected(config.getKeepBlankLines());
		panelGeneral.add(blanklinesCheck, "cell 1 4");

		labelBlankLines = new JLabel("Keep blank lines");
		labelBlankLines
				.setToolTipText("Decides if blank lines will be deleted for any comparison");
		panelGeneral.add(labelBlankLines, "cell 5 4,alignx left");

		checkBoxKeepPunctuation = new JCheckBox("");
		checkBoxKeepPunctuation.setBackground(white);
		checkBoxKeepPunctuation.setSelected(config.getKeepPuctuation());
		panelGeneral.add(checkBoxKeepPunctuation, "cell 1 5");

		lblKeepPunctuation = new JLabel("Keep punctuation");
		lblKeepPunctuation
				.setToolTipText("Decides if punctuation will be deleted for any comparison");
		panelGeneral.add(lblKeepPunctuation, "cell 5 5,alignx left");

		checkBoxKeepCapitalization = new JCheckBox("");
		checkBoxKeepCapitalization.setBackground(white);
		checkBoxKeepCapitalization.setSelected(config.getKeepCapitalization());
		panelGeneral.add(checkBoxKeepCapitalization, "cell 1 6");

		lblKeepCapitalization = new JLabel("Keep capitalization");
		lblKeepCapitalization.setHorizontalAlignment(SwingConstants.LEFT);
		lblKeepCapitalization
				.setToolTipText("If disabled all characters will be set to lowercase");
		panelGeneral.add(lblKeepCapitalization, "cell 5 6,alignx left");

		textFieldMaxLength = new JTextField();
		textFieldMaxLength.setText("" + config.getMaxLineLength());
		panelGeneral.add(textFieldMaxLength,
				"cell 1 7, alignx center,aligny center, grow");

		lblMaxLineLength = new JLabel("Maximum line length");
		lblMaxLineLength
				.setToolTipText("<html>If a line contains more characters than specified here the comparison will be aborted and valued at zero similarity<br>"
						+ "If set to  0, the line length will not be limited<br>"
						+ "May greatly improve performance for comparisons</html>");
		panelGeneral.add(lblMaxLineLength, "cell 5 7,alignx left");

		String[] comparisonModes = { "Compare characters", "Compare lines" };
		comboBoxComparisonModes = new JComboBox<Object>(comparisonModes);
		comboBoxComparisonModes.setSelectedIndex(config.getCompareLines() ? 1
				: 0);

		panelGeneral.add(comboBoxComparisonModes, "cell 1 8,alignx left, grow");

		comparisonModeLabel = new JLabel("Comparison mode");
		comparisonModeLabel.setHorizontalAlignment(SwingConstants.LEFT);
		comparisonModeLabel
				.setToolTipText("<html>Sets the comparison mode.<br>"
						+ "<i>Compare lines</i>: Compares files on a line-by-line base. Is affected by the \"Match lines\" setting in the \"Matching\" options menu<br>"
						+ "<i>Compare characters</i>: Compares all characters present in the files</html>");
		panelGeneral.add(comparisonModeLabel, "cell 5 8,alignx left");

		btnSetRootPath = new JButton();
		if (config.getRootDir() != null) {
			btnSetRootPath.setText(config.getRootDir());
		}
		btnSetRootPath
				.setToolTipText("If the wildcard search is used, this is the directory below which all files are located");
		panelGeneral.add(btnSetRootPath, "cell 5 10,alignx left");

		lblRootPath = new JLabel("Root search path");
		lblRootPath.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelGeneral.add(lblRootPath, "cell 1 10");

		checkBoxOpenLastComparison = new JCheckBox("");
		checkBoxOpenLastComparison.setBackground(white);
		checkBoxOpenLastComparison.setSelected(config.getOpenLastComparison());
		panelGeneral.add(checkBoxOpenLastComparison, "cell 1 11");

		lblOpenLastComparison = new JLabel(
				"Open last saved comparison on startup");
		if (!config.getLastComparisonPath().equals("")) {
			lblOpenLastComparison.setText(lblOpenLastComparison.getText()
					+ ". Currently: " + config.getLastComparisonPath());
		}
		panelGeneral.add(lblOpenLastComparison, "cell 5 11,alignx left");

		// ////////////////////////XML//////////////////////////////
		panelXML.setLayout(new MigLayout("",
				"[][164.00px][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][][77.00]"));

		String[] validations = { "None", "Internal XSD", "External XSD", "DTD" };

		lblXmlParameters = new JLabel("XML parameters");
		lblXmlParameters.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelXML.add(lblXmlParameters, "cell 1 1");
		comboBoxXMLValidation = new JComboBox<Object>(validations);
		comboBoxXMLValidation.setSelectedItem(config.getXmlValidation());
		panelXML.add(comboBoxXMLValidation, "cell 1 3,alignx left, grow");

		lblValidation = new JLabel("Validation");
		lblValidation
				.setToolTipText("Select if or how XML-files should be validated");
		panelXML.add(lblValidation, "cell 5 3");

		String[] prints = { "Pretty", "Raw", "Compact" };
		comboBoxXMLPrint = new JComboBox<Object>(prints);
		comboBoxXMLPrint.setSelectedItem(config.getXmlPrint());
		panelXML.add(comboBoxXMLPrint, "cell 1 4,alignx left, grow");

		lblPrint = new JLabel("Print");
		lblPrint.setToolTipText("Select how XML will be printed");
		panelXML.add(lblPrint, "cell 5 4");

		checkBoxSortElements = new JCheckBox("");
		checkBoxSortElements.setBackground(white);
		checkBoxSortElements.setSelected(config.getXmlSortElements());
		panelXML.add(checkBoxSortElements, "cell 1 5");

		lblSortElements = new JLabel("Sort elements");
		lblSortElements
				.setToolTipText("If selected, XML files will be sorted alphabetically");
		panelXML.add(lblSortElements, "cell 5 5");

		checkBoxSortAttibutes = new JCheckBox("");
		checkBoxSortAttibutes.setBackground(white);
		checkBoxSortAttibutes.setSelected(config.getXmlSortAttributes());
		panelXML.add(checkBoxSortAttibutes, "cell 1 6");

		lblSortAttributes = new JLabel("Sort attributes");
		lblSortAttributes
				.setToolTipText("If selected, attributes will be sorted alphabetically");
		panelXML.add(lblSortAttributes, "cell 5 6");

		checkBoxDeleteAttributes = new JCheckBox("");
		checkBoxDeleteAttributes.setBackground(white);
		checkBoxDeleteAttributes.setSelected(config.getXmlDeleteAttributes());
		panelXML.add(checkBoxDeleteAttributes, "cell 1 7");

		lblDeleteAttributes = new JLabel("Delete attributes");
		lblDeleteAttributes
				.setToolTipText("If selected, attributes will be deleted for comparisons");
		panelXML.add(lblDeleteAttributes, "cell 5 7");

		checkBoxDeleteComments = new JCheckBox("");
		checkBoxDeleteComments.setBackground(white);
		checkBoxDeleteComments.setSelected(config.getXmlDeleteComments());
		panelXML.add(checkBoxDeleteComments, "cell 1 8");

		lblDeleteComments = new JLabel("Delete comments");
		lblDeleteComments
				.setToolTipText("If selected, comments will be deleted for comparisons");
		panelXML.add(lblDeleteComments, "cell 5 8");

		checkBoxOnlyTags = new JCheckBox("");
		checkBoxOnlyTags.setBackground(white);
		checkBoxOnlyTags.setSelected(config.getXmlOnlyTags());
		panelXML.add(checkBoxOnlyTags, "cell 1 9");

		lblOnlyTags = new JLabel("Only Tags");
		lblOnlyTags
				.setToolTipText("If selected, only tags will be considered for comparison");
		panelXML.add(lblOnlyTags, "cell 5 9");

		checkBoxXMLSemantic = new JCheckBox("");
		checkBoxXMLSemantic.setBackground(white);
		checkBoxXMLSemantic.setSelected(config.isXmlUseSemanticComparison());
		panelXML.add(checkBoxXMLSemantic, "cell 1 10");

		lblXmlSemantic = new JLabel("Use semantic comparison");
		lblXmlSemantic.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblXmlSemantic
				.setToolTipText("If selected, XML-files will be compared on a structural level rather than on a line-by-line or character basis");
		panelXML.add(lblXmlSemantic, "cell 5 10");

		
		checkBoxCompareXMLComments = new JCheckBox("");
		checkBoxCompareXMLComments.setBackground(white);
		checkBoxCompareXMLComments.setSelected(config.getXmlCompareComments());
		panelXML.add(checkBoxCompareXMLComments, "cell 1 11");
		
		lblCompareXMLComments = new JLabel("Compare comments");
		lblCompareXMLComments
				.setToolTipText("If selected, comments will be compared in semantic comparison");
		panelXML.add(lblCompareXMLComments, "cell 5 11");
		
		// ////////////////////////Matching//////////////////////////////
		panelMatching.setLayout(new MigLayout("",
				"[][164.00][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][77.00]"));

		lblMatching = new JLabel("Matching");
		lblMatching.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelMatching.add(lblMatching, "cell 1 1");

		checkBoxMatchLines = new JCheckBox("");
		checkBoxMatchLines.setBackground(white);
		checkBoxMatchLines.setSelected(config.getLineMatch());
		panelMatching.add(checkBoxMatchLines, "cell 1 3");

		lblMatchLines = new JLabel("Match lines");
		lblMatchLines
				.setToolTipText("<html>If enabled, similar lines will be aligned in the diff view and if <i>Compare lines is enabled</i></html>");
		panelMatching.add(lblMatchLines, "cell 5 3");

		checkBoxBestMatch = new JCheckBox("");
		checkBoxBestMatch.setBackground(white);
		checkBoxBestMatch.setSelected(config.getBestMatch());
		panelMatching.add(checkBoxBestMatch, "cell 1 4");

		lblLookForBest = new JLabel("Look for best match");
		lblLookForBest
				.setToolTipText("Will match most similar line within the lookahead instead of stopping at the first match. May slow down the comparison");
		panelMatching.add(lblLookForBest, "cell 5 4");
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

		panelMatching
				.add(matchAtSlider, "cell 1 5,alignx center,aligny center");

		lblMatchAt = new JLabel("Match lines at "
				+ (int) (config.getMatchAt() * 100) + "% similarity");
		panelMatching.add(lblMatchAt, "cell 5 5");

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
		panelMatching.add(textFieldLookahead,
				"cell 1 6,alignx center,aligny center, grow");

		lblLookahead = new JLabel("Matching lookahead ");
		lblLookahead
				.setToolTipText("<html>Number of lines that will be checked to write similar text on the same line.<br> Setting it to 0 will cause the matcher to search through the whole document</html>");
		panelMatching.add(lblLookahead, "cell 5 6");

		// ///////////////////////JSON//////////////////////////
		panelJSON.setLayout(new MigLayout("",
				"[][164.00][][][31.00][grow,fill]",
				"[][20.00px][10px:10px:10px][][][][][][][][][77.00]"));

		lblJsonParameters = new JLabel("JSON parameters");
		lblJsonParameters.setFont(new Font("Tahoma", Font.BOLD, 13));
		panelJSON.add(lblJsonParameters, "cell 1 1");
		
		checkBoxJSONSemantic = new JCheckBox("");
		checkBoxJSONSemantic.setBackground(white);
		checkBoxJSONSemantic.setSelected(config.isJsonUseSemanticComparison());
		panelJSON.add(checkBoxJSONSemantic, "cell 1 3");

		lblJsonSemantic = new JLabel("Use semantic comparison");
		lblJsonSemantic.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblJsonSemantic
				.setToolTipText("If ticked, JSON-files will be compared on a structural level rather than on a line-by-line or character basis");
		panelJSON.add(lblJsonSemantic, "cell 5 3");

		
		lblKeepJsonArrayOrder = new JLabel("Keep JSON-Array order");
		lblKeepJsonArrayOrder.setToolTipText("If ticked, JSON-Arrays will not be sorted");
		panelJSON.add(lblKeepJsonArrayOrder, "cell 5 4");

		checkBoxKeepJsonArrayOrder = new JCheckBox("");
		checkBoxKeepJsonArrayOrder.setBackground(white);
		checkBoxKeepJsonArrayOrder.setSelected(config.getJsonKeepArrayOrder());
		panelJSON.add(checkBoxKeepJsonArrayOrder, "cell 1 4");

		lblSortKeys = new JLabel("Sort keys");
		lblSortKeys
				.setToolTipText("If ticked, files will be sorted alphabetically");
		panelJSON.add(lblSortKeys, "cell 5 5");
		
		checkBoxSortKeys = new JCheckBox("");
		checkBoxSortKeys.setBackground(white);
		checkBoxSortKeys.setSelected(config.getJsonSortKeys());
		panelJSON.add(checkBoxSortKeys, "cell 1 5");

		checkBoxDeleteValues = new JCheckBox("");
		checkBoxDeleteValues.setBackground(white);
		checkBoxDeleteValues.setSelected(config.getJsonDeleteValues());
		panelJSON.add(checkBoxDeleteValues, "cell 1 6");

		lblDeleteValues = new JLabel("Delete values");
		lblDeleteValues.setToolTipText("If ticked, only keys will be compared");
		panelJSON.add(lblDeleteValues, "cell 5 6");


		// //////////////////////////////////////////////////////////////
		tabbedPane.add(scrollGeneral, "    General    ");
		tabbedPane.add(scrollMatching, "    Matching    ");
		tabbedPane.add(scrollXML, "    XML    ");
		tabbedPane.add(scrollJSON, "    JSON    ");

		tabbedPane.setBackground(white);
		panelGeneral.setBackground(white);
		panelMatching.setBackground(white);
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
			this.setIconImage(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("icon.png")));
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		management.setConfigController(new ConfigController(this));
	}

	public void setLookaheadText() {
		try {
			int lookahead = config.getMatchingLookahead();
			String lookeheadText = Integer.toString(lookahead);
			textFieldLookahead.setText(lookeheadText);

		} catch (NumberFormatException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
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
		return panelMatching;
	}

	public JPanel getPanelJSON() {
		return panelJSON;
	}

	public JScrollPane getScrollGeneral() {
		return scrollGeneral;
	}

	public JScrollPane getScrollDiff() {
		return scrollMatching;
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

	public JComboBox<?> getComboBoxComparisonModes() {
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

	public JComboBox<?> getComboBoxXmlValidation() {
		return comboBoxXMLValidation;
	}

	public JLabel getLblValidation() {
		return lblValidation;
	}

	public JLabel getLblPrint() {
		return lblPrint;
	}

	public JComboBox<?> getComboBoxXmlPrint() {
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
		int configLookahead = management.getFileImporter().getConfig()
				.getMatchingLookahead();
		try {

			int inputLookahead = Integer.parseInt(textFieldLookahead.getText());
			if (inputLookahead < 0) {
				textFieldLookahead.setText("" + configLookahead);
				return configLookahead;
			}
			return inputLookahead;
		} catch (NumberFormatException e) {
			textFieldLookahead.setText("" + configLookahead);
			logger.setMessage("Invalid lookahead value", Logger.LEVEL_ERROR);
		}
		return configLookahead;
	}

	public JTextField getTextFieldLookahead() {
		return textFieldLookahead;
	}

	public int getTextFieldMaxLineLengthValue() {
		int configLineLength = management.getFileImporter().getConfig()
				.getMaxLineLength();
		try {

			int inputLookahead = Integer.parseInt(textFieldMaxLength.getText());
			if (inputLookahead < 0) {
				textFieldMaxLength.setText("" + configLineLength);
				return configLineLength;
			}
			return inputLookahead;
		} catch (NumberFormatException e) {
			textFieldMaxLength.setText("" + configLineLength);
			logger.setMessage("Invalid line length value", Logger.LEVEL_ERROR);
		}
		return configLineLength;
	}

	public JCheckBox getCheckBoxXMLSemantic() {
		return checkBoxXMLSemantic;
	}

	public JCheckBox getCheckBoxJSONSemantic() {
		return checkBoxJSONSemantic;
	}

	public JTextField getTextFieldMaxLength() {
		return textFieldMaxLength;
	}

	public void setCheckBoxXMLSemantic(JCheckBox checkBoxXMLSemantic) {
		this.checkBoxXMLSemantic = checkBoxXMLSemantic;
	}

	public void setCheckBoxJSONSemantic(JCheckBox checkBoxJSONSemantic) {
		this.checkBoxJSONSemantic = checkBoxJSONSemantic;
	}

	public void setTextFieldMaxLength(JTextField textFieldMaxLength) {
		this.textFieldMaxLength = textFieldMaxLength;
	}

	public JCheckBox getCheckBoxOpenLastComparison() {
		return checkBoxOpenLastComparison;
	}

	public void setCheckBoxOpenLastComparison(
			JCheckBox checkBoxOpenLastComparison) {
		this.checkBoxOpenLastComparison = checkBoxOpenLastComparison;
	}
	
	public JCheckBox getCheckBoxKeepJsonArrayOrder() {
		return checkBoxKeepJsonArrayOrder;
	}

	public JCheckBox getCheckBoxCompareXMLComments() {
		return checkBoxCompareXMLComments;
	}

	public void setCheckBoxKeepJsonArrayOrder(JCheckBox checkBoxKeepJsonArrayOrder) {
		this.checkBoxKeepJsonArrayOrder = checkBoxKeepJsonArrayOrder;
	}

	public void setCheckBoxCompareXMLComments(JCheckBox checkBoxCompareXMLComments) {
		this.checkBoxCompareXMLComments = checkBoxCompareXMLComments;
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
