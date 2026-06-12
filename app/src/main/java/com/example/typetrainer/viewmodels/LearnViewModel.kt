package com.example.typetrainer.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.typetrainer.data.db.PokemonDatabase
import com.example.typetrainer.data.models.QuestionEntity
import com.example.typetrainer.data.models.Quiz
import com.example.typetrainer.data.models.QuizEntity
import com.example.typetrainer.data.models.TypeAccuracy
import com.example.typetrainer.data.repositories.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LearnViewModel(application: Application) : AndroidViewModel(application) {

    private val quizRepository: QuizRepository

    private val _allQuizzes = MutableLiveData<List<QuizEntity>>()
    val allQuizzes: LiveData<List<QuizEntity>>
        get() = _allQuizzes

    private val _latestQuiz = MutableLiveData<Quiz?>()
    val latestQuiz: MutableLiveData<Quiz?>
        get() = _latestQuiz

    private val _typeAccuracy = MutableLiveData<List<TypeAccuracy>>()
    val typeAccuracy: LiveData<List<TypeAccuracy>>
        get() = _typeAccuracy

    private val _numberAttempted = MutableLiveData(0)
    val numberAttempted: LiveData<Int>
        get() = _numberAttempted

    private val _numberCorrect = MutableLiveData(0)
    val numberCorrect: LiveData<Int>
        get() = _numberCorrect

    private val _yourBestTypes = MutableLiveData<List<Pair<String, Float>>>()
    val yourBestTypes: LiveData<List<Pair<String, Float>>>
        get() = _yourBestTypes

    private val _yourWorstTypes = MutableLiveData<List<Pair<String, Float>>>()
    val yourWorstTypes: LiveData<List<Pair<String, Float>>>
        get() = _yourWorstTypes

    // Sharing
    private val _bestType = MutableLiveData<String>()
    val bestType: LiveData<String>
        get() = _bestType

    private val _worstType = MutableLiveData<String>()
    val worstType: LiveData<String>
        get() = _worstType

    fun getEmojiFromAccuracy(numberAnswered: Int, numberCorrect: Int): String {
        val accuracy = if (numberAnswered == 0) 0f else numberCorrect.toFloat() / numberAnswered
        return when {
            accuracy >= 0.8 -> "\uD83E\uDD2F"
            accuracy >= 0.6 -> "\uD83D\uDE0E"
            accuracy >= 0.4 -> "\uD83E\uDD76"
            accuracy >= 0.2 -> "\uD83D\uDE35"
            else -> "\uD83D\uDC80"
        }
    }

    init {
        val database = PokemonDatabase.getDatabase(application)
        val quizDao = database.quizDao()
        quizRepository = QuizRepository(quizDao)

        //TODO: stop using observeForever and follow same pattern as SharedDataViewModel

        // Observe type accuracy LiveData
        quizRepository.getTypeAccuracy().observeForever { accuracyData ->
            Log.d("LearnViewModel", "Accuracy data changed: $accuracyData")
            processTypeAccuracyData(accuracyData)
        }

        // Observe all quizzes LiveData
        quizRepository.getAllQuizzes().observeForever { quizzes ->
            Log.d("LearnViewModel", "All quizzes changed: $quizzes")
            _allQuizzes.value = quizzes

            //get the latest quiz
            if (quizzes.isNotEmpty()) {
                viewModelScope.launch {
                    val latestQuizId = quizzes.maxByOrNull { it.id }?.id ?: 0
                    val latestQuiz = quizRepository.getQuizById(latestQuizId)
                    if (latestQuiz != null) {
                        _latestQuiz.postValue(latestQuiz)
                    }
                }
            }
        }


    }


    private fun processTypeAccuracyData(accuracyData: List<TypeAccuracy>) {
        val transformedData = accuracyData.groupBy { it.type }
            .mapValues { entry ->
                entry.value.sumOf { it.correct } to entry.value.sumOf { it.total }
            }.map { (type, counts) ->
                mapOf(type to counts)
            }

        _typeAccuracy.value = accuracyData

        _numberAttempted.value = transformedData.sumOf { question -> question.values.sumOf { it.second } }

        _numberCorrect.value = transformedData.sumOf { question -> question.values.sumOf { it.first } }

        _bestType.value = transformedData.maxByOrNull { accuracy -> accuracy.values.sumOf { it.first } }?.keys?.firstOrNull() ?: "Unknown"

        Log.d("LearnViewModel", "Best type: ${_bestType.value}")
        Log.d("LearnViewModel", "Accuracy data: $transformedData")

        // Best + worst types calculation - sorting by correct / total in either direction

        val typesWithQuestionsAsked = transformedData.filter { it.values.sumOf { it.second } > 0 }

        val sortedTypes = typesWithQuestionsAsked.flatMap { it.entries }
            .map { (type, counts) ->
                val (correct, total) = counts
                val accuracy = if (total != 0) correct.toFloat() / total else 0f
                type to accuracy
            }
            .sortedByDescending { it.second }

        // Exposing best and worst as live data too (might want it on UI)
        _yourBestTypes.postValue(sortedTypes.take(5))
        _yourWorstTypes.postValue(sortedTypes.takeLast(5).reversed())
    }

    fun getQuestionsForQuiz(quizId: Long): LiveData<List<QuestionEntity>> {
        return quizRepository.getQuestionsForQuiz(quizId)
    }

    fun deleteQuiz(quizId: Long) = viewModelScope.launch {
        quizRepository.deleteQuiz(quizId)
    }
}

