package com.futor.dao;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.futor.beans.*;
import java.sql.Date;


public class RptUserPerformanceDAO {

	ArrayList getUser= null;

	public ArrayList getUsers(Connection con,Timestamp today,Timestamp yesterday)
	{ 
		ResultSet selectedUsers=null;
		 try
		{
			 getUser=new ArrayList();
		     StringBuilder getUid=new StringBuilder("select uid,course_id,category_id,batch_id,sum(ques_attempted) as ques_attempted ,sum(right_ques_attempted) as right_ques_attempted,start_time");
                           getUid.append(" from user_topic_status ");
                           getUid.append(" where row_type=0 and start_time between  ? and ? group by uid,batch_id,category_id order by uid");
             PreparedStatement  getUsers= con.prepareStatement(getUid.toString());      
                  getUsers.setTimestamp(1,yesterday);
                  getUsers.setTimestamp(2,today);
             selectedUsers =getUsers.executeQuery();
		 while(selectedUsers.next())
		 {
			 RptUserPerformanceBean oneUser=new RptUserPerformanceBean();
			 oneUser.setUser(selectedUsers.getInt("uid"));
			 oneUser.setCourseID(selectedUsers.getInt("course_id"));
			 oneUser.setBatchID(selectedUsers.getInt("batch_id"));
			 oneUser.setCategoryID(selectedUsers.getInt("category_id"));
			 oneUser.setStartTime(selectedUsers.getTimestamp("start_time"));
			 getUser.add(oneUser);
			 
		 }
		selectedUsers.close();
	}
		catch (Exception e) 
		{ 
			System.out.println("Error in selecting user query from user topic status");
	        e.printStackTrace();	
		}
	return getUser;
	}
 //2.  function to calculate caltime of category
	public double getCalTime(Connection con,int courseId,int rowType,int batchId, int categoryId)
        {
	 double categoryCalTime=0;
	 try{
	 StringBuilder getCalTimeOfCategory=new StringBuilder("select cal_time from mgc_personlized_course_info where course_id =? and row_type= ? and batch_id= ? and category_id= ?");
     PreparedStatement getCalTime = con.prepareStatement(getCalTimeOfCategory.toString());
     getCalTime.setInt(1,courseId);
	 getCalTime.setInt(2,rowType);// for category time row type is 2
	 getCalTime.setInt(3,batchId);
	 getCalTime.setInt(4,categoryId);
     
     
     ResultSet r4 = getCalTime.executeQuery();
	 r4.next();
	 categoryCalTime=r4.getDouble("cal_time");
	 
	 }
	 catch (Exception e) {
		
	}
	 return categoryCalTime;
        }
	
	

	
//3.funtion to calculate
	public ArrayList getEntryExist(Connection con,int user,int categoryId,int batchId,int courseId)
	{ ArrayList entryExist=null;
	 entryExist= new ArrayList();
	  try {
		  
	     StringBuilder getEntryExist = new StringBuilder("select on_date as on_date ,max(day_no) as day_no from rpt_user_performances");
                       getEntryExist.append(" where uid= ? and category_id = ? and batch_id = ? and course_id = ?  ");
         PreparedStatement getOnDate = con.prepareStatement(getEntryExist.toString());
         getOnDate.setInt(1,user);
         getOnDate.setInt(2,categoryId);
         getOnDate.setInt(3,batchId);
         getOnDate.setInt(4,courseId);
         ResultSet r2 = getOnDate.executeQuery();
         r2.next();
         int dayNo=r2.getInt("day_no");//day_no may b null
         System.out.println(dayNo);
         if(r2.wasNull())
         {
        	 entryExist.add(-1);
        	 entryExist.add(-1);
         }	 
         else
           {
        	 Timestamp t=r2.getTimestamp("on_date");
        	 entryExist.add(dayNo);
        	 entryExist.add(t);
        	            }
         
	  
	  }
	  catch(Exception e)
	  {   System.out.println("Error in getting data from already exist report ");
		  e.printStackTrace();
	  }
	 
     return entryExist;
	}
	//function to insert data in report table
	public void insertData(Connection con,RptUserPerformanceInsertBean beanObjectToInsert)
{
	StringBuilder insertRow= new StringBuilder("insert into rpt_user_performances (uid,course_id,batch_id,on_date,day_no,no_of_strands_completed,");
    insertRow.append("commulative_no_of_strands_completed,target_no_of_strands,commulative_target_no_of_strands_completed,category_id,category_caltime,");
    insertRow.append("no_of_questions_attempted,right_questions_attempted,time_spent_on_practice) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    try{ 
    PreparedStatement insertData = con.prepareStatement(insertRow.toString());
    insertData.setInt(1,beanObjectToInsert.getUId());
	 insertData.setInt(2,beanObjectToInsert.getCourseId());
	 insertData.setInt(3,beanObjectToInsert.getBatchId());
	 insertData.setInt(10,beanObjectToInsert.getCategoryId());
	 insertData.setDouble(11,beanObjectToInsert.getCategoryCalTime());
	 insertData.setTimestamp(4,beanObjectToInsert.getOnDate());
	 insertData.setInt(5,beanObjectToInsert.getDayNo());
	 insertData.setInt(6,beanObjectToInsert.getNoOfStrandsCompleted());
	 insertData.setInt(7,beanObjectToInsert.getCommulativeNoOfStrandsCompleted());
	 insertData.setInt(8,beanObjectToInsert.getTarget_no_of_strands());
	 insertData.setInt(9,beanObjectToInsert.getCommulative_target_no_of_strands_completed());
	 insertData.setInt(12,beanObjectToInsert.getNoOfQuestionsAttempted());
	 insertData.setInt(13,beanObjectToInsert.getRightQuestionsAttempted());
	 insertData.setLong(14,beanObjectToInsert.getTimeSpentOnPractice());
	 insertData.executeUpdate();
    
    }
    catch (Exception e)
    {
         e.printStackTrace();
	}
}
// 5 function to get strands done
	public int getStrandsDone(Connection con,Timestamp tempDate,Timestamp onDate,int user,int categoryId,int batchId,int courseId )
	{   int strandsDone=0;
		StringBuilder getStrand = new StringBuilder("select count(strand_id) as strands_done from user_strand_status where end_mastery_date is  not null and end_mastery_date between ? and ? "); 
    getStrand.append("and uid=? and category_id = ? and batch_id= ? and course_id= ?");
    try{
    PreparedStatement getStandsDone = con.prepareStatement(getStrand.toString());
    getStandsDone.setTimestamp(1,tempDate);
    getStandsDone.setTimestamp(2,onDate);
    getStandsDone.setInt(3,user);
    getStandsDone.setInt(4,categoryId);
    getStandsDone.setInt(5,batchId);
    getStandsDone.setInt(6,courseId);
    ResultSet r6 = getStandsDone.executeQuery();
    r6.next();
    strandsDone=r6.getInt("strands_done");
     }
    catch (Exception e) {
		e.printStackTrace();
	}
		
	 return strandsDone;	
		
	}
//6.
	public int getCommulativeStrandsDone(Connection con,Date currentDate,int user,int categoryId,int batchId,int courseId)	
{   
	int commulativeStrand=0;
	StringBuilder getCommulativeStrand = new StringBuilder("select max(strand_sr_no_in_category) as commulative_strand ,strand_id from mgc_personlized_course_info where strand_id in ");
    getCommulativeStrand.append( "( select strand_id from user_strand_status where end_mastery_date is  not null and end_mastery_date <=  ? and uid = ? and category_id= ? and batch_id= ? and course_id=?)");
try{
    PreparedStatement getCommulativeStrandDone = con.prepareStatement(getCommulativeStrand.toString());
	
	
	getCommulativeStrandDone.setDate(1,currentDate);//add current date
    getCommulativeStrandDone.setInt(2,user);
    getCommulativeStrandDone.setInt(3,categoryId);
    getCommulativeStrandDone.setInt(4,batchId);
    getCommulativeStrandDone.setInt(5,courseId);
    ResultSet r5 = getCommulativeStrandDone.executeQuery();
    r5.next();
    commulativeStrand=r5.getInt("commulative_strand"); 
}
catch (Exception e) {
	e.printStackTrace();
}
    return commulativeStrand;
	

}
//7. 
	public int getCommulativeTarget(Connection con,int categoryId,int batchId,int courseId,int rowType,int dayNo)
	{    int commulativeTargetStrand=0;
		StringBuilder getCommulativeTarget= new StringBuilder("select max(strand_sr_no_in_category) as commulative_target from mgc_personlized_course_info where category_id=? and batch_id=? and course_id=? and row_type=? and ideal_commulative_category_time <=?");
		 try{  
		PreparedStatement getTarget=con.prepareStatement(getCommulativeTarget.toString());
		getTarget.setInt(1,categoryId);
		   getTarget.setInt(2,batchId);
		   getTarget.setInt(3,courseId);
		   getTarget.setInt(4,0); // row type 0
		   getTarget.setDouble(5,dayNo);
		   ResultSet r7 =getTarget.executeQuery();
		   r7.next();
		   commulativeTargetStrand=r7.getInt("commulative_target"); 
		   
		 }
		 catch (Exception e) {
		  	e.printStackTrace();
		 }
		return commulativeTargetStrand;
		}
//8.
	public long getTimeSpent(Connection con,int user,Timestamp yesterday,Timestamp today )
	{   long timeSpentOnQuiz=0;
		StringBuilder getTimeSpent = new StringBuilder("select time_start,time_end from quiz_node_results where uid=? and time_start >=? and time_end <=? and time_end!=0  ");
	    try{  
		PreparedStatement getTime=con.prepareStatement(getTimeSpent.toString());	
		
		   getTime.setInt(1,user);	
		    getTime.setTimestamp(2,yesterday);
		    getTime.setTimestamp(3,today);
		    ResultSet r9 = getTime.executeQuery();
		    while(r9.next())
		    { int endTimeOfQuiz=r9.getInt("time_end");
		      int startTimeOfQuiz=r9.getInt("time_start");
		      timeSpentOnQuiz=timeSpentOnQuiz+(endTimeOfQuiz-startTimeOfQuiz);
		    }
		    
	    }
	    catch (Exception e) {
		            e.printStackTrace();
		}
	    return timeSpentOnQuiz;
	    }    
	
public Date getCurrentDate(Connection con)
     {  Date currentDate=null; 
	try{
       PreparedStatement todayDate = con.prepareStatement("select current_date as today");
 	   ResultSet r3 = todayDate.executeQuery();
 	   r3.next();
 	   currentDate = r3.getDate("today");// got current date to get commulative strands
 	   System.out.println(currentDate);
             }
     catch (Exception e) {
		e.printStackTrace();
	}
	return currentDate;
      }
	
}





