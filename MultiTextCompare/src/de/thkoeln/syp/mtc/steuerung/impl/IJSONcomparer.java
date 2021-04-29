package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class IJSONcomparer {
	private static final String NEW_LINE = "\n";
	private static final String FIELD_DELIMITER = ": ";
	private static final String ARRAY_PREFIX = "- ";
	private static final String YAML_PREFIX = "  ";

	private ObjectMapper mapperRef = new ObjectMapper();
	private ObjectMapper mapperComp = new ObjectMapper();

	private JsonNode rootNodeRef;
	private JsonNode rootNodeComp;

	private ITextvergleicher textComparer;

	public IJSONcomparer() {
		textComparer = new ITextvergleicherImpl();
	}

	public double compare(File ref, File comp) throws IOException {
		String jsonFileRef = new IJSONvergleicherImpl().jsonFileToString(ref);
		String jsonFileComp = new IJSONvergleicherImpl().jsonFileToString(comp);
		rootNodeRef = mapperRef.readTree(jsonFileRef);
		rootNodeComp = mapperComp.readTree(jsonFileComp);
		calcLevelWeight(rootNodeRef, rootNodeComp);
		List<Double> similarities = traverseGraph(rootNodeRef, rootNodeComp);
		double similarity = sumSimilarities(similarities);
		System.out.println(similarity);
		// StringBuilder b = new StringBuilder();
		// processNode(rootNodeRef, b, 0);
		return similarity;
	}

	private double sumSimilarities(List<Double> similarities){
		double similarity = 0.0;
		for(Double d : similarities){
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
					double sim = currentLevelWeight
							* compareFields(fieldValueRef, fieldValueComp);
					similarity.add(sim);
				}
				//

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
			// System.out.println(similarity);
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

	private double compareFields(JsonNode fieldValueRef, JsonNode fieldValueComp) {
		Iterator<String> itRef = fieldValueRef.fieldNames();
		ArrayList<String> fieldNames = iteratorToList(itRef);
		Iterator<String> itComp = fieldValueComp.fieldNames();
		ArrayList<String> fieldNamesComp = iteratorToList(itComp);

		ArrayList<String> matchingKeys = new ArrayList<String>();
		ArrayList<Double> equalValues = new ArrayList<Double>();
		
		if(fieldValueRef.isEmpty() && fieldValueComp.isEmpty()){
			return 1.0;
		}

		double currentWeight = calcLevelWeight(fieldValueRef, fieldValueComp);

		for (int i = 0; i < fieldNames.size(); i++) {
			String current = fieldNames.get(i);
			if (fieldNamesComp.contains(current)) {
				matchingKeys.add(current);
			}
		}

		for (int i = 0; i < matchingKeys.size(); i++) {
			JsonNode nodeRef = fieldValueRef.get(matchingKeys.get(i));
			JsonNode nodeComp = fieldValueComp.get(matchingKeys.get(i));
			if (!nodeRef.isContainerNode() || !nodeComp.isContainerNode()) {
				String nodeRefText = nodeRef.asText();
				String nodeCompText = nodeComp.asText();
				if (nodeRefText != null && nodeCompText != null) {
					equalValues.add(calcSimilarity(nodeRefText, nodeCompText,
							currentWeight));
				}
			} else {
				double sim = compareFields(nodeRef, nodeComp);
				equalValues.add(currentWeight * sim);
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
		for (int i = 0; i < fieldValueRef.size(); i++) {
			JsonNode currentRefNode = fieldValueRef.get(i);

			if (fieldValueComp.isValueNode()) {
				if (currentRefNode.equals(fieldValueComp)) {
					similarity += currentLevelWeight
							* (1.0 / (double) fieldValueRef.size());
				}
			} else if (fieldValueComp.isArray()) {
				ArrayList<JsonNode> compNodes = new ArrayList<JsonNode>();
				for (JsonNode node : fieldValueComp) {
					compNodes.add(node);
				}
				if (currentRefNode.isValueNode()) {
					if (compNodes.contains(currentRefNode)) {
						similarity += currentLevelWeight
								* (1.0 / (double) fieldValueComp.size());
					}
				} else if (currentRefNode.isArray()) {
					// check all comparison nodes for type
					for (JsonNode node : compNodes) {
						// current comp node is value node
						if (node.isValueNode()) {
							// check if value node exists in ref array
							for(JsonNode refNode : currentRefNode){
								if(refNode.equals(node)){
									similarity += calcLevelWeight(currentRefNode, node) * (1.0 / (double) currentRefNode.size());
								}
							}
						// compare array and array
						} else if (node.isArray()) {
							similarity += compareArrays(currentRefNode, node,
									calcLevelWeight(currentRefNode, node));
						// compare array and object
						} else if(node.isObject()){
							similarity += compareFields(currentRefNode, node);
						}
					}
				} else if (currentRefNode.isObject()) {
					for(JsonNode node : compNodes){
						if (node.isValueNode()) {
							// check if value node exists in ref array
							for(JsonNode refNode : currentRefNode){
								if(refNode.equals(node)){
									similarity += calcLevelWeight(currentRefNode, node) * (1.0 / (double) currentRefNode.size());
								}
							}
						// compare array and array
						} else if (node.isArray()) {
							similarity += currentLevelWeight * compareArrays(currentRefNode, node,
									calcLevelWeight(currentRefNode, node));
						// compare array and object
						} else if(node.isObject()){
							similarity += currentLevelWeight  * compareFields(currentRefNode, node);
						}
					}
				}
			} else if (fieldValueComp.isObject()) {

				if (currentRefNode.equals(fieldValueComp)) {
					System.out.println("yes " + i);
				} else {
					System.out.println("NO lol");
				}
			}
		}
		return similarity;
	}

	public double calcLevelWeight(JsonNode rootRef, JsonNode rootComp) {
		double weight = 0;
		int nodeCountLeft = getIteratorSize(rootRef.fields());
		int nodeCountRight = getIteratorSize(rootComp.fields());
		int maxNodeCount = Math.max(nodeCountLeft, nodeCountRight);
		weight = 1 / (double) maxNodeCount;
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
	
	public boolean existsInBoth(String refKey, JsonNode right) {
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
				.calculateLevenshteinDist(ref, comp, 20);
		if (longestLength != 0) {
			return weight
					* (double) ((longestLength - levenshteinDist) / longestLength);
		} else {
			return weight * 1.0;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////

	private void processNode(JsonNode jsonNode, StringBuilder yaml, int depth) {
		if (jsonNode.isValueNode()) {
			System.out.println("Value " + jsonNode.asText() + " depth : "
					+ depth);
		} else if (jsonNode.isArray()) {
			System.out.println("Array " + jsonNode.asText());
			for (JsonNode arrayItem : jsonNode) {
				System.out.println("array item " + arrayItem.asText()
						+ " depth : " + depth);
				appendNodeToYaml(arrayItem, yaml, depth, true);
			}
		} else if (jsonNode.isObject()) {
			System.out.println("Object " + jsonNode + " depth : " + depth);
			appendNodeToYaml(jsonNode, yaml, depth, false);
		}
	}

	private void appendNodeToYaml(JsonNode node, StringBuilder yaml, int depth,
			boolean isArrayItem) {
		Iterator<Entry<String, JsonNode>> fields = node.fields();
		boolean isFirst = true;
		while (fields.hasNext()) {
			Entry<String, JsonNode> jsonField = fields.next();
			addFieldNameToYaml(yaml, jsonField.getKey(), depth, isArrayItem
					&& isFirst);
			processNode(jsonField.getValue(), yaml, depth + 1);
			isFirst = false;
		}

	}

	private void addFieldNameToYaml(StringBuilder yaml, String fieldName,
			int depth, boolean isFirstInArray) {
		if (yaml.length() > 0) {
			yaml.append(NEW_LINE);
			int requiredDepth = (isFirstInArray) ? depth - 1 : depth;
			for (int i = 0; i < requiredDepth; i++) {
				yaml.append(YAML_PREFIX);
			}
			if (isFirstInArray) {
				yaml.append(ARRAY_PREFIX);
			}
		}
		yaml.append(fieldName);
		yaml.append(FIELD_DELIMITER);
	}
}
