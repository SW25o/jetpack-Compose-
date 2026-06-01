package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastModified DESC")
    fun getAllProjectsFlow(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Int): Project?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)

    @Query("SELECT * FROM project_files WHERE projectId = :projectId")
    fun getFilesForProjectFlow(projectId: Int): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId")
    suspend fun getFilesForProject(projectId: Int): List<ProjectFile>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId AND filePath = :filePath LIMIT 1")
    suspend fun getFileByPath(projectId: Int, filePath: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<ProjectFile>)

    @Update
    suspend fun updateFile(file: ProjectFile)

    @Query("DELETE FROM project_files WHERE id = :id")
    suspend fun deleteFileById(id: Int)

    @Query("DELETE FROM project_files WHERE projectId = :projectId AND filePath = :filePath")
    suspend fun deleteFileByPath(projectId: Int, filePath: String)
}
