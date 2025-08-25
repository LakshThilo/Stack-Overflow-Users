package com.blpw.pixelex.features.stackUserList.domain

data class StackUserInfoModel(
    val userId: Int,
    val accountId: Int? = null,
    val displayName: String,
    val profileImage: String? = null,
    val reputation: Int? = null,
    val location: String? = null,
    val userType: String,
    val link: String,
    val websiteUrl: String? = null,
    val acceptRate: Int? = null,
    val creationDate: Int,
    val isEmployee: Boolean,
    val lastAccessDate: Int,
    val lastModifiedDate: Int,
    val reputationChangeDay: Int,
    val reputationChangeMonth: Int,
    val reputationChangeQuarter: Int,
    val reputationChangeWeek: Int,
    val reputationChangeYear: Int,
    val bronze: Int,
    val silver: Int,
    val gold: Int
)
