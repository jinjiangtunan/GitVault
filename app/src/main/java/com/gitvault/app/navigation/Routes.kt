package com.gitvault.app.navigation

/**
 * Route constants for Compose Navigation.
 */
object Routes {
    const val REPO_LIST = "repo_list"
    const val ADD_REPO = "add_repo"
    const val FILE_BROWSER = "file_browser/{repoId}"
    const val MARKDOWN_READER = "markdown_reader/{repoId}/{filePath}"
    const val SETTINGS = "settings"

    fun fileBrowser(repoId: Long): String = "file_browser/$repoId"
    fun markdownReader(repoId: Long, filePath: String): String =
        "markdown_reader/$repoId/${java.net.URLEncoder.encode(filePath, "UTF-8")}"
}
