package com.gitvault.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gitvault.app.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.REPO_LIST
    ) {
        composable(Routes.REPO_LIST) {
            RepoListScreen(
                onAddRepo = { navController.navigate(Routes.ADD_REPO) },
                onOpenRepo = { repoId ->
                    navController.navigate(Routes.fileBrowser(repoId))
                },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.ADD_REPO) {
            AddRepoScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.FILE_BROWSER,
            arguments = listOf(navArgument("repoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getLong("repoId") ?: return@composable
            FileBrowserScreen(
                repoId = repoId,
                onBack = { navController.popBackStack() },
                onOpenFile = { filePath ->
                    navController.navigate(Routes.markdownReader(repoId, filePath))
                }
            )
        }

        composable(
            route = Routes.MARKDOWN_READER,
            arguments = listOf(
                navArgument("repoId") { type = NavType.LongType },
                navArgument("filePath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getLong("repoId") ?: return@composable
            val encodedPath = backStackEntry.arguments?.getString("filePath") ?: return@composable
            val filePath = java.net.URLDecoder.decode(encodedPath, "UTF-8")
            MarkdownReaderScreen(
                repoId = repoId,
                filePath = filePath,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
