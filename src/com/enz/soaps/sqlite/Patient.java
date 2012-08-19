package com.enz.soaps.sqlite;

public class Patient {
	private long id;
	private String name;
	private long entryCount;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getEntryCount () {
		return entryCount;
	}
	
	public void setEntryCount(long ec) {
		this.entryCount = ec;
	}
	
	public String toString() {
		return name;
	}
}
