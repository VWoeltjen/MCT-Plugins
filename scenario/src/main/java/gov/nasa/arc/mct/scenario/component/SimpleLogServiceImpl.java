package gov.nasa.arc.mct.scenario.component;

public class SimpleLogServiceImpl implements SimpleLogService {

	public void log(String message) {
		System.out.println(message);
	}
	
}
