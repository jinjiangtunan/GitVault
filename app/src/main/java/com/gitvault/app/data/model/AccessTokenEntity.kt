package com.gitvault.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "access_tokens")
data class AccessTokenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String,       // e.g. "GitHub", "Gitee"
    val token: String,       // encrypted token value
    val createdAt: Long = System.currentTimeMillis()
)
