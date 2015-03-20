package com.test;

 

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.interf.test.TestRemote;

public class Server_Impl extends UnicastRemoteObject implements TestRemote, Runnable {
		
	private static final long serialVersionUID = 1L;
	public static int p,q,r=3,s;
	public static int a;
	public String cnt_motion_state="";
	public String prev_motion_state="OFF ";
	public String next_motion_state;
	public boolean report_running;
	public String to_report_on;
	public String to_report_off;

	//String test_input = "/home/rufina/Desktop/test-input.csv";
	Semi semi;
	
	Thread trd = new Thread(this);
	
	
	protected Server_Impl() throws RemoteException {
		super();
	}
		

	
	//register method- all registers are given global ID
	public int register(String type,String name) throws RemoteException, NotBoundException
	{
		if(type.equalsIgnoreCase("sensor") && (name.equalsIgnoreCase("temperature")))
			return p=1;
		
		//a thread is started to notify when motion is started
		if(type.equalsIgnoreCase("sensor") && (name.equalsIgnoreCase("motion"))){
			q=2;

			semi = new Semi(q);
			return q;
		}
		
		//instantiating semi class(which is in RMI Server) to signal server that client side registry has been created
		if(type.equalsIgnoreCase("device") && name.equalsIgnoreCase("outlet")){
			r=3;
			semi = new Semi();
			trd.start(); 				
			return r;
		}
		if(type.equalsIgnoreCase("device") && (name.equalsIgnoreCase("bulb")))			
			return s=4;
						
		return 0;
	}
	
	//task-2 logic goes here
	public void run() {	
		try {
			cnt_motion_state = report_state(2,"");                       //first push notification                       
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(report_running = true){
			try {
				if(cnt_motion_state.equalsIgnoreCase("yes")){              //if motion is sensed 
					to_report_on="on";  
					System.out.println("Sensed motion? "+ cnt_motion_state);
					System.out.println("BULB is " + to_report_on);					                    
					semi.remoteDe.change_state(4,"ON");  				//the bulb is ON
					System.out.println("ALERT-----INTRUDER");
				}
				
				if(prev_motion_state.equalsIgnoreCase(cnt_motion_state))
					report_state(2,"Do Nothing");
				cnt_motion_state = report_state(2,"");
				if(cnt_motion_state.equalsIgnoreCase("no")){            // if there is no motion, wait for 5 minutes 
					try {
						Thread.sleep(300000);
					} catch (InterruptedException e) {			
						e.printStackTrace();
					}
				}				
				cnt_motion_state = semi.remote_sens.query_state(2);          //query the motion sensor after 5 minutes
				if(cnt_motion_state.equalsIgnoreCase("no"))   {              //if currently there is NO motion, OFF the bulb 
					semi.remoteDe.change_state(4,"OFF");	
					System.out.println("Sensed motion? "+ cnt_motion_state + "\n");
					System.out.println("BULB is " + to_report_on + "\n");
					prev_motion_state=cnt_motion_state;  
				}	
				to_report_on="off";	                                         //report that bulb is off
				                        //in the next iteration previous motion is assigned current iteration's motion state
				
			} catch (RemoteException e) {			
				e.printStackTrace();
			}
			try {
				trd.sleep(100000);
			} catch (InterruptedException e) {			
				e.printStackTrace();
			}
			if (Thread.interrupted()) 
	            return;			
		}
	}

	//report method
	public String report_state(int id, String state) throws RemoteException {
		// TODO Auto-generated method stub
		if(id==2 && state.equals("")){
			if(semi.remote_sens.query_state(2).equalsIgnoreCase("YES"))
				return "yes";
			else return "no";
		}
		if(id==2 && state.equalsIgnoreCase("Do Nothing"))
			return " ";
		
		if(id==2 && state.equalsIgnoreCase(cnt_motion_state))
			return cnt_motion_state;
		
		return " ";
	}
	
	
	
}
	
	



