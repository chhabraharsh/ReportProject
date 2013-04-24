package com.futor.beans;

import java.sql.Timestamp;

public class RptUserPerformanceInsertBean {
 
	 private int uId;
	 private int courseId;
	 private int batchId;
	 private Timestamp onDate;
	 private int dayNo;
	 private int noOfStrandsCompleted;
	 private int commulativeNoOfStrandsCompleted;
	 private int commulative_target_no_of_strands_completed;
     private int target_no_of_strands;
     private int categoryId;
     private double categoryCalTime;
     private int noOfQuestionsAttempted;
     private int rightQuestionsAttempted;
     private long timeSpentOnPractice;
	public int getUId() {
		return uId;
	}
	public void setUId(int id) {
		uId = id;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public int getBatchId() {
		return batchId;
	}
	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}
	public Timestamp getOnDate() {
		return onDate;
	}
	public void setOnDate(Timestamp onDate) {
		this.onDate = onDate;
	}
	public int getDayNo() {
		return dayNo;
	}
	public void setDayNo(int dayNo) {
		this.dayNo = dayNo;
	}
	public int getNoOfStrandsCompleted() {
		return noOfStrandsCompleted;
	}
	public void setNoOfStrandsCompleted(int noOfStrandsCompleted) {
		this.noOfStrandsCompleted = noOfStrandsCompleted;
	}
	public int getCommulativeNoOfStrandsCompleted() {
		return commulativeNoOfStrandsCompleted;
	}
	public void setCommulativeNoOfStrandsCompleted(
			int commulativeNoOfStrandsCompleted) {
		this.commulativeNoOfStrandsCompleted = commulativeNoOfStrandsCompleted;
	}
	public int getCommulative_target_no_of_strands_completed() {
		return commulative_target_no_of_strands_completed;
	}
	public void setCommulative_target_no_of_strands_completed(
			int commulative_target_no_of_strands_completed) {
		this.commulative_target_no_of_strands_completed = commulative_target_no_of_strands_completed;
	}
	public int getTarget_no_of_strands() {
		return target_no_of_strands;
	}
	public void setTarget_no_of_strands(int target_no_of_strands) {
		this.target_no_of_strands = target_no_of_strands;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public double getCategoryCalTime() {
		return categoryCalTime;
	}
	public void setCategoryCalTime(double categoryCalTime) {
		this.categoryCalTime = categoryCalTime;
	}
	public int getNoOfQuestionsAttempted() {
		return noOfQuestionsAttempted;
	}
	public void setNoOfQuestionsAttempted(int noOfQuestionsAttempted) {
		this.noOfQuestionsAttempted = noOfQuestionsAttempted;
	}
	public int getRightQuestionsAttempted() {
		return rightQuestionsAttempted;
	}
	public void setRightQuestionsAttempted(int rightQuestionsAttempted) {
		this.rightQuestionsAttempted = rightQuestionsAttempted;
	}
	public long getTimeSpentOnPractice() {
		return timeSpentOnPractice;
	}
	public void setTimeSpentOnPractice(long timeSpentOnPractice) {
		this.timeSpentOnPractice = timeSpentOnPractice;
	}




}
