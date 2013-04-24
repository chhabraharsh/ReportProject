package com.futor.dao;
import java.sql.*;
public class getDetail {

public void createFirstView(Connection con)
{   
	 try{
	StringBuilder query = new StringBuilder("create view r1 as select user_strand_status.uid, user_strand_status.current_topic_id, user_strand_status.strand_id, quiz_node_results.result_id"); 
query.append(" from user_strand_status"); 
query.append("left join quiz_node_results on user_strand_status.HW_ID = quiz_node_results.nid where user_strand_status.uid= 292");

PreparedStatement  p = con.prepareStatement(query.toString());
p.executeQuery();

	 }
	 catch (Exception e) {
  System.out.print("error in ist query");
		 // TODO: handle exception
	}
}

public void createSecondView(Connection con)
{
 try{
	 StringBuilder query2 =new StringBuilder(" create view r2 as (select quiz_node_results_answers.question_nid, quiz_node_results_answers.result_id,pdfutor_proddb content_field_complexity.field_complexity_value, content_type_multichoice.field_maxrt_value, quiz_node_results_answers.is_correct"); 
	 query2.append(" from quiz_node_results_answers, content_type_multichoice, content_field_complexity");
     query2.append(" where quiz_node_results_answers.question_nid= content_type_multichoice.nid and quiz_node_results_answers.question_nid= content_field_complexity.nid)");
	 PreparedStatement p1=con.prepareStatement(query2.toString());
	 p1.executeQuery();
	 
	 
	 
 }	
catch (Exception e) {
	System.out.print("error in 2nd query");
	// TODO: handle exception
}
}













}
