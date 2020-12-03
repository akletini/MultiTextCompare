package de.thkoeln.syp.mtc.steuerung.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import difflib.Chunk;
import difflib.Delta;

public interface ITextvergleicher {
	List<Chunk> getChangesFromOriginal() throws IOException;

	List<Chunk> getInsertsFromOriginal() throws IOException;

	List<Chunk> getDeletesFromOriginal() throws IOException;

	List<Chunk> getChunksByType(Delta.TYPE type) throws IOException;

	List<Delta> getDeltas() throws IOException;

	List<String> fileToLines(File file) throws IOException;

	void vergleiche();
}
