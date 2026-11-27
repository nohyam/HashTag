package com.nohyam.hashtag.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nohyam.hashtag.ui.home.HomeScreen
import com.nohyam.hashtag.ui.search.SearchResultScreen
import com.nohyam.hashtag.ui.detail.WorkDetailScreen

@Composable
fun HashTagNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(onSearchClick = {
                navController.navigate("search") {
                    launchSingleTop = true
                }
            })
        }
        composable(
            route = "work/{mediaType}/{id}",
            arguments = listOf(
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("id") { type = NavType.IntType }
            )
        ) { entry ->
            WorkDetailScreen(
                id = entry.arguments?.getInt("id") ?: -1,
                mediaType = entry.arguments?.getString("mediaType").orEmpty(),
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("search") {
            SearchResultScreen(onBackClick = {
                navController.popBackStack()
            }, onWorkClick = { id, mediaType ->
                navController.navigate("work/$mediaType/$id") {
                    launchSingleTop = true
                }
            })
        }
    }
}
