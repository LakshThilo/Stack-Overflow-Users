@file:OptIn(ExperimentalCoroutinesApi::class)

package com.blpw.pixelex.features.stackUserList.presentation

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StackUsersViewModelTest {

    @get:Rule val mockkRule = MockKRule(this)
    private val mainDispatcher = UnconfinedTestDispatcher()

    @RelaxedMockK
    private lateinit var repo: StackUserRepository

    private lateinit var vm: StackUsersViewModel

    private fun user(id: Int, name: String, rep: Int, followed: Boolean = false) =
        StackUserInfoModel(
            userId = id,
            displayName = name,
            profileImage = null,
            reputation = rep,
            location = null,
            link = "",
            websiteUrl = null,
            gold = 0, silver = 0, bronze = 0,
            isFollowed = followed,
            accountId = null,
            userType = "",
            acceptRate = null,
            creationDate = 123342,
            isEmployee = false,
            lastAccessDate = 64564,
            lastModifiedDate = null,
            reputationChangeDay = 56456,
            reputationChangeMonth = 56453,
            reputationChangeQuarter = 876845,
            reputationChangeWeek = 654123,
            reputationChangeYear = 4543
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
   }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun usersPagingFromRemote_emits_repo_data() = runTest {
        every { repo.getStackUserFromRemote() } returns
                flowOf(PagingData.from(listOf(user(1, "Alice", 1000))))

        val vm = StackUsersViewModel(repo)
        try {
            val list = vm.usersPagingFromRemote.first().let { pd ->
                flowOf(pd).asSnapshot()
            }
            assertThat(list.map { it.displayName }).containsExactly("Alice")
        } finally {
            vm.viewModelScope.cancel()
            advanceUntilIdle()
        }
    }

    @Test
    fun onFollowClick_toggles_and_calls_repo() = runTest {
        every { repo.getStackUserFromRemote() } returns
                flowOf(PagingData.from(listOf(user(1, "Alice", 1000))))

        val vm = StackUsersViewModel(repo)
        // Act
        vm.onFollowClick(userId = 42, isCurrentlyFollowed = false) // should follow
        vm.onFollowClick(userId = 42, isCurrentlyFollowed = true)  // should unfollow
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { repo.setFollow(42, true) }
        coVerify(exactly = 1) { repo.setFollow(42, false) }
    }
}