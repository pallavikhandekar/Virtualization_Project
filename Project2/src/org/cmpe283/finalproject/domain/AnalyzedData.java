package org.cmpe283.finalproject.domain;

import java.util.Date;

/***
 * Analysis Data for Each VM
 * @author pallavi
 *
 */
public class AnalyzedData {
	
	private String myIpAddress;
	private String myHost;
	private int cpuInformation;
	private int memoryInformation;
	private int storageInformation;
	private int networkInformation;
	private int cpuSpeed;
	private int runTimeInfo;
	private Date timestamp;
	
	
	public int getCpuInformation() {
		return cpuInformation;
	}
	public void setCpuInformation(int cpuInformation) {
		this.cpuInformation = cpuInformation;
	}
	public int getMemoryInformation() {
		return memoryInformation;
	}
	public void setMemoryInformation(int memoryInformation) {
		this.memoryInformation = memoryInformation;
	}
	public int getStorageInformation() {
		return storageInformation;
	}
	public void setStorageInformation(int storageInformation) {
		this.storageInformation = storageInformation;
	}
	public int getNetworkInformation() {
		return networkInformation;
	}
	public void setNetworkInformation(int networkInformation) {
		this.networkInformation = networkInformation;
	}
	public int getCpuSpeed() {
		return cpuSpeed;
	}
	public void setCpuSpeed(int cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
	}
	public int getRunTimeInfo() {
		return runTimeInfo;
	}
	public void setRunTimeInfo(int runTimeInfo) {
		this.runTimeInfo = runTimeInfo;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public String getMyIpAddress() {
		return myIpAddress;
	}
	public void setMyIpAddress(String myIpAddress) {
		this.myIpAddress = myIpAddress;
	}
	public String getMyHost() {
		return myHost;
	}
	public void setMyHost(String myHost) {
		this.myHost = myHost;
	}
	

}
