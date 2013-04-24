package com.futor.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import  java.io.*;  
import  java.sql.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import  org.apache.poi.hssf.usermodel.HSSFWorkbook; 
import  org.apache.poi.hssf.usermodel.HSSFRow;
import  org.apache.poi.hssf.usermodel.HSSFCell;

import com.futor.dbconnection.DbConnection;;
public class createDetail {
	   public static void main(String args[])
	   {
	   DbConnection dbConnection=new DbConnection();
	   Connection con = dbConnection.getConnection();
	   createDetail c=new createDetail();
		 try 
		 {
				String query1="create view r1 as ( select user_strand_status.uid, user_strand_status.current_topic_id, user_strand_status.strand_id, quiz_node_results.nid";
			 	       query1=query1+" from user_strand_status"; 
			 	      query1=query1+" left join quiz_node_results on user_strand_status.HW_ID = quiz_node_results.nid where user_strand_status.uid= 292 )";
               PreparedStatement p=con.prepareStatement(query1);
               p.execute();
               
               
               String query2=" create view r2 as (SELECT  DISTINCT qnra.question_nid, qnr.nid, cfc.field_complexity_value,qnra.answer_timestamp,ctm.field_maxrt_value, qnra.is_correct,qnr.time_start,qnra.number FROM quiz_node_results_answers qnra, content_type_multichoice ctm, content_field_complexity cfc, quiz_node_results qnr,quiz_node_relationship qnrs WHERE qnra.question_nid = ctm.nid AND qnra.question_nid = cfc.nid and qnra.result_id=qnr.result_id order by qnr.nid,qnra.number)";
               PreparedStatement p1=con.prepareStatement(query2);
               p1.execute();

               String query3="select r1.uid,r1.current_topic_id,r1.strand_id,r1.nid,r2.question_nid,r2.field_complexity_value,r2.time_start,r2.answer_timestamp,r2.field_maxrt_value,r2.is_correct,r2.number";
                      query3=query3+" from r1  Left join r2 on r1.nid=r2.nid order by r1.nid,r2.number ";
                      
               PreparedStatement p2=con.prepareStatement(query3);
               
               ResultSet rs= p2.executeQuery();
               
               c.actualResponseTime(rs);
               
           }
		 catch (Exception e) {
			e.printStackTrace();
		}
  }// end of main methode

	   public void actualResponseTime(ResultSet rs)
	   {
	   String filename="c:/harsh/data.xls" ;
	   HSSFWorkbook hwb=new HSSFWorkbook();
	   HSSFSheet sheet =  hwb.createSheet("new sheet");

	   HSSFRow rowhead=   sheet.createRow((short)0);
	   rowhead.createCell((short) 0).setCellValue("uid");
	   rowhead.createCell((short) 1).setCellValue("topic id");
	   rowhead.createCell((short) 2).setCellValue("strand id");
	   rowhead.createCell((short) 3).setCellValue("quiz id");
	   rowhead.createCell((short) 4).setCellValue("question id");
	   rowhead.createCell((short) 5).setCellValue("question level");
	   rowhead.createCell((short) 6).setCellValue("quiz start time");
	   rowhead.createCell((short) 7).setCellValue("answer time stamp");
	   rowhead.createCell((short) 8).setCellValue("max response time");
	   rowhead.createCell((short) 9).setCellValue("is correct");
	   rowhead.createCell((short) 10).setCellValue("sr. no in quiz");
	   rowhead.createCell((short) 11).setCellValue("actual response time");

	   int i=1;
	   int actualResponseTime;
	   int temp []= new int[2];
	   try {
	   	while(rs.next())

	   	{
	   	if(rs.getInt("number")==1)
	   	{
	   	temp[0]= rs.getInt("time_start");
	   	temp[1]= rs.getInt("answer_timestamp");
	   	actualResponseTime= temp[1]-temp[0];

	   	temp[0]=temp[1];
	   	
	   	}
	   	else
	   	{
	   		temp[1]= rs.getInt("answer_timestamp");
	   		actualResponseTime= temp[1]-temp[0];
	   		temp[0]=temp[1];
	   		
	   		
	   	}
	   	// dump a row in excel
	   	HSSFRow row=   sheet.createRow((short)i);
	   row.createCell((short) 0).setCellValue(Integer.toString(rs.getInt("uid")));
	   row.createCell((short) 1).setCellValue(Integer.toString(rs.getInt("current_topic_id")));
	   row.createCell((short) 2).setCellValue(Integer.toString(rs.getInt("strand_id")));
	   row.createCell((short) 3).setCellValue(Integer.toString(rs.getInt("nid")));
	   row.createCell((short) 4).setCellValue(Integer.toString(rs.getInt("question_nid")));
	   row.createCell((short) 5).setCellValue(rs.getString("field_complexity_value"));
	   row.createCell((short) 6).setCellValue(Integer.toString(rs.getInt("time_start")));
	   row.createCell((short) 7).setCellValue(Integer.toString(rs.getInt("answer_timestamp")));
	   row.createCell((short) 8).setCellValue(Integer.toString(rs.getInt("field_maxrt_value")));
	   row.createCell((short) 9).setCellValue(Integer.toString(rs.getInt("is_correct")));
	   row.createCell((short) 10).setCellValue(Integer.toString(rs.getInt("number")));
	   row.createCell((short) 11).setCellValue(Integer.toString(actualResponseTime));
	   i++;
	   }
	   FileOutputStream fileOut = new FileOutputStream(filename);
	   hwb.write(fileOut);
	   fileOut.close();
	   System.out.println("Your excel file has been generated!");

	   } 
	   catch (Exception e) {
	   	// TODO Auto-generated catch block
	   	e.printStackTrace();
	   }
	   }

	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   
	   

}











