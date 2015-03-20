package com.test;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import com.inter.client.ClientRemote;
import com.inter.client.ClientRemoteSens;
import com.interf.test.Constant;


class Semi extends Thread {
	
	String a_query_temp;
	ClientRemote remoteDe;
	ClientRemoteSens remote_sens;

	Server_Impl impl = new Server_Impl();
	Thread t1;
	
	/* registry created, at the motion sensor, which has query state method, 
	 * is being looked up and used here in the Server class
	 * */
	Semi(int a) throws AccessException, RemoteException, NotBoundException{
		Registry sens_register = LocateRegistry.getRegistry("localhost", 2007);
		remote_sens = (ClientRemoteSens) sens_register.lookup("localhost");		
	}
	
	/* Registers created at both the clients motion sensor and outlet device
	 * is being looked up here. A thread is started which  
	 * */
	Semi() throws RemoteException, NotBoundException
	{
		Registry sens_register = LocateRegistry.getRegistry("localhost", 2007);
		remote_sens = (ClientRemoteSens) sens_register.lookup("localhost");
		
		Registry dev_register=LocateRegistry.getRegistry("localhost", 2005); 
		remoteDe = (ClientRemote) (dev_register.lookup("localhost"));


		 t1 = new Thread(this,"3");		
		 t1.start();	
	 	
	}
	
	//logic for task-1 goes here
	 public void run() {
			// TODO Auto-generated method stub
			
			boolean running = true;
	if(t1.getName().equals("3")){
	
			while(running){
			try {
				
				a_query_temp = remote_sens.query_state(1);
			
			int cntTemp = Integer.parseInt(a_query_temp); 
			
			if(cntTemp < 1)			
					remoteDe.change_state(3, "ON");
				
			else if(cntTemp > 2)
				remoteDe.change_state(3, "OFF");
			
			System.out.println("After the change(if any), Now, Outlet device is " + remote_sens.query_state(3) + "\n");
			
			} catch (RemoteException e) {			
					e.printStackTrace();
				}		
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
			if (Thread.interrupted()) 
	            return;
			}		
		}
	 }
	 
		//look up the registry created by the motion sensor client 
	public void some_report_method() throws RemoteException, NotBoundException {
		Registry sens_register = LocateRegistry.getRegistry("localhost", Constant.RMI_port_dev2); 
		ClientRemoteSens remote_sens = (ClientRemoteSens) sens_register.lookup(Constant.RMI_ID);
	}
	
	//change_mode method
	public void change_mode(String mode){
		Scanner in=new Scanner(System.in);
		mode=in.nextLine();
		if(mode.equalsIgnoreCase("home")){
			impl.trd.interrupt();		
		}
	}
}

public  class RMIServer {	

	public static void main(String[] args) throws RemoteException, java.rmi.AlreadyBoundException, NotBoundException {

		Server_Impl impl = new Server_Impl();
		Registry registr = LocateRegistry.createRegistry(Constant.RMI_port);
		registr.bind(Constant.RMI_ID, impl);
				
		System.out.println("Server is started\n");
	}
}


