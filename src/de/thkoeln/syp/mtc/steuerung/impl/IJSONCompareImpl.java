package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.thkoeln.syp.mtc.steuerung.services.IJSONCompare;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

/**
 * Verantwortlich für den strukturellen Vergleich von JSON-Dateien
 * 
 * @author Allen Kletinitch
 *
 */
public class IJSONCompareImpl implements IJSONCompare {

	private final String uuid = UUID.randomUUID().toString();

	private ObjectMapper objectMapper = new ObjectMapper();

	private JsonNode rootNodeRef;
	private JsonNode rootNodeComp;

	private ITextvergleicher textComparer;
	private int maxLineLength;
	private List<Double> similarities;

	public IJSONCompareImpl(int maxLineLength) {
		textComparer = new ITextvergleicherImpl();
		similarities = new ArrayList<Double>();
		this.maxLineLength = maxLineLength;
	}

	/**
	 * Hauptmethode des JSON-Vergleichs. Liest die beiden Dateibäume der
	 * uebergebenen Dateien ein und ruft Methoden zur Berechnung der
	 * Aehnlichkeit auf
	 * 
	 * @param ref
	 *            Referenzdatei
	 * @param comp
	 *            Vergleichsdatei
	 * @return Aehnlichkeit der beiden Dateien
	 * @throws IOException
	 */
	@Override
	public double compare(File ref, File comp) throws IOException {
		String jsonFileRef = new IJSONHandlerImpl().jsonFileToString(ref);
		String jsonFileComp = new IJSONHandlerImpl().jsonFileToString(comp);
		rootNodeRef = objectMapper.readTree(jsonFileRef);
		rootNodeComp = objectMapper.readTree(jsonFileComp);
		calcLevelWeight(rootNodeRef, rootNodeComp);
		similarities = traverseGraph(rootNodeRef, rootNodeComp);
		double similarity = sumSimilarities(similarities);

		return similarity;
	}

	/**
	 * Summiert alle Werte von similarities auf
	 * 
	 * @param similarities
	 * @return die Gesamtähnlichkeit
	 */
	private double sumSimilarities(List<Double> similarities) {
		double similarity = 0.0;
		for (Double d : similarities) {
			similarity += d;
		}
		return similarity;
	}

	/**
	 * Traversiert die beiden Baumgraphen der eingelesenen Vergleichsdateien
	 * 
	 * @param rootRef
	 *            Wurzelknoten der Referenzdatei
	 * @param rootComp
	 *            Wurzelknoten der Vergleichsdatei
	 * @return Liste der Ähnlichkeiten für jeden Knoten der ersten Ebene
	 */
	private List<Double> traverseGraph(JsonNode rootRef, JsonNode rootComp) {
		List<Double> similarity = new ArrayList<Double>();
		double currentLevelWeight = calcLevelWeight(rootRef, rootComp);
		Iterator<String> fieldNames = rootRef.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode fieldValueRef = rootRef.get(fieldName);
			if (fieldValueRef.isValueNode()) {
				if (existsInBoth(fieldName, rootNodeComp)) {
					JsonNode fieldValueComp = rootComp.get(fieldName);
					double weight = calcLevelWeight(rootRef, rootComp);
					double sim = compareValues(fieldValueRef, fieldValueComp,
							weight);
					similarity.add(sim);

				}
			} else if (fieldValueRef.isObject()) {
				// Iterate through left tree
				if (existsInBoth(fieldName, rootNodeComp)) {
					JsonNode fieldValueComp = rootComp.get(fieldName);
					double sim = compareObjects(fieldValueRef, fieldValueComp,
							currentLevelWeight);
					similarity.add(sim);
				}

			} else if (fieldValueRef.isArray()) {
				if (existsInBoth(fieldName, rootNodeComp)) {
					JsonNode fieldValueComp = rootComp.get(fieldName);
					double sim = compareArrays(fieldValueRef, fieldValueComp,
							currentLevelWeight);
					similarity.add(sim);

				}
			}
		}
		return similarity;
	}

	/**
	 * Vergleicht die Ähnlichkeit zweier Value-Knoten
	 * 
	 * @param fieldValueRef
	 *            Referenknoten
	 * @param fieldValueComp
	 *            Vergleichsknoten
	 * @param currentLevelWeight
	 *            Gewichtung der aktuellen Ebene
	 * @return Ähnlichkeit der beiden Knoten
	 */
	private double compareValues(JsonNode fieldValueRef,
			JsonNode fieldValueComp, double currentLevelWeight) {
		double similarity = 0;
		if (fieldValueComp.isValueNode()) {
			String valueRef = fieldValueRef.asText();
			String valueComp = fieldValueComp.asText();
			similarity = calcSimilarity(valueRef, valueComp, currentLevelWeight);
		} else if (fieldValueComp.isArray()) {
			for (int i = 0; i < fieldValueComp.size(); i++) {
				if (fieldValueComp.get(i).equals(fieldValueRef)) {
					similarity = currentLevelWeight
							* (1.0 / (double) fieldValueComp.size());
					return similarity;
				}
			}
		}
		return similarity;
	}

	/**
	 * Vergleicht die Ähnlichkeit zweier Object-Knoten
	 * 
	 * @param fieldValueRef
	 *            Referenknoten
	 * @param fieldValueComp
	 *            Vergleichsknoten
	 * @param currentLevelWeight
	 *            Gewichtung der aktuellen Ebene
	 * @return Ähnlichkeit der beiden Knoten
	 */
	private double compareObjects(JsonNode fieldValueRef,
			JsonNode fieldValueComp, double currentLevelWeight) {
		Iterator<String> itRef = fieldValueRef.fieldNames();
		Iterator<String> itComp = fieldValueComp.fieldNames();

		ArrayList<String> fieldNamesRef = iteratorToList(itRef);
		ArrayList<String> fieldNamesComp = iteratorToList(itComp);

		ArrayList<String> matchingKeys = new ArrayList<String>();
		ArrayList<Double> equalValues = new ArrayList<Double>();

		if (fieldValueRef.isEmpty() && fieldValueComp.isEmpty()) {
			return currentLevelWeight * 1.0;
		}

		double currentWeight = calcLevelWeight(fieldValueRef, fieldValueComp);

		for (int i = 0; i < fieldNamesRef.size(); i++) {
			String current = fieldNamesRef.get(i);
			if (fieldNamesComp.contains(current)) {
				matchingKeys.add(current);
			}
		}

		for (int i = 0; i < matchingKeys.size(); i++) {
			JsonNode nodeRef = fieldValueRef.get(matchingKeys.get(i));
			JsonNode nodeComp = fieldValueComp.get(matchingKeys.get(i));
			if (nodeRef.isValueNode()) {
				if (nodeComp.isValueNode()) {
					double similarity = currentLevelWeight
							* calcSimilarity(nodeRef.asText(),
									nodeComp.asText(), currentWeight);
					equalValues.add(similarity);
				} else if (nodeComp.isArray()) {
					double similarity = currentLevelWeight = compareValues(
							nodeRef, nodeComp, currentWeight);
					equalValues.add(similarity);
				} else if (nodeComp.isObject()) {
					// Type mismatch
				}
			} else if (nodeRef.isArray()) {
				if (nodeComp.isValueNode()) {
					double similarity = currentLevelWeight = compareValues(
							nodeComp, nodeRef, currentWeight);
					equalValues.add(similarity);
				} else if (nodeComp.isArray()) {
					double similarity = currentLevelWeight
							* compareArrays(nodeRef, nodeComp, currentWeight);
					equalValues.add(similarity);
				} else if (nodeComp.isObject()) {
					// Type mismatch
				}
			} else if (nodeRef.isObject()) {
				if (nodeComp.isValueNode()) {
					// Type mismatch
				} else if (nodeComp.isArray()) {
					// Type mismatch
				} else if (nodeComp.isObject()) {
					double similarity = currentLevelWeight
							* compareObjects(nodeRef, nodeComp, currentWeight);
					equalValues.add(similarity);
				}
			}
		}

		double sim = 0.0;
		for (Double s : equalValues) {
			sim += s;
		}
		return sim;
	}

	/**
	 * Vergleicht die Ähnlichkeit zweier Array-Knoten
	 * 
	 * @param fieldValueRef
	 *            Referenknoten
	 * @param fieldValueComp
	 *            Vergleichsknoten
	 * @param currentLevelWeight
	 *            Gewichtung der aktuellen Ebene
	 * @return Ähnlichkeit der beiden Knoten
	 */
	private double compareArrays(JsonNode fieldValueRef,
			JsonNode fieldValueComp, double currentLevelWeight) {
		double similarity = 0.0;
		double maxArraySize = Math.max(fieldValueRef.size(),
				fieldValueComp.size());
		double currentWeight = calcLevelWeight(fieldValueRef, fieldValueComp);
		// array matching
		if (fieldValueRef.isArray() && fieldValueComp.isArray()) {

			ArrayNode refArray = (ArrayNode) fieldValueRef;
			ArrayNode compArray = (ArrayNode) fieldValueComp;

			for (int i = 0; i < refArray.size(); i++) {
				String refString = refArray.get(i).toString();
				for (int j = 0; j < compArray.size(); j++) {
					String compString = compArray.get(j).toString();
					if (compString.equals("[\"" + uuid + "\"]")) {
						continue;
					}
					if (refString.equals(compString)) {
						similarity += currentLevelWeight
								* calcLevelWeight(fieldValueRef, fieldValueComp);
						refArray.set(i, objectMapper.createArrayNode()
								.add(uuid));
						compArray.set(j,
								objectMapper.createArrayNode().add(uuid));
						break;
					}
				}
			}
			refArray = clearUUIDFields(refArray, uuid);
			compArray = clearUUIDFields(compArray, uuid);
			fieldValueRef = refArray;
			fieldValueComp = compArray;
		}

		int lastMatchedIndex = 0;
		for (int i = 0; i < fieldValueRef.size(); i++) {
			JsonNode currentRefNode = fieldValueRef.get(i);
			for (int j = lastMatchedIndex; j < fieldValueComp.size(); j++) {
				JsonNode currentCompNode = fieldValueComp.get(j);
				if (currentRefNode.isValueNode()) {
					if (currentCompNode.isValueNode()) {
						similarity += currentLevelWeight
								* calcSimilarity(currentRefNode.asText(),
										currentCompNode.asText(),
										(1.0 / maxArraySize));
						lastMatchedIndex = j + 1;
						break;
					} else if (currentCompNode.isArray()) {
						similarity += currentLevelWeight = compareValues(
								currentRefNode, currentCompNode,
								currentWeight);
					} else if (currentCompNode.isObject()) {
						// Type mismatch
					}
				} else if (currentRefNode.isArray()) {
					if (currentCompNode.isValueNode()) {
						similarity += currentLevelWeight = compareValues(
								currentCompNode, currentRefNode,
								currentWeight);
					} else if (currentCompNode.isArray()) {
						similarity += currentLevelWeight
								* compareArrays(
										currentRefNode,
										currentCompNode,
										currentWeight);
						lastMatchedIndex = j + 1;
						break;
					} else if (currentCompNode.isObject()) {
						// Type mismatch
					}
				} else if (currentRefNode.isObject()) {
					if (currentCompNode.isValueNode()) {
						// Type mismatch
					} else if (currentCompNode.isArray()) {
						// Type mismatch
					} else if (currentCompNode.isObject()) {

						if (objectsShareFields(currentRefNode, currentCompNode)) {

							double objectsim = currentLevelWeight
									* compareObjects(
											currentRefNode,
											currentCompNode,
											currentWeight);
							similarity += objectsim;
							lastMatchedIndex = j + 1;
							break;
						}
					}
				}
			}
		}
		return similarity;
	}

	/**
	 * Prüft ob zwei Objekt-Knoten die gleichen Felder haben
	 * 
	 * @param ref
	 *            Referenzknoten
	 * @param comp
	 *            Vergleichsknoten
	 * @return true wenn die Felder gleich sind, sonst false
	 */
	private boolean objectsShareFields(JsonNode ref, JsonNode comp) {
		Iterator<String> itRef = ref.fieldNames();
		Iterator<String> itComp = comp.fieldNames();

		ArrayList<String> fieldNamesRef = iteratorToList(itRef);
		ArrayList<String> fieldNamesComp = iteratorToList(itComp);

		ArrayList<String> matchingKeys = new ArrayList<String>();

		for (int i = 0; i < fieldNamesRef.size(); i++) {
			String current = fieldNamesRef.get(i);
			if (fieldNamesComp.contains(current)) {
				matchingKeys.add(current);
			}
		}

		if ((fieldNamesRef.size() == fieldNamesComp.size())
				&& (fieldNamesRef.size() == matchingKeys.size())) {
			return true;
		}
		return false;
	}

	/**
	 * Berechnet das Gewicht für die Knoten der aktuellen Ebene
	 * 
	 * @param rootRef
	 *            Referenzknoten
	 * @param rootComp
	 *            Vergleichsknoten
	 * @return Das aktuelle Gewicht fuer Knoten der Ebene
	 */
	private double calcLevelWeight(JsonNode rootRef, JsonNode rootComp) {
		double weight = 0;
		if (rootRef.isObject() && rootComp.isObject()) {
			int nodeCountLeft = getIteratorSize(rootRef.fields());
			int nodeCountRight = getIteratorSize(rootComp.fields());
			int maxNodeCount = Math.max(nodeCountLeft, nodeCountRight);
			weight = 1 / (double) maxNodeCount;
		} else if (rootRef.isArray() && rootComp.isArray()) {
			int nodeCountLeft = rootRef.size();
			int nodeCountRight = rootComp.size();
			int maxNodeCount = Math.max(nodeCountLeft, nodeCountRight);
			weight = 1 / (double) maxNodeCount;
		}
		return weight;
	}

	/**
	 * Berechnet wie viele Elemente im Iterator sind
	 * 
	 * @param iterator
	 * @return die Anzahl der Elemente in iterator
	 */
	private int getIteratorSize(Iterator<?> iterator) {
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			iterator.next();
		}
		return i;
	}

	/**
	 * Prüft ob ein Feld mit dem Namen refKey im Knoten right existiert
	 * 
	 * @param refKey
	 * @param right
	 * @return true wenn der Knoten in right existiert
	 */
	private boolean existsInBoth(String refKey, JsonNode right) {
		Iterator<String> fieldNamesComp = right.fieldNames();
		ArrayList<String> fields = iteratorToList(fieldNamesComp);
		for (String field : fields) {
			if (refKey.equals(field)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Speichert Elemente eines Iterators in einer Liste und gibt diese zurück
	 * 
	 * @param iterator
	 * @return Liste mit Elementen des Iterators
	 */
	private ArrayList<String> iteratorToList(Iterator<String> iterator) {
		ArrayList<String> actualList = new ArrayList<String>();
		while (iterator.hasNext()) {
			actualList.add(iterator.next());
		}
		return actualList;
	}

	/**
	 * Berechnet die String-Aehnlichkeit für ref und comp unter Beachtung des
	 * aktuellen Gewichts
	 * 
	 * @param ref Referenzstring
	 * @param comp Vergleichsstring
	 * @param weight aktuelles Gewicht
	 * @return Aehnlichkeit der beiden Strings
	 */
	private double calcSimilarity(String ref, String comp, double weight) {
		double longestLength = (double) Math.max(ref.length(), comp.length());
		double levenshteinDist = (double) textComparer
				.calculateLevenshteinDist(ref, comp, maxLineLength);
		if (levenshteinDist == -1.0) {
			return 0.0;
		}
		if (longestLength != 0) {
			return weight
					* (double) ((longestLength - levenshteinDist) / longestLength);
		} else {
			return weight * 1.0;
		}
	}

	/**
	 * Ersetzt Array-Felder mit UUIDs durch neue Array-Knoten
	 * @param original Das zu bearbeitende Array
	 * @param uuid aktuelle UUID
	 * @return verändertes Array
	 */
	private ArrayNode clearUUIDFields(ArrayNode original, String uuid) {
		ArrayNode returnArray = objectMapper.createArrayNode();
		for (int i = 0; i < original.size(); i++) {
			if (!original.get(i).toString().equals("[\"" + uuid + "\"]")) {
				returnArray.add(original.get(i));
			}
		}
		return returnArray;
	}
	
	public List<Double> getSimiList(){
		return similarities;
	}
}
