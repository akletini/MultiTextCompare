package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.input.SAXBuilder;

import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class IXMLComparerImpl {

	private int maxLineLength;
	private ITextvergleicher textvergleicher;
	private List<Double> similarities;

	public IXMLComparerImpl(int maxLineLength) {
		textvergleicher = new ITextvergleicherImpl();
		this.maxLineLength = maxLineLength;
	}

	public double compare(File ref, File comp) throws IOException,
			JDOMException {
		Document docRef, docComp;
		SAXBuilder builder = new SAXBuilder();
		Element rootRef, rootComp;
		docRef = builder.build(ref);
		docComp = builder.build(comp);

		rootRef = docRef.getRootElement();
		rootComp = docComp.getRootElement();

		similarities = traverseGraph(rootRef, rootComp);
		double similarity = sumSimilarities(similarities);
		return similarity;
	}

	private List<Double> traverseGraph(Element rootRef, Element rootComp) {
		List<Double> similarity = new ArrayList<Double>();
		double currentLevelWeight = calcLevelWeight(rootRef, rootComp);
		List<Element> refFirstLevelChildren = rootRef.getChildren();
		for (int i = 0; i < refFirstLevelChildren.size(); i++) {
			Element currentRef = refFirstLevelChildren.get(i);
			String currentRefName = currentRef.getName();
			List<Element> compChildren = rootComp.getChildren(currentRefName);
			
			if (!compChildren.isEmpty()) {
				Element currentComp = rootComp.getChildren(currentRefName).get(0);
				compareContent(currentRef.getContent(), currentComp.getContent());
				// Liste von Knoten
				if (hasChildren(currentRef) && hasChildren(currentComp)) {
					similarity.add(compareElementsRecursively(currentRef, currentComp,
							currentLevelWeight));
				}
				// einzelnes Feld
				else if (!hasChildren(currentRef) && !hasChildren(currentComp)) {
					similarity.add(compareElements(currentRef, currentComp,
							currentLevelWeight));
				}
				rootComp.getChildren(currentRefName).remove(0);
			}
		}
		return similarity;
	}

	private double compareElements(Element ref, Element comp,
			double currentLevelWeight) {
		double contentWeight = 0.5;
		double attributeWeight = 0.5;
		double totalSimilarity = 0, contentSimilarity, attributeSimilarity;
		double stringSimilarity = compareStrings(ref.getValue(), comp.getValue());
		contentSimilarity = currentLevelWeight * contentWeight
				* stringSimilarity;
		attributeSimilarity = currentLevelWeight
				* attributeWeight
				* calcAttributeSimilarity(ref.getAttributes(),
						comp.getAttributes());
		if(hasAttributes(ref) || hasAttributes(comp)){
			totalSimilarity = contentSimilarity + attributeSimilarity;
		}else {
			totalSimilarity = 2 * contentSimilarity;
		}
		
		return totalSimilarity;
	}

	private double calcAttributeSimilarity(List<Attribute> refAttr,
			List<Attribute> compAttr) {
		List<Attribute> matchingRef = new ArrayList<Attribute>();
		List<Attribute> matchingComp = new ArrayList<Attribute>();
		double similarity = 0;
		int refSize = refAttr.size();
		int compSize = compAttr.size();
		double maxSize = (double) Math.max(refSize, compSize);

		// if either list is empty
		if (refSize == 0 && compSize == 0) {
			return 1.0;
		} else if (refSize == 0 || compSize == 0) {
			return 0.0;
		}

		// Get machting attribute names
		for (Attribute refAttribute : refAttr) {
			for (Attribute compAttribute : compAttr) {
				if (refAttribute.getName().equals(compAttribute.getName())) {
					matchingRef.add(refAttribute);
					matchingComp.add(compAttribute);
				}
			}
		}

		for (int i = 0; i < matchingRef.size(); i++) {
			similarity += (1.0 / maxSize)
					* compareStrings(matchingRef.get(i).getValue(),
							matchingComp.get(i).getValue());
		}

		return similarity;
	}

	private double compareElementsRecursively(Element ref, Element comp,
			double currentLevelWeight) {
		List<Double> similarities = new ArrayList<Double>();
		List<Element> matchingRef = new ArrayList<Element>();
		List<Element> matchingComp = new ArrayList<Element>();

		double currentWeight = calcLevelWeight(ref, comp);

		int lastMatchedIndex = 0;
		for (int i = 0; i < ref.getChildren().size(); i++) {
			Element currentRef = ref.getChildren().get(i);
			for (int j = lastMatchedIndex; j < comp.getChildren().size(); j++) {
				Element currentComp = comp.getChildren().get(j);
				if (currentRef.getName().equals(currentComp.getName())) {
					matchingRef.add(currentRef);
					matchingComp.add(currentComp);
					lastMatchedIndex = j + 1;
					break;
				}
			}
		}

		for (int i = 0; i < matchingRef.size(); i++) {
			Element currentRef = matchingRef.get(i);
			Element currentComp = matchingComp.get(i);
			
			List<Content> refContent = currentRef.getContent();
			List<Content> compContent = currentComp.getContent();
			
			// BETA
			compareContent(refContent, compContent);
			
			if (hasChildren(currentRef) && hasChildren(currentComp)) {
				double similarity = currentLevelWeight * compareElementsRecursively(currentRef, currentComp,
						currentWeight);
				similarities.add(similarity);
			} else if (hasChildren(currentRef) && !hasChildren(currentComp)) {

			} else if (hasChildren(currentRef) && !hasChildren(currentComp)) {

			} else if (!hasChildren(currentRef) && !hasChildren(currentComp)) {
				double similarity = currentLevelWeight * compareElements(currentRef, currentComp, currentWeight);
				similarities.add(similarity);
			}
		}
		double sim = 0.0;
		for (Double s : similarities) {
			sim += s;
		}
		return sim;
	}

	private void compareContent(List<Content> refContent,
			List<Content> compContent) {
		int maxSize = Math.max(refContent.size(), compContent.size());
		int minSize = Math.min(refContent.size(), compContent.size());
		
		for(int i = 0; i < refContent.size(); i++){
			Object ref = refContent.get(i);
			for(int j = 0; j < compContent.size(); j++){
				Object comp = compContent.get(j);
				if(ref instanceof Comment && comp instanceof Comment){
					compareComments((Comment) ref, (Comment) comp);
					break;
				}
				else if(ref instanceof CDATA && comp instanceof CDATA){
					compareCDATA((CDATA) ref, (CDATA) comp);
					break;
				}
			}
		}
		
	}
	
	private double compareComments(Comment ref, Comment comp){
		return compareStrings(ref.getText(), comp.getText());
	}
	
	private double compareCDATA(CDATA ref, CDATA comp){
		return compareStrings(ref.getTextNormalize(), comp.getTextNormalize());
	}
	

	private double compareStrings(String ref, String comp) {
		double similarity = 1.0;
		double maxLength = Math.max(ref.length(), comp.length());
		if(maxLength > 0.0){
		similarity = 1 - (textvergleicher.calculateLevenshteinDist(ref,
				comp, maxLineLength) / maxLength);
		}
		return similarity;
	}

	private double calcLevelWeight(Element ref, Element comp) {
		double levelWeight = 0.0;
		int nodeCountLeft = ref.getChildren().size();
		int nodeCountRight = comp.getChildren().size();
		int maxNodeCount = Math.max(nodeCountLeft, nodeCountRight);
		levelWeight = 1 / (double) maxNodeCount;
		return levelWeight;
	}

	private boolean hasChildren(Element e) {
		if (e.getChildren().size() == 0) {
			return false;
		}
		return true;
	}
	
	private boolean hasAttributes(Element e){
		if(e.getAttributes().size() == 0){
			return false;
		}
		return true;
	}
	private double sumSimilarities(List<Double> similarities) {
		double similarity = 0.0;
		for (Double d : similarities) {
			similarity += d;
		}
		return similarity;
	}
	
	public List<Double> getSimilarities(){
		return similarities;
	}
}
