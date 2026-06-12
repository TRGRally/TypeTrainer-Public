package com.example.typetrainer.ui.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Nature
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Stadium
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.typetrainer.R
import com.example.typetrainer.data.models.Event
import com.example.typetrainer.ui.components.EventCard
import com.example.typetrainer.ui.components.EventCardSmall
import com.example.typetrainer.ui.components.RaidRotationCard
import com.example.typetrainer.viewmodels.SharedDataViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    navController: NavHostController,
    sharedDataViewModel: SharedDataViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val events by sharedDataViewModel.events.collectAsState()
    Log.d("EventsScreen", "events: $events")

    val eventsRaids by sharedDataViewModel.eventsRaids.observeAsState(emptyList())
    val eventsCommunityDays by sharedDataViewModel.eventsCommunityDays.observeAsState(emptyList())
    val eventsGoBattleLeague by sharedDataViewModel.eventsGoBattleLeague.observeAsState(emptyList())
    val eventsSpotlightHours by sharedDataViewModel.eventsSpotlightHours.observeAsState(emptyList())
    val eventsResearch by sharedDataViewModel.eventsResearch.observeAsState(emptyList())
    val eventsMaxBattles by sharedDataViewModel.eventsMaxBattles.observeAsState(emptyList())
    val eventsP2W by sharedDataViewModel.eventsP2W.observeAsState(emptyList())
    val eventsWildArea by sharedDataViewModel.eventsWildArea.observeAsState(emptyList())
    val eventsSeason by sharedDataViewModel.eventsSeason.observeAsState(emptyList())
    val eventsTeamGoRocket by sharedDataViewModel.eventsTeamGoRocket.observeAsState(emptyList())
    val eventsPokestopShowcases by sharedDataViewModel.eventsPokestopShowcases.observeAsState(emptyList())
    val eventsMisc by sharedDataViewModel.eventsMisc.observeAsState(emptyList())

    val currentRelevantEvents by sharedDataViewModel.currentRelevantEvents.observeAsState(emptyList())
    val currentEvents by sharedDataViewModel.currentEvents.observeAsState(emptyList())

    val scope = rememberCoroutineScope()


    //changeable selectedTag index for the primary tab row.
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val titles = listOf("Overview", "Calendar", "Max Battles")
    val pagerState = rememberPagerState(pageCount = { titles.size })

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }

    fun openUrl(url: String) {
        //opens event.link in browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        navController.context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Events"
                    )
                },
                actions = {
//                    IconButton(
//                        onClick = {
//                            //lol
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.MoreVert,
//                            contentDescription = "More options"
//                        )
//                    }
                }
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .padding(top = paddingValues.calculateTopPadding(), bottom = 0.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {

            //primary tab container for different event types
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            scope.launch {
                                selectedTabIndex = index
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                        icon = {
                            when (index) {
                                0 -> if (selectedTabIndex == index) {
                                    Icon(Icons.Default.Newspaper, contentDescription = "Overview")
                                } else {
                                    Icon(Icons.Outlined.Newspaper, contentDescription = "Overview")
                                }

                                1 -> if (selectedTabIndex == index) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar")
                                } else {
                                    Icon(Icons.Outlined.CalendarMonth, contentDescription = "Calendar")
                                }

                                2 -> if (selectedTabIndex == index) {
                                    Icon(Icons.Default.Stadium, contentDescription = "Max Battles")
                                } else {
                                    Icon(Icons.Outlined.Stadium, contentDescription = "Max Battles")
                                }

                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            //pager for the different tabs
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { index ->
                if (index == 0) {
                    //overview tab
                    OverviewTab(
                        currentEvents = currentEvents,
                        eventsCommunityDays = eventsCommunityDays,
                        eventsSpotlightHours = eventsSpotlightHours,
                        eventsMaxBattles = eventsMaxBattles,
                        eventsPokestopShowcases = eventsPokestopShowcases,
                        eventsRaids = eventsRaids,
                        eventsMisc = eventsMisc,
                        eventsSeason = eventsSeason,
                        eventsP2W = eventsP2W,
                        openUrl = { openUrl(it) }
                    )
                }
                if (index == 1) {
                    //calendar tab
                    EventCalendar(events)
                }
                if (index == 2) {

                    //max battles tab
                    LazyColumn(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            count = eventsMaxBattles.size,
                            key = { index -> eventsMaxBattles[index].eventID }
                        ) { index ->
                            val event = eventsMaxBattles[index]
                            EventCard(event) { openUrl(event.link) }
                        }
                    }

                }

            }
        }
    }
}

@Composable
fun OverviewTab(
    currentEvents: List<Event>,
    eventsCommunityDays: List<Event>,
    eventsSpotlightHours: List<Event>,
    eventsMaxBattles: List<Event>,
    eventsPokestopShowcases: List<Event>,
    eventsRaids: List<Event>,
    eventsMisc: List<Event>,
    eventsSeason: List<Event>,
    eventsP2W: List<Event>,
    openUrl: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (currentEvents.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Outlined.Info,
                    title = "Happening now"
                )
            }
        }

        items(
            count = currentEvents.size,
            key = { index -> currentEvents[index].eventID }
        ) { index ->
            val event = currentEvents[index]
            EventCardSmall(event) { openUrl(event.link) }
        }

        if (eventsCommunityDays.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.Hiking,
                    title = "Community days"
                )
            }
        }

        items(
            count = eventsCommunityDays.size,
            key = { index -> eventsCommunityDays[index].eventID }
        ) { index ->
            val event = eventsCommunityDays[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsSpotlightHours.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.Alarm,
                    title = "Spotlight hours"
                )
            }
        }

        items(
            count = eventsSpotlightHours.size,
            key = { index -> eventsSpotlightHours[index].eventID }
        ) { index ->
            val event = eventsSpotlightHours[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsMaxBattles.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.Stadium,
                    title = "Max battles"
                )
            }
        }

        items(
            count = eventsMaxBattles.size,
            key = { index -> eventsMaxBattles[index].eventID }
        ) { index ->
            val event = eventsMaxBattles[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsPokestopShowcases.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.EmojiEvents,
                    title = "Pokestop showcases"
                )
            }
        }

        items(
            count = eventsPokestopShowcases.size,
            key = { index -> eventsPokestopShowcases[index].eventID }
        ) { index ->
            val event = eventsPokestopShowcases[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsRaids.isNotEmpty()) {
            item {
                RowHeader(
                    icon = ImageVector.vectorResource(id = R.drawable.boss),
                    title = "Raids"
                )
            }
        }

        items(
            count = eventsRaids.size,
            key = { index -> eventsRaids[index].eventID }
        ) { index ->
            val event = eventsRaids[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsMisc.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.Nature,
                    title = "Other events"
                )
            }
        }

        items(
            count = eventsMisc.size,
            key = { index -> eventsMisc[index].eventID }
        ) { index ->
            val event = eventsMisc[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsSeason.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.DateRange,
                    title = "Season"
                )
            }
        }

        items(
            count = eventsSeason.size,
            key = { index -> eventsSeason[index].eventID }
        ) { index ->
            val event = eventsSeason[index]
            EventCard(event) { openUrl(event.link) }
        }

        if (eventsP2W.isNotEmpty()) {
            item {
                RowHeader(
                    icon = Icons.Default.Toll,
                    title = "Paid 'events'"
                )
            }
        }

        items(
            count = eventsP2W.size,
            key = { index -> eventsP2W[index].eventID }
        ) { index ->
            val event = eventsP2W[index]
            EventCard(event) { openUrl(event.link) }
        }

    }
}

@Composable
fun RowHeader(
    icon: ImageVector,
    title: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp, 16.dp, 0.dp, 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}



data class EventPeriod(
    val event: Event,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)

data class DateSegment(
    val startDate: LocalDate,
    var endDate: LocalDate,
    val events: List<Event>
)

@Composable
fun EventCalendar(events: List<Event>) {
    val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    // Parse events to get start and end LocalDateTimes
    val eventPeriods = events.mapNotNull { event ->
        try {
            val startDateTime = LocalDateTime.parse(event.start8601, dateFormatter)
            val endDateTime = LocalDateTime.parse(event.end8601, dateFormatter)
            EventPeriod(event, startDateTime, endDateTime)
        } catch (e: Exception) {
            // Handle parsing exception if needed
            null
        }
    }

    if (eventPeriods.isEmpty()) return

    // Determine the overall date range
    val earliestDate = eventPeriods.minOf { it.startDateTime.toLocalDate() }
    val latestDate = eventPeriods.maxOf { it.endDateTime.toLocalDate() }

    // Generate a list of all dates within the range
    val dateRange = generateDateRange(earliestDate, latestDate)

    // Current local date for highlighting
    val currentDate = LocalDate.now()

    // Build DateSegments
    val dateSegments = mutableListOf<DateSegment>()
    var currentSegment: DateSegment? = null

    for (date in dateRange) {
        val eventsOnDate = eventPeriods.filter { period ->
            !date.isBefore(period.startDateTime.toLocalDate()) &&
                    !date.isAfter(period.endDateTime.toLocalDate())
        }.map { it.event }

        if (eventsOnDate.isEmpty()) {
            // Skip dates with no events
            continue
        }

        if (currentSegment == null) {
            // Start a new segment
            currentSegment = DateSegment(
                startDate = date,
                endDate = date,
                events = eventsOnDate
            )
        } else if (eventsOnDate == currentSegment.events) {
            // Same events as current segment, extend it
            currentSegment.endDate = date
        } else {
            // Different events, finalize the current segment
            dateSegments.add(currentSegment)
            // Start a new segment
            currentSegment = DateSegment(
                startDate = date,
                endDate = date,
                events = eventsOnDate
            )
        }
    }

    // Add the last segment
    currentSegment?.let { dateSegments.add(it) }

    // Scrollable vertical calendar layout using LazyColumn
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(dateSegments) { segment ->
            if (segment.startDate == segment.endDate) {
                // Single day segment
                DateItem(
                    date = segment.startDate,
                    isToday = segment.startDate == currentDate,
                    events = segment.events
                )
            } else {
                // Multi-day segment with identical events
                // Display the first date
                DateItem(
                    date = segment.startDate,
                    isToday = segment.startDate == currentDate,
                    events = segment.events
                )
                // Display a break if the segment is longer than 2 days
                if (segment.startDate.plusDays(1) < segment.endDate.minusDays(1)) {
                    BreakItem()
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // Display the last date
                DateItem(
                    date = segment.endDate,
                    isToday = segment.endDate == currentDate,
                    events = segment.events
                )
            }
        }
    }
}

fun generateDateRange(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    var date = startDate
    while (!date.isAfter(endDate)) {
        dates.add(date)
        date = date.plusDays(1)
    }
    return dates
}

@Composable
fun BreakItem() {
    Icon(
        imageVector = Icons.Default.MoreVert,
        contentDescription = "Break",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun DateItem(date: LocalDate, isToday: Boolean, events: List<Event>) {
    // Prepare events with time display information
    val dateFormatter = DateTimeFormatter.ISO_DATE_TIME
    val eventsWithTimeInfo = events.map { event ->
        val startDateTime = LocalDateTime.parse(event.start8601, dateFormatter)
        val endDateTime = LocalDateTime.parse(event.end8601, dateFormatter)
        val startDate = startDateTime.toLocalDate()
        val endDate = endDateTime.toLocalDate()

        val showStartTime = date == startDate
        val showEndTime = date == endDate

        val timeText = when {
            showStartTime && showEndTime -> "${event.startTime} - ${event.endTime}"
            showStartTime -> "${event.startTime} -"
            showEndTime -> "- ${event.endTime}"
            else -> null // No time to display on intermediate days
        }

        EventDisplayData(
            event = event,
            timeText = timeText,
            showTime = timeText != null
        )
    }

    // Sort events: events showing time first
    val sortedEvents = eventsWithTimeInfo.sortedByDescending { it.showTime }

    // Define colors
    val dateTextColor = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp, 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                        style = MaterialTheme.typography.titleMedium,
                        color = dateTextColor
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "See more",
                        tint = dateTextColor
                    )
                }

                if(isToday) {
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

            }

            sortedEvents.forEach { eventDisplayData ->
                EventItem(eventDisplayData)
            }
        }
    }
}

data class EventDisplayData(
    val event: Event,
    val timeText: String?,
    val showTime: Boolean
)

@Composable
fun EventItem(eventDisplayData: EventDisplayData) {
    val event = eventDisplayData.event
    val timeText = eventDisplayData.timeText

    val textColor = if (timeText != null) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val textStyle = if (timeText != null) {
        MaterialTheme.typography.bodyLarge
    } else {
        MaterialTheme.typography.labelLarge
    }

    Column {
        Text(
            text = event.name,
            style = textStyle,
            color = textColor,
        )
        timeText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}