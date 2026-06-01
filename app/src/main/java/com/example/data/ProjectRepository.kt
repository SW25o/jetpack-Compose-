package com.example.data

import kotlinx.coroutines.flow.Flow
import java.io.File

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<Project>> = projectDao.getAllProjectsFlow()

    fun getFilesForProject(projectId: Int): Flow<List<ProjectFile>> {
        return projectDao.getFilesForProjectFlow(projectId)
    }

    suspend fun getProjectById(projectId: Int): Project? {
        return projectDao.getProjectById(projectId)
    }

    suspend fun getProjectFilesList(projectId: Int): List<ProjectFile> {
        return projectDao.getFilesForProject(projectId)
    }

    suspend fun createProject(name: String, packageName: String, minSdk: Int, targetSdk: Int): Project {
        val project = Project(
            name = name,
            packageName = packageName,
            minSdk = minSdk,
            targetSdk = targetSdk,
            createdAt = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis()
        )
        val projectId = projectDao.insertProject(project).toInt()
        val savedProject = project.copy(id = projectId)

        // Seed basic Kotlin + Jetpack Compose project files
        val templates = generateProjectTemplate(savedProject)
        projectDao.insertFiles(templates)

        return savedProject
    }

    suspend fun saveFile(file: ProjectFile) {
        projectDao.updateFile(file)
        val project = projectDao.getProjectById(file.projectId)
        if (project != null) {
            projectDao.updateProject(project.copy(lastModified = System.currentTimeMillis()))
        }
    }

    suspend fun addFile(projectId: Int, filePath: String, content: String): Boolean {
        val existing = projectDao.getFileByPath(projectId, filePath)
        if (existing != null) return false
        projectDao.insertFile(ProjectFile(projectId = projectId, filePath = filePath, content = content))
        return true
    }

    suspend fun deleteFile(projectId: Int, filePath: String) {
        projectDao.deleteFileByPath(projectId, filePath)
    }

    suspend fun deleteProject(projectId: Int) {
        projectDao.deleteProjectById(projectId)
    }

    private fun generateProjectTemplate(project: Project): List<ProjectFile> {
        val appName = project.name
        val pkg = project.packageName
        
        return listOf(
            ProjectFile(
                projectId = project.id,
                filePath = "app/build.gradle.kts",
                isReadOnly = true,
                content = """// Gradle build file for ${appName}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "${pkg}"
    compileSdk = ${project.targetSdk}

    defaultConfig {
        applicationId = "${pkg}"
        minSdk = ${project.minSdk}
        targetSdk = ${project.targetSdk}
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose")
}
""".trimIndent()
            ),
            ProjectFile(
                projectId = project.id,
                filePath = "app/src/main/AndroidManifest.xml",
                isReadOnly = false,
                content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="true"
        android:label="${appName}"
        android:supportsRtl="true">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()
            ),
            ProjectFile(
                projectId = project.id,
                filePath = "app/src/main/java/MainActivity.kt",
                isReadOnly = false,
                content = """package ${pkg}

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                BNAppTemplate(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun BNAppTemplate(modifier: Modifier = Modifier) {
    var count by remember { mutableStateOf(0) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to ${appName}!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Build incredible local apps in Kotlin and Jetpack Compose easily.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { count++ }) {
            Text(text = "Clicked: ${'$'}count times")
        }
    }
}
""".trimIndent()
            ),
            ProjectFile(
                projectId = project.id,
                filePath = "app/src/main/java/theme/Theme.kt",
                isReadOnly = false,
                content = """package ${pkg}.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6366F1),
    secondary = Color(0xFFD946EF),
    tertiary = Color(0xFF14B8A6)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
""".trimIndent()
            )
        )
    }
}
