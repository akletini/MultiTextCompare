package de.thkoeln.syp.mtc.gui.resources;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

// Extraklasse um die Scrollbars der einzelnen ScrollPanes zu
// synchronisieren

public class ScrollBarSynchronizer implements AdjustmentListener {
	JScrollBar[] scrollBars;

	public ScrollBarSynchronizer(JScrollBar... scrollBars) {
		this.scrollBars = scrollBars;

		for (JScrollBar scrollBar : scrollBars)
			scrollBar.addAdjustmentListener(this);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		JScrollBar source = (JScrollBar) e.getSource();
		int value = e.getValue();

		for (JScrollBar scrollBar : scrollBars) {
			if (scrollBar != source) {
				scrollBar.removeAdjustmentListener(this);
				scrollBar.setValue(value);
				scrollBar.addAdjustmentListener(this);
			}
		}
	}
}
