package com.blpw.pixelex.navigation

interface NavigationHelper {
    fun navigateTo(destination: String)
    fun back()
    fun showLoading()
    fun hideLoading()
    fun handleError(retry: () -> Unit)
}