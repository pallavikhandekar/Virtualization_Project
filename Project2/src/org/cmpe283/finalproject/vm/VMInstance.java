package org.cmpe283.finalproject.vm;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.jms.JMSException;

import org.cmpe283.finalproject.domain.VMStatisticsDAO;
import org.cmpe283.finalproject.messaging.Producer;

import com.vmware.vim25.Action;
import com.vmware.vim25.AlarmAction;
import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmTriggeringAction;
import com.vmware.vim25.GroupAlarmAction;
import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedEntityStatus;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SendEmailAction;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSnapshotTree;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

public class VMInstance {
	private VirtualMachine vm;
	public synchronized VirtualMachine getVm() {
		return vm;
	}
	public synchronized void setVm(VirtualMachine vm) {
		this.vm = vm;
	}
	Producer producer ;
	
	ServiceInstance si;

	public VMInstance() throws JMSException{
		producer = new Producer();
		producer.connect();
	}
	public VMInstance(VirtualMachine vm, ServiceInstance si) {
		this.vm = vm;
		this.si = si;
	}

	public void removeAlarm() {
		AlarmManager alarmMgr = si.getAlarmManager();
		try {
			Alarm[] arm = alarmMgr.getAlarm(vm);
			for (int i = 0; i < arm.length; i++) {
				arm[i].removeAlarm();
			}
		} catch (RuntimeFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	StateAlarmExpression createStateAlarmExpression() {
		StateAlarmExpression expression = new StateAlarmExpression();
		expression.setType("VirtualMachine");
		expression.setStatePath("runtime.powerState");
		expression.setOperator(StateAlarmOperator.isEqual);
		expression.setRed("poweredOff");
		return expression;
	}

	SendEmailAction createPowerOnAction() {
		SendEmailAction action = new SendEmailAction();
		action.setToList("ranjith.vn438@gmail.com");
		action.setCcList("ranjith.vn438@gmail.com");
		action.setSubject("Alarm trigger");
		action.setBody("User powered off the VM.");
		return action;
	}

	AlarmTriggeringAction createAlarmTriggerAction(Action action) {
		AlarmTriggeringAction alarmAction = new AlarmTriggeringAction();
		alarmAction.setYellow2red(true);
		alarmAction.setAction(action);
		return alarmAction;
	}

	public void createAlarm() {
		try {
			String alarmname = "SetAlarm" + vm.getName();
			AlarmManager alarmMgr = si.getAlarmManager();
			AlarmSpec spec = new AlarmSpec();
			StateAlarmExpression expression = createStateAlarmExpression();
			AlarmAction methodAction = createAlarmTriggerAction(createPowerOnAction());

			GroupAlarmAction gaa = new GroupAlarmAction();
			gaa.setAction(new AlarmAction[] { methodAction });
			spec.setAction(gaa);
			spec.setExpression(expression);
			spec.setName(alarmname);
			spec.setDescription("Monitor VM state and send email and power it on if VM powers off");

			System.out.println("Alarm to Monitor VM state and power it on if VM powers off");
			spec.setEnabled(true);

			AlarmSetting as = new AlarmSetting();
			as.setReportingFrequency(0); // as often as possible
			as.setToleranceRange(0);
			spec.setSetting(as);
			alarmMgr.createAlarm(vm, spec);
			System.out.println("\t\t\n Alarm set for " + vm.getName());
		} catch (RemoteException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	VirtualMachineSnapshot getSnapshotInTree(VirtualMachine vm, String snapName) {
		if (vm == null || snapName == null) {
			return null;
		}

		if (vm.getSnapshot() == null
				|| vm.getSnapshot().getRootSnapshotList() == null) {
			return null;
		}

		VirtualMachineSnapshotTree[] snapTree = vm.getSnapshot()
				.getRootSnapshotList();
		if (snapTree != null) {
			ManagedObjectReference mor = findSnapshotInTree(snapTree, snapName);
			if (mor != null) {
				return new VirtualMachineSnapshot(vm.getServerConnection(), mor);
			}
		}
		System.out.println("snap treee is nulll");
		return null;
	}

	ManagedObjectReference findSnapshotInTree(
			VirtualMachineSnapshotTree[] snapTree, String snapName) {
		for (int i = 0; i < snapTree.length; i++) {
			VirtualMachineSnapshotTree node = snapTree[i];
			if (snapName.equals(node.getName())) {
				return node.getSnapshot();
			} else {
				VirtualMachineSnapshotTree[] childTree = node
						.getChildSnapshotList();
				if (childTree != null) {
					ManagedObjectReference mor = findSnapshotInTree(childTree,
							snapName);
					if (mor != null) {
						return mor;
					}
				}
			}
		}
		return null;
	}

	public void createSnapshots() throws InvalidName, RuntimeFault,
			RemoteException {
		VirtualMachine vm = getVm();
		System.out.println("\nVM Service Instance:" + si);
		int len = vm.getName().length();

		String option = "create";
		String snapshotname = "MainSnapshot";
		String desc = "Latest Snapshot";

		if ("create".equalsIgnoreCase(option)) {
			System.out.println("vm retrieved for snapshoting " + vm.getName()
					+ "--" + snapshotname);

			VirtualMachineSnapshot vmsnap1 = getSnapshotInTree(vm, snapshotname);

			System.out.println("snapshot of retrieved vm" + vmsnap1);
			if (vmsnap1 != null)
				vmsnap1.renameSnapshot("Delete", "To be deleted");

			Task task1 = vm.createSnapshot_Task(snapshotname, desc, false,
					false);

			if (task1.waitForMe() == Task.SUCCESS) {
				System.out.println("Snapshot created for: " + vm.getName());
			}

			boolean removechild = false;

			VirtualMachineSnapshot vmsnap2 = getSnapshotInTree(vm, "Delete");

			if (vmsnap2 != null) {
				Task task2 = vmsnap2.removeSnapshot_Task(removechild);
				if (task2.waitForMe() == Task.SUCCESS)
					System.out.println("Removed snapshot:" + "delete");
			}
		}
	}

	public void helloVM() throws InvalidProperty, RuntimeFault, RemoteException {
		System.out.println("\tStats for VM : " + vm.getName() + " with IP: "
				+ vm.getGuest().getIpAddress() + "--->");
		System.out.println("\n\tHello " + vm.getName() + "!!!\n");
		VirtualMachineRuntimeInfo vrunt = vm.getRuntime();
		VirtualMachineConfigInfo vminfo = vm.getConfig();
		VirtualMachineCapability vmc = vm.getCapability();
		vm.getResourcePool();
		
		VMStatisticsDAO dao = new VMStatisticsDAO();
		dao.setVmname(vminfo.getGuestFullName());
		dao.setCPUUsage(vrunt.getMaxCpuUsage());
		dao.setMemoryUsage(vrunt.getMaxMemoryUsage());
		dao.setPowerstate(vrunt.getPowerState().toString());
		dao.setConnectionstate(vrunt.getConnectionState().toString());
		dao.setHost(vrunt.getHost().get_value());
		producer.beginSendingMessages(dao);
		
		System.out.println("\tGuestOS: " + vminfo.getGuestFullName());
		System.out.println("\tCPU usage: " + vrunt.maxCpuUsage + "CPU cycles");
		System.out.println("\tMemory usage: " + vrunt.maxMemoryUsage + "MB");
		System.out.println("\tPower State: " + vrunt.getPowerState());
		System.out.println("\tConnection State: " + vrunt.getConnectionState());
		System.out.println("\tVM's Host: " + vrunt.getHost().get_value());
		System.out.println("\tMultiple snapshot supported: "
				+ vmc.isMultipleSnapshotsSupported());
		System.out.println("\tGet Config:  " + vm.getConfig() + "\n");
		
		
		
	}

	public boolean checkStatus(String ip) throws IOException {
		System.out.println("\n\nPinging the VM\n\n");
		Boolean isReachable = false;
		Runtime r = Runtime.getRuntime();
		Process pingProcess = r.exec("ping " + ip);
		String pingResult = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(
				pingProcess.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			pingResult += inputLine;
		}
		if(pingResult.contains("Reply from "+ip)){
			isReachable = true;
			System.out.println("VM is alive");
		}else if(pingResult.contains("Ping request could not find host null. Please check the name and try again.")){
			System.out.println("Host Not Found");
			isReachable = false;
		}
		
		// If Ping fails
		else if (pingResult.contains("Request timed out")) {
			System.out.println("Host Not Found");
			isReachable = false;
		}else{
			System.out.println("Host Not Found");
			isReachable = false;
		}
		return isReachable;
	}

	public void createClone(String vmName) throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException {
		Folder rootFolder = si.getRootFolder();
		VirtualMachine vmClone = (VirtualMachine) new InventoryNavigator(
				rootFolder).searchManagedEntity("VirtualMachine", vmName);

		if (vmClone == null) {
			System.out.println("No VM " + vmName + " found");
			si.getServerConnection().logout();
			return;
		}

		VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
		cloneSpec.setLocation(new VirtualMachineRelocateSpec());
		cloneSpec.setPowerOn(false);
		cloneSpec.setTemplate(false);
		cloneSpec.snapshot = vmClone.getCurrentSnapShot().getMOR();

		String cloneName = "Ranjith-Cloned-" + vmName;

		Task task = vmClone.cloneVM_Task((Folder) vmClone.getParent(),
				cloneName, cloneSpec);

		System.out.println("Launching the VM clone task. Please wait ......  ");

		String status = task.waitForTask();

		if (status == Task.SUCCESS) {
			System.out.println("VM got cloned successfully.");
		} else {
			System.out.println("Failure -: VM cannot be cloned");
		}

	}

	public void migrateClone(String vmName, String newHostName) throws InvalidProperty, RuntimeFault, RemoteException, InterruptedException{

		Folder rootFolder = si.getRootFolder();
		VirtualMachine vmMigrate = (VirtualMachine) new InventoryNavigator(
				rootFolder).searchManagedEntity("VirtualMachine", vmName);

		HostSystem newHost = (HostSystem) new InventoryNavigator(rootFolder)
				.searchManagedEntity("HostSystem", newHostName);

		ComputeResource cr = (ComputeResource) newHost.getParent();

		String[] checks = new String[] { "cpu", "software" };
		HostVMotionCompatibility[] vmcs = si.queryVMotionCompatibility(
				vmMigrate, new HostSystem[] { newHost }, checks);

		String[] comps = vmcs[0].getCompatibility();

		if (checks.length != comps.length) {
			System.out.println("CPU/software NOT compatible. Exit.");
			si.getServerConnection().logout();
			return;
		}

		System.out.println("Launching new Clone Migrate task for" + vmName
				+ "\n Please wait ......  ");
		Task task = vmMigrate.migrateVM_Task(cr.getResourcePool(), newHost,
				VirtualMachineMovePriority.highPriority,
				VirtualMachinePowerState.poweredOff);

		if (task.waitForTask() == Task.SUCCESS) {
			System.out.println(vmName + " is VMotioned!");
		} else {
			System.out.println("VMotion failed!");
			TaskInfo info = task.getTaskInfo();
			System.out.println(info.getError().getFault());
		}

	}

	public void monitorVM() throws IOException, InterruptedException  {
		
		VirtualMachine vmi = getVm();
		
		String ipaddress = vmi.getGuest().getIpAddress();
		InetAddress inet = InetAddress.getByName(ipaddress);
		System.out.println("Sending Ping Request to " + vmi.getName());
		boolean isReachable = checkStatus(ipaddress);
		if (isReachable) {
			// vm is reachable
			System.out.println("VM " + vmi.getName() + " Reached");
			helloVM();
		} else {
			// vm not reachable
			System.out.println("Alarm state-------->"
					+ vmi.getTriggeredAlarmState());
			if (null != vmi.getTriggeredAlarmState()
					&& vmi.getTriggeredAlarmState()[0].getOverallStatus()
							.equals(ManagedEntityStatus.red)) {
				System.out
						.println("...Alarm triggered - VM was shutdown by a user, won't migrate.");
			} else {
				System.out.println("VM: " + vmi.getName() + " is down !!!");
				System.out.println("Starting recovery for : " + vmi.getName());

				createClone(vmi.getName());

				String cloneName = "Ranjith-Cloned-" + vmi.getName();
				String newHostIp = null;
				Folder rootFolder = si.getRootFolder();
				ManagedEntity newHost1[] = new InventoryNavigator(rootFolder)
						.searchManagedEntities("HostSystem");
				for (int j = 0; j < newHost1.length; j++) {
					HostSystem hs = (HostSystem) newHost1[j];
					if (hs.getName() != null) {
						// ping the host
						if (checkStatus(hs.getName())) {
							newHostIp = hs.getName();
							System.out.println("the vm is up");
							break;
						} else
							continue;
					}
				}
				if (newHostIp != null) {
					migrateClone(cloneName, newHostIp);
				} else {
					newHostIp = "130.65.132.143";
					migrateClone(cloneName, "130.65.132.143");
				}
				// migrateClone(cloneName,"130.65.132.164");

				Folder rootFolder2 = si.getRootFolder();
				VirtualMachine vmPowerOn = (VirtualMachine) new InventoryNavigator(
						rootFolder2).searchManagedEntity("VirtualMachine",
						cloneName);
				HostSystem newHost = (HostSystem) new InventoryNavigator(
						rootFolder2).searchManagedEntity("HostSystem",
						newHostIp);
				Task taskOn = vmPowerOn.powerOnVM_Task(newHost);

				if (taskOn.waitForTask() == Task.SUCCESS)
					System.out
							.println("---------> New Cloned VM Up and Ready for use !!!!!!!");
				else
					System.out
							.println("Recovery failed - failed to start VM !!!");

				removeAlarm();
				Task taskOff = vm.powerOffVM_Task(); // powerOnVM_Task(newHost);

				if (taskOff.waitForTask() == Task.SUCCESS)
					System.out
							.println("---------> Old VM powered off... ready to delete !!!!!!!");
				else
					System.out.println("Old VM still ON !!!");

				Task delVmTask = vm.destroy_Task();

				if (delVmTask.waitForTask() == Task.SUCCESS)
					System.out.println("---------> Old Obsolete Virtual machine Deleted !!!!!!!");
				else
					System.out.println("Recovery failed - failed to delete VM !!!");
				setVm(vmPowerOn); 
				createAlarm();

			}
		}

	}

	public void VMMonitor() throws IOException, InterruptedException{
		Thread t1 = new Thread(){
			public void run(){
			try {

				while (true) {
					System.out.println("\n\n----> Monitoring !!!");
					monitorVM();
					Thread.sleep(1000); // wait for 5 sec
				}
			} catch (Exception e) {
				e.printStackTrace();
			}}
		};
		Thread t2 = new Thread(){
			public void run() {
				try {

					while (true) {
						System.out.println("\n\n----> Creating Snapshots !!!");
						createSnapshots();
						Thread.sleep(60000); // wait for 5 sec
						//Thread.sleep(1800000);
					}

				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		//monitorVM();
		t1.start();
		//t2.start();
	}

}
