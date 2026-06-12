package com.example.typetrainer.viewmodels

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.typetrainer.data.db.PokemonDatabase
import com.example.typetrainer.data.models.*
import com.example.typetrainer.data.repositories.PokemonRepository
import com.example.typetrainer.data.repositories.TypeRepository
import com.example.typetrainer.data.repositories.QuizRepository
import com.example.typetrainer.util.Constants
import com.example.typetrainer.util.ImageCacheManager
import com.example.typetrainer.util.isInternetConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.net.ssl.HttpsURLConnection
import kotlin.system.measureTimeMillis


class SharedDataViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonRepository: PokemonRepository
    private val typeRepository: TypeRepository
    private val quizRepository: QuizRepository


    val aggregatedTypeInfo: LiveData<List<AggregatedTypeInfo>>

    private val _currentPokemon = MutableStateFlow<PokemonEntity?>(null)
    val currentPokemon: StateFlow<PokemonEntity?> = _currentPokemon

    private val _currentType = MutableStateFlow<TypeEntity?>(null)
    val currentType: StateFlow<TypeEntity?> = _currentType

    private val _effectivenessCache = mutableMapOf<Pair<String?, String?>, Map<String, Double>>()

    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded

    private val _currentEffectiveness = MutableStateFlow<Map<String, Double>?>(null)
    val currentEffectiveness: StateFlow<Map<String, Double>?> = _currentEffectiveness

    private val _pokemonBackTrigger = MutableStateFlow(false)
    val pokemonBackTrigger: StateFlow<Boolean> = _pokemonBackTrigger

    // Function to trigger the back navigation
    fun triggerBackNavigation() {
        _pokemonBackTrigger.value = true
    }

    // Function to reset the back trigger after navigation is handled
    fun resetBackTrigger() {
        _pokemonBackTrigger.value = false
    }

    private val _pokemonNavigationTrigger = MutableStateFlow<String?>(null)
    val pokemonNavigationTrigger: StateFlow<String?> = _pokemonNavigationTrigger

    //function to trigger navigation to Pokemon screen
    fun navigateToPokemon(id: String, formId: String) {
        val timeTaken = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {

                val pokemon = getPokemonByIdAndForm(id, formId)
                Log.d("SharedDataViewModel", "fetched pokemon ${pokemon?.name}")

                //update values on main
                withContext(Dispatchers.Main) {
                    _currentPokemon.value = pokemon
                    pokemon?.let {
                        val defendingTypes = listOfNotNull(it.primaryType, it.secondaryType)
                        _currentEffectiveness.value = getEffectiveness(defendingTypes)
                    }
                }

                Log.d("SharedDataViewModel debug", "_currentPokemon.value: ${_currentPokemon.value}")
                Log.d("SharedDataViewModel debug", "_currentEffectiveness.value: ${_currentEffectiveness.value}")
                //trigger navigation on main
                _pokemonNavigationTrigger.value = "pokemon/$id/$formId"
            }
        }
        Log.d("SharedDataViewModel debug", "Time taken to fetch and trigger navigate to Pokemon: $timeTaken ms")
    }

    // Reset the navigation trigger after handling
    fun resetNavigationTrigger() {
        _pokemonNavigationTrigger.value = null
    }

    private val _typeNavigationTrigger = MutableStateFlow<String?>(null)
    val typeNavigationTrigger: StateFlow<String?> = _typeNavigationTrigger

    fun navigateToType(type: String) {
        val timeTaken = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {

                val typeEntity = getTypeById(type)
                Log.d("SharedDataViewModel", "fetched type ${typeEntity.type}")

                //update value on main
                withContext(Dispatchers.Main) {
                    _currentType.value = typeEntity
                }

                Log.d("SharedDataViewModel debug", "_currentType.value: ${_currentType.value}")
                //trigger navigation on main
                _pokemonNavigationTrigger.value = "type/$type"
            }
        }
        Log.d("SharedDataViewModel debug", "Time taken to fetch and trigger navigate to type: $timeTaken ms")
    }

    fun resetTypeNavigationTrigger() {
        _typeNavigationTrigger.value = null
    }

    fun loadType(id: String) {
        viewModelScope.launch {
            val type = typeRepository.getTypeById(id)
            _currentType.value = type
        }
    }



    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loadComplete = MutableLiveData<Boolean>()
    val loadComplete: LiveData<Boolean> get() = _loadComplete

    val allPokemon: LiveData<List<PokemonEntity>>
    val allTypes: LiveData<List<TypeEntity>>
    val typesMap: LiveData<Map<String, TypeEntity>>
    val allMoves: LiveData<List<MoveEntity>>
    val moveMap: LiveData<Map<String, MoveEntity>>
    val allLocalHashes: LiveData<List<LocalHashEntity>>

    private val _events = MutableStateFlow(listOf<Event>())
    val events: StateFlow<List<Event>> = _events

    private val _raidRecommendations = MutableLiveData<List<EventRecommendation>>()
    val raidRecommendations: LiveData<List<EventRecommendation>> = _raidRecommendations

    private val _megaRaids = MutableLiveData<List<EventRecommendation>>()
    val megaRaids: LiveData<List<EventRecommendation>> = _megaRaids

    private val _legendaryMegaRaids = MutableLiveData<List<EventRecommendation>>()
    val legendaryMegaRaids: LiveData<List<EventRecommendation>> = _legendaryMegaRaids

    private val _ultraBeastRaids = MutableLiveData<List<EventRecommendation>>()
    val ultraBeastRaids: LiveData<List<EventRecommendation>> = _ultraBeastRaids

    private val _exRaids = MutableLiveData<List<EventRecommendation>>()
    val exRaids: LiveData<List<EventRecommendation>> = _exRaids

    private val _lvl1Raids = MutableLiveData<List<EventRecommendation>>()
    val lvl1Raids: LiveData<List<EventRecommendation>> = _lvl1Raids

    private val _lvl3Raids = MutableLiveData<List<EventRecommendation>>()
    val lvl3Raids: LiveData<List<EventRecommendation>> = _lvl3Raids

    private val _lvl5Raids = MutableLiveData<List<EventRecommendation>>()
    val lvl5Raids: LiveData<List<EventRecommendation>> = _lvl5Raids


    private val _currentRelevantEvents = MutableLiveData<List<Event>>()
    val currentRelevantEvents: LiveData<List<Event>> = _currentRelevantEvents

    private val _currentEvents = MutableLiveData<List<Event>>()
    val currentEvents: LiveData<List<Event>> = _currentEvents

    private val _eventsRaids = MutableLiveData<List<Event>>()
    val eventsRaids: LiveData<List<Event>> = _eventsRaids

    private val _eventsCommunityDays = MutableLiveData<List<Event>>()
    val eventsCommunityDays: LiveData<List<Event>> = _eventsCommunityDays

    private val _eventsGoBattleLeague = MutableLiveData<List<Event>>()
    val eventsGoBattleLeague: LiveData<List<Event>> = _eventsGoBattleLeague

    private val _eventsSpotlightHours = MutableLiveData<List<Event>>()
    val eventsSpotlightHours: LiveData<List<Event>> = _eventsSpotlightHours

    private val _eventsResearch = MutableLiveData<List<Event>>()
    val eventsResearch: LiveData<List<Event>> = _eventsResearch

    private val _eventsMaxBattles = MutableLiveData<List<Event>>()
    val eventsMaxBattles: LiveData<List<Event>> = _eventsMaxBattles

    private val _eventsP2W = MutableLiveData<List<Event>>()
    val eventsP2W: LiveData<List<Event>> = _eventsP2W

    private val _eventsWildArea = MutableLiveData<List<Event>>()
    val eventsWildArea: LiveData<List<Event>> = _eventsWildArea

    private val _eventsSeason = MutableLiveData<List<Event>>()
    val eventsSeason: LiveData<List<Event>> = _eventsSeason

    private val _eventsTeamGoRocket = MutableLiveData<List<Event>>()
    val eventsTeamGoRocket: LiveData<List<Event>> = _eventsTeamGoRocket

    private val _eventsPokestopShowcases = MutableLiveData<List<Event>>()
    val eventsPokestopShowcases: LiveData<List<Event>> = _eventsPokestopShowcases

    private val _eventsMisc = MutableLiveData<List<Event>>()
    val eventsMisc: LiveData<List<Event>> = _eventsMisc



    private val _isSearchBarActive = MutableStateFlow(false)
    val isSearchBarActive: StateFlow<Boolean> = _isSearchBarActive.asStateFlow()

    fun setSearchBarActive(active: Boolean) {
        _isSearchBarActive.value = active
    }

    private val _queryText = MutableStateFlow("")
    val queryText: StateFlow<String> = _queryText.asStateFlow()

    fun updateQueryText(query: String) {
        _queryText.value = query
    }

    fun clearQueryText() {
        _queryText.value = ""
    }

    private val _searchResults = MutableStateFlow<List<Any>>(emptyList())
    val searchResults: StateFlow<List<Any>> get() = _searchResults

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    private val _selectedOption = MutableStateFlow<String?>(null)
    val selectedOption: StateFlow<String?> = _selectedOption.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private val _quitQuiz = MutableStateFlow(false)
    val quitQuiz: StateFlow<Boolean> = _quitQuiz.asStateFlow()

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> = _correctAnswersCount.asStateFlow()

    private val _effectivenessMap = MutableLiveData<Map<String, Double>?>(null)
    val effectivenessMap: LiveData<Map<String, Double>?> = _effectivenessMap

    private val _remainingTime = MutableStateFlow(10)
    val remainingTime: StateFlow<Int> get() = _remainingTime

    private var timerJob: Job? = null

    fun startTimer(timeLimit: Int) {
        _remainingTime.value = timeLimit
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            var stoppedTimer = false
            while (_remainingTime.value > 0) {
                if (_quitQuiz.value || _isQuizFinished.value) {
                    Log.d("SharedDataViewModel", "Stopping timer due to quitQuiz")
                    stoppedTimer = true
                    break
                }
                Log.d("SharedDataViewModel", "Remaining time: ${_remainingTime.value}")
                delay(1000L)
                _remainingTime.value -= 1
            }
            Log.d("SharedDataViewModel", "Timer finished")
            if (!stoppedTimer) {
                submitAnswer()
            } else {
                Log.d("SharedDataViewModel", "Timer was stopped, not submitting answer")
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _remainingTime.value = 10
    }

    fun selectOption(option: String) {
        _selectedOption.value = option
    }

    private val _questions = MutableStateFlow(listOf(
        QuizQuestion(
            questionText = AnnotatedString("Loading..."),
            options = listOf("Loading..."),
            correctAnswer = "Loading...",
            timeLimit = 0
        )
    ))

    val questions: StateFlow<List<QuizQuestion>> = _questions.asStateFlow()

    val totalQuestions: Int
        get() = questions.value.size

    init {
        Log.d("SharedDataViewModel", "ViewModel initialized. Checking and loading data if necessary.")

        val database = PokemonDatabase.getDatabase(application)
        val pokemonDao = database.pokemonDao()
        val typeDao = database.pokemonTypeDao()
        val moveDao = database.moveDao()
        val localHashDao = database.localHashDao()

        pokemonRepository = PokemonRepository(application, pokemonDao, moveDao, typeDao, localHashDao)
        typeRepository = TypeRepository(application, typeDao)
        quizRepository = QuizRepository(database.quizDao())

        allPokemon = pokemonRepository.allPokemon
        if (allPokemon.value.isNullOrEmpty()) {
            Log.d("SharedDataViewModel debug", "No pokemon data found in database when value assigned.")
        }
        allTypes = typeRepository.allTypes
        if (allTypes.value.isNullOrEmpty()) {
            Log.d("SharedDataViewModel debug", "No type data found in database when value assigned.")
        }
        typesMap = allTypes.map { types ->
            types.associateBy { it.type }
        }

        allMoves = pokemonRepository.allMoves

        moveMap = allMoves.map { moves ->
            moves.associateBy { it.id }
        }

        allLocalHashes = pokemonRepository.allLocalHashes
        Log.d("SharedDataViewModel", "All local hashes: ${allLocalHashes.value}")

        aggregatedTypeInfo = typeRepository.getAggregatedTypeInfo()

        _isDataLoaded.value = false

        viewModelScope.launch {
            checkAndLoadData()
        }

        fetchRaidRotation()
        fetchEvents()

        //ensure type effectiveness data is available before doing this
        precomputeTypeEffectiveness()

    }

    //1ms compute (yippee! no database!!!)
    private fun precomputeTypeEffectiveness() {
        val timeTaken = measureTimeMillis {
            viewModelScope.launch {

                val allTypes = typeRepository.getAllTypes()
                val typeRelations = typeRepository.getAllTypeRelations()

                for (type1 in allTypes) {
                    for (type2 in allTypes + null) { //null for single types
                        val key = Pair(type1.type, type2?.type)
                        //symmetric pair is irrelevant
                        if (_effectivenessCache.containsKey(key)) continue

                        val effectivenessMap = calculateTypeEffectiveness(listOfNotNull(type1.type, type2?.type), typeRelations)
                        _effectivenessCache[key] = effectivenessMap
                    }
                }

            }
        }

        Log.d("SharedDataViewModel debug", "Precomputed effectivenessCache in $timeTaken ms")

    }

    private fun calculateTypeEffectiveness(defendingTypes: List<String>, typeRelations: List<TypeRelationEntity>): Map<String, Double> {
        val effectivenessMap = mutableMapOf<String, Double>()

        defendingTypes.forEach { defendingType ->
            typeRelations.filter { it.type == defendingType }.forEach { relation ->
                val effectiveness = when (relation.relationType) {
                    "doubleDamageFrom" -> 2.0
                    "halfDamageFrom" -> 0.5
                    "noDamageFrom" -> 0.0
                    else -> 1.0
                }
                effectivenessMap[relation.relatedType] =
                    (effectivenessMap[relation.relatedType] ?: 1.0) * effectiveness
            }
        }

        return effectivenessMap
    }

    //0ms lookups
    fun getEffectiveness(defendingTypes: List<String>): Map<String, Double>? {
        return _effectivenessCache[Pair(defendingTypes.getOrNull(0), defendingTypes.getOrNull(1))]
    }


    private suspend fun checkAndLoadData() {
        val pokemonCount = pokemonRepository.getPokemonCount()
        if (pokemonCount == 0) {
            Log.d("SharedDataViewModel debug", "No pokemon data found in database. Loading from JSON.")
            pokemonRepository.loadPokemonDataFromJson()
        }

        val typesCount = typeRepository.getTypeCount()
        if (typesCount == 0) {
            Log.d("SharedDataViewModel debug", "No type data found in database. Loading from JSON.")
            typeRepository.loadTypeData()

        }
        //can now recalculate the type effectiveness cache with the data
        precomputeTypeEffectiveness()

        //state used to stop the user interacting until data ready
        _isDataLoaded.value = true
    }


    suspend fun submitQuizResult() {
        val quiz = QuizEntity(
            timestamp = System.currentTimeMillis()
        )
        val questions = questions.value.map { quizQuestion ->
            Log.d("SharedDataViewModel", "QuestionEntity: ${quizQuestion.questionText}")
            QuizQuestion(
                questionText = quizQuestion.questionText,
                types = quizQuestion.types,
                options = quizQuestion.options,
                timeLimit = quizQuestion.timeLimit,
                correctAnswer = quizQuestion.correctAnswer,
                selectedOption = _selectedOption.value ?: "",
                timeLeftOnSubmit = _remainingTime.value
            )
        }
        quizRepository.createQuiz(quiz, questions)
    }


    fun insert(pokemon: PokemonEntity) = viewModelScope.launch {
        pokemonRepository.insert(pokemon)
    }

    fun update(pokemon: PokemonEntity) = viewModelScope.launch {
        pokemonRepository.update(pokemon)
    }

    fun delete(pokemon: PokemonEntity) = viewModelScope.launch {
        pokemonRepository.delete(pokemon)
    }

    fun loadPokemonFromJson(manual: Boolean = false) {
        if (manual) {
            Log.e("SharedDataViewModel", "Loading Pokemon data MANUAL")
        } else {
            Log.e("SharedDataViewModel", "Loading Pokemon data AUTO")
        }
        if (_isLoading.value) return // Prevent reloading if already loading
        viewModelScope.launch {
            _isLoading.value = true
            try {
                pokemonRepository.loadPokemonDataFromJson()
                _loadComplete.value = true
            } catch (e: Exception) {
                Log.e("SharedDataViewModel", "Error loading Pokemon data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTypeDataFromJson() {
        if (_isLoading.value) return // Prevent reloading if already loading
        viewModelScope.launch {
            _isLoading.value = true
            try {
                typeRepository.loadTypeData()
                _loadComplete.value = true
            } catch (e: Exception) {
                Log.e("SharedDataViewModel", "Error loading Type data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetLoadComplete() {
        _loadComplete.value = false
    }


    var text by mutableStateOf("")
    var active by mutableStateOf(false)

    suspend fun getPokemonByIdAndForm(id: String, form: String): PokemonEntity? {
        Log.d("SharedDataViewModel", "Getting Pokemon: $id $form")
        return pokemonRepository.getPokemonByIdAndForm(id, form)
    }

    fun getMoveByMoveId(id: String): MoveEntity? {
        return allMoves.value?.find {
            it.id == id
        }
    }

    fun getMovesByMoveIds(ids: List<String>): List<MoveEntity> {
        val currentMoveMap = moveMap.value ?: emptyMap()
        return ids.mapNotNull { currentMoveMap[it] }
    }

    fun calculateAllTypeEffectiveness(defendingTypes: List<String>) {
        viewModelScope.launch {
            _effectivenessMap.postValue(null) //null indicates loading
            Log.e("SharedDataViewModel", "Calculating type effectiveness for $defendingTypes")

            val typeRelations = typeRepository.getAllTypeRelations()

            val effectivenessMap = mutableMapOf<String, Double>()
            defendingTypes.forEach { defendingType ->
                typeRelations.filter { it.type == defendingType }.forEach { relation ->
                    val effectiveness = when (relation.relationType) {
                        "doubleDamageFrom" -> 2.0
                        "halfDamageFrom" -> 0.5
                        "noDamageFrom" -> 0.0
                        else -> 1.0
                    }
                    effectivenessMap[relation.relatedType] = (effectivenessMap[relation.relatedType] ?: 1.0) * effectiveness
                }
            }
            _effectivenessMap.postValue(effectivenessMap)
        }
    }

    private suspend fun getTypeById(id: String): TypeEntity {
        var type: TypeEntity? = null
        val timeTaken = measureTimeMillis {
            Log.d("SharedDataViewModel", "Getting type: $id")
            type = typeRepository.getTypeById(id)
        }
        Log.d("SharedDataViewModel debug", "Time taken to get type: $timeTaken ms")
        return type!!
    }

    fun search(limit: Int = 10) {
        val query = _queryText.value
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            val timeTaken = measureTimeMillis {
                Log.d("SharedDataViewModel", "Searching for '$query'")

                val startsWithLimit = limit / 2
                val containsLimit = limit - startsWithLimit

                val filteredPokemonStartsWith =
                    pokemonRepository.searchPokemonByNameStartsWith(query, startsWithLimit)
                val filteredPokemonContains =
                    pokemonRepository.searchPokemonByNameContains(query, containsLimit)

                val filteredTypesStartsWith =
                    typeRepository.searchTypeByNameStartsWith(query, startsWithLimit)
                val filteredTypesContains =
                    typeRepository.searchTypeByNameContains(query, containsLimit)

                val combinedResults =
                    (filteredPokemonStartsWith + filteredTypesStartsWith + filteredPokemonContains + filteredTypesContains).take(
                        limit
                    )

                _searchResults.value = combinedResults

                Log.d(
                    "SharedDataViewModel",
                    "Search results for '$query': ${combinedResults.size} items"
                )
            }
            Log.d("SharedDataViewModel debug", "Time taken to search: $timeTaken ms")
        }
    }


    private fun generateQuizQuestions(n: Int, optionsPerQuestion: Int = 4) {
        viewModelScope.launch {
            val typeList = typeRepository.getAllTypes()
            val typeRelations = typeRepository.getAllTypeRelations()
            val questions = mutableListOf<QuizQuestion>()

            if (typeList.isEmpty()) {
                throw IllegalStateException("Type list is empty. Cannot generate quiz questions.")
            }

            for (i in 1..n) {
                val randomType = typeList.random()
                val typeListStrings = typeList.map { it.type }

                val superEffectiveTypes = typeRelations.filter { it.type == randomType.type && it.relationType == "doubleDamageFrom" }.map { it.relatedType }

                val correctAnswer = superEffectiveTypes.random()
                val randomTypeName = randomType.type

                //generates other options that arent super effective (not in superEffectiveTypes)
                var options = typeListStrings.filter { it !in superEffectiveTypes }
                    .shuffled()
                    .take(optionsPerQuestion - 1)
                    .map { it }

                options = options + correctAnswer

                //makes sure its not just the last options every time LMAO
                options = options.shuffled()

                Log.d("SharedDataViewModel", "Correct answer: $correctAnswer")

                //annotated string used for the bolding of name and damage type
                val questionText = createAnnotatedQuestionText(randomTypeName)

                val question = QuizQuestion(
                    types = listOf(randomTypeName),
                    questionText = questionText,
                    options = options,
                    timeLimit = 10,
                    correctAnswer = correctAnswer
                )
                questions.add(question)
            }

            _questions.value = questions
        }
    }

    fun nextQuestion() {
        if (_isQuizFinished.value) {
            return
        }
        if (_currentQuestionIndex.value < questions.value.size - 1) {
            _currentQuestionIndex.value++
            resetTimer()
            startTimer(getCurrentQuestion().timeLimit)
            _selectedOption.value = null
            _isAnswerCorrect.value = null
        } else {
            _isQuizFinished.value = true
        }
    }

    fun getCurrentQuestion(): QuizQuestion {
        return questions.value[_currentQuestionIndex.value]
    }

    fun submitAnswer() {

        val currentQuestion = getCurrentQuestion()
        val correctAnswer = currentQuestion.correctAnswer

        val selectedOption = _selectedOption.value

        _isAnswerCorrect.value = selectedOption == correctAnswer
        Log.d("SharedDataViewModel", "Is answer correct: ${_isAnswerCorrect.value}")
        Log.d("SharedDataViewModel", "Correct answer: $correctAnswer")

        if (selectedOption != null) {
            Log.d("SharedDataViewModel", "Submitting option: $selectedOption")

            if (selectedOption == correctAnswer) {
                _correctAnswersCount.value++
                Log.d("SharedDataViewModel", "Correct answers: ${_correctAnswersCount.value}")
            }

            val updatedQuestion = currentQuestion.copy(
                selectedOption = selectedOption,
                timeLeftOnSubmit = _remainingTime.value
            )

            val updatedQuestions = questions.value.toMutableList()
            updatedQuestions[_currentQuestionIndex.value] = updatedQuestion
            _questions.value = updatedQuestions
        }

        nextQuestion()
    }

    fun resetQuiz() {
        _currentQuestionIndex.value = 0
        _isQuizFinished.value = false
        _quitQuiz.value = false
        _selectedOption.value = null
        _isAnswerCorrect.value = null
        _correctAnswersCount.value = 0
        try {
            generateQuizQuestions(10)
            Log.d("SharedDataViewModel", "QuizEntity reset")
            //log the quiz questions
            questions.value.forEachIndexed { index, quizQuestion ->
                Log.d("SharedDataViewModel", "QuestionEntity $index: ${quizQuestion.questionText}")
                Log.d("SharedDataViewModel", "Options: ${quizQuestion.options}")
            }
        } catch (e: IllegalStateException) {
            Log.e("SharedDataViewModel", "Failed to reset quiz: ${e.message}")
            _isQuizFinished.value = true
        }
    }

    fun quitQuiz() {
        _quitQuiz.value = true
    }

    fun refreshPokemonData() {
        viewModelScope.launch {
            pokemonRepository.loadPokemonDataFromJson()
        }
    }

    private fun fetchRaidRotation() {
        Log.d("SearchViewModel", "Fetching raid rotation")
        viewModelScope.launch {
            val raidData = pokemonRepository.loadRaidRotation()
            _raidRecommendations.postValue(raidData)
            _megaRaids.postValue(raidData.filter { it.level == "mega" })
            _legendaryMegaRaids.postValue(raidData.filter { it.level == "legendary_mega" })
            _ultraBeastRaids.postValue(raidData.filter { it.level == "ultra_beast" })
            _exRaids.postValue(raidData.filter { it.level == "ex" })
            _lvl1Raids.postValue(raidData.filter { it.level == "lvl1" })
            _lvl3Raids.postValue(raidData.filter { it.level == "lvl3" })
            Log.d("SharedDataViewModel", "lvl3Raids: ${raidData.filter { it.level == "lvl3" }}")
            _lvl5Raids.postValue(raidData.filter { it.level == "lvl5" })
        }
    }

    //theres a typo in the api where "unannounced" is sometimes spelled "unannoucned"???
    @Suppress("SpellCheckingInspection")
    private fun fetchEvents() {
        Log.d("SearchViewModel", "Fetching events")
        //eventType values 10-10-2024:
        //raid-hour raid-day raid-battles community-day pokestop-showcase event team-go-rocket go-battle-league pokemon-spotlight-hour research max-battles ticketed-event wild-area season
        viewModelScope.launch {

            //test LocalDateTime: 22nd October 2024 @ 6:30PM
            val testDateTime = LocalDateTime.of(2024, 10, 22, 18, 30)

            val currentDateTime = LocalDateTime.now() //8601 - same as the start8601 and end8601 fields on Event
            //val currentDateTime = testDateTime

            val allEvents = pokemonRepository.loadEvents()

            val eventsFriendly = allEvents.map {
                it.copy(
                    startDate = convertDateToFriendly(it.startDate),
                    startTime = convert24To12(it.startTime),
                    endDate = convertDateToFriendly(it.endDate),
                    endTime = convert24To12(it.endTime)
                )
            }

            //current relevant events for the badge on the nav bar
            val irrelevantEventTypes = listOf("go-battle-league", "raid-battles", "max-battles", "ticketed-event", "wild-area", "season", "pokestop-showcase")


            val currentEvents = eventsFriendly.sortedBy {
                val startDateTime = LocalDateTime.parse(it.start8601, DateTimeFormatter.ISO_DATE_TIME)
                val endDateTime = LocalDateTime.parse(it.end8601, DateTimeFormatter.ISO_DATE_TIME)
                java.time.Duration.between(startDateTime, endDateTime).toMillis()
            }.filter {
                val startDateTime = LocalDateTime.parse(it.start8601, DateTimeFormatter.ISO_DATE_TIME)
                val endDateTime = LocalDateTime.parse(it.end8601, DateTimeFormatter.ISO_DATE_TIME)
                currentDateTime.isAfter(startDateTime) && currentDateTime.isBefore(endDateTime)
            }

            val currentRelevantEvents = currentEvents.filter { it.eventType !in irrelevantEventTypes }

            val currentEventsFree = currentEvents.filter { it.eventType !in listOf("ticketed-event") }

            val events = eventsFriendly.filter {
                !(it.name.contains("Unannounced")
                        || it.eventID.contains("unannounced")
                        || it.name.contains("Unannoucned")
                        || it.eventID.contains("unannoucned"))
            }

            //this is a stupid system and will be reworked
            _events.value = events
            events.forEach {
                if (it.eventType.contains("max")) {
                    Log.d("SharedDataViewModel debug", "Max event: $it")
                }
            }
            _eventsRaids.postValue(events.filter { it.eventType == "raid-hour" || it.eventType == "raid-day" || it.eventType == "raid-battles" })
            _eventsCommunityDays.postValue(events.filter { it.eventType == "community-day" })
            _eventsGoBattleLeague.postValue(events.filter { it.eventType == "go-battle-league" })
            _eventsSpotlightHours.postValue(events.filter { it.eventType == "pokemon-spotlight-hour" })
            _eventsResearch.postValue(events.filter { it.eventType == "research" })
            _eventsMaxBattles.postValue(events.filter { it.eventType == "max-battles" })
            _eventsP2W.postValue(events.filter { it.eventType == "ticketed-event" })
            _eventsWildArea.postValue(events.filter { it.eventType == "wild-area" })
            _eventsSeason.postValue(events.filter { it.eventType == "season" })
            _eventsTeamGoRocket.postValue(events.filter { it.eventType == "team-go-rocket" })
            _eventsPokestopShowcases.postValue(events.filter { it.eventType == "pokestop-showcase" })
            _eventsMisc.postValue(events.filter {
                //all other events not in the previous categories
                it.eventType !in listOf("raid-hour", "raid-day", "raid-battles", "community-day", "go-battle-league", "pokemon-spotlight-hour", "research", "max-battles", "ticketed-event", "wild-area", "season", "team-go-rocket", "pokestop-showcase")

            })

            //this is the collection that causes the badge notification
            _currentRelevantEvents.postValue(currentRelevantEvents)
            currentRelevantEvents.forEach {
                Log.d("SharedDataViewModel debug", "Current event: ${it.name}")
            }

            //this is the collection that appears at the top of the events screen
            _currentEvents.postValue(currentEventsFree)

        }

    }



    private fun createAnnotatedQuestionText(name: String): AnnotatedString {
        return buildAnnotatedString {
            append("Which type deals ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("super effective")
            }
            append(" damage against ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(name)
            }
        }
    }

    fun deleteQuizData(manual: Boolean) {
        viewModelScope.launch {
            quizRepository.deleteAllQuizzes()
            if (manual) {
                Log.e("SharedDataViewModel", "QuizEntity data deleted MANUAL")
            } else {
                Log.e("SharedDataViewModel", "QuizEntity data deleted AUTO")
            }
        }
    }

    fun loadImage(url: String) {
        viewModelScope.launch {
            val cachedBitmap = ImageCacheManager.getImage(url)
            if (cachedBitmap == null) {
                val bitmap = fetchImage(url)
                if (bitmap != null) {
                    ImageCacheManager.putImage(url, bitmap)
                }
            }
        }
    }

    private suspend fun fetchImage(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                BitmapFactory.decodeStream(URL(url).openStream())
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error fetching image: $url", e)
                null
            }
        }
    }

    fun pokemonToWikiLink(pokemon: PokemonEntity): String {
        return "${Constants.WIKI_BASE}/${pokemon.dexNr}"
    }

    private fun getJSONFromApi(url: String): String {
        val hasInternet = isInternetConnected(getApplication())
        Log.e("SearchViewModel", "Has internet: $hasInternet")
        Log.d("SearchViewModel", "Fetching JSON from $url")
        var result = ""
        var conn: HttpsURLConnection? = null
        try {
            Log.d("SearchViewModel", "Opening connection to $url")
            val request = URL(url)
            conn = request.openConnection() as HttpsURLConnection
            Log.d("SearchViewModel", "Before connection")
            conn.connect()
            Log.d("SearchViewModel", "Connected to $url")
            val inStream: InputStream = conn.inputStream
            result = convertInputStreamToString(inStream)
        } catch (e: IOException) {
            Log.e("SearchViewModel", "IOException while connecting to $url", e)
        } catch (e: Exception) {
            Log.e("SearchViewModel", "Error fetching JSON from $url", e)
        } finally {
            Log.d("SearchViewModel", "Disconnecting from $url")
            conn?.disconnect()
        }
        Log.d("SearchViewModel", "JSON result: $result")
        return result
    }

    @Throws(IOException::class)
    private fun convertInputStreamToString(inS: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inS))
        val result = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            result.append(line)
        }
        inS.close()
        return result.toString()
    }

    private val _webHashes = MutableStateFlow<Map<String, String>>(emptyMap())
    val webHashes: StateFlow<Map<String, String>> = _webHashes.asStateFlow()

    fun checkWebHashes() {
        //route for hash is https://pokemon-go-api.github.io/pokemon-go-api/api/hashes.json
        viewModelScope.launch(Dispatchers.IO) {
            val url = "https://pokemon-go-api.github.io/pokemon-go-api/api/hashes.json"
            val json = getJSONFromApi(url)
            val jsonObject = JSONObject(json)
            val hashes = jsonObject.getJSONObject("sha512")

            val hashMap = mutableMapOf<String, String>()
            val keys = hashes.keys()
            for (key in keys) {
                hashMap[key] = hashes.getString(key)
            }

            _webHashes.value = hashMap

        }
    }



    fun copySourcesToClipboard() {
        val sources = """
            Sources:
            - Pokemon, types, moves, and raid data: https://github.com/pokemon-go-api/pokemon-go-api
            - Events data: https://github.com/bigfoott/ScrapedDuck
            - Type icons recoloured from: https://github.com/partywhale/pokemon-type-icons
            - App icon svg (fighting icon): https://github.com/duiker101/pokemon-type-svg-icons
        """
        val clipboardManager = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Sources", sources)
        clipboardManager.setPrimaryClip(clip)
    }
    
    //takes a string in form xx:00 and converts it to xx AM/PM
    private fun convert24To12(time: String): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val parsedTime = LocalTime.parse(time, formatter)
        val formattedTime = parsedTime.format(DateTimeFormatter.ofPattern("ha"))
            .uppercase(Locale.getDefault())
        return formattedTime
    }

    //takes a string in form yyyy-MM-dd and converts it to EEEE, d MMMM
    private fun convertDateToFriendly(date: String): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDate.parse(date, formatter)

        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        return when (parsedDate) {
            today -> "Today"
            tomorrow -> "Tomorrow"
            else -> {
                val day = parsedDate.dayOfMonth
                val suffix = getOrdinal(day)
                parsedDate.format(DateTimeFormatter.ofPattern("EEEE d'${suffix}' MMMM"))
            }
        }
    }

    //date ordinal suffixes
    private fun getOrdinal(day: Int): String {
        return if (day in 11..13) {
            "th"
        } else {
            when (day % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
    }


}


