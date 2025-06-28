package com.ryun.ishare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // 👈 추가된 import
import com.ryun.ishare.ui.theme.IShareTheme
import com.ryun.ishare.ui.screens.MainScreen
import com.ryun.ishare.ui.screens.IdeaListScreen
import com.ryun.ishare.ui.screens.MyIdeaScreen
//import com.ryun.ishare.ui.screens.IdeaDetailScreen
//import com.ryun.ishare.ui.screens.MyIdeaDetailScreen // MyIdeaDetailScreen import 추가
import com.ryun.ishare.ui.screens.IdeaWriteScreen // MyIdeaDetailScreen import 추가


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IShareTheme {
                // Set the nav controller
                val navController = rememberNavController()

                // Define the NavHost to manage navigation between screens
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable(
                        "idea_list?category={category}&subCategory={subCategory}",
                        arguments = listOf(
                            navArgument("category") { defaultValue = "" },
                            navArgument("subCategory") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        val subCategory = backStackEntry.arguments?.getString("subCategory") ?: ""

                        IdeaListScreen(
                            navController = navController
                        )
                    }
                    composable(
                        "my_idea?category={category}&subCategory={subCategory}",
                        arguments = listOf(
                            navArgument("category") { defaultValue = "" },
                            navArgument("subCategory") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("category") ?: ""
                        val subCategory = backStackEntry.arguments?.getString("subCategory") ?: ""
                        MyIdeaScreen(
                            navController = navController
                        )
                    }
                    composable(
                        route  = "idea_detail/{ideaId}?category={category}&subCategory={subCategory}",
                        arguments = listOf(
                            navArgument("ideaId") { },
                            navArgument("category") { defaultValue = "" },
                            navArgument("subCategory") { defaultValue = "" }
                        )
                    ) { backStackEntry -> // 수정된 부분: 경로에 파라미터 추가
                        val ideaId = requireNotNull(backStackEntry.arguments?.getString("ideaId"))
                        //IdeaDetailScreen(navController, ideaId)
                    }
                    composable(
                        route = "my_idea_detail/{ideaId}?category={category}&subCategory={subCategory}",
                        arguments = listOf(
                            navArgument("ideaId") { },
                            navArgument("category") { defaultValue = "" },
                            navArgument("subCategory") { defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val ideaId = backStackEntry.arguments?.getString("ideaId")
                        //MyIdeaDetailScreen(navController = navController, ideaId = ideaId)
                    }
                    composable(
                        route = "idea_write?ideaId={ideaId}&title={title}&content={content}&category={category}&subCategory={subCategory}",
                        arguments = listOf(
                            navArgument("ideaId") { nullable = true },
                            navArgument("title") { nullable = true },
                            navArgument("content") { nullable = true },
                            navArgument("category") { defaultValue = "" },
                            navArgument("subCategory") { defaultValue = "" }
                        )
                    ) {backStackEntry ->
                        IdeaWriteScreen(
                            navController = navController,
                            ideaId = backStackEntry.arguments?.getString("ideaId"),    // ✅ ideaId 넘겨주기
                            title = backStackEntry.arguments?.getString("title"),
                            content = backStackEntry.arguments?.getString("content"),
                            category = backStackEntry.arguments?.getString("category"),
                            subCategory = backStackEntry.arguments?.getString("subCategory")
                        )
                    }
                }
            }
        }
    }
}
