package gov.nasa.arc.mct.labs.ancestorview.view;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.FeedProvider;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.ViewRoleSelection;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;

import java.awt.BorderLayout;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public class NodeView extends View {
	private static final long serialVersionUID = -1918540228199715462L;
	public static final ViewInfo VIEW_INFO = new ViewInfo(NodeView.class, "Node", 
			ViewType.TITLE);
	
	private boolean mousePressed = false;	
	
	public NodeView (AbstractComponent ac, ViewInfo vi) {
	    super(ac,vi);
	    setLayout(new BorderLayout());

	    final JLabel icon = new JLabel(ac.getIcon());
	    setOpaque(false);
	    icon.setOpaque(false);
	    
	    add(icon, BorderLayout.WEST);
        setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        
        //ac.addViewManifestation(this);
        addMouseListener( new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
					getManifestedComponent().open();
				} // TODO: Popup?
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {			
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
//					PlatformAccess.getPlatform().getMenuManager().getManifestationPopupMenu(ReferenceView.this)
//						.show(ReferenceView.this, evt.getX(), evt.getY());
				}
				mousePressed = true;
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					// Not quite - the menu manager assumes you will have a selection (and thus a selection manager)
					// for this method to work (otherwise, just ends up referring to the view you're sitting on.)
//					PlatformAccess.getPlatform().getMenuManager().getManifestationPopupMenu(ReferenceView.this)
//						.show(ReferenceView.this, evt.getX(), evt.getY());
				}
				mousePressed = false;
			}
        	
        });
        

        addMouseMotionListener( new MouseMotionListener() {
			public void mouseDragged(MouseEvent evt) {
				if (mousePressed) { 
					icon.getTransferHandler().exportAsDrag(icon, evt, TransferHandler.COPY);
				}
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
			}
        	
        });
        
        icon.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 4636428854269090018L;

			@Override
            public int getSourceActions(JComponent c) {
            	return COPY;
            }
            
            @Override
            protected Transferable createTransferable(JComponent c) {
            	View v[] = { NodeView.this };
                return new ViewRoleSelection(v);
            }
            
        	
        });
        
	}
	
	
}
