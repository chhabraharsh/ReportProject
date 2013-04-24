package com.futor.cron;

import com.futor.dbconnection.*;
import java.sql.Connection;
import java.util.*;
import com.futor.dao.*;
import com.futor.beans.*;

public class PopulateBatchCalTime {
	public void batchCalTime(int examId, int batchId, int gradeId, int subjectId, int [] categoryId, int courseId, int creatorId, float timeDiff) 
	{
		DbConnection db = new DbConnection();
		Connection con = db.getConnection();
		CalTimeDBOperations c = null;
		CalTimeBean bean = null;
		ArrayList arr = new ArrayList();

		float ideaCommulativeCategoryTime = 0;

		/*int examId = 1; // these are the inputs require from user interface
		int batchId = 1;
		int gradeId = 10;
		int subjectId = 5;
		int categoryId[] = { 27, 28, 30 };
		int courseId = 6897;
		int creatorId = 123;
		float timeDiff = 40;*/

		int j = 0; // using as index of category
		int category_serial_no = 1;// it should be j+1
		int courseIndex = 0;
		int srNoOfStrandInCategory = 0;// always do increment by 1 in category
										// loop and again intialize by 0
		int srNoOfStrandInTopic = 0; // increment till one topic then initalize
										// by 1
		try {
			c = new CalTimeDBOperations();
			arr = c.selectStrandDifficultyLevel(con, examId, gradeId,
					subjectId, categoryId);
			Iterator it2 = arr.listIterator();
			while (it2.hasNext()) {
				courseIndex = courseIndex + (Integer)it2.next();
			}// now got course index
		} catch (Exception e) {

			e.printStackTrace();
		}
		float categoryTime = 0;

		// int preTopicId=-1;

		for (j = 0; j < categoryId.length; j++) // for each category
		{
			categoryTime = 0;
			srNoOfStrandInCategory = 0;
			srNoOfStrandInTopic = 0;
			arr = new ArrayList();
			arr = c.selectCourseComplexityInfo(con, examId, gradeId, subjectId,
					categoryId[j]);
			// System.out.println(categoryId[j]);
			Iterator it = arr.listIterator();
			 // 1.

			int topic_id = -1;
			float calTime = -1;

			float idealCommulativeTopicTime = 0;

			float topicTime = 0;
			boolean isFirstRow = true;
			int preTopicId = -1;
			// int preCategoryId=-1;
			int rowType = 0;
			while (it.hasNext()) {
				rowType = 0;
				bean = new CalTimeBean();

				bean = (CalTimeBean) it.next();
				int strandId = bean.getStrandId();
				int strandDifficultyLevel = bean.getStrandDifficultyLevel();

				topic_id = bean.getTopicId();
				float topic_index = bean.getTopicIndex();

				float category_index = bean.getCategoryIndex();
				String field_is_last_topic_of_category = bean
						.getFieldIsLastTopicOfCategory();

				String field_is_last_topic_of_course = "No";// make an update to
															// last topic only
															// (remaining)
				String isLastCategoryOfCourse = "No";// make an update to last
														// category only

				if (isFirstRow) {
					preTopicId = topic_id;
					isFirstRow = false;
				}

				if (j + 1 == categoryId.length) {
					isLastCategoryOfCourse = "Yes"; // instead of doing that can
													// make an update in the end

				}
				calTime = (((float) ((float) strandDifficultyLevel / (float) courseIndex)) * timeDiff);
				categoryTime = categoryTime + calTime;
				bean.setIsLastCategoryOfCourse(isLastCategoryOfCourse);
				bean.setCreatorId(creatorId);
				bean.setCourseId(courseId);
				bean.setCounter(j + 1);
				bean.setCategoryId(categoryId[j]);
				bean.setCalTime(calTime);
				bean.setRowType(rowType);
				float calTimeTemp = calTime;
				// c.setPstmtForInsertion(con);
				// c.insertInMGC_Personalised(bean);
				c.insertInMGC_Personalised(con, bean);

				// c.insertInMGC_Personalised(con, isLastCategoryOfCourse,
				// creatorId, j+1, calTime, rowType );
				if (topic_id == preTopicId) {

					System.out
							.println("topic_id and preTopicId IS SAME topic_id ");
					topicTime = topicTime + calTime;
					// idealCommulativeTopicTime=idealCommulativeTopicTime+cal_time;
					srNoOfStrandInTopic++;
					bean.setSrNoOfStrandInTopic(srNoOfStrandInTopic);

				} else {
					System.out.println("topic_id and preTopicId IS NOT SAME ");

					rowType = 1; // need to set all strand parameter at -1 make
									// insert and then rowtype to 0
					srNoOfStrandInTopic = -1;// make to 1
					bean.setRowType(rowType);
					bean.setTopicTime(topicTime);
					bean.setSrNoOfStrandInTopic(srNoOfStrandInTopic);
					bean.setCategoryTime(categoryTime);

					c.insertInMGC_Personalised(bean, preTopicId);// overloaded
																	// function
																	// insertInMGC_Personalised

					// pstmt1.addBatch();// one row inserted for topic level
					// ideaCommulativeCategoryTime=calTime;
					// System.out.println(idealCommulativeTopicTime);

					// System.out.println("Rowtye"+rowType);
					// rowType=0;
					// System.out.println("Rowtye"+rowType);
					// cal_time=calTimeTemp;

					topicTime = calTimeTemp;
					ideaCommulativeCategoryTime = calTimeTemp;
					srNoOfStrandInTopic = 1;
					bean.setSrNoOfStrandInTopic(srNoOfStrandInTopic);

				}
				rowType = 0;
				preTopicId = topic_id;
				bean.setTopicTime(topicTime);
				bean.setCategoryTime(categoryTime);
				++srNoOfStrandInCategory;
				bean.setSrNoOfStrandInCategory(srNoOfStrandInCategory);
				// ideaCommulativeCategoryTime=calTimeTemp;
				c.insertInMGC_Personalised2(bean);
			} // all strands of category inserted

			rowType = 1;
			bean.setRowType(rowType);// this will work always for last topic of
										// category whether your data
			srNoOfStrandInTopic = -1; // correct or not
			bean.setSrNoOfStrandInTopic(srNoOfStrandInTopic);
			srNoOfStrandInCategory = -1;
			bean.setSrNoOfStrandInCategory(srNoOfStrandInCategory);
			bean.setCalTime(topicTime);
			c.insertInMGC_Personalised3(bean, courseId);

			categoryTime = 0;

		}
	}

	
}
