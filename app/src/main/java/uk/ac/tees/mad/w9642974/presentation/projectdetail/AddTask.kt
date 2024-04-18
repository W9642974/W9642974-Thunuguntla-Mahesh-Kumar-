package uk.ac.tees.mad.w9642974.presentation.projectdetail

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Task
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination
import uk.ac.tees.mad.w9642974.presentation.home.CustomDatePickerDialog
import uk.ac.tees.mad.w9642974.presentation.home.MembersDropdownMenu
import uk.ac.tees.mad.w9642974.presentation.projectdetail.viewmodels.TaskViewModel
import uk.ac.tees.mad.w9642974.utils.toFormattedDateString
import java.util.Calendar
import java.util.Date
import java.util.UUID

val priorityList = listOf("Low", "Medium", "High")

@Composable
fun AddTaskScreen(
    taskViewModel: TaskViewModel = hiltViewModel(),
    onTaskAdded: () -> Unit
) {
    val addTaskState = taskViewModel.addTaskState.collectAsState(initial = null)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(Date()) }
    var priority by remember { mutableStateOf("") }
    var priorityIndex by remember { mutableIntStateOf(0) }
    var members by remember { mutableStateOf(listOf<Member>()) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val memberList = taskViewModel.allMembers

    LaunchedEffect(addTaskState.value?.isSuccess) {
        addTaskState.value?.isSuccess?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onTaskAdded()
        }
    }

    LaunchedEffect(addTaskState.value?.isError) {
        addTaskState.value?.isError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add new task",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Title")
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                shape = RoundedCornerShape(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Description")
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                shape = RoundedCornerShape(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "End date")
            DueDatePicker(
                onEndDateSelected = {
                    dueDate = Date(it)
                }
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Priority")
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                priorityList.forEachIndexed { index, item ->
                    Box(modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (priorityIndex == index) MaterialTheme.colorScheme.primary.copy(
                                0.2f
                            ) else Color.Unspecified
                        )
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            priorityIndex = index
                            priority = priorityList[index]
                        }
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Select members")
            MembersDropdownMenu(
                member = {
                    if (!members.contains(it))
                        members = members + it
                },
                memberList = memberList
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Selected members")
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(members) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(8.dp)) {
                            Text(
                                text = it.username,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = it.email,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onTaskAdded,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = {

                        val task = Task(
                            id = UUID.randomUUID().toString(), // Generate a unique ID for the task
                            title = title,
                            description = description,
                            assignedTo = members,
                            dueDate = dueDate,
                            status = "Pending",
                            priority = priority
                        )
                        taskViewModel.addTaskToProject(task)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (addTaskState.value?.isLoading == true) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Add Task")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DueDatePicker(
    endDate: Date? = null,
    onEndDateSelected: (Long) -> Unit
) {

    var shouldDisplayEndDatePicker by remember { mutableStateOf(false) }
    val interactionSourceEnd = remember { MutableInteractionSource() }
    val isEndPickerPressed by interactionSourceEnd.collectIsPressedAsState()
    if (isEndPickerPressed) {
        shouldDisplayEndDatePicker = true
    }

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val currentDayMillis = today.timeInMillis

    val initialEndDateMillis = endDate?.time ?: System.currentTimeMillis()


    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialEndDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Ensure end date is after start date
                return utcTimeMillis > currentDayMillis

            }
        }
    )

    var selectedEndDate by rememberSaveable {
        mutableStateOf(
            endDate?.toFormattedDateString() ?: ""
        )
    }

    LaunchedEffect(endDate) {
        selectedEndDate = endDate?.toFormattedDateString() ?: ""
    }

    // End Date Picker Dialog
    if (shouldDisplayEndDatePicker) {
        CustomDatePickerDialog(
            state = endDatePickerState,
            onConfirmClicked = { selectedDateInMillis ->
                selectedEndDate = selectedDateInMillis.toFormattedDateString()
                onEndDateSelected(selectedDateInMillis)
                shouldDisplayEndDatePicker = false
            },
            dismissRequest = {
                shouldDisplayEndDatePicker = false
            }
        )
    }

    // End Date TextField
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        value = selectedEndDate,
        onValueChange = {},
        placeholder = { Text("End Date") },
        trailingIcon = {
            Icons.Default.DateRange
        },
        interactionSource = interactionSourceEnd,
        shape = RoundedCornerShape(20.dp)
    )

}


object AddTaskDestination : NavigationDestination {
    override val route: String = "add_task"
    const val projectIdArg = "projectId"
    val routeWithArgs = "$route/{$projectIdArg}"
}