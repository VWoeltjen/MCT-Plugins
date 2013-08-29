package gov.nasa.arc.mct.satellite.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;
import gov.nasa.arc.mct.components.PropertyEditor;
import gov.nasa.arc.mct.satellite.Trajectory;
import gov.nasa.arc.mct.satellite.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import jsattrak.utilities.TLE;

/*
 * used to be UserOrbitalComponent
 */

public class SatelliteComponent extends AbstractComponent {
	private AtomicReference<SatelliteModel> model = new AtomicReference<SatelliteModel>();
	
	/*
	 * change this name to setSatelliteParameters, or setSatellite.  This method will take a TLE,
	 * create a SatelliteModel and pass the TLE to the model.  The model will build the satellite
	 * based on the TLE
	 */
	public void setOrbitalParameters(Vector position, Vector velocity,long start) {
		SatelliteModel m = new SatelliteModel();
		m.set(position, velocity, start);
		model.set(m); //this line will stay the same
	}
	public void setOrbitalParameters(TLE curTLE ) {
		SatelliteModel m = new SatelliteModel();
		m.set(curTLE);
		model.set(m); //this line will stay the same
	}
	
	
	public SatelliteModel getModel() {
		return model.get();
	}
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<SatelliteModel> persistence = new JAXBModelStatePersistence<SatelliteModel>() {

				@Override
				protected SatelliteModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(SatelliteModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<SatelliteModel> getJAXBClass() {
					return SatelliteModel.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		
		return null;
	}

}