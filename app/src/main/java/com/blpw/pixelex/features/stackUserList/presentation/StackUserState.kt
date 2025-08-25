package com.blpw.pixelex.features.stackUserList.presentation

import com.blpw.pixelex.common.data.LoadingStatus
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel

data class StackUserState(
    val users: List<StackUserInfoModel> = emptyList(),
    val loadingStatus: LoadingStatus = LoadingStatus.NotLoaded,
    val error: UiErrorType = UiErrorType.UNKNOWN
)

enum class UiErrorType {
    NO_INTERNET,
    UNKNOWN
}