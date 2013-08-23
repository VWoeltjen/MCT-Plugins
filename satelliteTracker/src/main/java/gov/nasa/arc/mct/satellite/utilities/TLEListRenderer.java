package gov.nasa.arc.mct.satellite.utilities;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jsattrak.utilities.TLE;

/*
 * This cell renderer is for the TLE lists, so the TLE satellite names are displayed within the list
 */
@SuppressWarnings("serial")
public class TLEListRenderer extends JLabel implements ListCellRenderer {

	public TLEListRenderer
	() {
		setOpaque(true);
	}
	
	public Component
		getListCellRendererComponent
		( JList list, Object value, 
	  int index,   
      boolean isSelected,  
      boolean cellHasFocus)
	{
		  if (isSelected) {
			  setBackground(list.getSelectionBackground());
			  setForeground(list.getSelectionForeground());
	      } else {
	    	  setBackground(list.getBackground());
	    	  setForeground(list.getForeground());
	      }
		  setText(((TLE)value).getSatName());  
		  setOpaque(true);  
	     
		  return this;
		  
	}//--end getListCellRendererComponent  
	
}
/* Here is an anonomous class version; I chose against it as I need to call the cell-renderer a few times.
  
      tmp.setCellRenderer(new DefaultListCellRenderer() {
	 		public Component
	 		getListCellRendererComponent
	 		( JList list, Object value, 
			  int index,   
              boolean isSelected,  
              boolean cellHasFocus)
			{
				  if (isSelected) {
			        setBackground(list.getSelectionBackground());
			        setForeground(list.getSelectionForeground());
			      } else {
			        setBackground(list.getBackground());
			        setForeground(list.getForeground());
			      }
 				setText(((TLE)value).getSatName());  
 				setOpaque(true);  
 				return this;  
			}//--end getListCellRendererComponent  
        });//--end custom cell renderer
 */