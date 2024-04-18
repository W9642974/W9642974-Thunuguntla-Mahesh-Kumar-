package uk.ac.tees.mad.w9642974.presentation.projectdetail

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Task
import uk.ac.tees.mad.w9642974.navigation.NavigationDestination
import uk.ac.tees.mad.w9642974.presentation.home.MembersDropdownMenu
import uk.ac.tees.mad.w9642974.presentation.home.toFormattedDateString
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.ResponseStatus
import uk.ac.tees.mad.w9642974.presentation.projectdetail.components.UploadFilePopupBox
import uk.ac.tees.mad.w9642974.presentation.projectdetail.viewmodels.ProjectUiState
import uk.ac.tees.mad.w9642974.presentation.shared.ProgressIndicator
import uk.ac.tees.mad.w9642974.ui.theme.success
import uk.ac.tees.mad.w9642974.utils.AndroidDownloader
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetails(
    onNavigateUp: () -> Unit,
    onProjectDelete: () -> Unit,
    onTaskDelete: (String) -> Unit,
    isLoading: Boolean,
    uiState: ProjectUiState,
    onAddTask: (String) -> Unit,
    onTaskUpdate: (Task) -> Unit,
    onFileUpload: (ByteArray, String, String) -> Unit,
    isTaskUpdating: Boolean,
    fileUploadStatus: State<ResponseStatus?>,
    reload: () -> Unit,
    removeMemberStatus: State<ResponseStatus?>,
    removeMember: (String) -> Unit,
    addMemberStatus: State<ResponseStatus?>,
    addMember: (List<String>) -> Unit,
    membersList: List<Member>,
    deleteTaskState: State<ResponseStatus?>
) {
    val context = LocalContext.current
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    var removedMember by remember {
        mutableStateOf("")
    }
    var bottomSheetTask by remember {
        mutableStateOf(Task())
    }
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    var showAddMemberBottomSheet by remember {
        mutableStateOf(false)
    }
    var showTaskDeleteConfirmation by remember {
        mutableStateOf(false)
    }
    var showProjectDeleteConfirmation by remember {
        mutableStateOf(false)
    }
    val addMemberBottomSheetState = rememberModalBottomSheetState()

    val bottomSheetState = rememberModalBottomSheetState()

    var showFileUploadDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var fileUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
            fileUri = result
            showFileUploadDialog = true
        }

    LaunchedEffect(fileUploadStatus.value?.isError) {
        fileUploadStatus.value?.isError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(fileUploadStatus.value?.isSuccess) {
        fileUploadStatus.value?.isSuccess?.let {
            reload()
            showFileUploadDialog = false
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(deleteTaskState.value?.isError) {
        deleteTaskState.value?.isError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(deleteTaskState.value?.isSuccess) {
        deleteTaskState.value?.isSuccess?.let {
            reload()
            showTaskDeleteConfirmation = false
            bottomSheetTask = Task()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(removeMemberStatus.value?.isSuccess) {
        removeMemberStatus.value?.isSuccess?.let {
            reload()
            removedMember = ""
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(addMemberStatus.value?.isSuccess) {
        addMemberStatus.value?.isSuccess?.let {
            reload()
            showAddMemberBottomSheet = false
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }
    Scaffold(topBar = {
        Box(
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                IconButton(onClick = {
                    showProjectDeleteConfirmation = true
                }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete"
                    )
                }
            }
        }
    }) {
        if (isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(0.2f))
                    .padding(it),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            UploadFilePopupBox(
                showPopup = showFileUploadDialog,
                onClickOutside = { showFileUploadDialog = false },
                onConfirm = onFileUpload,
                fileUri = fileUri,
                isLoading = fileUploadStatus.value?.isLoading == true
            )
            if (showTaskDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showTaskDeleteConfirmation = false },
                    confirmButton = {
                        Text(
                            text = "Delete",
                            modifier = Modifier.clickable { onTaskDelete(bottomSheetTask.id) },
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    dismissButton = {
                        Text(text = "Cancel",
                            modifier = Modifier.clickable { showTaskDeleteConfirmation = false })
                    },
                    title = {
                        Text(text = "Delete confirmation")
                    },
                    text = {
                        Text(text = "Are you sure to delete task: ${bottomSheetTask.title}?")
                    }
                )
            }
            if (showProjectDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showProjectDeleteConfirmation = false },
                    confirmButton = {
                        Text(
                            text = "Delete",
                            modifier = Modifier.clickable {
                                onProjectDelete()
                                showProjectDeleteConfirmation = false
                            },
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    dismissButton = {
                        Text(text = "Cancel",
                            modifier = Modifier.clickable { showTaskDeleteConfirmation = false }
                        )
                    },
                    title = {
                        Text(text = "Delete confirmation")
                    },
                    text = {
                        Text(text = "Are you sure to delete this project: ${uiState.name}?")
                    }
                )
            }
            if (showAddMemberBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showAddMemberBottomSheet = false },
                    sheetState = addMemberBottomSheetState,
                    windowInsets = WindowInsets.ime
                ) {
                    MemberListSheetContent(
                        membersList = membersList,
                        onDoneClick = { memList ->
                            addMember(memList)
                        },
                        isLoading = addMemberStatus.value?.isLoading == true
                    )
                }
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = bottomSheetState,
                    windowInsets = WindowInsets.ime
                ) {
                    BottomSheetContent(
                        task = bottomSheetTask,
                        isTaskLoading = isTaskUpdating,
                        onDoneClick = {
                            onTaskUpdate(
                                bottomSheetTask.copy(
                                    isCompleted = !bottomSheetTask.isCompleted
                                )
                            )
                        },
                        isLoading = deleteTaskState.value?.isLoading == true,
                        onTaskDelete = {
                            showTaskDeleteConfirmation = true
                        }
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(0.2f))
                    .padding(it),
            ) {
                ProjectHeader(
                    uiState,
                    showAddMemberBottomSheet = {
                        showAddMemberBottomSheet = true
                    }
                )

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White), horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ProjectTabList.forEachIndexed { index, item ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (selectedTabIndex == ProjectTabList.indexOf(item)) MaterialTheme.colorScheme.primary else Color.Unspecified
                                )
                                .weight(1f)
                                .height(50.dp)
                                .clickable {
                                    selectedTabIndex = index
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                color = if (selectedTabIndex == ProjectTabList.indexOf(item)) Color.White else Color.Unspecified

                            )
                        }
                    }

                }

                when (selectedTabIndex) {
                    0 -> {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Add Task",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.clickable {
                                        onAddTask(uiState.id)
                                    })
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                items(uiState.tasks) { task ->
                                    TaskCard(task = task, onClick = {
                                        bottomSheetTask = task
                                        showBottomSheet = true
                                    })
                                }
                            }
                        }
                    }

                    1 -> {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Files",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.clickable {
                                        launcher.launch("*/*")
                                    })
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.files) { file ->
                                    FileCard(name = file.name,
                                        description = file.description,
                                        onDownload = {
                                            val downloader = AndroidDownloader(context)
                                            downloader.downloadFile(
                                                url = file.url, fileName = file.name
                                            )
                                            Toast.makeText(
                                                context,
                                                "${file.name} downloading...",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                }
                            }
                        }
                    }

                    2 -> {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            Row {
                                Text(
                                    text = "Members",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 20.sp
                                )
                            }
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.members) { member ->
                                    MemberCard(
                                        member,
                                        onRemoveMember = {
                                            removedMember = member.id
                                            removeMember(member.id)
                                        },
                                        isLoading = removedMember == member.id && removeMemberStatus.value?.isLoading == true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MemberListSheetContent(
    membersList: List<Member>,
    onDoneClick: (List<String>) -> Unit,
    isLoading: Boolean
) {
    var members by remember { mutableStateOf(listOf<Member>()) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 30.dp)
    ) {
        Text(text = "Add members", fontSize = 24.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            MembersDropdownMenu(
                member = {
                    if (!members.contains(it))
                        members = members + it
                },
                memberList = membersList
            )


        }
        Button(onClick = {
            onDoneClick(members.map { it.id })
        }) {
            Text(text = "Add members")
        }
    }
}

@Composable
fun BottomSheetContent(
    task: Task,
    onDoneClick: () -> Unit,
    isTaskLoading: Boolean,
    isLoading: Boolean,
    onTaskDelete: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 30.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.weight(1f)) {
                    Text(text = task.title, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                }
                IconButton(onClick = onTaskDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Delete task",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DoneButton(
                    text = "${task.priority.uppercase()} PRIORITY",
                    onClick = {},
                    color = when (task.priority) {
                        "High" -> {
                            MaterialTheme.colorScheme.error
                        }

                        "Medium" -> {
                            MaterialTheme.colorScheme.secondary
                        }

                        else -> {
                            success
                        }
                    }
                )
                DoneButton(
                    onClick = onDoneClick,
                    isLoading = isTaskLoading,
                    color = if (task.isCompleted) success else Color.LightGray
                )
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = Calendar.getInstance().time.toFormattedDateString(),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        Text(text = "Team", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy((-24).dp)) {
            task.assignedTo.forEach {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(3.dp, Color.White), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (it.profileImage.isEmpty()) {
                        Icon(
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = "Profile photo",
                            tint = Color.Gray
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                                .data(it.profileImage).build(),
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = task.description,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }

}


val ProjectTabList = listOf(
    "Task list", "File", "Members"
)


@Composable
fun ProjectHeader(
    uiState: ProjectUiState,
    showAddMemberBottomSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(Color.White)
            .padding(horizontal = 30.dp)
    ) {
        Text(text = uiState.name, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = uiState.description, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy((-16).dp)
                    ) {

                        uiState.members.take(3).forEach {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(BorderStroke(3.dp, Color.White), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (it.profileImage.isEmpty()) {
                                    Icon(
                                        imageVector = Icons.Outlined.PersonOutline,
                                        contentDescription = "Profile photo",
                                        tint = Color.Gray
                                    )
                                } else {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .crossfade(true).data(it.profileImage).build(),
                                        contentDescription = "Profile photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(70.dp)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .border(BorderStroke(3.dp, Color.White), CircleShape)
                                .clickable {
                                    showAddMemberBottomSheet()
                                }, contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Profile photo",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            ProgressIndicator(
                size = 100.dp, strokeWidth = 12.dp, fontSize = 20.sp, progress = uiState.progress
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = uiState.endDate.toFormattedDateString(), color = Color.Gray
                )
            }
            DoneButton(
                onClick = {},
                text = if (uiState.progress.toInt() == 100) "Completed" else "Pending",
                color = if (uiState.progress.toInt() == 100) success else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DoneButton(
    onClick: () -> Unit,
    color: Color = Color.LightGray,
    isLoading: Boolean = false,
    text: String = "Done"
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(color.copy(0.2f))
            .border(
                BorderStroke(1.dp, color), RoundedCornerShape(30.dp)
            )
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = text,
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun MemberCard(
    member: Member, onRemoveMember: () -> Unit, isLoading: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .padding(end = 20.dp)
                    .clip(CircleShape)
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (member.profileImage.isEmpty()) {
                    Icon(
                        imageVector = Icons.Outlined.PersonOutline,
                        contentDescription = "Add photo",
                        tint = Color.Gray
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).crossfade(true)
                            .data(member.profileImage).build(),
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {

                Column(Modifier.weight(1f)) {
                    Text(
                        text = member.username,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = member.email, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    IconButton(onClick = onRemoveMember) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FileCard(
    name: String, description: String, onDownload: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Attachment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(30.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {

                Column(Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onDownload) {
                    Icon(imageVector = Icons.Default.ArrowCircleDown, contentDescription = null)
                }
            }
        }
    }
}


@Composable
fun TaskCard(
    task: Task, onClick: () -> Unit
) {
    val isCompleted = task.isCompleted
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.padding(end = 12.dp)) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = null,
                    colors = if (isCompleted) CheckboxDefaults.colors(success.copy(0.5f))
                    else CheckboxDefaults.colors(MaterialTheme.colorScheme.tertiary.copy(0.5f))
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {

                Column(Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color.Gray else Color.Black,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = task.description, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    }
}


object ProjectDetailsDestination : NavigationDestination {
    override val route: String = "project_details"
    const val projectIdArg = "projectId"
    val routeWithArgs = "$route/{$projectIdArg}"
}