package uk.ac.tees.mad.w9642974.presentation.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.w9642974.data.repository.FirestoreRepository
import uk.ac.tees.mad.w9642974.domain.Member
import uk.ac.tees.mad.w9642974.domain.Resource
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
): ViewModel() {

    private val _currentUserData = Channel<UserState>()
    val currentUserData = _currentUserData.receiveAsFlow()

    private val _uiState = MutableStateFlow(UserResponse.CurrentUser())
    val uiState = _uiState.asStateFlow()

    fun updateUiState(value: UserResponse.CurrentUser) {
        _uiState.update {
            value
        }
    }

    init {
        getAllMembers()
        getUserData()
    }


    var allMembers by mutableStateOf(listOf<Member>())
        private set

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

    private fun getUserData() = viewModelScope.launch {
        firestoreRepository.getCurrentUser().collect {
            when (it) {
                is Resource.Error -> {
                    _currentUserData.send(UserState(error = it.message))
                }

                is Resource.Loading -> {
                    _currentUserData.send(UserState(isLoading = true))
                }

                is Resource.Success -> {
                    _currentUserData.send(UserState(data = it.data))
                }
            }
        }
    }
}

data class UserState(
    val data: UserResponse? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

data class UserResponse(
    val item: CurrentUser?,
    val key: String?
) {
    data class CurrentUser(
        val name: String = "",
        val email: String = "",
        val profileImage: String = ""
    )
}