package com.example.kotlinsample.view

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.view.pages.HomeScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.unit.sp
import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.viewmodel.ServiceViewModel
import com.example.kotlinsample.view.UpdateServiceActivity
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.launch

class NavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationBody()
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBody() {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

    // Bottom Navigation Items - 4 tabs
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Add Service", Icons.Default.Add),
        BottomNavItem("My Services", Icons.Default.History),
        BottomNavItem("My Profile", Icons.Default.Person)
    )

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedIndex == index,
                        onClick = { 
                            if (index == 1) {
                                // Add Service - launch activity
                                if (!inPreview) {
                                    val intent = Intent(context, AddServiceActivity::class.java)
                                    context.startActivity(intent)
                                }
                            } else {
                                selectedIndex = index
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> HomeScreen() // Homepage
                2 -> HistoryScr() // My Services (History)
                3 -> ProfileNavigationHandler() // My Profile
            }
        }
    }
}

/**
 * Handles navigation to Profile Activity in a Composable-safe way
 */
@Composable
fun ProfileNavigationHandler() {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

    if (!inPreview) {
        LaunchedEffect(Unit) {
            val intent = Intent(context, Profile::class.java)
            context.startActivity(intent)
            // Don't finish the current activity, let Profile handle its own back navigation
        }
    } else {
        Text("Profile Screen (Preview)")
    }
}

/**
 * My Services Screen - shows user's own services (history)
 */
@Composable
fun HistoryScr() {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val viewModel = remember { ServiceViewModel(ServiceResImpl()) }
    val serviceList by viewModel.serviceList.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }

    // Load user's services on component mount
    LaunchedEffect(Unit) {
        viewModel.getAllServices()
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val userServices = serviceList.filterNotNull().filter { service -> service.userId == currentUserId }
            
            if (userServices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No Services",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No services posted yet",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap 'Add Service' to post your first service",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "My Services (${userServices.size})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    items(userServices, key = { it.serviceId }) { service ->
                        UserServiceCard(
                            service = service,
                            viewModel = viewModel,
                            context = context
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserServiceCard(
    service: Service,
    viewModel: ServiceViewModel,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Service Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.serviceName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, UpdateServiceActivity::class.java)
                            intent.putExtra("serviceId", service.serviceId)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.delService(service.serviceId) { success, message ->
                                scope.launch { 
                                    snackbarHostState.showSnackbar(message) 
                                }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Service Details
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Type: ${service.professionalType}", fontSize = 14.sp, color = Color.Gray)
                Text("Price: NPR ${service.price}", fontSize = 14.sp, color = Color.Gray)
                Text("Location: ${service.location}", fontSize = 14.sp, color = Color.Gray)
                Text("Contact: ${service.contactNumber}", fontSize = 14.sp, color = Color.Gray)
                Text("Description: ${service.description}", fontSize = 14.sp, color = Color.Gray, maxLines = 2)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Likes: ${service.likes.size}", fontSize = 12.sp, color = Color.Gray)
                Text("Interested: ${service.interested.size}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBodyPreview() {
    NavigationBody()
}
