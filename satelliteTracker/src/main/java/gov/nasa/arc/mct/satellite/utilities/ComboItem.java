package gov.nasa.arc.mct.satellite.utilities;

/*
 * 
 * 
 */
public class ComboItem implements CanEnable {
    
	Object obj;
    boolean isEnable;

    public
    ComboItem(Object obj, boolean isEnable) {
      this.obj = obj;
      this.isEnable = isEnable;
    }

    public ComboItem(Object obj) {
      this(obj, true);
    }

    public boolean isEnabled() {
      return isEnable;
    }

    public void setEnabled(boolean isEnable) {
      this.isEnable = isEnable;
    }

    public String toString() {
      return obj.toString();
    }
}//--end class ComboItem