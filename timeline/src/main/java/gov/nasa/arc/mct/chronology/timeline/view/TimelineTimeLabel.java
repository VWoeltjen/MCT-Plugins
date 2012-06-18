package gov.nasa.arc.mct.chronology.timeline.view;

import gov.nasa.arc.mct.chronology.event.UNIXTimeInstant;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class TimelineTimeLabel extends JLabel {
	private static final long serialVersionUID = -677459241262257631L;

	private String prefix = "Now: ";
	private String selection = "";
	
	private List<Highlight> highlights = new ArrayList<Highlight>();

	public TimelineTimeLabel() {
		super();
		
		addMouseListener(new CharacterClickListener() {
			@Override
			public void characterClicked(int character) {
				character -= prefix.length();
				if (character >= 0) {
					selection = "" + UNIXTimeInstant.DATE_FORMAT_STRING.charAt(character);
					System.out.println(selection);
					updateHighlights();
				}
			}	
		});	
	}
	
	public void setTime(UNIXTimeInstant time) {
		setText(prefix + time.toString());
		updateHighlights();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		for (Highlight h : highlights) {
			h.paint(g);
		}
	}

	private void updateHighlights() {
		highlights.clear();
		if (selection.isEmpty()) return;
		
		String str = UNIXTimeInstant.DATE_FORMAT_STRING;
		for (int i = str.indexOf(selection); i >= 0; i = str.indexOf(selection, i+1)) {
			FontRenderContext context = getFontMetrics(getFont()).getFontRenderContext();
			int x = (int) getFont().getStringBounds(getText(), 0, prefix.length() + i, context).getWidth();
			int w = (int) getFont().getStringBounds(getText(), prefix.length() + i, prefix.length() + i + 1, context).getWidth();
			highlights.add(new Highlight(x, w));
		}
	}

	private static final Color HIGHLIGHT_COLOR = new Color(240, 200, 60, 96);
	
	private class Highlight {
		private int x;
		private int w;

		public Highlight(int x, int w) {
			this.x = x;
			this.w = w;
		}
		public void paint (Graphics g) {
			Color c = g.getColor();
			g.setColor(HIGHLIGHT_COLOR);
			g.fillRect(x, 5, w, 16);
			g.setColor(c);
		}
	}
}
