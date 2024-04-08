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
import uk.ac.tees.mad.w9642974.data.repository.ApiRepository
import uk.ac.tees.mad.w9642974.data.repository.FirestoreRepository
import uk.ac.tees.mad.w9642974.domain.Project
import uk.ac.tees.mad.w9642974.domain.Quotes
import uk.ac.tees.mad.w9642974.domain.Resource
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    var quoteUiState: QuoteUiState by mutableStateOf(QuoteUiState.Loading)
        private set

    fun fetchQuote() = viewModelScope.launch {
        quoteUiState = try {
            QuoteUiState.Success(
                apiRepository.getQuote()
            )
        } catch (e: IOException) {
            e.printStackTrace()
            QuoteUiState.Error
        }
    }
    private val _myProjectState = Channel<ProjectListResponseStatus>()
    val myProjectState = _myProjectState.receiveAsFlow()

    fun getMyAllProjects() = viewModelScope.launch {
        firestoreRepository.getMyProjects().collect{
            when(it) {
                is Resource.Error ->
                    _myProjectState.send(ProjectListResponseStatus(isError = it.message))

                is Resource.Loading -> {
                    _myProjectState.send(ProjectListResponseStatus(isLoading = true))
                }

                is Resource.Success -> {
                    _myProjectState.send(ProjectListResponseStatus(isSuccess = it.data))
                }
            }
        }
    }
}

data class ProjectListResponseStatus(
    val isLoading: Boolean = false,
    val isSuccess: List<Project>? = null,
    val isError: String? = null
)

sealed interface QuoteUiState {
    data class Success(val quote: Quotes) : QuoteUiState
    object Error : QuoteUiState
    object Loading : QuoteUiState
}