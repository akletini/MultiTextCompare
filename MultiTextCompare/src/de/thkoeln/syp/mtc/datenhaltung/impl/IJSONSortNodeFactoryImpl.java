package de.thkoeln.syp.mtc.datenhaltung.impl;

import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class IJSONSortNodeFactoryImpl extends JsonNodeFactory {
	
	@Override
    public ObjectNode objectNode() {
      return new ObjectNode(this, new TreeMap<String, JsonNode>());
    }
	
}
