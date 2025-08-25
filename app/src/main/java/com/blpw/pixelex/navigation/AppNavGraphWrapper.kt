package com.blpw.pixelex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

val LocalNavigationHelper = staticCompositionLocalOf<NavigationHelper> {
    error("NavigationHelper not provided")
}

@Composable
fun AppNavGraphWrapper(navController: NavHostController) {
    val helper = remember { NavigationHelperImpl(navController) }

    CompositionLocalProvider(LocalNavigationHelper provides helper) {
        AppNavGraph(navController)
    }
}