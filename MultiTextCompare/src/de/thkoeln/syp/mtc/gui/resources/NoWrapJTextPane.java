package de.thkoeln.syp.mtc.gui.resources;

import java.awt.Dimension;

import javax.swing.JTextPane;

/**
 * Extra JTextPane Klasse, die "Word Wrapping" verhindert
 * @author Allen Kletinitch
 *
 */
public class NoWrapJTextPane extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 527168883171479510L;

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// Only track viewport width when the viewport is wider than the
		// preferred width
		return getUI().getPreferredSize(this).width <= getParent().getSize().width;
	};

	@Override
	public Dimension getPreferredSize() {
		// Avoid substituting the minimum width for the preferred width when
		// the viewport is too narrow
		return getUI().getPreferredSize(this);
	};
}
