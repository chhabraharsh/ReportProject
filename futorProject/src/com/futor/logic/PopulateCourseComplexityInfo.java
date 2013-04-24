package com.futor.logic;
import com.futor.beans.*;
import com.futor.dbconnection.*;
import com.futor.dao.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
 
public class PopulateCourseComplexityInfo {
	
	

	
	 
   public static void main(String args[])
    {
	   DbConnection dbConnection=new DbConnection();
	   Connection con = dbConnection.getConnection();
    try
    {   
    	CourseComplexityInfoDAO daoObject = new CourseComplexityInfoDAO(); 
    	List courseInfo= new ArrayList();
    	courseInfo= daoObject.selectCourseInfo(con);
          
    	String truncate="TRUNCATE TABLE course_complexity_info";  // delete all rows of tables 
    	PreparedStatement psmt0=con.prepareStatement(truncate);
    	psmt0.execute();
    	
    	boolean isFirstRow = true;
    	int preTopicID =-1;
    	int preCategoryID = -1;
        int preExamId = -1;
    	int preGrade = -1;
    	int preSubjectId = -1;
    	int topicIndex=0;
    	int categoryIndex=0;
    	int topicID= 0;
    	
    	
    	
    	
    	
    	Iterator it= courseInfo.listIterator();
    	while(it.hasNext())
    	{
    		CourseComplexityInfoBean beanObject= new CourseComplexityInfoBean();
    		beanObject = (CourseComplexityInfoBean)it.next();
    		 int examID= beanObject.getExamID(); 
    		 int gradeID= beanObject.getGradeID();
    		 int subjectID= beanObject.getSubjectID();
    		 int categoryID= beanObject.getCategoryID();
    		  topicID= beanObject.getTopicID();
    		 int strandID= beanObject.getStrandID();
    	     int strandDifficultyLevel= beanObject.getStrandDifficultyLevel();
    		 int strandSrNo= beanObject.getStrandSrNo();
    		 int topicSrNo= beanObject.getTopicSrNo();
    		 String	 fieldIsLastTopicOfCategoryValue= beanObject.getFieldIsLastTopicOfCategoryValue();
    		
    		 if(isFirstRow){
     			preTopicID = topicID;
     			preCategoryID =categoryID;
     			preExamId = examID;
     			preGrade = gradeID;
     			preSubjectId = subjectID;
     			isFirstRow = false;
     		             }
    		 if(fieldIsLastTopicOfCategoryValue==null)
    		 {//should not be null
     			fieldIsLastTopicOfCategoryValue="No";
    		 }
    		 
    		 if(topicID==preTopicID)
     		{
     		 
    	      topicIndex=topicIndex+strandDifficultyLevel;	

     		}
    		 else{
     			daoObject.updateTopicIndex(topicIndex, preTopicID, con);
     			topicIndex= strandDifficultyLevel;
     		    }
    		 if(categoryID==preCategoryID && examID==preExamId && preGrade == gradeID && preSubjectId == subjectID)
     		{
     		 categoryIndex=categoryIndex+strandDifficultyLevel;	
     		}
    		 else
     		{ 
     	     daoObject.updateCategoryindex(con,categoryIndex,preCategoryID,preExamId,preGrade,preSubjectId);
     		 
     		   categoryIndex=strandDifficultyLevel;	
     		}
    		preTopicID = topicID;
 			preExamId = examID;
 			preGrade = gradeID;
 			preSubjectId = subjectID;
 			preCategoryID=categoryID;
 			
 			daoObject.insertCourseInfo(con, beanObject);
    	 	
    	
    	}
    	
    	 daoObject.updateTopicIndex(topicIndex, topicID, con);
    	 daoObject.updateCategoryindex(con, categoryIndex, preCategoryID, preExamId, preGrade, preSubjectId);
    	 con.close();
    }
    catch(Exception e)
    {
    	System.out.println("ERROR IN LOGIC CODE");
    	e.printStackTrace();
    }
   
    }
}
             
 	  
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	

 



