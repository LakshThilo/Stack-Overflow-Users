package com.blpw.pixelex.navigation

import androidx.navigation.NavHostController

class NavigationHelperImpl(
    private val navController: NavHostController
) : NavigationHelper {

    override fun navigateTo(destination: String) {
        navController.navigate(destination)
    }

    override fun back() {
        navController.popBackStack()
    }

    override fun showLoading() {
    }

    override fun hideLoading() {
    }

    override fun handleError(retry: () -> Unit) {
        retry()
    }
}