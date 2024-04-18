package uk.ac.tees.mad.w9642974.presentation.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Project
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.ResponseStatus
import uk.ac.tees.mad.w9642974.utils.toFormattedDateString
import java.util.Calendar
import java.util.Date

@Composable
fun AddProjectScreen(
    onProjectAdded: (Project) -> Unit,
    memberList: List<Member>,
    createProjectStatus: ResponseStatus?,
    onNavigateUp: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(Date()) }
    var endDate by remember { mutableStateOf(Date()) }
    var members by remember { mutableStateOf(listOf<Member>()) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Create new project",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                shape = RoundedCornerShape(20.dp)

            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Project Description") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                minLines = 3,
                maxLines = 3,
                shape = RoundedCornerShape(20.dp)
            )
            StartEndDatePicker(
                onStartDateSelected = {
                    startDate = Date(it)
                },
                onEndDateSelected = {
                    endDate = Date(it)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            MembersDropdownMenu(
                member = {
                    if (!members.contains(it))
                        members = members + it
                },
                memberList = memberList
            )
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(members) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(8.dp)) {
                            Text(text = it.username, fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
                    onClick = onNavigateUp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancel", color = MaterialTheme.colorScheme.error)
                }
                Button(
                    onClick = {
                        val list = members.map {
                            it.id
                        }

                        val project = Project(
                            name = name,
                            description = description,
                            startDate = startDate,
                            endDate = endDate,
                            members = list + Firebase.auth.currentUser?.uid!!,
                            createdBy = Firebase.auth.currentUser?.uid!!
                        )
                        onProjectAdded(project)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    if (createProjectStatus?.isLoading == true) {
                        CircularProgressIndicator()
                    } else {
                        Text("Create")
                    }
                }
            }
        }
        LaunchedEffect(key1 = createProjectStatus?.isSuccess) {
            if (createProjectStatus?.isSuccess != null) {
                Toast.makeText(context, "Project created", Toast.LENGTH_SHORT).show()
                onNavigateUp()
            }
        }
        LaunchedEffect(key1 = createProjectStatus?.isError) {
            if (createProjectStatus?.isError != null) {
                Toast.makeText(context, "${createProjectStatus.isError}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartEndDatePicker(
    startDate: Date? = null,
    endDate: Date? = null,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit
) {

    var shouldDisplayStartDatePicker by remember { mutableStateOf(false) }
    var shouldDisplayEndDatePicker by remember { mutableStateOf(false) }
    val interactionSourceStart = remember { MutableInteractionSource() }
    val interactionSourceEnd = remember { MutableInteractionSource() }
    val isStartPickerPressed by interactionSourceStart.collectIsPressedAsState()
    val isEndPickerPressed by interactionSourceEnd.collectIsPressedAsState()
    if (isStartPickerPressed) {
        shouldDisplayStartDatePicker = true
    }
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

    val initialStartDateMillis = startDate?.time ?: System.currentTimeMillis()
    val initialEndDateMillis = endDate?.time ?: System.currentTimeMillis()

    val startDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialStartDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= currentDayMillis
            }
        }
    )

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialEndDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Ensure end date is after start date
                return utcTimeMillis > startDatePickerState.selectedDateMillis!!
            }
        }
    )

    var selectedStartDate by rememberSaveable {
        mutableStateOf(
            startDate?.toFormattedDateString() ?: ""
        )
    }
    var selectedEndDate by rememberSaveable {
        mutableStateOf(
            endDate?.toFormattedDateString() ?: ""
        )
    }

    LaunchedEffect(startDate) {
        selectedStartDate = startDate?.toFormattedDateString() ?: ""
    }

    LaunchedEffect(endDate) {
        selectedEndDate = endDate?.toFormattedDateString() ?: ""
    }

    // Start Date Picker Dialog
    if (shouldDisplayStartDatePicker) {
        CustomDatePickerDialog(
            state = startDatePickerState,
            onConfirmClicked = { selectedDateInMillis ->
                selectedStartDate = selectedDateInMillis.toFormattedDateString()
                onStartDateSelected(selectedDateInMillis)
                shouldDisplayStartDatePicker = false
            },
            dismissRequest = {
                shouldDisplayStartDatePicker = false
            }
        )
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

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        // Start Date TextField
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            readOnly = true,
            value = selectedStartDate,
            onValueChange = {},
            label = { Text("Start Date") },
            trailingIcon = {
                Icons.Default.DateRange
            },
            interactionSource = interactionSourceStart,
            shape = RoundedCornerShape(20.dp)
        )


        // End Date TextField
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            readOnly = true,
            value = selectedEndDate,
            onValueChange = {},
            label = { Text("End Date") },
            trailingIcon = {
                Icons.Default.DateRange
            },
            interactionSource = interactionSourceEnd,
            shape = RoundedCornerShape(20.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    state: DatePickerState,
    onConfirmClicked: (selectedDateInMillis: Long) -> Unit,
    dismissRequest: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = dismissRequest,
        confirmButton = {
            Button(
                modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp),
                onClick = {
                    state.selectedDateMillis?.let {
                        onConfirmClicked(it)
                    }
                    dismissRequest()
                }
            ) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = dismissRequest) {
                Text(text = "Cancel")
            }
        },
        content = {
            DatePicker(
                state = state,
                showModeToggle = false,
                headline = {
                    state.selectedDateMillis?.toFormattedDateString()?.let {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = it
                        )
                    }
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersDropdownMenu(member: (Member) -> Unit, memberList: List<Member>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember {
            mutableStateOf(
                "Select members"
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {},
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },

                shape = RoundedCornerShape(20.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                memberList.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.username) },
                        onClick = {
                            selectedOptionText = selectionOption.username
                            member(selectionOption)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

object AddProjectDestination : NavigationDestination {
    override val route = "add_project"
}
