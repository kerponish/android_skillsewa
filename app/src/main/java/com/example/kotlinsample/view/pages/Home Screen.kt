package com.example.kotlinsample.view.pages

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.model.Task
import com.example.kotlinsample.repository.TaskResImpl
import com.example.kotlinsample.view.AddTaskActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home Screen - Main screen showing all user tasks/requests
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    
    // State management
    val taskRepo = remember { TaskResImpl() }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var taskList by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load tasks from Firebase
    LaunchedEffect(Unit) {
        loadTasks(taskRepo, context) { tasks ->
            taskList = tasks
            isLoading = false
        }
    }

    // Filter tasks based on search query
    val filteredTasks = filterTasks(taskList, searchQuery.text)

    // Main UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // Search Section
        SearchSection(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it }
        )

        // Tasks List Section - Takes remaining space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TasksListSection(
                isLoading = isLoading,
                tasks = filteredTasks
            )
        }

        // Action Buttons Section
        ActionButtonsSection(context = context)
    }
}

/**
 * Search bar component
 */
@Composable
private fun SearchSection(
    searchQuery: TextFieldValue,
    onSearchChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        leadingIcon = { 
            Icon(
                Icons.Default.Search, 
                contentDescription = "Search tasks"
            ) 
        },
        placeholder = { 
            Text("Search tasks, professionals, or users...") 
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

/**
 * Tasks list with loading and empty states
 */
@Composable
private fun TasksListSection(
    isLoading: Boolean,
    tasks: List<Task>
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            tasks.isEmpty() -> {
                EmptyTasksMessage()
            }
            else -> {
                TasksList(tasks = tasks)
            }
        }
    }
}

/**
 * Empty state message
 */
@Composable
private fun EmptyTasksMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tasks found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Be the first to post a task!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Scrollable list of tasks
 */
@Composable
private fun TasksList(tasks: List<Task>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks) { task ->
            TaskCard(task = task)
        }
    }
}

/**
 * Action buttons at the bottom
 */
@Composable
private fun ActionButtonsSection(context: android.content.Context) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Post Task Button
        FloatingActionButton(
            onClick = {
                val intent = Intent(context, AddTaskActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.size(56.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                Icons.Default.Add, 
                contentDescription = "Post new task",
                modifier = Modifier.size(24.dp)
            )
        }

        // Profile Button
        FloatingActionButton(
            onClick = {
                Toast.makeText(
                    context, 
                    "Profile feature coming soon", 
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.size(56.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(
                Icons.Default.Person, 
                contentDescription = "User profile",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Individual task card component
 */
@Composable
fun TaskCard(task: Task) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val postedDate = Date(task.postedDate)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with title and urgency
            TaskHeader(task = task)
            
            // Task details
            TaskDetails(task = task)
            
            // Contact and date info
            TaskFooter(task = task, dateFormat = dateFormat, postedDate = postedDate)
        }
    }
}

/**
 * Task card header with title and urgency chip
 */
@Composable
private fun TaskHeader(task: Task) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = task.taskTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        UrgencyChip(urgency = task.urgency)
    }
}

/**
 * Urgency level chip with color coding
 */
@Composable
private fun UrgencyChip(urgency: String) {
    val chipColor = when (urgency.lowercase()) {
        "urgent" -> MaterialTheme.colorScheme.error
        "normal" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }

    AssistChip(
        onClick = {},
        label = { 
            Text(
                text = urgency, 
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary
            ) 
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = chipColor
        )
    )
}


/**
 * Task details section
 */
@Composable
private fun TaskDetails(task: Task) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Posted by: ${task.userName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Professional needed: ${task.requiredProfessionalType}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = task.taskDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Task footer with budget, location, contact, and date
 */
@Composable
private fun TaskFooter(
    task: Task,
    dateFormat: SimpleDateFormat,
    postedDate: Date
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (task.budget > 0) {
            Text(
                text = "Budget: Rs. ${task.budget}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        if (task.location.isNotBlank()) {
            Text(
                text = "📍 ${task.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (task.contactNumber.isNotBlank()) {
            Text(
                text = "📞 ${task.contactNumber}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "Posted: ${dateFormat.format(postedDate)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Load tasks from Firebase
 */
private fun loadTasks(
    taskRepo: TaskResImpl,
    context: android.content.Context,
    onTasksLoaded: (List<Task>) -> Unit
) {
    taskRepo.getAllTasks { success, message, list ->
        if (success) {
            val tasks = list.filterNotNull()
            println("DEBUG: Loaded ${tasks.size} tasks")
            tasks.forEach { task ->
                println("DEBUG: Task - ${task.taskTitle} by ${task.userName}")
            }
            onTasksLoaded(tasks)
        } else {
            println("DEBUG: Failed to load tasks - $message")
            Toast.makeText(
                context, 
                "Error loading tasks: $message", 
                Toast.LENGTH_SHORT
            ).show()
            onTasksLoaded(emptyList())
        }
    }
}

/**
 * Filter tasks based on search query
 */
private fun filterTasks(tasks: List<Task>, query: String): List<Task> {
    return if (query.isBlank()) {
        tasks
    } else {
        tasks.filter { task ->
            task.taskTitle.contains(query, ignoreCase = true) ||
            task.requiredProfessionalType.contains(query, ignoreCase = true) ||
            task.userName.contains(query, ignoreCase = true)
        }
    }
}
