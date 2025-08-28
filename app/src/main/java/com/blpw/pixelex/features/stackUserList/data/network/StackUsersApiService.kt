package com.blpw.pixelex.features.stackUserList.data.network

import com.blpw.pixelex.features.stackUserList.data.dto.StackUserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StackUsersApiService {

    @GET("users")
    suspend fun getStackExchangeUsers(
        @Query("page") page: Int = 1,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "reputation",
        @Query("site") site: String = "stackoverflow",
        @Query("key") key: String = "rl_rab7S3vTi1eYdpPXcxBtqx1gw"
    ): StackUserDto
}