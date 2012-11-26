package com.enz.soaps;

import java.util.Date;

public class Form {
	private long id;
	private long patientId;
	private Date date;
	private String subjective;
	private String objective;
	private String asomething;
	private String psomething;
	private String ssomething;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPatientId() {
		return patientId;
	}

	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(String date) {
		this.date = new Date(date);
	}

	public String getSubjective() {
		return subjective;
	}

	public void setSubjective(String subjective) {
		this.subjective = subjective;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public String getAsomething() {
		return asomething;
	}

	public void setAsomething(String asomething) {
		this.asomething = asomething;
	}

	public String getPsomething() {
		return psomething;
	}

	public void setPsomething(String psomething) {
		this.psomething = psomething;
	}

	public String getSsomething() {
		return ssomething;
	}

	public void setSsomething(String ssomething) {
		this.ssomething = ssomething;
	}

	@Override
	public String toString() {
		return date.toString();
	}
}
