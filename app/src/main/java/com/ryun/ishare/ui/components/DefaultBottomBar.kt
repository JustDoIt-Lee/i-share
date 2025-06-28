package com.ryun.ishare.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.ryun.ishare.navigation.NavRoutes
import navigateSingleTopTo

@Composable
fun DefaultBottomBar(navController: NavController) {
    IdeaBottomBar(
        onBackClick = {
            navController.popBackStack()
        },
        onMainClick = {
            navController.navigateSingleTopTo(NavRoutes.MAIN)
        },
        onWriteClick = {
            navController.navigateSingleTopTo(NavRoutes.IDEA_WRITE)
        }
    )
}
