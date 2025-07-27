// MainActivity.kt

package com.example.kotlinsample.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.view.pages.HomeScreen
import com.example.kotlinsample.viewmodel.ServiceViewModel
import com.google.firebase.auth.FirebaseAuth


// Define your primary color palette
private val PrimaryBlue = Color(0xFF1976D2)
private val HeaderPurple = Color(0xFF6200EE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    MainScreen()
                }
            }
        }
    }
}

// Tabs definition
data class TabItem(val label: String, val icon: ImageVector)

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        TabItem("Home", Icons.Default.Home),
        TabItem("Search", Icons.Default.Search),
        TabItem("Add", Icons.Default.Add),
        TabItem("Profile", Icons.Default.Person),
        TabItem("Saved", Icons.Default.Bookmark)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> SearchScreen()
                2 -> AddContentScreen()
                3 -> com.example.kotlinsample.view.ProfileScre()
                4 -> MyServicesScreen()
            }
        }

        BottomNavigationSection(tabs, selectedTab) { selectedTab = it }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(HeaderPurple)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Skills Sewa", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = { /* Notification click */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationSection(
    tabs: List<TabItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                val isCenter = index == 2
                val animatedTint by animateColorAsState(
                    targetValue = if (isSelected) PrimaryBlue else Color.Gray,
                    label = "tabColor"
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCenter) {
                        FloatingActionButton(
                            onClick = { onTabSelected(index) },
                            containerColor = PrimaryBlue,
                            contentColor = Color.White,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(tab.icon, contentDescription = tab.label)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = animatedTint,
                                modifier = Modifier.size(24.dp)
                            )
                            if (isSelected) {
                                Text(
                                    tab.label,
                                    fontSize = 10.sp,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Dummy placeholders
@Composable fun SearchScreen() = EmptyStateScreen(Icons.Default.Search, "Search Screen")
@Composable fun AddContentScreen() = EmptyStateScreen(Icons.Default.Add, "Add Content Screen")
@Composable fun ProfileScreen() = EmptyStateScreen(Icons.Default.Person, "Profile Screen")

// Replace MyServicesScreen with a basic version that takes a static list
@Composable
fun MyServicesScreen() {
    val currentUserId = "user123"
    val services = listOf(
        Service(
            serviceId = "1",
            userId = currentUserId,
            serviceName = "Plumbing",
            professionalType = "Plumber",
            price = 500.0,
            description = "Fixing pipes",
            duration = "2 hours",
            location = "Kathmandu",
            contactNumber = "9800000000"
        )
        // Add more if needed
    )

    Box(modifier = Modifier.fillMaxSize()) {
        val filteredServices = services.filter { it.userId == currentUserId }
        if (filteredServices.isEmpty()) {
            EmptyStateScreen(Icons.Default.Bookmark, "No services found.")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredServices) { service ->
                    MyServiceCard(
                        service = service,
                        onEdit = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}

@Composable
fun MyServiceCard(service: Service, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(service.serviceName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            listOf(
                "Type" to service.professionalType,
                "Price" to "Rs. ${service.price}",
                "Location" to service.location,
                "Contact" to service.contactNumber
            ).forEach { (label, value) ->
                Text("$label: $value", fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onEdit) { Text("Edit") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete", color = Color.White) }
            }
        }
    }
}

@Composable
fun EmptyStateScreen(icon: ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, fontSize = 18.sp, color = Color.Gray)
        }
    }
}
