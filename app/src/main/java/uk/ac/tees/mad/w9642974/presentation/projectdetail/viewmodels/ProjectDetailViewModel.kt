package uk.ac.tees.mad.w9642974.presentation.projectdetail.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9642974.data.repository.FirestoreRepository
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.ProjectFile
import uk.ac.tees.mad.w9642974.domain.Resource
import uk.ac.tees.mad.w9642974.domain.Task
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.ResponseStatus
import uk.ac.tees.mad.w9642974.presentation.projectdetail.AddTaskDestination
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val projectId: String = checkNotNull(savedStateHandle[AddTaskDestination.projectIdArg])

    private val _myProjectState = Channel<ProjectResponseStatus>()
    val myProjectState = _myProjectState.receiveAsFlow()

    private val _taskUpdateStatus = Channel<ResponseStatus>()
    val taskUpdateStatus = _taskUpdateStatus.receiveAsFlow()

    private val _memberRemoveStatus = Channel<ResponseStatus>()
    val memberRemoveStatus = _memberRemoveStatus.receiveAsFlow()

    private val _memberAddStatus = Channel<ResponseStatus>()
    val memberAddStatus = _memberAddStatus.receiveAsFlow()

    private val _fileUploadStatus = Channel<ResponseStatus>()
    val fileUploadStatus = _fileUploadStatus.receiveAsFlow()

    private val _deleteTaskState = Channel<ResponseStatus>()
    val deleteTaskState = _deleteTaskState.receiveAsFlow()

    private val _deleteProjectState = Channel<ResponseStatus>()
    val deleteProjectState = _deleteProjectState.receiveAsFlow()

    init {
        reload()
    }

    fun reload() {
        getProjectById()
    }

    var allMembers by mutableStateOf(listOf<Member>())
        private set

    init {
        getAllMembers()
    }

    private fun getAllMembers() = viewModelScope.launch {
        firestoreRepository.getAllMembers().collect {
            when (it) {
                is Resource.Error -> {
                    Log.d("MEMBER STATUS", it.message.toString())
                }

                is Resource.Loading -> {
                    Log.d("MEMBER STATUS", "true")
                }

                is Resource.Success -> {
                    allMembers = it.data ?: emptyList()
                }
            }
        }
    }

    private fun getProjectById() = viewModelScope.launch {
        firestoreRepository.getProjectById(projectId).collect {
            when (it) {
                is Resource.Error ->
                    _myProjectState.send(ProjectResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _myProjectState.send(ProjectResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _myProjectState.send(ProjectResponseStatus(isSuccess = it.data))
                }
            }
        }
    }

    fun changeTaskStatus(task: Task) = viewModelScope.launch {
        firestoreRepository.updateTaskInProject(projectId, task).collect {
            when (it) {
                is Resource.Error ->
                    _taskUpdateStatus.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _taskUpdateStatus.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _taskUpdateStatus.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }

    fun removeMember(memberId: String) = viewModelScope.launch {
        firestoreRepository.removeMembersFromProject(projectId, memberId).collect {
            when (it) {
                is Resource.Error ->
                    _memberRemoveStatus.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _memberRemoveStatus.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _memberRemoveStatus.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }

    fun addMember(memberId: List<String>) = viewModelScope.launch {
        firestoreRepository.addMemberToProject(projectId, memberId).collect {
            when (it) {
                is Resource.Error ->
                    _memberAddStatus.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _memberAddStatus.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _memberAddStatus.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }


    fun uploadFile(fileName: String, fileDescription: String, fileByte: ByteArray) =
        viewModelScope.launch {
            firestoreRepository.uploadFileAndStoreInfo(
                projectId,
                fileName,
                fileDescription,
                fileByte
            ).collect {
                when (it) {
                    is Resource.Error ->
                        _fileUploadStatus.send(ResponseStatus(isError = it.message))

                    is Resource.Loading -> {
                        _fileUploadStatus.send(ResponseStatus(isLoading = true))
                    }

                    is Resource.Success -> {
                        _fileUploadStatus.send(ResponseStatus(isSuccess = it.data))
                    }
                }
            }
        }

    fun deleteTask(taskId: String) = viewModelScope.launch {
        firestoreRepository.deleteTaskFromProject(projectId, taskId).collect {
            when (it) {
                is Resource.Error ->
                    _deleteTaskState.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _deleteTaskState.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _deleteTaskState.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }

    fun deleteProject() = viewModelScope.launch {
        firestoreRepository.deleteProject(projectId).collect {
            when (it) {
                is Resource.Error ->
                    _deleteProjectState.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _deleteProjectState.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _deleteProjectState.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }
}

data class ProjectResponseStatus(
    val isLoading: Boolean = false,
    val isSuccess: ProjectUiState? = null,
    val isError: String? = null
)

data class ProjectUiState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val members: List<Member> = emptyList(),
    val createdBy: String = "",
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val tasks: List<Task> = emptyList(),
    val files: List<ProjectFile> = emptyList(),
    val isCompleted: Boolean = false
) {
    val progress: Float
        get() = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks.toFloat()) * 100 else 0f
}