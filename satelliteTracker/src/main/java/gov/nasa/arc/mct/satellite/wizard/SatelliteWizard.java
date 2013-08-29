package gov.nasa.arc.mct.satellite.wizard;


import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.collection.CollectionComponent;
import gov.nasa.arc.mct.satellite.Vector;
import gov.nasa.arc.mct.satellite.component.CoordinateComponent;
import gov.nasa.arc.mct.satellite.component.CoordinateModel;
import gov.nasa.arc.mct.satellite.component.SatelliteComponent;
import gov.nasa.arc.mct.satellite.component.VectorComponent;
import gov.nasa.arc.mct.satellite.utilities.ComboItem;
import gov.nasa.arc.mct.satellite.utilities.ComboListener;
import gov.nasa.arc.mct.satellite.utilities.ComboRenderer;
import gov.nasa.arc.mct.satellite.utilities.TLEListRenderer;
import gov.nasa.arc.mct.satellite.utilities.TLEUtility;
import gov.nasa.arc.mct.services.component.ComponentRegistry;
import gov.nasa.arc.mct.services.component.CreateWizardUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jsattrak.utilities.TLE;

public class SatelliteWizard extends CreateWizardUI {
		
	/*
	 * Based from Celestrak.com, this 2D-array represents satellite categories, and all choices within said categories
	 */
	private static final String[][] SatCat = new String[][] {
		{""},//initially have the ComboBox blank
		{"-----Special-Interest-----", 
			 "Last 30 Days' Launches", "Space Stations", "100 (or so) Brightest", "FENGYUN 1C Debris",
			 "IRIDIUM 33 Debris", "COSMOS 2251 Debris", "BREEZE-M R/B Breakup (2012-044C)" }, 
		{"-----Weather & Earth Resources-----",
			 "Weather", "NOAA", "GOES", "Earth Resources", "Search & Rescue (SARSAT)",
			 "Disaster Monitoring", "Tracking and Data Relay Satellite System (TDRSS)" },
		{"-----Communications-----",
			 "Geostationary", "Intelsat", "Gorizont",	"Raduga",	"Molniya", "Iridium",
			 "Orbcomm",	"Globalstar", "Amateur Radio", "Experimental",	"Other" },
		{"-----Navigation-----",
			 "GPS Operational",	"Glonass Operational", "Galileo",	"Beidou", 
			 "Satellite-Based Augmentation System (WAAS/EGNOS/MSAS)",
			 "Navy Navigation Satellite System (NNSS)", "Russian LEO Navigation" },
		{"-----Scientific-----",
			 "Space & Earth Science", "Geodetic",	"Engineering", "Education" },
		{"-----Miscellaneous-----",
		     "Miscellaneous Military",	"Radar Calibration", 	 "CubeSats",	"Other"},		
	};//--end of 2D array of satellite categories
	
	
	private TLEUtility tleUtil;
	private Set<String> chosenSats;
	
	private JComboBox jcbSatCategories;

	private DefaultListModel lmSatChoices;
	private DefaultListModel lmSatChosen;
	private JList jlSatChoices;
	private JList jlSatChosen;
	private JScrollPane jscrlpSatChoices;
	private JScrollPane jscrlpSatChosen;
	
	private JButton jbAddSat;
	private JButton jbAddAllSat;
	private JButton jbRemoveSat;
	private JButton jbRemoveAllSat;
	private JButton jbUpdateTLEs;
	
	private JCheckBox jchkbMakeCollection;
	private JTextField jtfCollectionName;
	
	private JLabel lblChooseSat;
	private JLabel lblChoiceSat;
	private JLabel lblChosenSat;
	private JLabel lblCollectionName;
	

	@Override
	public JComponent getUI(final JButton jbCreate) {
		
		tleUtil = new TLEUtility();
		chosenSats = new HashSet<String>();
		lmSatChoices = new DefaultListModel();
		lmSatChosen  = new DefaultListModel();
		
		jbAddSat = new JButton("Add -->");
		jbAddAllSat = new JButton("Add All -->");
		jbRemoveSat = new JButton("Remove <--");
		jbRemoveAllSat = new JButton("Remove All <--");
		jbUpdateTLEs = new JButton("Update TLEs");
		
		lblChooseSat = new JLabel("Satellite Group:");
		lblChoiceSat = new JLabel("Choices in Satellite Group");
		lblChosenSat = new JLabel("My Chosen Satellites");
		lblCollectionName = new JLabel("Collection name:");
		
		jchkbMakeCollection = new JCheckBox("Create as a Collection");
		jtfCollectionName = new JTextField("", 10);
		
		
		//--initially buttons and checkbox and text field are disabled
		jbAddSat.setEnabled(false);
		jbAddAllSat.setEnabled(false);
		jbRemoveSat.setEnabled(false);
		jbRemoveAllSat.setEnabled(false);
		jbCreate.setEnabled(false);
		jchkbMakeCollection.setSelected(false);
		jchkbMakeCollection.setEnabled(false);
		jtfCollectionName.setEnabled(false);
		lblCollectionName.setEnabled(false);
			
		
		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new FlowLayout());
		
		JPanel motherPanel = new JPanel();
		motherPanel.setLayout(new BoxLayout(motherPanel, BoxLayout.Y_AXIS));
		
		JPanel headPanel = new JPanel();
		headPanel.setLayout(new BoxLayout(headPanel, BoxLayout.LINE_AXIS));
		
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.LINE_AXIS));
		
		JPanel footPanel = new JPanel();
		footPanel.setLayout(new BorderLayout());
		
		
		
		
		JPanel satChoicePanel = new JPanel();
		satChoicePanel.setLayout(new BoxLayout(satChoicePanel, BoxLayout.Y_AXIS));
		
		JPanel SatChosenPanel = new JPanel();
		SatChosenPanel.setLayout(new BoxLayout(SatChosenPanel, BoxLayout.Y_AXIS));
		
		JPanel addBtnsPanel = new JPanel();
		addBtnsPanel.setLayout(new GridBagLayout());
		
		JPanel removeBtnsPanel = new JPanel();
		removeBtnsPanel.setLayout(new GridBagLayout());
		
		JPanel makeCollectionPanel = new JPanel();
		makeCollectionPanel.setLayout(new BoxLayout(makeCollectionPanel, BoxLayout.Y_AXIS));
		
		JPanel nameCollectionPanel = new JPanel();
		nameCollectionPanel.setLayout(new BoxLayout(nameCollectionPanel, BoxLayout.X_AXIS));
		
		
		GridBagConstraints cAdd = new GridBagConstraints();
		cAdd.fill = GridBagConstraints.BOTH;
        cAdd.weightx = 1.0;
        
        GridBagConstraints cRemove = new GridBagConstraints();
		cRemove.fill = GridBagConstraints.BOTH;
        cRemove.weightx = 1.0;
        
        
        
		
        /* ComboBox:
		 * create all of the elements in the combo-box, but Disable the Prime Categories,
		 * like 'Special Interest', 'Weather & Earth Resources', etc
		 */
		List<Object> jcbElements = new ArrayList<Object>();
	
		for( int i=0; i< SatCat.length; i++) {
			jcbElements.add(new ComboItem(SatCat[i][0], false));
			for(int j=1; j< SatCat[i].length; j++) {
				jcbElements.add(new ComboItem(SatCat[i][j], true));
			}//--inner
		}//--outer
		
		/*
		 * ComboBox: Here we populate and appropriately display the Enabled/Disabled
		 * elements within the ComboBox
		 */
		jcbSatCategories = new JComboBox(jcbElements.toArray());
		jcbSatCategories.setBackground(Color.WHITE);
    	jcbSatCategories.setRenderer(new ComboRenderer());
		jcbSatCategories.addActionListener(new ComboListener(jcbSatCategories));
		
		
		/*ActionListener for JComboBox that holds the satellite categories
		 * 
		 *    When the user selects a satellite grouping
		 *      -populate the SatChoices List
		 *      -Enable the Add/AddAll buttons
		 *      -Set the focus to the SatChoices List
		 *      -Select the first element in the SatChoices List
		 *      
		 *    If the user selects a disabled element, do nothing.
		 */
		jcbSatCategories.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent le) {
				 //if user selected an enabled element of the ComboBox...
				if( ((ComboItem)jcbSatCategories.getSelectedItem()).isEnabled()==true){
					lmSatChoices.clear();
					
					String choice = jcbSatCategories.getSelectedItem().toString();
					List<TLE> userSatChoices = tleUtil.getTLEs(choice);
					
					//TODO: mark the TLEs already in the Chosen list as '**already added**'?
					for(int i=0; i < userSatChoices.size(); i++)
						lmSatChoices.addElement(userSatChoices.get(i));
					
					jbAddSat.setEnabled(true);
					jbAddAllSat.setEnabled(true);
					
					jlSatChoices.requestFocusInWindow();
					jlSatChoices.setSelectedIndex(0);
				}				
			}//--end actionPerformed
		});
		
		jbUpdateTLEs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tleUtil.updateTLEs();
			}
		});
		
		/*ActionListener for Add button:
		 *     add TLEs from list SatChoice to list SatChosen,
		 *     and then remove them from the SatChoice list.  If the SatChoice list is empty,
		 *     we must disable the Add/AddAll buttons
		 */
		jbAddSat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				
				int selected[] = jlSatChoices.getSelectedIndices();
				int len = selected.length;
				
				//happens if user clicks add when focus is away from the list
				if(len==0) {
					return;
				}
				
				jbRemoveSat.setEnabled(true);
				jbRemoveAllSat.setEnabled(true);
				jchkbMakeCollection.setEnabled(true);
				jbCreate.setEnabled(true);
				
				//add the Satellites in the order as they appear in the SatChoice list, 
				for(int i=0; i< len; i++) {
					Object chosen = lmSatChoices.get(selected[i]);
					if( !satAlreadyAdded((TLE)chosen)) {
						addToChosen((TLE)chosen);				
						lmSatChosen.addElement(chosen);
					}
				}
				
				//remove added Satellites from SatChoice (in reverse order to not to cause index shifting)
				for(int i=0; i< len; i++)
					lmSatChoices.remove(selected[len-1-i]);
				
				if(lmSatChoices.isEmpty()) {
					jbAddSat.setEnabled(false);
					jbAddAllSat.setEnabled(false);
				}
				else
					jlSatChoices.setSelectedIndex(0);
			}//--end actionPerformed
			
		});
		
		
		/*Action listener: Add All button
		 *  Take everything from the SatChoices list and 
		 */
		jbAddAllSat.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int size = lmSatChoices.getSize();
				for(int i=0; i< size; i++) {
					if( !satAlreadyAdded((TLE)lmSatChoices.get(0))) {
						addToChosen((TLE)lmSatChoices.get(0));
						lmSatChosen.addElement(lmSatChoices.get(0));
					}
					lmSatChoices.remove(0);
				}
				
				jbAddSat.setEnabled(false);
				jbAddAllSat.setEnabled(false);
				
				jbRemoveSat.setEnabled(true);
				jbRemoveAllSat.setEnabled(true);
				jchkbMakeCollection.setEnabled(true);
				jbCreate.setEnabled(true);
			}
		});
		
		/*Action listener: Remove button
		 * 
		 */
		jbRemoveSat.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected[] = jlSatChosen.getSelectedIndices();
				
				if(selected.length==0)
					return;
				
				//remove all of the selected elements (from highest index to lowest; in order to avoid an index-shift)
				//And put them back in the Choice list
				for(int i=0; i<selected.length; i++) {
					removeFromChosen((TLE)lmSatChosen.get(selected[selected.length-1-i]));
					lmSatChosen.remove(selected[selected.length-1-i]);
				}
				
				if(lmSatChosen.isEmpty()) {
					jbRemoveSat.setEnabled(false);
					jbRemoveAllSat.setEnabled(false);
					jchkbMakeCollection.setEnabled(false);
					jbCreate.setEnabled(false);
					jcbSatCategories.requestFocus();  //since no elements in the Chosen list, set focus to comboBox
				}
				else
					jlSatChosen.setSelectedIndex(0);
			}
		});
		
		/*Action listener: Remove All button
		 * 
		 */
		jbRemoveAllSat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				lmSatChosen.clear();
				chosenSats.clear();
				jbRemoveSat.setEnabled(false);
				jbRemoveAllSat.setEnabled(false);
				jchkbMakeCollection.setEnabled(false);
				jbCreate.setEnabled(false);
			}
		});
		
		/*Check Box
		 * 
		 */
		jchkbMakeCollection.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if( e.getStateChange()==ItemEvent.SELECTED ) {
					lblCollectionName.setEnabled(true);
					jtfCollectionName.setEnabled(true);
					jtfCollectionName.requestFocusInWindow();
					jtfCollectionName.setText("Satellite Collection");
					jtfCollectionName.selectAll();
				}
				else {
					lblCollectionName.setEnabled(false);
					jtfCollectionName.setText("");
					jtfCollectionName.setEnabled(false);
				}
			}
		});
		
		/*TextField
		 * 
		 */
		jtfCollectionName.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				if(jtfCollectionName.getText().isEmpty() && jchkbMakeCollection.isSelected()) 
					jbCreate.setEnabled(false);
				else
					jbCreate.setEnabled(true);
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				jbCreate.setEnabled(true);
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(jtfCollectionName.getText().isEmpty() && jchkbMakeCollection.isSelected()) {
					jbCreate.setEnabled(false);
				}
				else
					jbCreate.setEnabled(true);
			}
		});
		
		
		//--populate JLists from the listModels
		jlSatChoices = new JList(lmSatChoices);
		jlSatChoices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlSatChoices.setCellRenderer(new TLEListRenderer());
		jscrlpSatChoices = new JScrollPane(jlSatChoices);
	    
		jlSatChosen = new JList(lmSatChosen);
		jlSatChosen.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jlSatChosen.setCellRenderer(new TLEListRenderer());
		jscrlpSatChosen  = new JScrollPane(jlSatChosen);
		
		
		//-------Lastly set the layout of the GUI
		headPanel.add(lblChooseSat);
		headPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		headPanel.add(jcbSatCategories);
		headPanel.add(Box.createRigidArea(new Dimension(40, 0)));
		headPanel.add(jbUpdateTLEs);
		
		satChoicePanel.add(lblChoiceSat);
		lblChoiceSat.setAlignmentX(Component.CENTER_ALIGNMENT);
		satChoicePanel.add(Box.createRigidArea(new Dimension(0, 5)));
		satChoicePanel.add(jscrlpSatChoices);
		
		satChoicePanel.setMinimumSize(new Dimension(250, 300));
		satChoicePanel.setPreferredSize(new Dimension(250, 300));
		satChoicePanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
     	addBtnsPanel.add(jbAddSat, cAdd);
		cAdd.gridx++;
		cAdd.insets = new Insets(5, 0, 0, 0);
		addBtnsPanel.add(jbAddAllSat, cAdd);
		
		SatChosenPanel.add(lblChosenSat);
		lblChosenSat.setAlignmentX(Component.CENTER_ALIGNMENT);
		SatChosenPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		SatChosenPanel.add(jscrlpSatChosen);
		
		SatChosenPanel.setMinimumSize(new Dimension(250, 300));
		SatChosenPanel.setPreferredSize(new Dimension(250, 300));
		SatChosenPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		removeBtnsPanel.add(jbRemoveSat, cRemove);
		cRemove.gridx++;
		cRemove.insets = new Insets(5, 0, 0, 0);
		removeBtnsPanel.add(jbRemoveAllSat, cRemove);
		
		bodyPanel.add(satChoicePanel);
		bodyPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		bodyPanel.add(addBtnsPanel);
		bodyPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		bodyPanel.add(SatChosenPanel);
		bodyPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		bodyPanel.add(removeBtnsPanel);
	
		/*
		 * Panel containing check box and text field 
		 */
		makeCollectionPanel.add(jchkbMakeCollection);
		jchkbMakeCollection.setAlignmentX(Component.LEFT_ALIGNMENT);
		nameCollectionPanel.add(lblCollectionName);
		nameCollectionPanel.add(jtfCollectionName);
		//jtfCollectionName.setPreferredSize(new Dimension(200, (int)jtfCollectionName.getSize().getHeight()));
		nameCollectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		makeCollectionPanel.add(nameCollectionPanel);
		
		footPanel.add(makeCollectionPanel, BorderLayout.EAST);
		
		//Here you can turn on the boarders to all of the panels
		//		headPanel.setBorder(BorderFactory.createTitledBorder("Head Panel"));
		//		bodyPanel.setBorder(BorderFactory.createTitledBorder("Body Panel"));
		//		motherPanel.setBorder(BorderFactory.createTitledBorder("Mother Panel"));
		//		rootPanel.setBorder(BorderFactory.createTitledBorder("Root Panel"));
		
		motherPanel.add(headPanel);
		motherPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		motherPanel.add(bodyPanel);
		motherPanel.add(footPanel);
	
		
		rootPanel.add(motherPanel, Component.CENTER_ALIGNMENT);
		
		
		return rootPanel;
		
	}
	
	/*
	 * Determine whether the satellite TLE being added is already contained in the Chosen list.
	 * 
	 * Note: the satellite number (which is actually a NORAD catalog number) in a TLE is a unique key 
	 *    ref: Celestrak:: "The NORAD Catalog Number is a unique identifier assigned by
	 *                      NORAD for each earth-orbiting artificial satellite in their
	 *                      SATCAT (Satellite Catalog)."
     * Part of satAlreadyAdded to include the functionality: Do not allow multiple copies of satellites
	 * into the 'SatChosen' list.
	 */
	private boolean
	satAlreadyAdded
	( TLE tle ) {
		
		String SatNum = tle.getLine2().split("\\s+")[1]; //grab the Sat Num (2nd token on 2nd line of TLE)
		return chosenSats.contains(SatNum);
	}
	
	/*
	 * Associated with satAlreadyAdded, this is to include the functionality: Do not allow multiple copies of satellites
	 * into the 'SatChosen' list
	 */
	private void
	addToChosen
	( TLE tle ) {
		String SatNum = tle.getLine2().split("\\s+")[1];
		chosenSats.add(SatNum);
	}
	
	/*
	 * Associated with satAlreadyAdded, this is to include the functionality: Do not allow multiple copies of satellites
	 * into the 'SatChosen' list
	 *    The user has removed a sat from the chosen list, so we must clear it from the chosenSat lookup
	 */
	private void
	removeFromChosen
	(TLE tle) {
		String SatNum = tle.getLine2().split("\\s+")[1];
		chosenSats.remove(SatNum);
	}

	/*
	 * This method is called when the 'create' button is hit
	 * 
	 * Precondition: the list model containing the chosen satellites to create (lmSatChosen) is not empty
	 */
	@Override
	public AbstractComponent createComp(ComponentRegistry registry,
			AbstractComponent parentComp) {

		Collection<AbstractComponent> chosenSats = new ArrayList<AbstractComponent>();

		//all satellites will have this as their parent; root may either be a collection (if selected) or will be the
		AbstractComponent rootComponent;
		
		if(jchkbMakeCollection.isSelected()) {
			rootComponent = registry.newInstance(registry.newInstance("gov.nasa.arc.mct.components.collection.CollectionComponent").getClass(), parentComp);
			rootComponent.setDisplayName(jtfCollectionName.getText());
			rootComponent.save();
		}
		else
			rootComponent = parentComp;

		
		int createSize = lmSatChosen.getSize();

		for(int i=0; i<createSize; i++) {
			//here we are creating new instances and adding them the the parent component (which may be a collection or the given parentComp)
			SatelliteComponent satComponent = registry.newInstance(SatelliteComponent.class, rootComponent);
			TLE currentTLE = (TLE)lmSatChosen.get(i);
			satComponent.setDisplayName(currentTLE.getSatName());
			satComponent.setOrbitalParameters(currentTLE);
			satComponent.save();
			chosenSats.add(satComponent);
			
			boolean[] truths   = { false,   true };
			String[]  axisName = { "X", "Y", "Z" };
			
			for (boolean velocity : truths) {
				String name = velocity ? "Velocity" : "Position";
				
				VectorComponent vectorComponent = registry.newInstance(VectorComponent.class, satComponent);
				vectorComponent.setDisplayName( name );
				vectorComponent.save();
				
				//make x, y, z
				for (int axis = 0; axis < 3; axis++) {
					CoordinateComponent coordinateComponent = registry.newInstance(CoordinateComponent.class, vectorComponent);
					coordinateComponent.setDisplayName(name + " " + axisName[axis]);
					coordinateComponent.setModel(new CoordinateModel(axis, velocity, satComponent.getComponentId(), currentTLE));
					coordinateComponent.save();
				}
			}
		}

		return rootComponent;
        
	}
	
}