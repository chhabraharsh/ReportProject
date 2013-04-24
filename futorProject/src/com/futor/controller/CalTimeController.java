package com.futor.controller;

import com.futor.cron.*;

public class CalTimeController {
	public static void main(String [] args)
	{
		int examId = 1; // these are the inputs require from user interface
		int batchId = 1;
		int gradeId = 10;
		int subjectId = 5;
		int categoryId[] = { 27, 28, 30 };
		int courseId = 6897;
		int creatorId = 123;
		float timeDiff = 40;
		PopulateBatchCalTime pb =new PopulateBatchCalTime();
		 pb.batchCalTime(examId, batchId, gradeId, subjectId, categoryId, courseId, creatorId, timeDiff);
	}

}
