package com.blpw.pixelex.features.stackUserList.data.mappers

import com.blpw.pixelex.features.stackUserList.data.dto.UserInfoDto
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel

fun UserInfoDto.toDomain() = StackUserInfoModel(
    userId = userId,
    accountId = accountId,
    displayName = displayName,
    profileImage = profileImage,
    reputation = reputation,
    location = location,
    userType = userType,
    link = link,
    websiteUrl = websiteUrl,
    acceptRate = acceptRate,
    creationDate = creationDate,
    isEmployee = isEmployee,
    lastAccessDate = lastAccessDate,
    lastModifiedDate = lastModifiedDate,
    reputationChangeDay = reputationChangeDay,
    reputationChangeMonth = reputationChangeMonth,
    reputationChangeQuarter = reputationChangeQuarter,
    reputationChangeWeek = reputationChangeWeek,
    reputationChangeYear = reputationChangeYear,
    bronze = badgeCounts.bronze,
    silver = badgeCounts.silver,
    gold = badgeCounts.gold,
)

