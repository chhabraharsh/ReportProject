package com.futor.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLException;
import com.futor.dbconnection.*;
import com.futor.beans.*;
import java.util.*;
import com.futor.beans.*;

public class CourseComplexityInfoDAO {

	List courseInfo= null;
	
	public List selectCourseInfo(Connection con)
{   
	ResultSet selectedInfo=null;
	
    
	try
	{      courseInfo= new ArrayList();
		   String sql="SELECT cfe.field_exam_value,cfg.field_grade_value,cfs.field_subject_value,cfc.field_category_value  as category_id,";
	    	sql=sql +"cft.field_topic_nid as topic_id,ctt.field_tcode_value as topic_code,cts.nid as strand_id,ctt.field_is_last_topic_of_category_value as is_last_topic ,";
	    	sql=sql	+"ctt.field_sno_value as topic_sr_no,cts.field_strand_difficulty_value,cts.field_strand_sno_value FROM content_type_strands cts, node n,content_field_topic cft,";
	    	sql=sql	+"content_type_topic ctt,content_field_category cfc,content_field_exam cfe,content_field_grade cfg,content_field_subject cfs WHERE n.nid = cts.nid ";
	    	sql=sql	+"and cft.nid = n.nid and cft.field_topic_nid and ctt.nid = cft.field_topic_nid and cfc.nid = ctt.nid and cfe.nid = ctt.nid and cfg.nid = ctt.nid ";
	    	sql=sql	+"and cfs.nid = ctt.nid "  ;
	    	sql=sql	+"and field_strand_difficulty_value is not NULL ";
	    	sql=sql	+"order by cfe.field_exam_value,cfg.field_grade_value,cfs.field_subject_value,";
	    	sql=sql	+"cfc.field_category_value,cft.field_topic_nid asc";
	    	
	    	PreparedStatement pstmt = con.prepareStatement(sql);
	        selectedInfo =pstmt.executeQuery();
	        int finalLevel=0;
	        
	        
	        while(selectedInfo.next())
	        {
	        	CourseComplexityInfoBean oneInfo = new CourseComplexityInfoBean();
	        	
	        	oneInfo.setExamID(selectedInfo.getInt("field_exam_value"));
	 		    oneInfo.setGradeID(selectedInfo.getInt("field_grade_value"));
	 		    oneInfo.setSubjectID(selectedInfo.getInt("field_subject_value")); 
	 		    oneInfo.setCategoryID(selectedInfo.getInt("category_id")); 
	 		    oneInfo.setTopicID(selectedInfo.getInt("topic_id")); 
	 		    oneInfo.setStrandID(selectedInfo.getInt("strand_id")); 
	 		    String level=selectedInfo.getString("field_strand_difficulty_value");
	 		      if(level.equals("L1"))
	 		      {
	 		    	 finalLevel=1;
	 		    	  
	 		      }
	 		      else if(level.equals("L2"))
	 		      {
	 		    	 finalLevel=2;
	 		      }
	 		      else if(level.equals("L2"))
	 		      {
	 		    	 finalLevel=3;
	 		      }
	        	oneInfo.setStrandDifficultyLevel(finalLevel);
	 		    oneInfo.setFieldIsLastTopicOfCategoryValue(selectedInfo.getString("is_last_topic"));
	            oneInfo.setStrandSrNo(selectedInfo.getInt("field_strand_sno_value"));
	 		    oneInfo.setTopicSrNo(selectedInfo.getInt("topic_sr_no"));
	            oneInfo.setTopicCode(selectedInfo.getString("topic_code"));
	            courseInfo.add(oneInfo);
	        
	        
	        }
	    	
	selectedInfo.close();
	}
	catch(SQLException e)
	{
	    System.out.println("Error in selection query");
		e.printStackTrace();	
	}
  return courseInfo;

}

 public void updateCategoryindex(Connection con,int categoryIndex,int preCategoryID,int preExamId,int preGrade,int preSubjectId)
 {  try{
	 String updateCategoryIndex="UPDATE course_complexity_info  SET category_index = ? where category_id = ? and exam_id =? and grade_id = ? and subject_id = ? ";
	 PreparedStatement pstmt3 = con.prepareStatement(updateCategoryIndex);
	   pstmt3.setInt(1,categoryIndex);
	   pstmt3.setInt(2,preCategoryID);
	   pstmt3.setInt(3,preExamId);
	   pstmt3.setInt(4,preGrade);
	   pstmt3.setInt(5,preSubjectId);
	   pstmt3.executeUpdate();
 
 
         }
    catch(SQLException e)
     {
	   System.out.println("Error in update category index query");
	   e.printStackTrace();
     }
 }


public void updateTopicIndex(int topicIndex, int preTopicID, Connection con)
{
	String updateTopicIndex =  "UPDATE course_complexity_info SET topic_index = ? where topic_id = ?";
	try {
		PreparedStatement pstmt2 = con.prepareStatement(updateTopicIndex);
		pstmt2.setInt(1,topicIndex);
		pstmt2.setInt(2,preTopicID);
		pstmt2.executeUpdate();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void insertCourseInfo(Connection con,CourseComplexityInfoBean beanObject )
{
	String sql1 = "INSERT INTO course_complexity_info(exam_id,grade_id,subject_id,category_id,topic_id,strand_id,category_index,topic_index,strand_difficulty_level,field_is_last_topic_of_category_value " ;
	sql1 = sql1 + ",strand_sr_no,topic_sr_no) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	try {
		PreparedStatement pstmt1 = con.prepareStatement(sql1);
		pstmt1.setInt(1,beanObject.getExamID());
		pstmt1.setInt(2,beanObject.getGradeID());
		pstmt1.setInt(3,beanObject.getSubjectID());
		pstmt1.setInt(4,beanObject.getCategoryID());
		pstmt1.setInt(5,beanObject.getTopicID());
		pstmt1.setInt(6,beanObject.getStrandID());
		pstmt1.setInt(7,-1);
		pstmt1.setInt(8,-1);
		pstmt1.setInt(9,beanObject.getStrandDifficultyLevel());
		pstmt1.setString(10,beanObject.getFieldIsLastTopicOfCategoryValue());
		pstmt1.setInt(11,beanObject.getStrandSrNo());
		pstmt1.setInt(12,beanObject.getTopicSrNo());
		pstmt1.executeUpdate();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	
	
}

}





















