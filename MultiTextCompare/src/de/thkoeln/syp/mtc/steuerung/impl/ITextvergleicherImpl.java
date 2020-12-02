package de.thkoeln.syp.mtc.steuerung.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import difflib.Chunk;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ITextvergleicherImpl implements ITextvergleicher {

	private IMatrixImpl iMatrixImpl;

	private final File original;
	 
    private final File revised;
 
    public ITextvergleicherImpl(File original, File revised) {
        this.original = original;
        this.revised = revised;
    }
    
    @Override
    public List<Chunk> getChangesFromOriginal() throws IOException {
        return getChunksByType(Delta.TYPE.CHANGE);
    }
    
    @Override
    public List<Chunk> getInsertsFromOriginal() throws IOException {
        return getChunksByType(Delta.TYPE.INSERT);
    }
    
    @Override
    public List<Chunk> getDeletesFromOriginal() throws IOException {
        return getChunksByType(Delta.TYPE.DELETE);
    }
    
    @Override
    public List<Chunk> getChunksByType(Delta.TYPE type) throws IOException {
        final List<Chunk> listOfChanges = new ArrayList<Chunk>();
        final List<Delta> deltas = getDeltas();
        for (Delta delta : deltas) {
            if (delta.getType() == type) {
                listOfChanges.add(delta.getRevised());
            }
        }
        return listOfChanges;
    }
    
    @Override
    public List<Delta> getDeltas() throws IOException {
 
        final List<String> originalFileLines = fileToLines(original);
        final List<String> revisedFileLines = fileToLines(revised);
 
        final Patch patch = DiffUtils.diff(originalFileLines, revisedFileLines);
 
        return patch.getDeltas();
    }
    
    @Override
    public List<String> fileToLines(File file) throws IOException {
        final List<String> lines = new ArrayList<String>();
        String line;
        final BufferedReader in = new BufferedReader(new FileReader(file));
        while ((line = in.readLine()) != null) {
        	lines.add(line);
        }
        in.close();
        
        return lines;
    }
}
