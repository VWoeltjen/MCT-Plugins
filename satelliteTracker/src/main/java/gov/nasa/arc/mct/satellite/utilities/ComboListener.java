package gov.nasa.arc.mct.satellite.utilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;


/*
 * Part of the package that disables elements within a JComboBox.
 * Here, we do not allow the user to select the disabled elements
 * within a JComboBox
 */
public class ComboListener implements ActionListener {
	
    JComboBox combo;
    Object currentItem;

    public ComboListener(JComboBox combo) {
      this.combo = combo;
      combo.setSelectedIndex(0);	//have no selection in the comboBox
      currentItem = combo.getSelectedItem();
    }

    /**
     * If the user selected an element (in the JComboBox)
     * that is disabled, do not change the currently selected
     * item.
     */
    public void actionPerformed(ActionEvent e) {
      Object tempItem = combo.getSelectedItem();
      if (!((CanEnable) tempItem).isEnabled()) {
        combo.setSelectedItem(currentItem);
      } else {
        currentItem = tempItem;
      }
    }

}//--end class ComboListener
