package com.futor.cron;
import com.futor.cron.*;
import com.futor.utils.AlarmEntry;
import com.futor.utils.AlarmListener;
import com.futor.utils.AlarmManager;

public class CroneCycle {
	
	public static void main(String args[]){
		try{
		
	AlarmManager mgr = new AlarmManager();
	int minute =4;
	int hour=-1;
	int dayOfMonth=-1;
	 int month=-1;
	 int dayOfWeek=-1;
	 int year=-1;
	 System.out.println("mgr ................... ");
	mgr.addAlarm(minute, hour, dayOfMonth, month, dayOfWeek, year, new AlarmListener()
	{
	                            public void handleAlarm(AlarmEntry entry) 
	                            {
	                            	/* place your code inside this block
	                                   or call your function here to which you want to execute                                      
	                                 */
	
	                          }
	});
		}catch(Exception exp){
			System.out.println("EXCEPTION " + exp);
			
		}
		
		System.out.println("TERMINATED  .... ");
	

	}
}


