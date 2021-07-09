package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JScrollBar;

import de.thkoeln.syp.mtc.gui.view.ComparisonView;

/**
 * Controller fuer Diff-Panels
 * @author Allen Kletinitch
 *
 */
public class ComparisonController {

	private Management management;

	public ComparisonController(ComparisonView comparisonView) {
		management = Management.getInstance();
		management.setComparisonController(this);
		comparisonView.addMouseWheelListenerLeft(new LeftMouseWheelListener());
		comparisonView
				.addMouseWheelListenerMiddle(new MiddleMouseWheelListener());
	}

}

class LeftMouseWheelListener implements MouseWheelListener {
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		ComparisonView comp = Management.getInstance().getComparisonView();
		final JScrollBar horizontalScrollBar = comp.getScrollPaneLeft()
				.getHorizontalScrollBar();

		if (comp.getScrollPaneRight().getVerticalScrollBar().isVisible()
				&& !e.isShiftDown()) {
			comp.getScrollPaneRight().dispatchEvent(e);
		} else if (!comp.getScrollPaneRight().getVerticalScrollBar()
				.isVisible()) {
			comp.getScrollPaneRight().dispatchEvent(e);
		} else if (comp.getScrollPaneRight().getHorizontalScrollBar()
				.isVisible()
				&& e.isShiftDown()) {
			if (e.getWheelRotation() >= 1)// mouse wheel was rotated
			// down/ towards the user
			{
				int iScrollAmount = e.getScrollAmount();
				int iNewValue = horizontalScrollBar.getValue()
						+ horizontalScrollBar.getBlockIncrement()
						* iScrollAmount * Math.abs(e.getWheelRotation());
				if (iNewValue <= horizontalScrollBar.getMaximum()) {
					horizontalScrollBar.setValue(iNewValue);
				}
			} else if (e.getWheelRotation() <= -1)// mouse wheel was
			// rotated up/away
			// from the user
			{
				int iScrollAmount = e.getScrollAmount();
				int iNewValue = horizontalScrollBar.getValue()
						- horizontalScrollBar.getBlockIncrement()
						* iScrollAmount * Math.abs(e.getWheelRotation());
				if (iNewValue >= 0) {
					horizontalScrollBar.setValue(iNewValue);
				}
			}
		}
	}
}

class MiddleMouseWheelListener implements MouseWheelListener {
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		ComparisonView comp = Management.getInstance().getComparisonView();
		final JScrollBar horizontalScrollBar = comp.getScrollPaneMid()
				.getHorizontalScrollBar();
		if (comp.getScrollPaneRight().getVerticalScrollBar().isVisible()
				&& !e.isShiftDown()) {
			comp.getScrollPaneRight().dispatchEvent(e);
		} else if (!comp.getScrollPaneRight().getVerticalScrollBar()
				.isVisible()) {
			comp.getScrollPaneRight().dispatchEvent(e);
		} else if (comp.getScrollPaneRight().getHorizontalScrollBar()
				.isVisible()
				&& e.isShiftDown()) {
			if (e.getWheelRotation() >= 1)// mouse wheel was rotated
			// down/ towards the user
			{
				int iScrollAmount = e.getScrollAmount();
				int iNewValue = horizontalScrollBar.getValue()
						+ horizontalScrollBar.getBlockIncrement()
						* iScrollAmount * Math.abs(e.getWheelRotation());
				if (iNewValue <= horizontalScrollBar.getMaximum()) {
					horizontalScrollBar.setValue(iNewValue);
				}
			} else if (e.getWheelRotation() <= -1)// mouse wheel was
			// rotated up/away
			// from the user
			{
				int iScrollAmount = e.getScrollAmount();
				int iNewValue = horizontalScrollBar.getValue()
						- horizontalScrollBar.getBlockIncrement()
						* iScrollAmount * Math.abs(e.getWheelRotation());
				if (iNewValue >= 0) {
					horizontalScrollBar.setValue(iNewValue);
				}
			}
		}

	}
}

