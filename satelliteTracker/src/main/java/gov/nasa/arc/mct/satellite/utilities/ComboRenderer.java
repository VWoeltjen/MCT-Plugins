package gov.nasa.arc.mct.satellite.utilities;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/*
 * Part of the package that implements a JComboBox that allows elements to be
 * enabled or disabled.  This class draws the disabled elements blue, and
 * draws the enabled elements the way the List draws them
 */
@SuppressWarnings("serial")
public class ComboRenderer extends JLabel implements ListCellRenderer {
	
	public ComboRenderer() {
      setOpaque(true);
      setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    public Component
    getListCellRendererComponent
    ( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
	      if (isSelected) {
	        setBackground(list.getSelectionBackground());
	        setForeground(list.getSelectionForeground());
	      } else {
	    	setBackground(list.getBackground());
	        setForeground(list.getForeground());
	      }
	      if (!((CanEnable) value).isEnabled()) { //if we are a disabled element
	    	 setBackground(list.getBackground());
	        //setForeground(UIManager.getColor("Label.disabledForeground"));
	        setForeground(Color.BLUE);
	      }
	      setFont(list.getFont());
	      setText((value == null) ? "" : value.toString()); //trim off that extra whitespace
	      return this;
    }
}