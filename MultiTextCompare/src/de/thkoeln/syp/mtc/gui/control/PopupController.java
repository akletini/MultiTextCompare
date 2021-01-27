package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.thkoeln.syp.mtc.gui.control.MainController.HilfeListener;
import de.thkoeln.syp.mtc.gui.view.PopupView;

public class PopupController {
	private PopupView popupView;
	public PopupController(PopupView popupView){
		this.popupView = popupView;
		popupView.addOkListener(new OkListener());
	}
	
	class OkListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			popupView.setVisible(false); //you can't see me!
			popupView.dispose(); //Destroy the JFrame object
		}
	}
}
