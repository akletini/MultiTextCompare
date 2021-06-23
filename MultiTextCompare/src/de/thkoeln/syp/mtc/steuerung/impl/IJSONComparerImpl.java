package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class IJSONComparerImpl {
	private static final String NEW_LINE = "\n";
	private static final String FIELD_DELIMITER = ": ";
	private static final String ARRAY_PREFIX = "- ";
	private static final String YAML_PREFIX = "  ";

	private final String uuid = UUID.randomUUID().toString();

	private ObjectMapper objectMapper = new ObjectMapper();

	private JsonNode rootNodeRef;
	private JsonNode rootNodeComp;

	private ITextvergleicher textComparer;
	private int maxLineLength;

	public IJSONComparerImpl(int maxLineLength) {
		textComparer = new ITextvergleicherImpl();
		this.maxLineLength = maxLineLength;
	}

	public double compare(File ref, File comp) throws IOException {
		String jsonFileRef = new IJSONHandlerImpl().jsonFileToString(ref);
		String jsonFileComp = new IJSONHandlerImpl().jsonFileToString(comp);
		rootNodeRef = objectMapper.readTree(jsonFileRef);
		rootNodeComp = objectMapper.readTree(jsonFileComp);
		calcLevelWeight(rootNodeRef, rootNodeComp);
		List<Double> similarities = traverseGraph(rootNodeRef, rootNodeComp);
		double similarity = sumSimilarities(similarities);

		return similarity;
	}


	private double sumSimilarities(List<Double> similarities) {
		double similarity = 0.0;
		for (Double d : similarities) {
			similarity += d;
		}
		return similarity;
	}

	public List<Double> traverseGraph(JsonNode rootRef, JsonNode rootComp) {
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

	private double compareArrays(JsonNode fieldValueRef,
			JsonNode fieldValueComp, double currentLevelWeight) {
		double similarity = 0.0;
		double maxArraySize = Math.max(fieldValueRef.size(),
				fieldValueComp.size());

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
								calcLevelWeight(fieldValueRef, fieldValueComp));
					} else if (currentCompNode.isObject()) {
						// Type mismatch
					}
				} else if (currentRefNode.isArray()) {
					if (currentCompNode.isValueNode()) {
						similarity += currentLevelWeight = compareValues(
								currentCompNode, currentRefNode,
								calcLevelWeight(fieldValueRef, fieldValueComp));
					} else if (currentCompNode.isArray()) {
						similarity += currentLevelWeight
								* compareArrays(
										currentRefNode,
										currentCompNode,
										calcLevelWeight(fieldValueRef,
												fieldValueComp));
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

							double objectsim = (1 / maxArraySize)
									* currentLevelWeight
									* compareObjects(
											currentRefNode,
											currentCompNode,
											calcLevelWeight(fieldValueRef,
													fieldValueComp));
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

	public double calcLevelWeight(JsonNode rootRef, JsonNode rootComp) {
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

	private int getIteratorSize(Iterator<?> iterator) {
		int i = 0;
		while (iterator.hasNext()) {
			i++;
			iterator.next();
		}
		return i;
	}

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

	private ArrayList<String> iteratorToList(Iterator<String> iterator) {
		ArrayList<String> actualList = new ArrayList<String>();
		while (iterator.hasNext()) {
			actualList.add(iterator.next());
		}
		return actualList;
	}

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


	private ArrayNode clearUUIDFields(ArrayNode original, String uuid) {
		ArrayNode returnArray = objectMapper.createArrayNode();
		for (int i = 0; i < original.size(); i++) {
			if (!original.get(i).toString().equals("[\"" + uuid + "\"]")) {
				returnArray.add(original.get(i));
			}
		}
		return returnArray;
	}
}
