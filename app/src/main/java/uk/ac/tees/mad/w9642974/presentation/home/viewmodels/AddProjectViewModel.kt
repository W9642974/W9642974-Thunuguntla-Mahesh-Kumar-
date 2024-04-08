package uk.ac.tees.mad.w9642974.presentation.home.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9642974.data.repository.FirestoreRepository
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Project
import uk.ac.tees.mad.w9642974.domain.Resource
import javax.inject.Inject

@HiltViewModel
class AddProjectViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _createProjectState = Channel<ResponseStatus>()
    val createProjectState = _createProjectState.receiveAsFlow()

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

    fun addProject(project: Project) = viewModelScope.launch {
        firestoreRepository.createNewProject(project).collect {
            when (it) {
                is Resource.Error ->
                    _createProjectState.send(ResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _createProjectState.send(ResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _createProjectState.send(ResponseStatus(isSuccess = it.data))
                }
            }
        }
    }
}

data class ResponseStatus(
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val isError: String? = null
)