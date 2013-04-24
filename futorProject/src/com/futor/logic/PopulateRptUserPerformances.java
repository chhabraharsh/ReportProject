package com.futor.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.sql.Date; 
import com.futor.beans.*;

import com.futor.dao.RptUserPerformanceDAO;
import com.futor.dbconnection.*;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;

public class PopulateRptUserPerformances {

     public static void main(String args[])
     {  int dayNo=0;
    	Timestamp onDate=null;
    	int commulativeStrand=0;
    	int strandsDone=0;
        int commulativeTargetStrand=0;
        int preCommulativeTarget=0;
        int targetStrands=0;
        Date currentDate=new Date(1); 
       try
       {    
    	   double categoryCalTime=0;
    	  
    	   
    	  
             
    	   
    	   
    	   
    	    Calendar todayCal= Calendar.getInstance();
    	    todayCal.add(Calendar.DATE,-7);
    	    todayCal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
    	    todayCal.set(Calendar.MINUTE, 0);                 // set minute in hour
    		todayCal.set(Calendar.SECOND, 0);                 // set second in minute
    		todayCal.set(Calendar.MILLISECOND, 0);
    	    
    	    Timestamp today= new Timestamp(todayCal.getTimeInMillis());
    	  
    	    
    	    
    	    System.out.println(today.toString());
    		
    		Calendar yesterdayCal= Calendar.getInstance();
    		yesterdayCal.add(Calendar.DATE, -8);
    		yesterdayCal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
    		yesterdayCal.set(Calendar.MINUTE, 0);                 // set minute in hour
    		yesterdayCal.set(Calendar.SECOND, 0);                 // set second in minute
    		yesterdayCal.set(Calendar.MILLISECOND, 0);
    		Timestamp yesterday= new Timestamp(yesterdayCal.getTimeInMillis());
    		System.out.println(yesterday.toString());
    	     // here we got current date and yerterday date at midnight. 	 
     
    		DbConnection dbConnection=new DbConnection();
    		Connection con = dbConnection.getConnection();
             
    		RptUserPerformanceDAO daoObject = new RptUserPerformanceDAO();
    		
    		ArrayList userInList = new ArrayList();
    		userInList = daoObject.getUsers(con, today, yesterday);
          
    		 
          	currentDate=daoObject.getCurrentDate(con);
    		
    		
    		
    		
    		Iterator it = userInList.iterator();
    		while(it.hasNext())
    		    {
    			RptUserPerformanceBean beanObject=new RptUserPerformanceBean();
    			beanObject =(RptUserPerformanceBean)it.next();
    			
    			RptUserPerformanceInsertBean beanObjectToInsert = new RptUserPerformanceInsertBean();
    			
    			
    			
    			int currentUser=beanObject.getUser();
    			if(currentUser==292)//3
    			{
    			int courseID=beanObject.getCourseID();
    			int batchID=beanObject.getBatchID();
    			int categoryID=beanObject.getCategoryID();
    			int questionsAttempted=beanObject.getQuestionsAttempted();
    			int rightQuestionsAttempted=beanObject.getRightQuestionsAttempted();
    			Timestamp startTime=beanObject.getStartTime();
    			
    	
                 beanObjectToInsert.setUId(currentUser);
    	    	 beanObjectToInsert.setCourseId(courseID);
    	    	 beanObjectToInsert.setBatchId(batchID);
    	    	 beanObjectToInsert.setCategoryId(categoryID);  
    				
    			 categoryCalTime= daoObject.getCalTime(con, courseID, 2, batchID, categoryID);
    			
    			 beanObjectToInsert.setCategoryCalTime(categoryCalTime);//setting 11th parameter
    			
    			 boolean isRowExists = true;
    			 ArrayList userAlready = new ArrayList();
    			 userAlready=daoObject.getEntryExist(con, currentUser, categoryID, batchID, courseID);
                 if((Integer)userAlready.get(0)==-1)
                 {
                	onDate=startTime;
                	isRowExists = false;
                	dayNo=0;
                
               	 beanObjectToInsert.setOnDate(onDate);
               	 beanObjectToInsert.setDayNo(dayNo);
               	 beanObjectToInsert.setNoOfStrandsCompleted(0);
               	 beanObjectToInsert.setCommulativeNoOfStrandsCompleted(0);
               	 beanObjectToInsert.setTarget_no_of_strands(0);
               	 beanObjectToInsert.setCommulative_target_no_of_strands_completed(0);
               	 beanObjectToInsert.setNoOfQuestionsAttempted(0);
               	 beanObjectToInsert.setRightQuestionsAttempted(0);
               	 beanObjectToInsert.setTimeSpentOnPractice(0);
               	   
               	 daoObject.insertData(con,beanObjectToInsert);  
               	 
               	userAlready=daoObject.getEntryExist(con, currentUser, categoryID, batchID, courseID);
               	  } 
                 if(!isRowExists)
                 {
               	 
                 }
                 else
                 {
               	 
           	     onDate=(Timestamp)userAlready.get(1);
           	     dayNo=(Integer)userAlready.get(0);
                 }
               	 
                 Timestamp tempDate = onDate;
                 if(dayNo!=0)
           	  {
           		  Calendar c3 = Calendar.getInstance();
           		  c3.setTime(onDate);
           		  c3.add(Calendar.DATE, 1);  // number of days to add
           	      onDate=new Timestamp(c3.getTimeInMillis());
           		
           	  }
                 else
                 {
                	 
                 }
       // add a funtion to add current date
                
                 commulativeStrand= daoObject.getCommulativeStrandsDone(con, currentDate, currentUser, categoryID, batchID, courseID);  
                 
                 strandsDone=daoObject.getStrandsDone(con, tempDate, onDate, currentUser, categoryID, batchID, courseID);
                 
                 int rowType=0;
                 commulativeTargetStrand=daoObject.getCommulativeTarget(con, categoryID, batchID, courseID, rowType, dayNo+1);
                 
                 preCommulativeTarget=daoObject.getCommulativeTarget(con, categoryID, batchID, courseID, rowType, dayNo);
                 
                 targetStrands=commulativeTargetStrand-preCommulativeTarget;
                  long timeSpentOnQuiz=0;
                  
                  timeSpentOnQuiz=daoObject.getTimeSpent(con, currentUser, yesterday, today);
                 
                     beanObjectToInsert.setOnDate(onDate);
                	 beanObjectToInsert.setDayNo((dayNo+1));
                	 beanObjectToInsert.setNoOfStrandsCompleted(strandsDone);
                	 beanObjectToInsert.setCommulativeNoOfStrandsCompleted(commulativeStrand);
                	 beanObjectToInsert.setTarget_no_of_strands(targetStrands);
                	 beanObjectToInsert.setCommulative_target_no_of_strands_completed(commulativeTargetStrand);
                	 beanObjectToInsert.setNoOfQuestionsAttempted(questionsAttempted);
                	 beanObjectToInsert.setRightQuestionsAttempted(rightQuestionsAttempted);
                	 beanObjectToInsert.setTimeSpentOnPractice(timeSpentOnQuiz);

                	 daoObject.insertData(con,beanObjectToInsert);  

                 
               	   
                	 
    		    }
    			 

    			
    				 
    			 
    			
    	     	}
     
     
     
     
       }
       catch (Exception e) {
	e.printStackTrace();
	}
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     }
























}
