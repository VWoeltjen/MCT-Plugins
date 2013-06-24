package gov.nasa.arc.mct.scenario.component;

public interface DurationCapability {
	public long getStart();
	public long getEnd();
	public void setStart(long start);
	public void setEnd(long end);
}
