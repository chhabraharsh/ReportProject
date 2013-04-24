package com.futor.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import com.futor.beans.*;

public class CalTimeDBOperations {
	CalTimeBean bean= new CalTimeBean();
	ArrayList arr= new ArrayList();
	ResultSet r1= null;
	PreparedStatement pstmt1 = null;
	 String sql1 = "INSERT INTO mgc_personlized_course_info(course_id,category_id,topic_id,field_is_last_topic_of_category, " ;
	
	public ArrayList  selectStrandDifficultyLevel(Connection con, int examId, int gradeId, int subjectId, int categoryId[])
	{
	
	String courseWeight="select sum(strand_difficulty_level) as sum from course_complexity_info where exam_id= ? and grade_id=? " +
            " and subject_id = ? and category_id= ?"; 

try {
	PreparedStatement getCategoryIndex=con.prepareStatement(courseWeight);
	getCategoryIndex.setInt(1,examId);
	getCategoryIndex.setInt(2,gradeId);
	getCategoryIndex.setInt(3,subjectId); 
	for(int i =0;i<categoryId.length;i++)
	{ getCategoryIndex.setInt(4,categoryId[i]);
	 r1=getCategoryIndex.executeQuery();
	while(r1.next())
	{
	arr.add(r1.getInt("sum"));	
	}	
	}
	r1.close();
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
return arr;


/* 
* this loop code result course index for those categories are involved in created course
* 
*/



} 
public ArrayList selectCourseComplexityInfo(Connection con, int examId, int gradeId, int subjectId, int categoryId) // contains query to select topicid, strandid, etc from course complexity info
{
	String sql= "select topic_sr_no,topic_id,strand_sr_no,strand_id,strand_difficulty_level,topic_index,category_index,field_is_last_topic_of_category_value from course_complexity_info " ;
    sql= sql +	"where exam_id = ? and grade_id = ? and subject_id = ? and category_id = ? order by topic_sr_no,strand_sr_no asc";
    arr= new ArrayList();
    
	try
	{
 PreparedStatement pstmt = 	con.prepareStatement(sql);
 pstmt.setInt(1,examId);
 pstmt.setInt(2,gradeId);
 pstmt.setInt(3,subjectId);
 pstmt.setInt(4,categoryId);//variable as many categories are involved
 
 ResultSet r=pstmt.executeQuery(); 	
 while(r.next())
 {
	 bean= new CalTimeBean();
bean.setTopicId(r.getInt("topic_id"));	 
bean.setTopicSrNo(r.getInt("topic_sr_no")); 
bean.setStrandSrNo(r.getInt("strand_sr_no"));	 
bean.setStrandDifficultyLevel(r.getInt("strand_difficulty_level"));	 
bean.setTopicIndex(r.getInt("topic_index"));	 
bean.setCategoryIndex(r.getInt("category_index"));	 
//bean.setCategoryId(categoryId);
bean.setStrandId(r.getInt("strand_id"));	 
bean.setFieldIsLastTopicOfCategory(r.getString("field_is_last_topic_of_category_value") );
arr.add(bean);


 }
 r.close();
}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
	return arr;
	}

/* public void setPstmtForInsertion(Connection con)
 {
	 try {
		pstmt1 = con.prepareStatement(sql1);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }*/
public void insertInMGC_Personalised(Connection con, CalTimeBean bean)
{
	
    String sql1 = "INSERT INTO mgc_personlized_course_info(course_id,category_id,topic_id,field_is_last_topic_of_category, " ;
    sql1 = sql1 + "is_last_category_of_course,creator_id,category_serial_no,cal_time,row_type,strand_sr_no_in_topic,strand_sr_no_in_category,strand_id,ideal_commulative_topic_time,ideal_commulative_category_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    try {
		pstmt1 = con.prepareStatement(sql1);
		 pstmt1.setInt(1,bean.getCourseId());
	        pstmt1.setInt(2, bean.getCategoryId());
	        pstmt1.setInt(3,bean.getTopicId());
	        pstmt1.setString(4,bean.getFieldIsLastTopicOfCategory());
	        pstmt1.setString(5, bean.getIsLastCategoryOfCourse()); 
	        pstmt1.setInt(6,bean.getCreatorId());
	        pstmt1.setInt(7,bean.getCounter()); //category serial no.
	    	
	    	
	    	pstmt1.setFloat(8,bean.getCalTime());
	        pstmt1.setLong(9,bean.getRowType());
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

public void insertInMGC_Personalised( CalTimeBean bean, int preTopicId)
{
	
	try
	{
		
	
	pstmt1.setInt(3,preTopicId);
	   pstmt1.setFloat(8,bean.getTopicTime());
	   pstmt1.setLong(9,bean.getRowType());
	   pstmt1.setLong(10,bean.getSrNoOfStrandInTopic());
	   pstmt1.setLong(11,-1);
	   pstmt1.setLong(12,-1);
	   pstmt1.setFloat(13,bean.getTopicTime());
	   pstmt1.setFloat(14,(bean.getCategoryTime()- bean.getCalTime()));
	   pstmt1.executeUpdate();
	   bean.setRowType(0);
	  pstmt1.setInt(3,bean.getTopicId());
	   
}
	catch(SQLException e)
	{
		e.printStackTrace();
	}

}

public void insertInMGC_Personalised2( CalTimeBean bean)
{
	try
	{
	
	 pstmt1.setFloat(8,bean.getCalTime());
	    pstmt1.setLong(9,bean.getRowType());
	    pstmt1.setLong(10,bean.getSrNoOfStrandInTopic());
	    int temp= bean.getSrNoOfStrandInCategory();
	    //++temp;
	   bean.setSrNoOfStrandInCategory(temp);
	    pstmt1.setLong(11,bean.getSrNoOfStrandInCategory());
	    pstmt1.setInt(12,bean.getStrandId());
	    pstmt1.setFloat(13,bean.getTopicTime());
	    pstmt1.setFloat(14,bean.getCategoryTime());
	    pstmt1.executeUpdate();  // adding row for strand level  
	}
	catch(SQLException e )
	{
		e.printStackTrace();
	}
}

public void insertInMGC_Personalised3( CalTimeBean bean, int courseId)
{
	try {
		 pstmt1.setInt(3,bean.getTopicId());
	     pstmt1.setFloat(8,bean.getCalTime());
		 pstmt1.setLong(9,bean.getRowType());
		 pstmt1.setLong(10,bean.getSrNoOfStrandInTopic());
		 pstmt1.setLong(11,-1);
		 pstmt1.setLong(12,-1);
		 pstmt1.setFloat(13,bean.getTopicTime());
		 pstmt1.setFloat(14,bean.getCategoryTime());
		 pstmt1.executeUpdate();
		 pstmt1.setInt(1, courseId);
		    pstmt1.setInt(3,-1);//topic id -1 for category
		    pstmt1.setFloat(8,bean.getCategoryTime());
		    pstmt1.setLong(9,2); //here rowType is 2 representing category level
		    //bean.setSrNoOfStrandInTopic(0);
		    pstmt1.setLong(10,-1);
		    pstmt1.setLong(11,-1);// all field related to strands at 0
		    pstmt1.setLong(12,-1);
		    pstmt1.setFloat(13,-1);
		    pstmt1.setFloat(14,bean.getCategoryTime());
		    pstmt1.addBatch();  
		    pstmt1.executeBatch();
	} catch (SQLException e) {
		
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

}

