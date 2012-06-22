package gov.nasa.arc.mct.qspersistence.search;

import gov.nasa.arc.mct.services.component.SearchProvider;

import javax.swing.JComponent;

public class QSSearchProvider implements SearchProvider {

	@Override
	public String getName() {
		return "Search";
	}

	@Override
	public JComponent createSearchUI() {
		return new SearchUI();
	}
	
}
