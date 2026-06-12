package com.example.typetrainer.data.repositories


import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.LiveData
import com.example.typetrainer.data.db.QuizDao
import com.example.typetrainer.data.models.QuestionEntity
import com.example.typetrainer.data.models.QuestionTypeRelationEntity
import com.example.typetrainer.data.models.Quiz
import com.example.typetrainer.data.models.QuizEntity
import com.example.typetrainer.data.models.QuizQuestion
import com.example.typetrainer.data.models.QuizQuestionJoinEntity
import com.example.typetrainer.data.models.TypeAccuracy
import com.example.typetrainer.data.models.UserAnswerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private val quizDao: QuizDao) {

    // helper function to allow the viewmodel to just submit a QuizEntity and a list of QuizQuestions
    // QuizQuestions are used near the ui layer to represent both a question and its user answer
    suspend fun createQuiz(
        quizEntity: QuizEntity,
        quizQuestions: List<QuizQuestion>
    ): Long {
        return withContext(Dispatchers.IO) {
            //inserts quiz and gets ID
            val quizId = quizDao.insertQuiz(quizEntity)

            //inserts questions and gets IDs
            val questions = quizQuestions.map { quizQuestion ->
                QuestionEntity(
                    questionText = quizQuestion.questionText.toString(),
                    questionType = "super effective",
                    timeAllowed = quizQuestion.timeLimit
                )
            }
            val questionIds = quizDao.insertQuestions(questions)


            //inserts quiz-questions relationships
            val quizQuestionJoins = quizQuestions.zip(questionIds).map { (quizQuestion, questionId) ->
                QuizQuestionJoinEntity(
                    quizId = quizId,
                    questionId = questionId,
                )
            }
            quizDao.insertQuizQuestionJoins(quizQuestionJoins)


            //inserts question-types relationships
            val questionTypeRelations = quizQuestions.flatMap { quizQuestion ->
                quizQuestion.types.map { type ->
                    QuestionTypeRelationEntity(
                        questionId = questionIds[quizQuestions.indexOf(quizQuestion)],
                        type = type
                    )
                }
            }
            quizDao.insertQuestionTypeRelations(questionTypeRelations)


            //inserts existing question-answer relationships
            val userAnswers = quizQuestions.filter { it.selectedOption != null }.map { quizQuestion ->
                UserAnswerEntity(
                    quizQuestionId = questionIds[quizQuestions.indexOf(quizQuestion)],
                    userAnswer = quizQuestion.selectedOption,
                    isCorrect = quizQuestion.selectedOption == quizQuestion.correctAnswer,
                    timeLeftOnSubmit = quizQuestion.timeLeftOnSubmit
                )
            }
            quizDao.insertUserAnswers(userAnswers)

            quizId
        }
    }

    // helper function to allow the viewmodel access to Quiz objects which have all data needed for the UI
    // Quiz type has id, timestamp, list of QuizQuestions
    suspend fun getQuizById(
        id: Long
    ): Quiz? {
        return withContext(Dispatchers.IO) {
            val quizEntity = quizDao.getQuizEntityById(id).value
            val questions = quizDao.getQuestionsForQuiz(id).value!!.map { questionEntity ->
                val types = quizDao.getTypesForQuestion(questionEntity.id).value!!.map { it.type }
                val userAnswer = quizDao.getUserAnswerForQuestion(questionEntity.id).value

                QuizQuestion(
                    types = types,
                    questionText = AnnotatedString(questionEntity.questionText),
                    options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
                    timeLimit = questionEntity.timeAllowed,
                    correctAnswer = "Option 1",
                    selectedOption = userAnswer?.userAnswer,
                    timeLeftOnSubmit = userAnswer?.timeLeftOnSubmit ?: 0
                )

            }
            quizEntity?.let {
                Quiz(
                    id = it.id,
                    timestamp = quizEntity.timestamp,
                    questions = questions
                )
            }
        }
    }


    // Get the number of questions for a quiz
    suspend fun getNumberOfQuestionsForQuiz(quizId: Long): Int {
        return quizDao.getNumberOfQuestionsForQuiz(quizId)
    }

    // Get the number of correct answers for a quiz
    suspend fun getNumberOfCorrectAnswersForQuiz(quizId: Long): Int {
        return quizDao.getNumberOfCorrectAnswersForQuiz(quizId)
    }

    // Get all quizzes
    fun getAllQuizzes(): LiveData<List<QuizEntity>> {
        return quizDao.getAllQuizzes()
    }

    // Get questions for a quiz
    fun getQuestionsForQuiz(quizId: Long): LiveData<List<QuestionEntity>> {
        return quizDao.getQuestionsForQuiz(quizId)
    }

    // Get answer for a quiz question
    fun getUserAnswerForQuestion(questionId: Long): LiveData<UserAnswerEntity> {
        return quizDao.getUserAnswerForQuestion(questionId)
    }

    // Get types for a question
    fun getTypesForQuestion(questionId: Long): LiveData<List<QuestionTypeRelationEntity>> {
        return quizDao.getTypesForQuestion(questionId)
    }

    // Delete a quiz
    suspend fun deleteQuiz(quizId: Long) {
        quizDao.deleteQuiz(quizId)
    }

    // Delete all quizzes
    suspend fun deleteAllQuizzes() {
        quizDao.deleteAllQuizzes()
    }

    //
    //type accuracy
    //

    // Get type accuracy
    fun getTypeAccuracy(): LiveData<List<TypeAccuracy>> {
        return quizDao.getTypeAccuracy()
    }
    // Get the type accuracy for a quiz
    suspend fun getTypeAccuracyForQuiz(quizId: Long): LiveData<List<TypeAccuracy>> {
        return quizDao.getTypeAccuracyForQuiz(quizId)
    }


}