package com.blpw.pixelex.features.stackUserList.data.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StackUserDto(
    @Json(name = "has_more") val hasMore: Boolean,
    @Json(name = "items") val items: List<UserInfoDto>,
    @Json(name = "quota_max") val quotaMax: Int,
    @Json(name = "quota_remaining") val quotaRemaining: Int
)

@JsonClass(generateAdapter = true)
data class UserInfoDto(
    @Json(name = "user_id") val userId: Int,
    @Json(name = "account_id") val accountId: Int? = null,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "profile_image") val profileImage: String? = null,
    @Json(name = "reputation") val reputation: Int,
    @Json(name = "location") val location: String? = null,
    @Json(name = "badge_counts") val badgeCounts: BadgeCountsDto,
    @Json(name = "user_type") val userType: String,
    @Json(name = "link") val link: String,
    @Json(name = "website_url") val websiteUrl: String? = null,
    @Json(name = "accept_rate") val acceptRate: Int? = null,
    @Json(name = "creation_date") val creationDate: Int,
    @Json(name = "is_employee") val isEmployee: Boolean,
    @Json(name = "last_access_date") val lastAccessDate: Int,
    @Json(name = "last_modified_date") val lastModifiedDate: Int,
    @Json(name = "reputation_change_day") val reputationChangeDay: Int,
    @Json(name = "reputation_change_month") val reputationChangeMonth: Int,
    @Json(name = "reputation_change_quarter") val reputationChangeQuarter: Int,
    @Json(name = "reputation_change_week") val reputationChangeWeek: Int,
    @Json(name = "reputation_change_year") val reputationChangeYear: Int,
)

data class BadgeCountsDto(
    @Json(name = "bronze") val bronze: Int,
    @Json(name = "gold") val gold: Int,
    @Json(name = "silver") val silver: Int
)