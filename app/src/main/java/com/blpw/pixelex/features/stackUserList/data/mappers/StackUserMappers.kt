package com.blpw.pixelex.features.stackUserList.data.mappers

import com.blpw.pixelex.features.stackUserList.data.dto.BadgeCountsDto
import com.blpw.pixelex.features.stackUserList.data.dto.StackUserInfoDto
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserInfoEntity
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel


fun StackUserInfoDto.toStackUserInfoModel() = StackUserInfoModel(
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
    isFollowed = false
)

fun StackUserInfoDto.toStackUserInfoEntity() = StackUserInfoEntity(
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
    gold = badgeCounts.gold
)

fun StackUserInfoEntity.toStackUserInfoDto() = StackUserInfoDto(
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
    badgeCounts = BadgeCountsDto(
        bronze = bronze,
        silver = silver,
        gold = gold,
    )
)

