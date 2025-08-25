package com.blpw.pixelex.features.stackUserList.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blpw.pixelex.common.data.LoadingStatus
import com.blpw.pixelex.common.domain.DataError.Remote
import com.blpw.pixelex.common.domain.onError
import com.blpw.pixelex.common.domain.onSuccess
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StackUsersViewModel @Inject constructor(
    private val stackUserRepository: StackUserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(StackUserState())
    val state = _state
        .onStart {
            getStackExchangeUsers()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun getStackExchangeUsers() {
        _state.update { it.copy(loadingStatus = LoadingStatus.Loading) }
        viewModelScope.launch {
            stackUserRepository
                .getStackUsers()
                .onSuccess { users ->
                    _state.update { it.copy(users = users, loadingStatus = LoadingStatus.Loaded) }
                    Log.d("TAG", "getStackExchangeUsers: $users")
                }
                .onError { error->
                    _state.update { it.copy(loadingStatus = LoadingStatus.Error) }
                    handleError(error)
                }
        }
    }

    private fun handleError(error: Remote) {
        when(error) {
            is Remote.NoInternet -> updateErrorState(UiErrorType.NO_INTERNET)
            else -> updateErrorState(UiErrorType.UNKNOWN)
        }
    }

    private fun updateErrorState(error: UiErrorType) {
        Log.d("TAG", "updateErrorState: $error")
        _state.update { it.copy(error = error) }
    }

    fun retry() {
        // avoid parallel loads
        if (_state.value.loadingStatus == LoadingStatus.Loading) return
        getStackExchangeUsers()
    }
}