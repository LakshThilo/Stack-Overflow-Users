@file:OptIn(ExperimentalPagingApi::class)

package com.blpw.pixelex.features.stackUserList.data

import android.content.Context
import android.util.Log
import androidx.paging.*
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result
import com.blpw.pixelex.features.stackUserList.data.dto.BadgeCountsDto
import com.blpw.pixelex.features.stackUserList.data.local.StackUserDatabase
import com.blpw.pixelex.features.stackUserList.data.local.dao.StackUserInfoDao
import com.blpw.pixelex.features.stackUserList.data.local.entities.FollowEntity
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserInfoEntity
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserJoin
import com.blpw.pixelex.features.stackUserList.data.local.StackUserInfoMediator
import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSource
import com.blpw.pixelex.features.stackUserList.data.dto.StackUserDto
import com.blpw.pixelex.features.stackUserList.data.dto.StackUserInfoDto
import com.blpw.pixelex.features.stackUserList.data.repository.DefaultStackUsersRepository
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class DefaultStackUsersRepositoryTest {

    @get:Rule val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var remote: RemoteStackUsersDataSource

    private lateinit var db: StackUserDatabase
    private lateinit var dao: StackUserInfoDao

    private lateinit var mediator: StackUserInfoMediator
    private lateinit var repo: DefaultStackUsersRepository

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
        dao = db.stackUserInfoDao()

        mediator = object : StackUserInfoMediator(api = mockk(relaxed = true), db = db) {
            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, StackUserJoin>
            ): MediatorResult = MediatorResult.Success(endOfPaginationReached = true)
        }

        repo = DefaultStackUsersRepository(remote, mediator, db)
    }

    @After
    fun tearDown() {
        db.close()
        unmockkAll()
    }


    @Test
    fun getStackUsers_success_mapsToDomain() = runTest {
        // Arrange
        val dto = StackUserDto(
            hasMore = false,
            items = listOf(
                userDto(1, "Alice", 1000),
                userDto(2, "Bob", 900)
            ),
            quotaMax = 10_000,
            quotaRemaining = 9_999,
            backoff = null
        )
        coEvery { remote.getStackUsers() } returns Result.Success(dto)

        // Act
        val res = repo.getStackUsers()

        // Assert
        assertThat(res).isInstanceOf(Result.Success::class.java)
        val list = (res as Result.Success).data
        assertThat(list).hasSize(2)
        assertThat(list.first()).isEqualTo(
            StackUserInfoModel(
                userId = 1,
                accountId = null,
                displayName = "Alice",
                profileImage = null,
                reputation = 1000,
                location = null,
                userType = "",
                link = "",
                websiteUrl = null,
                acceptRate = null,
                creationDate = 4546,
                isEmployee = false,
                lastAccessDate = 4546,
                lastModifiedDate = null,
                reputationChangeDay = 5467,
                reputationChangeMonth = 5467,
                reputationChangeQuarter = 5467,
                reputationChangeWeek = 5467,
                reputationChangeYear = 5467,
                gold = 456,
                silver = 768,
                bronze = 657,
                isFollowed = false
            )
        )
    }

    @Test
    fun getStackUsers_error_propagates() = runTest {
        coEvery { remote.getStackUsers() } returns Result.Error(DataError.Remote.NoInternet)
        val res = repo.getStackUsers()
        assertThat(res).isInstanceOf(Result.Error::class.java)
        assertThat((res as Result.Error).error).isEqualTo(DataError.Remote.NoInternet)
    }

    @Test
    fun getStackUserFromRemote_readsRoom_and_mapsToDomain() = runTest {

        dao.upsertUsers(
            listOf(
                userEntry(id = 1, name = "Alice", rep = 1000),
                userEntry(id = 2, name = "Bob", rep = 900)
            )
        )

        dao.follow(FollowEntity(userId = 2))

        val flow = repo.getStackUserFromRemote()
        val snapshot: List<StackUserInfoModel> = flow.asSnapshot()


        assertThat(snapshot).hasSize(2)
        val bob = snapshot.first { it.userId == 2 }
        assertThat(bob.displayName).isEqualTo("Bob")
        assertThat(bob.isFollowed).isTrue()
        val alice = snapshot.first { it.userId == 1 }
        assertThat(alice.isFollowed).isFalse()
    }


    @Test
    fun setFollow_calls_follow_unfollow() = runTest {
        repo.setFollow(userId = 10, follow = true)
        repo.setFollow(userId = 10, follow = false)

        val follows = db.stackUserInfoDao().getAllFollowsIds()
        assertThat(follows).doesNotContain(10)
    }


    @Test
    fun getStackUsersUsingPaging_emits_items_from_custom_pagingSource() = runTest {
        mockkConstructor(StackUsersPagingSource::class)
        coEvery {
            anyConstructed<StackUsersPagingSource>()
                .load(any())
        } returns PagingSource.LoadResult.Page(
            data = listOf(
                userModel(id = 1, name = "Alice", rep = 1000),
                userModel(id = 2, name = "Bob", rep = 900)
            ),
            prevKey = null,
            nextKey = null
        )

        val flow = repo.getStackUsersUsingPaging(sort = "reputation")
        val snapshot = flow.asSnapshot()

        assertThat(snapshot.map { it.userId }).containsExactly(1, 2)
    }

    private fun userDto(id: Int, name: String, rep: Int) = StackUserInfoDto(
        userId = id,
        accountId = null,
        displayName = name,
        profileImage = null,
        reputation = rep,
        location = null,
        userType = "",
        link = "",
        websiteUrl = null,
        acceptRate = null,
        creationDate = 4546,
        isEmployee = false,
        lastAccessDate = 4546,
        lastModifiedDate = null,
        reputationChangeDay = 5467,
        reputationChangeMonth = 5467,
        reputationChangeQuarter = 5467,
        reputationChangeWeek = 5467,
        reputationChangeYear = 5467,
        badgeCounts = BadgeCountsDto(gold = 456, silver = 768, bronze = 657)
    )

    private fun userEntry(id: Int, name: String, rep: Int) = StackUserInfoEntity(
        userId = id,
        accountId = null,
        displayName = name,
        profileImage = null,
        reputation = rep,
        location = null,
        userType = "",
        link = "",
        websiteUrl = null,
        acceptRate = null,
        creationDate = 4546,
        isEmployee = false,
        lastAccessDate = 4546,
        lastModifiedDate = null,
        reputationChangeDay = 5467,
        reputationChangeMonth = 5467,
        reputationChangeQuarter = 5467,
        reputationChangeWeek = 5467,
        reputationChangeYear = 5467,
        gold = 456, silver = 768, bronze = 657
    )

    private fun userModel(id: Int, name: String, rep: Int) = StackUserInfoModel(
        userId = id,
        accountId = null,
        displayName = name,
        profileImage = null,
        reputation = rep,
        location = null,
        userType = "",
        link = "",
        websiteUrl = null,
        acceptRate = null,
        creationDate = 4546,
        isEmployee = false,
        lastAccessDate = 4546,
        lastModifiedDate = null,
        reputationChangeDay = 5467,
        reputationChangeMonth = 5467,
        reputationChangeQuarter = 5467,
        reputationChangeWeek = 5467,
        reputationChangeYear = 5467,
        gold = 456, silver = 768,
        bronze = 657,
        isFollowed = false
    )
}
