package com.example.kotlinsample.view.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.viewmodel.ServiceViewModel
import android.content.Intent
import com.example.kotlinsample.view.UpdateServiceActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import android.widget.Toast
import androidx.compose.ui.graphics.Color

@Composable
fun HistoryScr(userId: String) {
    val context = LocalContext.current
    val viewModel = remember { ServiceViewModel(ServiceResImpl()) }
    var serviceList by remember { mutableStateOf<List<Service>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshKey by remember { mutableStateOf(0) }
    var debugUserId by remember { mutableStateOf(userId) }
    var showAllFallback by remember { mutableStateOf(false) }

    fun refresh() {
        isLoading = true
        if (debugUserId.isNotEmpty()) {
            viewModel.getServicesByUserId(debugUserId)
            showAllFallback = false
        } else {
            // Fallback: show all services if userId is empty
            viewModel.getAllServices()
            showAllFallback = true
        }
    }

    // Observe service list
    LaunchedEffect(debugUserId, refreshKey) {
        refresh()
        viewModel.serviceList.observeForever { list ->
            serviceList = list.filterNotNull()
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("My Services", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { refreshKey++ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("[DEBUG] userId: $debugUserId", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        if (debugUserId.isEmpty()) {
            Text("[WARNING] You are not logged in. Showing all services as fallback.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (serviceList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No requested services found.")
            }
        } else {
            if (showAllFallback) {
                Text("[DEBUG] Fallback: Showing all services (userId was empty)", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(serviceList) { service ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(service.serviceName, style = MaterialTheme.typography.titleMedium)
                            Text("Type: ${service.professionalType}", style = MaterialTheme.typography.bodyMedium)
                            Text("Price: Rs. ${service.price}", style = MaterialTheme.typography.bodySmall)
                            Text("Location: ${service.location}", style = MaterialTheme.typography.bodySmall)
                            Text("Contact: ${service.contactNumber}", style = MaterialTheme.typography.bodySmall)
                            Text(service.description, style = MaterialTheme.typography.bodySmall)
                            Text("[DEBUG] service.userId: ${service.userId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    val intent = Intent(context, UpdateServiceActivity::class.java)
                                    intent.putExtra("serviceId", service.serviceId)
                                    intent.putExtra("serviceName", service.serviceName)
                                    intent.putExtra("professionalType", service.professionalType)
                                    intent.putExtra("price", service.price)
                                    intent.putExtra("description", service.description)
                                    intent.putExtra("duration", service.duration)
                                    intent.putExtra("location", service.location)
                                    intent.putExtra("contactNumber", service.contactNumber)
                                    context.startActivity(intent)
                                    // After returning, refresh
                                    refreshKey++
                                }) {
                                    Text("Update")
                                }
                                TextButton(onClick = {
                                    viewModel.delService(service.serviceId ?: "") { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        if (success) refreshKey++
                                    }
                                }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 