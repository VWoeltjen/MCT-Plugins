package gov.nasa.arc.mct.chronology.timeline.view;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;

import javax.swing.JLabel;

public abstract class CharacterClickListener implements MouseListener {
	public abstract void characterClicked(int character);
	

	@Override
	public void mouseClicked(MouseEvent e) {
		Object s = e.getSource();
		if (s instanceof JLabel) {
			JLabel label = (JLabel) s;
			String str = label.getText();
			characterClicked(
				findCharacter(e.getX(),
							  str,
							  label.getFont(),
							  label.getFontMetrics(label.getFont()).getFontRenderContext(),
							  0,
							  0,
							  str.length() ) 
			);
		}
	}
	
	private int findCharacter(int x, String s, Font font, FontRenderContext context, int offset, int start, int end) {
		if (end - start <= 1) return start;
		int mid   = (start+end) / 2;
		int width = (int) font.getStringBounds(s, start, mid, context).getWidth();
		
		if (x < offset + width) return findCharacter(x, s, font, context, offset, start, mid); 
		else return findCharacter(x, s, font, context, offset+width, mid, end);  
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
