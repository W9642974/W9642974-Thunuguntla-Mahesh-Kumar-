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
import uk.ac.tees.mad.w9642974.domain.Resource
import uk.ac.tees.mad.w9642974.domain.Task
import uk.ac.tees.mad.w9642974.presentation.home.viewmodels.ResponseStatus
import uk.ac.tees.mad.w9642974.presentation.projectdetail.AddTaskDestination
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle[AddTaskDestination.projectIdArg])

    private val _addTaskState = Channel<ResponseStatus>()
    val addTaskState = _addTaskState.receiveAsFlow()

    var allMembers by mutableStateOf(listOf<Member>())
        private set

    init {
        getAllProjectMembers()
    }

    private fun getAllProjectMembers() = viewModelScope.launch {
        firestoreRepository.getAllProjectMembers(projectId).collect {
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

    fun addTaskToProject(task: Task) = viewModelScope.launch {
        firestoreRepository.addTaskToProject(projectId, task).collect {
            when (it) {
                is Resource.Error ->
                    _addTaskState.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _addTaskState.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _addTaskState.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }
}