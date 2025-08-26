package com.blpw.pixelex.features.stackUserList.presentation.util

import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel

enum class StackUserSort(val label: String) {
    REPUTATION("Reputation"),
    CREATION("Creation"),
    NAME("Name"),
    MODIFIED("Modified")
}

fun List<StackUserInfoModel>.sortedByOption(option: StackUserSort): List<StackUserInfoModel> =
    when (option) {
        StackUserSort.REPUTATION -> sortedByDescending { it.reputation }
        StackUserSort.CREATION   -> sortedByDescending { it.creationDate.toLong() } // seconds -> bigger = newer
        StackUserSort.NAME       -> sortedBy { it.displayName.lowercase() }
        StackUserSort.MODIFIED   -> sortedByDescending { it.lastModifiedDate?.toLong() }
    }