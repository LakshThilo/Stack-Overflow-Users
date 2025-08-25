package com.blpw.pixelex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.blpw.pixelex.features.stackUserList.presentation.StackUserListScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "stackUser_list"
    ) {
        composable("stackUser_list") {
            StackUserListScreen()
        }
    }
}