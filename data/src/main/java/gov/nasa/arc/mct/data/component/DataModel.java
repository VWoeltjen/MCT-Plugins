package gov.nasa.arc.mct.data.component;

public class DataModel {
	
	private String feedId;
	private String timeStamp;
	private String value;
	
	public DataModel() {}
	
	public DataModel(String feedId, String timeStamp, String value) {
		this.feedId = feedId;
		this.timeStamp = timeStamp;
		this.value = value;
	}

	public String getFeedId() {
		return feedId;
	}

	public void setFeedId(String feedId) {
		this.feedId = feedId;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
}
