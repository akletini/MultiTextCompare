package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import de.thkoeln.syp.mtc.gui.view.ComparisonView;

public class ComparisonController {
	
	private Management management;
	
	public ComparisonController(ComparisonView comparisonView){
		management = Management.getInstance();
		management.setComparisonController(this);
		comparisonView.addMouseWheelListenerLeft(new LeftMouseWheelListener());
		comparisonView.addMouseWheelListenerMiddle(new MiddleMouseWheelListener());
	}

}

class LeftMouseWheelListener implements MouseWheelListener {
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		ComparisonView comp = Management.getInstance().getComparisonView();
		if(comp.getScrollPaneRight().getVerticalScrollBar().isVisible()){
			comp.getScrollPaneRight().dispatchEvent(e);
		}
	}
}
class MiddleMouseWheelListener implements MouseWheelListener {
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		ComparisonView comp = Management.getInstance().getComparisonView();
		if(comp.getScrollPaneRight().getVerticalScrollBar().isVisible()){
			comp.getScrollPaneRight().dispatchEvent(e);
		}
	}
}