package org.cmpe283.finalproject.vm;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.jms.JMSException;

import org.cmpe283.finalproject.messaging.Consumer;

import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class Solution {

	static Thread t1 = new Thread(){
		public void run() {
			try {

				while (true) {
					System.out.println("\n\n----> Monitoring !!!");
					monitorvms();
					Thread.sleep(5000); // wait for 5 sec
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void monitorvms() {
			VMInstance[] myvms;
			ServiceInstance si = null;
			try {
				URL url = new URL(ServerConfig.Url);
				si = new ServiceInstance(url, ServerConfig.username, ServerConfig.password, true);
				Folder rootFolder = si.getRootFolder();
				String rootname = rootFolder.getName();
				System.out.println("\n\n\tRoot name for vCenter: " + rootname + "\n\n");
				ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
				if (mes == null || mes.length == 0) {
					System.out.println("\n\tNo Virtual machine Found !!!");
					return;
				}
				myvms = new VMInstance[mes.length];
				for (int i = 0; i < mes.length; i++) {
					VirtualMachine vm = (VirtualMachine) mes[i];
					myvms[i] = new VMInstance(vm,si);
					myvms[i].monitorVM();
				}
			}catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  finally{
				if(si!=null)
					si.getServerConnection().logout();
				myvms = null;
			}
			
		};
	};

	static Thread t2 = new Thread(){
		public void run() {
			try {

				while (true) {
					System.out.println("\n\n----> Monitoring !!!");
					monitorvms();
					Thread.sleep(1800000); // wait for 5 sec
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void monitorvms() {
			VMInstance[] myvms;
			ServiceInstance si = null;
			try {
				URL url = new URL(ServerConfig.Url);
				si = new ServiceInstance(url, ServerConfig.username, ServerConfig.password, true);
				Folder rootFolder = si.getRootFolder();
				String rootname = rootFolder.getName();
				System.out.println("\n\n\tRoot name for vCenter: " + rootname + "\n\n");
				ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
				if (mes == null || mes.length == 0) {
					System.out.println("\n\tNo Virtual machine Found !!!");
					return;
				}
				myvms = new VMInstance[mes.length];
				for (int i = 0; i < mes.length; i++) {
					VirtualMachine vm = (VirtualMachine) mes[i];
					myvms[i] = new VMInstance(vm,si);
					myvms[i].createSnapshots();
				}
			}catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}   finally{
				if(si!=null)
					si.getServerConnection().logout();
				myvms = null;
			}
			
		};
	};

	
	/*public static void main(String[] args) {
		final VMInstance[] myvms;
		ServiceInstance si = null;
		try {
			URL url = new URL(ServerConfig.Url);
			si = new ServiceInstance(url, ServerConfig.username, ServerConfig.password, true);
			Folder rootFolder = si.getRootFolder();
			String rootname = rootFolder.getName();
			System.out.println("\n\n\tRoot name for vCenter: " + rootname + "\n\n");
			ManagedEntity[] mes = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
			if (mes == null || mes.length == 0) {
				System.out.println("\n\tNo Virtual machine Found !!!");
				return;
			}
			myvms = new VMInstance[mes.length];
			for (int i = 0; i < mes.length; i++) {
				VirtualMachine vm = (VirtualMachine) mes[i];
				myvms[i] = new VMInstance(vm,si);
				//myvms[i].helloVM();
				//myvms[i].removeAlarm();
				//myvms[i].createAlarm();
			}
			
			Thread t1 = new Thread(){
				public void run() {
					try {

						while (true) {
							System.out.println("\n\n----> Monitoring !!!");
							myvms[1].monitorVM();
							Thread.sleep(1000); // wait for 5 sec
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			t1.start();			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  finally{
			if(si!=null)
				si.getServerConnection().logout();
		}
	}*/
	public static void main(String[] args) throws JMSException {
		t1.start();
		t2.start();
		Consumer c = new Consumer();
		c.startListening();
	}

}
