package com.example.kotlinsample.view.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kotlinsample.model.Comment
import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.ui.theme.KotlinsampleTheme
import com.example.kotlinsample.view.AddServiceActivity
import com.example.kotlinsample.view.UpdateServiceActivity
import com.example.kotlinsample.viewmodel.ServiceViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material.icons.filled.Notifications
import com.google.firebase.database.*
import com.example.kotlinsample.view.SkillsLoginActivity

class HomeScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinsampleTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val snackbarHostState = remember { SnackbarHostState() }
    var notificationCount by remember { mutableIntStateOf(0) }
    var notifications by remember { mutableStateOf(listOf<Map<String, Any>>()) }
    var showNotificationMenu by remember { mutableStateOf(false) }

    // Real-time notification listener
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            val notificationRef = FirebaseDatabase.getInstance().reference.child("notifications").child(currentUserId)
            notificationRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notifList = snapshot.children.mapNotNull { it.value as? Map<String, Any> }
                    notifications = notifList.sortedByDescending { it["timestamp"] as? Long ?: 0L }
                    notificationCount = notifications.size
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Skill Sewa")
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            IconButton(onClick = { showNotificationMenu = !showNotificationMenu }) {
                                BadgedBox(badge = {
                                    if (notificationCount > 0) {
                                        Badge { Text(notificationCount.toString()) }
                                    }
                                }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                                }
                            }
                            DropdownMenu(
                                expanded = showNotificationMenu,
                                onDismissRequest = { showNotificationMenu = false }
                            ) {
                                if (notifications.isEmpty()) {
                                    DropdownMenuItem(text = { Text("No notifications") }, onClick = { })
                                } else {
                                    notifications.take(10).forEach { notification ->
                                        val type = notification["type"] as? String ?: ""
                                        val fromUserId = notification["fromUserId"] as? String ?: ""
                                        val serviceId = notification["serviceId"] as? String ?: ""
                                        DropdownMenuItem(
                                            text = { Text("$type from $fromUserId") },
                                            onClick = { showNotificationMenu = false }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // Logout functionality
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(context, SkillsLoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.Red
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Services Content
            HomeTabContent(
                currentUserId = currentUserId,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
fun HomeTabContent(
    currentUserId: String,
    snackbarHostState: SnackbarHostState
) {
    val viewModel = remember { ServiceViewModel(ServiceResImpl()) }
    val scope = rememberCoroutineScope()
    val serviceList by viewModel.serviceList.observeAsState(emptyList())
    val statusMessage by viewModel.statusMessage.observeAsState("")
    var isLoading by remember { mutableStateOf(true) }

    // Load all services on component mount
    LaunchedEffect(Unit) {
        viewModel.getAllServices()
        isLoading = false
    }

    LaunchedEffect(statusMessage) {
        if (statusMessage.isNotEmpty()) {
            scope.launch { snackbarHostState.showSnackbar(statusMessage) }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = {
            isLoading = true
            viewModel.getAllServices()
            isLoading = false
        }) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            val allServices = serviceList.filterNotNull().filter { it.serviceId.isNotBlank() }
            items(allServices, key = { it.serviceId }) { service ->
                ServiceCard(
                    service = service,
                    currentUserId = currentUserId,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: Service,
    currentUserId: String,
    viewModel: ServiceViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(service.likes.contains(currentUserId)) }
    var likeCount by remember { mutableIntStateOf(service.likes.size) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Posted By
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Posted By: ${service.postedBy}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (service.userId == currentUserId) {
                    var showOptions by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showOptions = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showOptions,
                            onDismissRequest = { showOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    showOptions = false
                                    val intent = Intent(context, UpdateServiceActivity::class.java)
                                    intent.putExtra("serviceId", service.serviceId)
                                    context.startActivity(intent)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showOptions = false
                                    viewModel.delService(service.serviceId) { success, message ->
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Image & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (service.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = service.imageUrl,
                        contentDescription = "Service Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "No Image", tint = Color.White)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(service.serviceName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Type: ${service.professionalType}", fontSize = 14.sp, color = Color.Gray)
                    Text("Price: NPR ${service.price}", fontSize = 14.sp, color = Color.Gray)
                    Text("Location: ${service.location}", fontSize = 14.sp, color = Color.Gray)
                    Text("Contact: ${service.contactNumber}", fontSize = 14.sp, color = Color.Gray)
                    Text(service.description, fontSize = 14.sp, color = Color.Gray, maxLines = 2)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Like / Comment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Like
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (isLiked) {
                                    viewModel.unlikeService(service.serviceId, currentUserId) { success, message ->
                                        if (success) {
                                            isLiked = false
                                            likeCount--
                                        }
                                    }
                                } else {
                                    viewModel.likeService(service.serviceId, currentUserId) { success, message ->
                                        if (success) {
                                            isLiked = true
                                            likeCount++
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ThumbUp, contentDescription = "Like", tint = if (isLiked) Color.Blue else Color.Gray)
                        }
                        Text("$likeCount likes", fontSize = 12.sp)
                    }
                    // Mark as Interested
                    Button(
                        onClick = {
                            viewModel.markAsInterested(service.serviceId, currentUserId, service.userId) { success, message ->
                                scope.launch { snackbarHostState.showSnackbar(message) }
                            }
                        },
                        enabled = !service.interested.contains(currentUserId)
                    ) {
                        Text(if (service.interested.contains(currentUserId)) "Interested" else "Mark as Interested")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(
    comment: Comment,
    currentUserId: String,
    viewModel: ServiceViewModel,
    serviceId: String,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(comment.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                val formattedTimestamp = remember(comment.timestamp) {
                    SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
                }
                Text(formattedTimestamp, fontSize = 10.sp, color = Color.Gray)
            }
            Text(comment.comment, fontSize = 12.sp)
            if (comment.userId == currentUserId) {
                TextButton(
                    onClick = {
                        viewModel.deleteComment(serviceId, comment.commentId) { success, message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    }
                ) {
                    Text("Delete", fontSize = 10.sp, color = Color.Red)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(4.dp))
}