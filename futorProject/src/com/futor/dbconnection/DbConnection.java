package com.futor.dbconnection;
import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
		public Connection con;
		public Connection getConnection()
		
		 {    
			try{ 
				
				Class.forName("com.mysql.jdbc.Driver");
				
	            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/futortables?user=root&password=");
	            
			}
	        catch(Exception e)
	       {  e.printStackTrace();
	  	      System.out.print("error in making connection "+e);

	        }       
		    return con;
		 } 
		
	}

