package gov.nasa.arc.mct.scenario.component;

import gov.nasa.arc.mct.scenario.util.DurationFormatter;

import java.text.ParseException;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActivityData {

	private double power;
	private double comm;
	private long duration;
	private Date startDate;
	private Date endDate;

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public double getComm() {
		return comm;
	}

	public void setComm(double comm) {
		this.comm = comm;
	}
	
	public String getDurationTime()
	{
		return DurationFormatter.formatDuration(duration);
	}
	
	public void setDurationTime(String duration)
	{
		try {
		this.duration = DurationFormatter.parse(duration);
		} catch (ParseException e) {
			this.duration = 30L * 60L * 1000L;
		}
		Date endDate = new Date(this.startDate.getTime() + this.duration);
		this.endDate = endDate;
	}

	public Date getStartTime() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		this.duration = this.endDate.getTime() - startDate.getTime();
	}

	public Date getEndTime() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		this.duration = this.endDate.getTime() - startDate.getTime();
	}
	
}
