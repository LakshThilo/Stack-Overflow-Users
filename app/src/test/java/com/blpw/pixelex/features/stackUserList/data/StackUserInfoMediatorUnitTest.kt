@file:OptIn(ExperimentalPagingApi::class)

package com.blpw.pixelex.features.stackUserList.data

import android.content.Context
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.blpw.pixelex.features.stackUserList.data.dto.BadgeCountsDto
import com.blpw.pixelex.features.stackUserList.data.dto.StackUserDto
import com.blpw.pixelex.features.stackUserList.data.dto.StackUserInfoDto
import com.blpw.pixelex.features.stackUserList.data.local.ApiBackoff
import com.blpw.pixelex.features.stackUserList.data.local.StackUserDatabase
import com.blpw.pixelex.features.stackUserList.data.local.StackUserInfoMediator
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserJoin
import com.blpw.pixelex.features.stackUserList.data.network.StackUsersApiService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class StackUserInfoMediatorUnitTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var api: StackUsersApiService

    private lateinit var db: StackUserDatabase
    private lateinit var mediator: StackUserInfoMediator

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, StackUserDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        mediator = StackUserInfoMediator(api, db)
    }

    @After
    fun tearDown() {
        db.close()
        unmockkAll()
    }

    @Test
    fun refresh_success_inserts_and_notEnd() = runTest {
        coEvery {
            api.getStackExchangeUsers(page = 1, pageSize = 30, any(), any(), any(), any())
        } returns dtoPage((1..30).toList(), hasMore = true, backoff = null)

        val result = mediator.load(LoadType.REFRESH, emptyState(pageSize = 30))

        assert(result is RemoteMediator.MediatorResult.Success && !result.endOfPaginationReached)
        Assert.assertEquals(30, db.stackUserInfoDao().countUsers())
    }

    @Test
    fun append_success_ends_when_no_more() = runTest {
        coEvery {
            api.getStackExchangeUsers(page = 1, pageSize = 30, any(), any(), any(), any())
        } returns dtoPage((1..30).toList(), hasMore = true)

        mediator.load(LoadType.REFRESH, emptyState(pageSize = 30))

        coEvery {
            api.getStackExchangeUsers(page = 2, pageSize = 30, any(), any(), any(), any())
        } returns dtoPage((31..60).toList(), hasMore = false)

        val existing = onePageStateFromDb(pageSize = 30)
        val result = mediator.load(LoadType.APPEND, existing)

        assert(result is RemoteMediator.MediatorResult.Success && result.endOfPaginationReached)
        Assert.assertEquals(60, db.stackUserInfoDao().countUsers())
    }

    @Test
    fun prepend_returns_end_without_api_call() = runTest {
        val result = mediator.load(LoadType.PREPEND, emptyState(pageSize = 30))
        assert(result is RemoteMediator.MediatorResult.Success && result.endOfPaginationReached)
        coVerify(exactly = 0) {
            api.getStackExchangeUsers(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun refresh_sets_backoff_when_body_has_backoff() = runTest {
        mockkObject(ApiBackoff)
        every { ApiBackoff.remainingMs() } returns 0
        every { ApiBackoff.setBackoffSeconds(any()) } just Runs

        coEvery {
            api.getStackExchangeUsers(page = 1, pageSize = 30, any(), any(), any(), any())
        } returns dtoPage((1..30).toList(), hasMore = true, backoff = 7)

        mediator.load(LoadType.REFRESH, emptyState(pageSize = 30))

        verify { ApiBackoff.setBackoffSeconds(7) }
    }

    @Test
    fun httpException_sets_backoff_from_retry_after() = runTest {
        mockkObject(ApiBackoff)
        every { ApiBackoff.remainingMs() } returns 0
        every { ApiBackoff.setBackoffSeconds(any()) } just Runs

        val body = """{"error_name":"throttle_violation","error_message":"back off"}"""
            .toResponseBody("application/json".toMediaType())

        val raw = builtResponse()

        val response = retrofit2.Response.error<StackUserDto>(body, raw)

        coEvery {
            api.getStackExchangeUsers(page = 1, pageSize = 30, any(), any(), any(), any())
        } throws retrofit2.HttpException(response)

        val result = mediator.load(LoadType.REFRESH, emptyState(pageSize = 30))

        assert(result is RemoteMediator.MediatorResult.Error)
        verify { ApiBackoff.setBackoffSeconds(5) }
    }

    @Test
    fun ioException_returns_error() = runTest {
        coEvery {
            api.getStackExchangeUsers(page = 1, pageSize = 30, any(), any(), any(), any())
        } throws IOException("boom")

        val result = mediator.load(LoadType.REFRESH, emptyState(pageSize = 30))
        assert(result is RemoteMediator.MediatorResult.Error)
    }

    private fun emptyState(pageSize: Int) = PagingState<Int, StackUserJoin>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 0
    )

    private suspend fun onePageStateFromDb(pageSize: Int): PagingState<Int, StackUserJoin> {
        val page = db.stackUserInfoDao().pagingSource()
            .load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = pageSize,
                    placeholdersEnabled = false
                )
            ) as PagingSource.LoadResult.Page
        return PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 0
        )
    }

    private fun dtoUser(id: Int) = StackUserInfoDto(
        userId = id,
        accountId = null,
        displayName = "User $id",
        profileImage = null,
        reputation = 14256,
        location = null,
        badgeCounts = BadgeCountsDto(bronze = 123, gold = 344, silver = 657),
        userType = "",
        link = "",
        websiteUrl = null,
        acceptRate = 14256,
        creationDate = 14256,
        isEmployee = false,
        lastAccessDate = 14256,
        lastModifiedDate = 14256,
        reputationChangeDay = 14256,
        reputationChangeMonth = 14256,
        reputationChangeQuarter = 14256,
        reputationChangeWeek = 14256,
        reputationChangeYear = 14256,
    )

    private fun dtoPage(ids: List<Int>, hasMore: Boolean, backoff: Int? = null) =
        StackUserDto(
            hasMore = hasMore,
            items = ids.map(::dtoUser),
            quotaMax = 10000,
            quotaRemaining = 9999,
            backoff = backoff
        )

    private fun builtResponse(): Response = Response.Builder()
        .request(
            Request.Builder()
                .url("https://api.stackexchange.com/2.3/users")
                .build()
        )
        .protocol(Protocol.HTTP_1_1)
        .code(400)
        .message("Bad Request")
        .header("Retry-After", "5")
        .build()
}

