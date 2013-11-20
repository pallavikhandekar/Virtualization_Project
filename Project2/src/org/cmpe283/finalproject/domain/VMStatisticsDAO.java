package org.cmpe283.finalproject.domain;

import java.sql.Timestamp;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MapMessage;

public class VMStatisticsDAO {
	private String vmname;
	private Date date;
	private Timestamp timestamp;
	private double CPUUsage, memoryUsage;
	private String powerstate, connectionstate, host;

	public String getVmname() {
		return vmname;
	}
	public void setVmname(String vmname) {
		this.vmname = vmname;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public double getCPUUsage() {
		return CPUUsage;
	}
	public void setCPUUsage(double cPUUsage) {
		CPUUsage = cPUUsage;
	}
	public double getMemoryUsage() {
		return memoryUsage;
	}
	public void setMemoryUsage(double memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
	public String getPowerstate() {
		return powerstate;
	}
	public void setPowerstate(String powerstate) {
		this.powerstate = powerstate;
	}
	public String getConnectionstate() {
		return connectionstate;
	}
	public void setConnectionstate(String connectionstate) {
		this.connectionstate = connectionstate;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public VMStatisticsDAO() {
		date = new Date();
		timestamp = new Timestamp(date.getTime());
	}
	
}
