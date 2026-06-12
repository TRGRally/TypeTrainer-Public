package com.example.typetrainer.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.typetrainer.data.models.QuestionEntity
import com.example.typetrainer.data.models.QuestionTypeRelationEntity
import com.example.typetrainer.data.models.QuizEntity
import com.example.typetrainer.data.models.QuizQuestionJoinEntity
import com.example.typetrainer.data.models.TypeAccuracy
import com.example.typetrainer.data.models.UserAnswerEntity

@Dao
interface QuizDao {

    // QuizEntity-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuiz(quizEntity: QuizEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuizQuestionJoins(quizQuestions: List<QuizQuestionJoinEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(questionEntities: List<QuestionEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM quiz_question_join_table WHERE quizId = :quizId")
    fun getNumberOfQuestionsForQuiz(quizId: Long): Int

    @Query("""
        SELECT COUNT(*) FROM user_answer_table 
        WHERE quizQuestionId IN (SELECT id FROM quiz_question_join_table WHERE quizId = :quizId) 
        AND isCorrect = 1
    """)
    fun getNumberOfCorrectAnswersForQuiz(quizId: Long): Int

    @Query("SELECT * FROM quiz_table WHERE id = :quizId")
    fun getQuizEntityById(quizId: Long): LiveData<QuizEntity>

    @Query("SELECT * FROM quiz_table")
    fun getAllQuizzes(): LiveData<List<QuizEntity>>

    @Query("SELECT * FROM question_table WHERE id IN (SELECT questionId FROM quiz_question_join_table WHERE quizId = :quizId)")
    fun getQuestionsForQuiz(quizId: Long): LiveData<List<QuestionEntity>>

    // QuestionTypeRelation-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestionTypeRelations(relations: List<QuestionTypeRelationEntity>)

    @Query("SELECT * FROM question_type_relation_table WHERE questionId = :questionId")
    fun getTypesForQuestion(questionId: Long): LiveData<List<QuestionTypeRelationEntity>>

    // UserAnswerEntity-related operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserAnswers(answers: List<UserAnswerEntity>)

    @Query("SELECT * FROM user_answer_table WHERE quizQuestionId = :quizQuestionId")
    fun getUserAnswerForQuestion(quizQuestionId: Long): LiveData<UserAnswerEntity>

    // Delete a quiz
    @Query("DELETE FROM quiz_table WHERE id = :quizId")
    fun deleteQuiz(quizId: Long)

    // Delete all quizzes
    @Query("DELETE FROM quiz_table")
    fun deleteAllQuizzes()


    // type accuracy
    @Query("""
        SELECT type, SUM(correct) AS correct, SUM(total) AS total
        FROM (
            SELECT question_type_relation_table.type AS type,
                   CASE WHEN user_answer_table.isCorrect THEN 1 ELSE 0 END AS correct,
                   1 AS total
            FROM question_type_relation_table
            JOIN question_table ON question_type_relation_table.questionId = question_table.id
            JOIN quiz_question_join_table ON question_table.id = quiz_question_join_table.questionId
            JOIN user_answer_table ON quiz_question_join_table.id = user_answer_table.quizQuestionId
        ) AS subquery
        GROUP BY type
    """)
    fun getTypeAccuracy(): LiveData<List<TypeAccuracy>>

    @Query("""
        SELECT type, SUM(correct) AS correct, SUM(total) AS total
        FROM (
            SELECT question_type_relation_table.type AS type,
                   CASE WHEN user_answer_table.isCorrect THEN 1 ELSE 0 END AS correct,
                   1 AS total
            FROM question_type_relation_table
            JOIN question_table ON question_type_relation_table.questionId = question_table.id
            JOIN quiz_question_join_table ON question_table.id = quiz_question_join_table.questionId
            JOIN user_answer_table ON quiz_question_join_table.id = user_answer_table.quizQuestionId
            WHERE quiz_question_join_table.quizId = :quizId
        ) AS subquery
        GROUP BY type
    """)
    fun getTypeAccuracyForQuiz(quizId: Long): LiveData<List<TypeAccuracy>>
}