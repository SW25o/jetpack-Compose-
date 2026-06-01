package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val packageName: String,
    val targetSdk: Int = 35,
    val minSdk: Int = 24,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["projectId", "filePath"], unique = true)]
)
data class ProjectFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,
    val filePath: String,
    val content: String,
    val isReadOnly: Boolean = false
)
