package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.RootNameLookup;

public class IJSONcomparer {
	private static final String NEW_LINE = "\n";
	private static final String FIELD_DELIMITER = ": ";
	private static final String ARRAY_PREFIX = "- ";
	private static final String YAML_PREFIX = "  ";

	private ObjectMapper mapperRef = new ObjectMapper();
	private ObjectMapper mapperComp = new ObjectMapper();

	private JsonNode rootNodeRef;
	private JsonNode rootNodeComp;

	public JsonNode init() throws IOException {
		String jsonFileRef = new IJSONvergleicherImpl()
				.jsonFileToString(new File(System.getProperty("user.dir")
						+ File.separator + "file1.json"));
		String jsonFileComp = new IJSONvergleicherImpl()
				.jsonFileToString(new File(System.getProperty("user.dir")
						+ File.separator + "file2.json"));
		rootNodeRef = mapperRef.readTree(jsonFileRef);
		rootNodeComp = mapperComp.readTree(jsonFileComp);
		calcLevelWeight(rootNodeRef, rootNodeComp);
		traverseGraph(rootNodeRef, rootNodeComp, 0);
		// StringBuilder b = new StringBuilder();
		// processNode(rootNodeRef, b, 0);
		return rootNodeRef;
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

	@SuppressWarnings("unused")
	public void traverseGraph(JsonNode rootRef, JsonNode rootComp, int depth) {
		Iterator<String> fieldNames = rootRef.fieldNames();
		Iterator<String> fieldNamesComp = rootComp.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			// System.out.println(fieldName + " " + depth);
			JsonNode fieldValueRef = rootRef.get(fieldName);
			if (fieldValueRef.isValueNode()) {
				if (existsInBoth(fieldName, rootNodeComp)) {
					JsonNode fieldValueComp = rootComp.get(fieldName);
					compareValues(fieldValueRef, fieldValueComp);
				}
			} else if (fieldValueRef.isObject()) {
				// Iterate through left tree
				if (existsInBoth(fieldName, rootNodeComp)) {
					JsonNode fieldValueComp = rootComp.get(fieldName);
					compareFields(fieldValueRef, fieldValueComp);
					// traverse(fieldValue, depth + 1);
				}
				//

			} else if (fieldValueRef.isArray()) {
				
					if (existsInBoth(fieldName, rootComp)) {
						JsonNode fieldValueComp = rootComp.get(fieldName);
						compareArrays(fieldValueRef, fieldValueComp);
						// System.out.println(arrayItem.get(fieldName));
					}
					// traverse(arrayElement, rootComp, depth);
				
			}
		}
	}

	private void compareArrays(JsonNode fieldValueRef, JsonNode fieldValueComp) {
		for(int i = 0; i < fieldValueRef.size(); i++){
			JsonNode currentRefNode = fieldValueRef.get(i);

			if(fieldValueComp.isValueNode()){
				if(currentRefNode.equals(fieldValueComp)){
					System.out.println("gleich");
				}
				else {
					System.out.println("ungleich");
				}
			}
			else if(fieldValueComp.isContainerNode()){
				ArrayList<JsonNode> compNodes = new ArrayList<JsonNode>();
				for(JsonNode node : fieldValueComp){
					compNodes.add(node);
				}
				
				if(compNodes.contains(currentRefNode)){
					System.out.println("yes");
				}
			}
		}
	}

	private void compareValues(JsonNode fieldValueRef, JsonNode fieldValueComp) {

		if (fieldValueComp.isValueNode()) {
			String valueRef = fieldValueRef.asText();
			String valueComp = fieldValueComp.asText();
			if (valueRef.equals(valueComp)) {
				System.out.println(valueRef);
			}
		}
		else if(fieldValueComp.isContainerNode()){
			System.out.println("Do sth else idk");
		}
	}

	private void compareFields(JsonNode fieldValueRef, JsonNode fieldValueComp) {
		Iterator<String> itRef = fieldValueRef.fieldNames();
		ArrayList<String> fieldNames = iteratorToList(itRef);
		Iterator<String> itComp = fieldValueRef.fieldNames();
		ArrayList<String> fieldNamesComp = iteratorToList(itComp);

		ArrayList<String> matchingKeys = new ArrayList<String>();
		ArrayList<Boolean> equalValues = new ArrayList<Boolean>();

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
				equalValues.add(nodeRef.equals(nodeComp));
			} else {
				compareFields(nodeRef, nodeComp);
			}
		}

//		 for (String s : matchingKeys) {
//		 // JsonNode node = fieldValueRef.get(s);
//		 System.out.println(s);
//		 }

		for (Boolean s : equalValues) {
			// JsonNode node = fieldValueRef.get(s);
			System.out.println(s);
		}

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
