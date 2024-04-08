package uk.ac.tees.mad.w9642974.presentation.home

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.ac.tees.mad.w9642974.domain.Project
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.HomeViewModel
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.QuoteUiState
import uk.ac.tees.mad.w9642974.ui.theme.success
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit, onAddProject: () -> Unit, viewModel: HomeViewModel = hiltViewModel()
) {
    val quoteUiState = viewModel.quoteUiState
    val myProjects by viewModel.myProjectState.collectAsState(initial = null)
    LaunchedEffect(Unit) {
        viewModel.fetchQuote()
        viewModel.getMyAllProjects()
    }
    Scaffold(topBar = {
        GroupFlowTepAppBar(onProfileClick = onProfileClick)
    }, floatingActionButton = {
        FloatingActionButton(onClick = onAddProject) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "new project")
        }
    }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(Color.LightGray.copy(alpha = 0.2f))
        ) {

            Column(
                Modifier
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .fillMaxWidth()

            ) {
                Text(
                    text = "Today",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                QuoteCard(quoteUiState = quoteUiState)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Card(
                        Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "In Process", fontSize = 22.sp)
                        }
                    }
                    Card(
                        Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(success)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Outlined.DoneAll,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Completed", fontSize = 22.sp, color = Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                Modifier
                    .fillMaxWidth()

                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "My Projects", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                        contentDescription = null
                    )
                }

                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        if (myProjects?.isLoading == true) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        if (myProjects?.isError != null) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Log.d("Error", myProjects?.isError.toString())
                                Text(text = "Error")
                            }
                        }
                    }
                    myProjects?.isSuccess?.let {
                        if (it.isEmpty()) {
                            item {
                                Box(
                                    Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No assigned projects",
                                        modifier = Modifier.padding(24.dp)
                                    )
                                }
                            }
                        } else {
                            items(it) {
                                MyProject(project = it)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MyProject(
    project: Project
) {
    val isCompleted = project.isCompleted
    Card(
        onClick = { /*TODO*/ },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.padding(end = 12.dp)) {
                Checkbox(
                    checked = true,
                    onCheckedChange = null,
                    colors = if (isCompleted)
                        CheckboxDefaults.colors(success.copy(0.5f))
                    else
                        CheckboxDefaults.colors(MaterialTheme.colorScheme.tertiary.copy(0.5f))
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Color.Gray else Color.Black,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = project.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column() {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun QuoteCard(quoteUiState: QuoteUiState) {
    Column(
        Modifier
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)

    ) {
        when (quoteUiState) {
            is QuoteUiState.Error -> {
                Text(text = "Error fetching result")
            }

            is QuoteUiState.Loading -> CircularProgressIndicator()
            is QuoteUiState.Success -> {
                Text(
                    text = "\"${quoteUiState.quote.content}\"",
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "- ${quoteUiState.quote.author}",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

            }
        }
    }
}

@Composable
fun GroupFlowTepAppBar(onProfileClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(IntrinsicSize.Max)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = Calendar.getInstance().time.toFormattedDateString())
            Spacer(modifier = Modifier.width(12.dp))
            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "")
        }
        Box(modifier = Modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .border(BorderStroke(2.dp, Color.Black), CircleShape)
            .clickable { onProfileClick() }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                modifier = Modifier
                    .padding(4.dp)
                    .size(30.dp)

            )
        }
    }
}

object HomeDestination : NavigationDestination {
    override val route = "explore"
}

fun Date.toFormattedDateString(): String {
    val sdf = SimpleDateFormat("EEEE, LLLL dd", Locale.getDefault())
    return sdf.format(this)
}