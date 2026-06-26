package com.gitvault.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val url: String,
    val tokenId: Long? = null,
    val localPath: String,
    val lastPulledAt: Long = 0, // epoch millis
    val createdAt: Long = System.currentTimeMillis()
)
