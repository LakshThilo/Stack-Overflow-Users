package com.blpw.pixelex.features.stackUserList.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import com.blpw.pixelex.features.stackUserList.domain.StackUserSort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StackUsersViewModel @Inject constructor(
    private val stackUserRepository: StackUserRepository,
) : ViewModel() {

    val sort = MutableStateFlow(StackUserSort.REPUTATION)

    val usersPagingFromRemote: Flow<PagingData<StackUserInfoModel>> =
        stackUserRepository.getStackUserFromRemote()
            .cachedIn(viewModelScope)

    fun onFollowClick(userId: Int, isCurrentlyFollowed: Boolean) {
        viewModelScope.launch {
            stackUserRepository.setFollow(userId, follow = !isCurrentlyFollowed)
        }
    }

    val usersPaging: StateFlow<PagingData<StackUserInfoModel>> =
        sort.flatMapLatest { stackUserRepository.getStackUsersUsingPaging(it.name) }
            .cachedIn(viewModelScope)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                PagingData.empty()
            )

    fun updateSort(newSort: StackUserSort) {
        if (sort.value != newSort) sort.value = newSort
    }
}

