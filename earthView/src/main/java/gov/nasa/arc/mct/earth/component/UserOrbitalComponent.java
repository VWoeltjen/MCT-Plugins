package gov.nasa.arc.mct.earth.component;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.components.JAXBModelStatePersistence;
import gov.nasa.arc.mct.components.ModelStatePersistence;
import gov.nasa.arc.mct.components.PropertyDescriptor;
import gov.nasa.arc.mct.components.PropertyDescriptor.VisualControlDescriptor;
import gov.nasa.arc.mct.components.PropertyEditor;
import gov.nasa.arc.mct.earth.Trajectory;
import gov.nasa.arc.mct.earth.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserOrbitalComponent extends AbstractComponent {
	private AtomicReference<OrbitalModel> model = new AtomicReference<OrbitalModel>();
	
	public void setOrbitalParameters(Vector position, Vector velocity,long start) {
		OrbitalModel m = new OrbitalModel();
		m.set(position, velocity, start);
		model.set(m);
	}
	
	public OrbitalModel getModel() {
		return model.get();
	}
	
	@Override
	protected <T> T handleGetCapability(Class<T> capability) {
		if (ModelStatePersistence.class.isAssignableFrom(capability)) {
		    JAXBModelStatePersistence<OrbitalModel> persistence = new JAXBModelStatePersistence<OrbitalModel>() {

				@Override
				protected OrbitalModel getStateToPersist() {
					return model.get();
				}

				@Override
				protected void setPersistentState(OrbitalModel modelState) {
					model.set(modelState);
				}

				@Override
				protected Class<OrbitalModel> getJAXBClass() {
					return OrbitalModel.class;
				}
		        
			};
			
			return capability.cast(persistence);
		}
		
		return null;
	}
	
	@Override
	public List<PropertyDescriptor> getFieldDescriptors()  {
		
        List<PropertyDescriptor> values = new ArrayList<PropertyDescriptor>();
        
        String axisName[] = { "X" , "Y" , "Z" };
        boolean truths[]  = { false  ,   true };
        for (boolean velocity : truths) {
        	for (int axis = 0; axis < 3; axis++) {
        		values.add(new PropertyDescriptor( (velocity ? "Velocity " : "Position ") + axisName[axis],
        				new VectorComponentEditor(axis, velocity), VisualControlDescriptor.TextField));
        	}
        }
        
        for (PropertyDescriptor value : values) {
        	value.setFieldMutable(true);
        }
        
		return values;
	}

	private class VectorComponentEditor implements PropertyEditor<String> {
		private int axis;
		private boolean velocity;
		
		public VectorComponentEditor (int axis, boolean velocity) {
			this.axis = axis;
			this.velocity = velocity;
		}

		@Override
		public String getAsText() {
			Trajectory t = model.get().getInitialTrajectory();
			Vector     v = velocity ? t.getVelocity() : t.getPosition();
			double[]   values = { v.getX(), v.getY(), v.getZ() };
			return Double.toString(values[axis]);
		}

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			try {
				Trajectory t = model.get().getInitialTrajectory();
				Vector vel = t.getVelocity();
				Vector pos = t.getPosition();
				Vector     v = velocity ? vel : pos;
				double[]   values = { v.getX(), v.getY(), v.getZ() };
				values[axis] = Double.parseDouble(text);
				v = new Vector(values[0], values[1], values[2]);
				if (velocity) {
					vel = v;
				} else {
					pos = v;
				}
				model.get().set(pos, vel, System.currentTimeMillis());
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public String getValue() {
			return getAsText();
		}

		@Override
		public void setValue(Object value) throws IllegalArgumentException {
			setAsText(value.toString());
		}

		@Override
		public List<String> getTags() {
			return Arrays.asList("");
		}
	
	}
}
