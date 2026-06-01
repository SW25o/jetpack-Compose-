package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Project
import com.example.data.ProjectFile
import kotlinx.coroutines.launch

// Color themes mapping for the code editor
private val AeroDarkBackground = Color(0xFF131722)
private val MidnightSynthBackground = Color(0xFF0F0C1B)
private val ObsidianBackground = Color(0xFF1B1B1F)
private val CreamLightBackground = Color(0xFFFCF8F2)

private val CodeKeywords = setOf("val", "var", "fun", "class", "package", "import", "override", "if", "else", "for", "in", "by", "remember")
private val ComposeKeywords = setOf("Text", "Button", "Column", "Row", "Card", "Spacer", "Scaffold", "MaterialTheme")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDevelopmentSuite(viewModel: IdeViewModel, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    
    // Determine active layout based on selected project
    val selectedId = viewModel.selectedProjectId
    
    Box(modifier = modifier.fillMaxSize()) {
        if (selectedId == null) {
            BeginningDashboard(viewModel)
        } else {
            StudioWorkspace(viewModel)
        }
    }
}

@Composable
fun BeginningDashboard(viewModel: IdeViewModel) {
    val projects by viewModel.allProjects.collectAsState()
    var isCreateDialogOpen by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("projects") } // "projects" or "sandbox"
    
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AeroDarkBackground)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BN",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "BN Developer Studio",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Android IDE for Kotlin & Compose",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    // Header Status
                    Text(
                        text = "V1.0 LITE",
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Dashboard Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(4.dp)
                ) {
                    TabHeaderButton(
                        label = "Compose Projects",
                        isSelected = activeTab == "projects",
                        icon = Icons.Default.Build,
                        modifier = Modifier.weight(1f)
                    ) {
                        activeTab = "projects"
                    }
                    TabHeaderButton(
                        label = "Kotlin Sandbox",
                        isSelected = activeTab == "sandbox",
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.weight(1f)
                    ) {
                        activeTab = "sandbox"
                    }
                }
            }
        },
        floatingActionButton = {
            if (activeTab == "projects") {
                ExtendedFloatingActionButton(
                    text = { Text("New Project", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Project") },
                    onClick = { isCreateDialogOpen = true },
                    containerColor = Color(0xFF6366F1),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("create_project_fab")
                )
            }
        },
        containerColor = AeroDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "DashboardTabTransition"
            ) { tab ->
                when (tab) {
                    "projects" -> {
                        ProjectListsView(
                            projects = projects,
                            onProjectClick = { viewModel.loadProject(it.id) },
                            onProjectDelete = { viewModel.deleteProject(it.id) }
                        )
                    }
                    "sandbox" -> {
                        KotlinSandboxPlayground(viewModel)
                    }
                }
            }
        }
    }
    
    if (isCreateDialogOpen) {
        CreateProjectDialog(
            onDismiss = { isCreateDialogOpen = false },
            onCreate = { name, pkg, min, target ->
                viewModel.createAndOpenProject(name, pkg, min, target)
                isCreateDialogOpen = false
            }
        )
    }
}

@Composable
fun TabHeaderButton(
    label: String,
    isSelected: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF6366F1) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProjectListsView(
    projects: List<Project>,
    onProjectClick: (Project) -> Unit,
    onProjectDelete: (Project) -> Unit
) {
    if (projects.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Projects Created",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Click the button below to initialize a clean modern Kotlin + Jetpack Compose workspace template.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(projects) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProjectClick(project) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = project.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = project.packageName,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Android Min SDK: ${project.minSdk} | Target: ${project.targetSdk}",
                                    fontSize = 11.sp,
                                    color = Color(0xFFEC4899)
                                )
                            }
                        }
                        
                        IconButton(
                            onClick = { onProjectDelete(project) }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete project",
                                tint = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, Int, Int) -> Unit
) {
    var appName by remember { mutableStateOf("My Sweet Project") }
    var packageName by remember { mutableStateOf("com.bn.sweetapp") }
    var minSdk by remember { mutableStateOf("24") }
    var targetSdk by remember { mutableStateOf("35") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1B1B1F),
                contentColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Initialize Compose Project",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                // Form layout fields
                OutlinedTextField(
                    value = appName,
                    onValueChange = { appName = it },
                    label = { Text("Application Name") },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth().testTag("proj_name_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = packageName,
                    onValueChange = { packageName = it },
                    label = { Text("Package Name") },
                    textStyle = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth().testTag("package_name_input")
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = minSdk,
                        onValueChange = { minSdk = it },
                        label = { Text("Min SDK") },
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetSdk,
                        onValueChange = { targetSdk = it },
                        label = { Text("Target SDK") },
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Language: Kotlin + Jetpack Compose (Read-Only)",
                    fontSize = 12.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White.copy(alpha = 0.6f))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val minVal = minSdk.toIntOrNull() ?: 24
                            val targetVal = targetSdk.toIntOrNull() ?: 35
                            onCreate(appName, packageName, minVal, targetVal)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        modifier = Modifier.testTag("submit_create_project")
                    ) {
                        Text("Create Workspace", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun KotlinSandboxPlayground(viewModel: IdeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header console bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "main.kt (Pure Sandbox Console)",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.kotlinSandboxCode = """fun main() {
    println("Welcome back to Clean Console Sandbox!")
}"""
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset Sandbox", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { viewModel.executeKotlinSandbox() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Code", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                // Content area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    BasicTextField(
                        value = viewModel.kotlinSandboxCode,
                        onValueChange = { viewModel.kotlinSandboxCode = it },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = Color.White
                        ),
                        cursorBrush = SolidColor(Color.White),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Console view
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            modifier = Modifier.height(180.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Standard Out Trace:",
                    color = Color(0xFF6366F1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (viewModel.isSandboxRunning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(12.dp), color = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Evaluating script compiler sandbox...", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }
                } else if (viewModel.sandboxOutput.isEmpty()) {
                    Text(
                        text = "Output console is currently silent. Trigger \"Run Code\" above.",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    viewModel.sandboxOutput.forEach { line ->
                        Text(
                            text = line,
                            color = if (line.contains("Error") || line.contains("Exception")) Color(0xFFF43F5E) else Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// --- Studio Working Editor Environment ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioWorkspace(viewModel: IdeViewModel) {
    val context = LocalContext.current
    var isExplorerOpen by remember { mutableStateOf(false) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    var isReferenceOpen by remember { mutableStateOf(false) }
    
    val selectedFile = viewModel.selectedFile
    val activeProject = viewModel.activeProject
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = activeProject?.name ?: "BN Studio",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = activeProject?.packageName ?: "",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.closeProject() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Fast action headers bar
                    IconButton(onClick = { isExplorerOpen = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "File Explorer", tint = Color.White)
                    }
                    IconButton(onClick = { isReferenceOpen = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Libraries registry", tint = Color.White)
                    }
                    IconButton(onClick = { isSettingsOpen = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Editor setups", tint = Color.White)
                    }
                    Button(
                        onClick = { viewModel.runApkCompilerSimulation() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .padding(end = 8.dp)
                            .testTag("compile_apk_button")
                    ) {
                        Text("Build APK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AeroDarkBackground)
            )
        },
        containerColor = AeroDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen View layout (Code / Interactive Preview / Terminal Console logs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector Tabs switcher
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .padding(2.dp)
                ) {
                    TabPill(label = "Code Editor", isActive = viewModel.activeStudioTab == "Code") {
                        viewModel.activeStudioTab = "Code"
                    }
                    TabPill(label = "Simulated Live View", isActive = viewModel.activeStudioTab == "Preview") {
                        viewModel.activeStudioTab = "Preview"
                    }
                    TabPill(label = "Build Output logs", isActive = viewModel.activeStudioTab == "Logs") {
                        viewModel.activeStudioTab = "Logs"
                    }
                }
                
                // Display active file path
                Text(
                    text = selectedFile?.filePath ?: "No file open",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
            }
            
            Box(modifier = Modifier.weight(1f)) {
                when (viewModel.activeStudioTab) {
                    "Code" -> CodeEditorWorkspace(viewModel)
                    "Preview" -> LiveEmulatorView(viewModel)
                    "Logs" -> DiagnosticsTerminalConsole(viewModel)
                }
            }
        }
    }
    
    // Slide-out drawers simulation modals
    if (isExplorerOpen) {
        FileExplorerModal(
            viewModel = viewModel,
            onDismiss = { isExplorerOpen = false }
        )
    }
    
    if (isSettingsOpen) {
        EditorSettingsModal(
            viewModel = viewModel,
            onDismiss = { isSettingsOpen = false }
        )
    }
    
    if (isReferenceOpen) {
        LibrariesDirectoryReference(onDismiss = { isReferenceOpen = false })
    }
    
    // Successful compilation popup
    if (viewModel.isApkBuildFinished) {
        ApkBuildSuccessModal(
            viewModel = viewModel,
            onDismiss = { viewModel.isApkBuildFinished = false }
        )
    }
}

@Composable
fun TabPill(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isActive) Color(0xFF6366F1) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CodeEditorWorkspace(viewModel: IdeViewModel) {
    val editorBg = when (viewModel.editorTheme) {
        "Aero Dark" -> AeroDarkBackground
        "Midnight Synth" -> MidnightSynthBackground
        "Obsidian" -> ObsidianBackground
        "Cream Light" -> CreamLightBackground
        else -> MidnightSynthBackground
    }
    val contentColor = if (viewModel.editorTheme == "Cream Light") Color(0xFF2E2A24) else Color.White
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(editorBg)
    ) {
        // Toolbar with quick editor operations
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.03f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.undo() }) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Undo", tint = contentColor.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = { viewModel.redo() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Redo", tint = contentColor.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Quick block wrap tags helpers
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    WrapChip(label = "wrap Column {", containerColor = Color(0xFF6366F1)) {
                        viewModel.wrapSelectionIn("Column")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    WrapChip(label = "wrap Row {", containerColor = Color(0xFFEC4899)) {
                        viewModel.wrapSelectionIn("Row")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    WrapChip(label = "wrap Card {", containerColor = Color(0xFF10B981)) {
                        viewModel.wrapSelectionIn("Card")
                    }
                }
            }
            
            // Read-Only state indicator
            if (viewModel.isReadOnlyMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF59E0B).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Read Only", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        // Multi-line code edit pane with custom line numbers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Line numbers column
            val lineCount = viewModel.selectedFileContent.lines().size
            Column(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.15f))
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .width(32.dp),
                horizontalAlignment = Alignment.End
            ) {
                for (i in 1..lineCount) {
                    Text(
                        text = "$i",
                        color = contentColor.copy(alpha = 0.35f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = viewModel.editorFontSize.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.height(20.dp)
                    )
                }
            }
            
            // Text Editor Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp, horizontal = 14.dp)
            ) {
                if (viewModel.selectedFileContent.isEmpty()) {
                    Text(
                        text = "File is empty or loading structure...",
                        color = contentColor.copy(alpha = 0.3f),
                        fontSize = viewModel.editorFontSize.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Actual interactive field editor
                BasicTextField(
                    value = viewModel.selectedFileContent,
                    onValueChange = { viewModel.updateSelectedFileContent(it) },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = viewModel.editorFontSize.sp,
                        color = contentColor,
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(contentColor),
                    readOnly = viewModel.isReadOnlyMode,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    modifier = Modifier.fillMaxSize().testTag("code_editor_field")
                )
            }
        }
    }
}

@Composable
fun WrapChip(label: String, containerColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor.copy(alpha = 0.15f))
            .border(1.dp, containerColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = containerColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

// --- Live Simulated Emulator screen viewport representation ---
@Composable
fun LiveEmulatorView(viewModel: IdeViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AeroDarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Simulated Phone Device shell
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(3.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.95f),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Phone notch status indicator bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "10:30 PM",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Device notch placeholder
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 18.dp)
                            .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                            .background(Color.Black)
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    }
                }
                
                // Virtual Mobile Application Surface
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131317))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Recursive widgets layout rendering
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        viewModel.previewNodes.forEach { node ->
                            RenderDynamicNode(node, viewModel)
                        }
                    }
                }
                
                // Device bottom navigation pill bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f))
                    )
                }
            }
        }
    }
}

@Composable
fun RenderDynamicNode(node: RenderNode, viewModel: IdeViewModel) {
    when (node) {
        is RenderNode.TextNode -> {
            Text(
                text = node.text,
                color = Color.White,
                fontSize = node.sizeSp.sp,
                fontWeight = if (node.isHeadline) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        is RenderNode.SpacerNode -> {
            Spacer(modifier = Modifier.height(node.heightDp.dp))
        }
        is RenderNode.ButtonNode -> {
            Button(
                onClick = { viewModel.triggerPreviewSimulatedAction(node.actionKey) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(text = node.label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
            }
        }
        is RenderNode.CardNode -> {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    node.children.forEach { child ->
                        RenderDynamicNode(child, viewModel)
                    }
                }
            }
        }
        is RenderNode.ColumnNode -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                node.children.forEach { child ->
                    RenderDynamicNode(child, viewModel)
                }
            }
        }
        is RenderNode.RowNode -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                node.children.forEach { child ->
                    RenderDynamicNode(child, viewModel)
                }
            }
        }
    }
}

// --- Diagnostics compiler output terminal and line debugger tracker ---
@Composable
fun DiagnosticsTerminalConsole(viewModel: IdeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Studio Terminal output console:",
                color = Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            
            Row {
                val errorsCount = viewModel.compileErrors.size
                if (errorsCount > 0) {
                    Text(
                        text = "Problems ($errorsCount)",
                        color = Color(0xFFF43F5E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color(0xFFF43F5E).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Split Layout: Terminal output (Left/Top) & Clickable compiling errors trace (Right/Bottom)
        Column(modifier = Modifier.weight(1f)) {
            // Logs Terminal
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .background(Color(0xFF0C0C0E), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (viewModel.isCompiling) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp), color = Color(0xFF10B981))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Triggering full incremental Gradle compilation task...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else if (viewModel.compileLogs.isEmpty()) {
                    Text(
                        text = "Build logs are empty. Click \"Build APK\" at top menu to trigger diagnostics parser.",
                        color = Color.White.copy(alpha = 0.25f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column {
                        viewModel.compileLogs.forEach { log ->
                            val color = when (log.type) {
                                LogType.SUCCESS -> Color(0xFF10B981)
                                LogType.ERROR -> Color(0xFFF43F5E)
                                LogType.COMPILER -> Color(0xFF6366F1)
                                LogType.INFO -> Color.White
                            }
                            Text(
                                text = log.text,
                                color = color,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Syntax check Errors list layout
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hyperlinked Compiler Diagnostics issues list (Click list item to jump to line error inside Editor):",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (viewModel.compileErrors.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0C0C0E), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No compiler diagnostics errors detected. Let's package APK!",
                            color = Color.White.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0C0C0E), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF43F5E).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    ) {
                        itemsIndexed(viewModel.compileErrors) { index, err ->
                            val isSelected = index == viewModel.selectedCompileErrorIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) Color(0xFFF43F5E).copy(alpha = 0.12f) else Color.Transparent)
                                    .clickable {
                                        viewModel.selectedCompileErrorIndex = index
                                        // Auto-focus target file structure and load target index inside Editor Tab
                                        val targetFile = viewModel.filesList.find { it.filePath == err.filePath }
                                        if (targetFile != null) {
                                            viewModel.selectFile(targetFile)
                                            viewModel.activeStudioTab = "Code"
                                        }
                                    }
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF43F5E),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "${err.fileName} inside Line ${err.line}",
                                        color = Color(0xFFF43F5E),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = err.message,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

// --- Side Drawer Simulations Panel Modal ---
@Composable
fun FileExplorerModal(
    viewModel: IdeViewModel,
    onDismiss: () -> Unit
) {
    var isNewFileDialogOpen by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("src/main/java/Utils.kt") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1F)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Explorer Directory Workspace",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = { isNewFileDialogOpen = true }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add custom file", tint = Color.Green)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // Explorer lists
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    items(viewModel.filesList) { file ->
                        val isOpen = file.id == viewModel.selectedFile?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isOpen) Color(0xFF6366F1).copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    viewModel.selectFile(file)
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (file.filePath.endsWith(".gradle.kts")) Icons.Default.Settings else Icons.Default.List,
                                    contentDescription = null,
                                    tint = if (isOpen) Color(0xFF6366F1) else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = file.filePath,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isOpen) Color(0xFF6366F1) else Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            // Delete button for custom files
                            if (file.filePath.startsWith("src/") || file.filePath.endsWith(".kt")) {
                                IconButton(
                                    onClick = { viewModel.deleteFile(file.filePath) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White.copy(alpha = 0.25f), modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Close Explorer", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    
    if (isNewFileDialogOpen) {
        Dialog(onDismissRequest = { isNewFileDialogOpen = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1F)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Create Custom File", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        label = { Text("Relative File Path") },
                        textStyle = TextStyle(color = Color.White),
                        suffix = { Text(".kt") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { isNewFileDialogOpen = false }) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.6f))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                val fullPath = if (newFileName.endsWith(".kt")) newFileName else "$newFileName.kt"
                                viewModel.createNewFile(
                                    filePath = fullPath,
                                    content = "package ${viewModel.activeProject?.packageName ?: "com.example"}\n\n// Write code here..."
                                )
                                isNewFileDialogOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Add File")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditorSettingsModal(
    viewModel: IdeViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1F)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Editor Customizations",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                // Font Size slider selection
                Text(
                    text = "Code Font Size: ${viewModel.editorFontSize}sp",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Slider(
                    value = viewModel.editorFontSize,
                    onValueChange = { viewModel.editorFontSize = it },
                    valueRange = 12f..22f,
                    steps = 5,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFF6366F1), activeTrackColor = Color(0xFF6366F1))
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Theme selector cards
                Text(
                    text = "Code Highlighting Palette:",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                
                val themes = listOf("Aero Dark", "Midnight Synth", "Obsidian", "Cream Light")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    themes.forEach { tName ->
                        val isSel = viewModel.editorTheme == tName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF6366F1) else Color.White.copy(alpha = 0.05f))
                                .border(1.dp, if (isSel) Color.White else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.editorTheme = tName }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tName,
                                color = if (isSel) Color.White else Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Save Configurations", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LibrariesDirectoryReference(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1F)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Libraries Registry (Read-Only)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reference specifications of pre-built dependency bundles. When importing any, the compiler automatically includes it.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.height(240.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val sampleRegistry = listOf(
                        "androidx.compose.ui:ui" to "Foundation components, widgets and drawing canvas engine (Included by default).",
                        "androidx.compose.material3:material3" to "Material 3 standard cards, textfields and buttons design systems.",
                        "androidx.room:room-runtime:2.7.0" to "Fully integrated sqlite persistence layer (Use RoomDatabase & Dao annotations)",
                        "io.coil-kt:coil-compose:2.7.0" to "Image loader with integrated coroutine disk cache.",
                        "com.squareup.retrofit2:retrofit:2.12.0" to "Type-safe HTTP API network client."
                    )
                    
                    items(sampleRegistry) { (name, desc) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                name,
                                color = Color(0xFF10B981),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                desc,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Okay", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ApkBuildSuccessModal(
    viewModel: IdeViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1F)),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Build Compiled Successfully!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Compiled APK was assembled from template successfully. Clean Kotlin and Jetpack Compose structure matched rules.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Specs specs
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BuildSpecRow("Package Name", viewModel.activeProject?.packageName ?: "com.bn.app")
                    BuildSpecRow("Target Version", "Android API Level 35 (V15)")
                    BuildSpecRow("Total Size", "12.4 MB")
                    BuildSpecRow("Asset output", "app-debug.apk")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Install APK Simulation", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share Application File", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun BuildSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
