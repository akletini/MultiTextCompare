package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IJSONParseError;
import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IJSONParseErrorImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IJSONSortNodeFactoryImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IJSONvergleicher;

public class IJSONvergleicherImpl extends JsonNodeFactory implements
		IJSONvergleicher {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2223880091323111953L;

	private List<IParseError> errorListe;

	private IJSONParseError parseError;

	private IFileImporter iFileImporter;

	private IConfig iConfig;

	public IJSONvergleicherImpl(IFileImporter fileImporter) {
		this.errorListe = new ArrayList<>();
		this.parseError = null;
		this.iFileImporter = fileImporter;
		this.iConfig = fileImporter.getConfig();
	}

	public IJSONvergleicherImpl() {

	}

	/**
	 * Parst mittels Jackson gegebenes File auf Wohlgeformtheit
	 * 
	 * @param Map
	 *            <File, File> Map mit zu manipulierender JSON-Dateiein
	 * 
	 * @return Map<File, File> Map welche nach den in der Config getroffenen
	 *         Einstellungen manipuliert wurde
	 * @throws IOException
	 */
	@Override
	public Map<File, File> jsonPrepare(Map<File, File> tempFiles)
			throws IOException {
		this.clearErrorList();

		boolean deleteValues = iConfig.getJsonDeleteValues();
		boolean sortKeys = iConfig.getJsonSortKeys();

		for (Map.Entry<File, File> entry : tempFiles.entrySet()) {

			if (!this.parseFile(entry.getKey())) {

				String jsonString = jsonFileToString(entry.getKey());

				if (sortKeys) {

					jsonString = this.sortKeysAlphabetical(jsonString);

				}

				if (deleteValues) {

					jsonString = this.deleteValues(jsonString);

				}

				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(entry.getValue()), "UTF-8"));
					writer.write(jsonString);
					writer.close();
				} catch (Exception e) {

					e.printStackTrace();

				}

			} else {
				// inkorrekt
			}

		}

		return tempFiles;
	}

	/**
	 * Ersetzt aus gegebenem String, welcher ein vollständiges JSON Dokument
	 * darstellt, alle Values
	 * 
	 * @param String
	 *            JSON-Dokument als String
	 * 
	 * @return String JSON-Dokument als String ohne Values
	 */
	public String deleteValues(String jsonString) {

		String[] jsonStringArray = jsonString.split("");

		// keyValue
		boolean insideKeyValueContext = false;
		boolean openingKeyValueIndicatorFound = false;
		boolean closingKeyValueIndicatorFound = false;

		// keyValueSeperatorAtIndex -> :
		boolean keyValueSeperatorFound = false;
		int keyValueSeperatorAtIndex = -1;

		// seperatorFound-> , ] }
		boolean seperatorFound = false;
		int seperatorAtIndex = -1;

		// array
		boolean arrayIndicatorFound = false;
		int arrayIndiacatorAtIndex = -1;

		boolean arrayClosingIndicatorFound = false;
		int arrayClosingIndicatorAtIndex = -1;

		boolean arrayComplete = false;

		for (int index = 0; index < jsonStringArray.length; index++) {

			if (jsonStringArray[index].equals("\"")) {

				if (openingKeyValueIndicatorFound == true
						&& closingKeyValueIndicatorFound == false) {

					closingKeyValueIndicatorFound = true;
					insideKeyValueContext = false;

				}

				if (openingKeyValueIndicatorFound == false
						&& closingKeyValueIndicatorFound == false) {

					openingKeyValueIndicatorFound = true;
					insideKeyValueContext = true;

				}

			}
			if (insideKeyValueContext) {
				// ignore Context
			} else {

				if (jsonStringArray[index].equals(":")) {
					openingKeyValueIndicatorFound = false;
					closingKeyValueIndicatorFound = false;
					insideKeyValueContext = false;

					keyValueSeperatorFound = true;
					keyValueSeperatorAtIndex = index;

				} else if (jsonStringArray[index].equals("[")) {
					arrayIndicatorFound = true;
					arrayIndiacatorAtIndex = index;

				} else if (jsonStringArray[index].equals(",")
						|| jsonStringArray[index].equals("]")
						|| jsonStringArray[index].equals("}")) {

					seperatorFound = true;
					seperatorAtIndex = index;

					if (jsonStringArray[index].equals("]")) {

						arrayClosingIndicatorAtIndex = index;
						arrayComplete = true;

					}

				}

			}
			if (keyValueSeperatorFound == true && seperatorFound
					&& arrayComplete && arrayIndicatorFound) {
				// array specific
				for (int i = arrayIndiacatorAtIndex + 1; i < arrayClosingIndicatorAtIndex; i++) {
					jsonStringArray[i] = "";
				}
				arrayComplete = false;
				arrayIndicatorFound = false;
				arrayIndiacatorAtIndex = -1;
				arrayClosingIndicatorFound = false;
				arrayClosingIndicatorAtIndex = -1;
				insideKeyValueContext = false;
				openingKeyValueIndicatorFound = false;
				closingKeyValueIndicatorFound = false;
				keyValueSeperatorFound = false;
				keyValueSeperatorAtIndex = -1;
				seperatorFound = false;
				seperatorAtIndex = -1;

			} else if (keyValueSeperatorFound == true && seperatorFound
					&& !arrayComplete && !arrayIndicatorFound) {
				for (int i = keyValueSeperatorAtIndex + 1; i < seperatorAtIndex; i++) {
					jsonStringArray[i] = "";

				}

				arrayComplete = false;
				arrayIndicatorFound = false;
				arrayIndiacatorAtIndex = -1;
				arrayClosingIndicatorFound = false;
				arrayClosingIndicatorAtIndex = -1;
				insideKeyValueContext = false;
				openingKeyValueIndicatorFound = false;
				closingKeyValueIndicatorFound = false;
				keyValueSeperatorFound = false;
				keyValueSeperatorAtIndex = -1;
				seperatorFound = false;
				seperatorAtIndex = -1;

			}
			seperatorFound = false;

		}
		String returnString = "";
		for (String x : jsonStringArray) {
			returnString = returnString + x;
		}
		return returnString;

	}

	/**
	 * Diese Methode setzt vorraus, dass das JSON-Dokument bereits erfolgreich
	 * geparst wurde!
	 * 
	 * @param jsonString
	 *            repraesentiert ein JSON Dokument dessen Values alphabetisch
	 *            sortiert werden sollen.
	 * 
	 * @return representiert das urspruengliche Dokument mit alphabeitsch
	 *         geordneten Values.
	 * 
	 * */
	public String sortKeysAlphabetical(String jsonString) {

		ObjectMapper mapper = JsonMapper.builder()
				.nodeFactory(new IJSONSortNodeFactoryImpl()).build();

		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

		JsonNode root = null;
		String sortedJSONString = "";

		try {

			root = mapper.readTree(jsonString);
			Iterator<String> fieldNames = root.fieldNames();
			while (fieldNames.hasNext()) {
				String fieldName = fieldNames.next();
				JsonNode fieldValue = root.get(fieldName);
				sortArraysRecursively(fieldValue);
			}
			sortedJSONString = mapper.writeValueAsString(root);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sortedJSONString;
	}

	private void sortArraysRecursively(JsonNode currentRoot) {
		if (currentRoot.isArray()) {
			ArrayList<JsonNode> arrayNodes = new ArrayList<JsonNode>();
			for (JsonNode node : currentRoot) {
				if (node.isValueNode()) {
					arrayNodes.add(node);
				} else if (node.isArray()) {
					sortArraysRecursively(node);
					arrayNodes.add(node);
				} else if (node.isObject()) {
					sortArraysRecursively(node);
					arrayNodes.add(node);
				}
			}
			Collections.sort(arrayNodes, new JsonArrayComparator());
			for (int i = 0; i < arrayNodes.size(); i++) {
				((ArrayNode) currentRoot).set(i, arrayNodes.get(i));
			}
		} else if (currentRoot.isObject()) {
			for (JsonNode node : currentRoot) {
				if (node.isValueNode()) {
					// besitzt keine Kindelemente
				} else if (node.isArray()) {
					sortArraysRecursively(node);
				} else if (node.isObject()) {
					sortArraysRecursively(node);
				}
			}
		}
	}

	/**
	 * Parst mittels Jackson gegebenes File auf Wohlgeformtheit
	 * 
	 * @param file
	 *            File welches JSON-Objekte beinhaltet
	 * 
	 * @return boolean Wenn ein Parse-Error aufgetreten ist true, sonst false.
	 */
	@Override
	public boolean parseFile(File file) {

		boolean parseErrorOccured = false;

		FileInputStream fileInputStream = null;

		byte[] fileInBytes = new byte[(int) file.length()];

		try {

			fileInputStream = new FileInputStream(file);
			fileInputStream.read(fileInBytes);
			fileInputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		String jsonString = new String(fileInBytes);

		JsonNode root = null;

		try {
			ObjectMapper mapper = JsonMapper.builder().build();

			root = mapper.readTree(jsonString);

		} catch (JsonParseException jpe) {
			parseError = new IJSONParseErrorImpl(file, jpe.getOriginalMessage()
					+ " at line: " + jpe.getLocation().getLineNr()
					+ ", column: " + jpe.getLocation().getColumnNr(), jpe
					.getLocation().getColumnNr(), jpe.getLocation().getLineNr());
			addErrorToErrorList(parseError);
			parseErrorOccured = true;

		} catch (JsonMappingException jme) {
			parseError = new IJSONParseErrorImpl(file, jme.getOriginalMessage()
					+ " at line: " + jme.getLocation().getLineNr()
					+ ", column: " + jme.getLocation().getColumnNr(), jme
					.getLocation().getColumnNr(), jme.getLocation().getLineNr());
			addErrorToErrorList(parseError);
			parseErrorOccured = true;

		} catch (JsonProcessingException jpe) {
			parseError = new IJSONParseErrorImpl(file, jpe.getOriginalMessage()
					+ " at line: " + jpe.getLocation().getLineNr()
					+ ", column: " + jpe.getLocation().getColumnNr(), jpe
					.getLocation().getColumnNr(), jpe.getLocation().getLineNr());
			addErrorToErrorList(parseError);
			parseErrorOccured = true;

		}

		return parseErrorOccured;
	}

	/**
	 * Hilfsmethode Diese Methode setzt vorraus, dass das XML-Dokument bereits
	 * erfolgreich geparst wurde!
	 * 
	 * @param file
	 *            repraesentiert eine JSON-Datei die in einen String umgeformt
	 *            werden soll.
	 * 
	 * @return content repraesentiert uebergebene JSON-Datei als String.
	 * @throws IOException
	 */
	@Override
	public String jsonFileToString(File file) throws IOException {
		return new String(
				Files.readAllBytes(Paths.get(file.getAbsolutePath())),
				StandardCharsets.UTF_8);
	}

	/**
	 * get Methode ermoeglicht Zugriff auf die Errorliste
	 * 
	 * @return List<IXMLParseError> Liste mit moeglichen Elementen der Klasse
	 *         IXMLParseError, die bei auftretenden Fehlern waehrend des Parsens
	 *         festegestellt wurden
	 */
	@Override
	public List<IParseError> getErrorList() {
		return this.errorListe;
	}

	/**
	 * Hilfsmethode Methode zum neu Initialisieren der Error Liste
	 * 
	 */
	@Override
	public void clearErrorList() {
		this.errorListe = new ArrayList<>();
	}

	/**
	 * Methode fuegt festgestellte Parse-Error zur Liste der festgestellten
	 * Fehler hinzu
	 * 
	 * @param error
	 *            Objekt der Klasse IJSONParseErrorImpl
	 */
	public void addErrorToErrorList(IJSONParseError error) {
		errorListe.add(error);
	}

	class JsonArrayComparator implements Comparator<JsonNode> {

		@Override
		public int compare(JsonNode o1, JsonNode o2) {
			if (o1.isValueNode() && o2.isValueNode()) {
				return o1.asText().compareTo(o2.asText());
			} else if (o1.isValueNode() && o2.isArray()) {
				return -1;
			} else if (o1.isValueNode() && o2.isObject()) {
				return -1;
			}

			else if (o1.isArray() && o2.isValueNode()) {
				return 1;
			} else if (o1.isArray() && o2.isArray()) {
				if (o1.size() > o2.size()) {
					return 1;
				} else if (o1.size() < o2.size()) {
					return -1;
				}
				if (!o1.isEmpty() && !o2.isEmpty()) {
					int minSize = Math.min(o1.size(), o2.size());
					for (int i = 0; i < minSize; i++) {
						String val1 = o1.get(i).toString();
						String val2 = o2.get(i).toString();
						if (!val1.equals(val2)) {
							return val1.compareTo(val2);
						}
					}
				}
				return 0;
			} else if (o1.isArray() && o2.isObject()) {
				return -1;
			} else if (o1.isObject() && o2.isValueNode()) {
				return 1;
			} else if (o1.isObject() && o2.isArray()) {
				return 1;
			} else if (o1.isObject() && o2.isObject()) {
				if (o1.size() > o2.size()) {
					return 1;
				} else if (o1.size() < o2.size()) {
					return -1;
				}

				// for same size, compare keys
				if (!o1.isEmpty() && !o2.isEmpty()) {
					Iterator<String> nodesO1 = o1.fieldNames();
					Iterator<String> nodesO2 = o2.fieldNames();
					List<String> values1 = new ArrayList<String>();
					List<String> values2 = new ArrayList<String>();
					while (nodesO1.hasNext() && nodesO2.hasNext()) {
						String node1 = nodesO1.next();
						String node2 = nodesO2.next();
						if (!node1.equals(node1)) {
							return node2.compareTo(node1);
						}
						values1.add(node1);
						values2.add(node2);
					}
					for (int i = 0; i < values1.size(); i++) {
						String val1 = o1.get(values1.get(i)).toString();
						String val2 = o2.get(values2.get(i)).toString();
						if (!val1.equals(val2)) {
							return val1.compareTo(val2);
						}
					}
					return 0;
				}
			}
			return 0;
		}

	}

}
