package com.gitvault.app.domain

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Represents a node in the repository file tree.
 */
data class FileNode(
    val name: String,
    val path: String,         // relative to repo root
    val isDirectory: Boolean,
    val children: List<FileNode> = emptyList()
)

/**
 * Reads directory structure of a cloned repository.
 */
@Singleton
class FileExplorer @Inject constructor() {

    companion object {
        // Files we display in the browser
        val DISPLAY_EXTENSIONS = setOf(
            "md", "txt", "json", "yaml", "yml", "xml", "csv",
            "kt", "java", "py", "js", "ts", "html", "css",
            "gradle", "properties", "toml", "cfg", "ini", "sh",
            "sql", "graphql", "proto", "rs", "go", "swift", "c", "cpp", "h",
            "conf", "env", "lock", "gitignore"
        )
        val MARKDOWN_EXTENSIONS = setOf("md", "markdown", "mkd", "mdx")
    }

    /**
     * Build a tree of displayable files under [rootPath].
     * Respects .gitignore? Not in v1 — just show everything.
     */
    fun listFiles(rootPath: String): List<FileNode> {
        val root = File(rootPath)
        if (!root.exists() || !root.isDirectory) return emptyList()

        return buildTree(root, root.absolutePath)
    }

    private fun buildTree(dir: File, rootPath: String): List<FileNode> {
        val entries = dir.listFiles() ?: return emptyList()

        return entries
            .filter { !it.isHidden || it.name == ".gitignore" } // mostly skip .git
            .filter { file ->
                // Keep directories and displayable files
                file.isDirectory || isDisplayable(file.name)
            }
            .sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase() })
            .map { file ->
                val relativePath = file.absolutePath.removePrefix(rootPath).removePrefix("/")
                FileNode(
                    name = file.name,
                    path = relativePath,
                    isDirectory = file.isDirectory,
                    children = if (file.isDirectory) buildTree(file, rootPath) else emptyList()
                )
            }
    }

    /**
     * Read file content. Returns null if the file cannot be read.
     */
    fun readContent(filePath: String): String? {
        return try {
            File(filePath).readText()
        } catch (_: Exception) {
            null
        }
    }

    fun isMarkdown(path: String): Boolean {
        val extension = path.substringAfterLast('.', "").lowercase()
        return extension in MARKDOWN_EXTENSIONS
    }

    private fun isDisplayable(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return extension in DISPLAY_EXTENSIONS
    }
}
