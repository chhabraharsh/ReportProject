package com.futor.beans;


import java.sql.Timestamp;

public class RptUserPerformanceBean {
 
	private int user;
	private int courseID;
	private int categoryID;
	private int batchID;
	private int questionsAttempted;
	private int rightQuestionsAttempted;
	private Timestamp startTime;
	public int getUser() {
		return user;
	}
	public void setUser(int user) {
		this.user = user;
	}
	public int getCourseID() {
		return courseID;
	}
	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}
	public int getCategoryID() {
		return categoryID;
	}
	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
	public int getBatchID() {
		return batchID;
	}
	public void setBatchID(int batchID) {
		this.batchID = batchID;
	}
	public int getQuestionsAttempted() {
		return questionsAttempted;
	}
	public void setQuestionsAttempted(int questionsAttempted) {
		this.questionsAttempted = questionsAttempted;
	}
	public int getRightQuestionsAttempted() {
		return rightQuestionsAttempted;
	}
	public void setRightQuestionsAttempted(int rightQuestionsAttempted) {
		this.rightQuestionsAttempted = rightQuestionsAttempted;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
}
