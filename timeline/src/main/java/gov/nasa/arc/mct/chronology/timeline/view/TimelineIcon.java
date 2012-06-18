package gov.nasa.arc.mct.chronology.timeline.view;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Enumerates icons used by this timeline (pan buttons, etc)
 * @author vwoeltje
 *
 */
public enum TimelineIcon {
	/**
	 * An icon to pan to the left
	 */
	LEFT("plot_pan_left_arrow"),
	
	/**
	 * An icon to pan to the right
	 */
	RIGHT("plot_pan_right_arrow");
	
	private Icon icon;
	private TimelineIcon(String file) {
		icon = new ImageIcon(
				getClass().getClassLoader().getResource("images/" + file + ".png"));
	}
	
	/**
	 * Get the enumerated icon.
	 * @return the enumerated icon
	 */
	public Icon getIcon() {
		return icon;
	}
}
