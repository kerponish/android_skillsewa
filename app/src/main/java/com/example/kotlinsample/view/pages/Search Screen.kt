package com.example.kotlinsample.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.model.Service
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage

@Composable
fun SearchScree() {
    val userRepo = remember { UserRepositoryImpl() }
    val serviceRepo = remember { ServiceResImpl() }
    var email by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<Map<String, Any>?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
            .padding(16.dp)
    ) {
        Text(
            text = "Search for a user by email",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Enter user email...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            isLoading = true
            errorMessage = ""
            searchResult = null
            services = emptyList()
            userRepo.searchUserByEmail(email) { success, message, userMap ->
                isLoading = false
                if (success && userMap != null) {
                    searchResult = userMap
                    val userId = userMap["userId"] as? String ?: ""
                    if (userId.isNotEmpty()) {
                        serviceRepo.getServicesByUserId(userId) { ok, msg, serviceList ->
                            if (ok && serviceList != null) {
                                services = serviceList.filterNotNull()
                            }
                        }
                    }
                } else {
                    errorMessage = message
                }
            }
        }, enabled = email.isNotBlank()) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (errorMessage.isNotBlank()) {
            Text(errorMessage, color = Color.Red)
        }
        searchResult?.let { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { showProfile = true },
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${user["name"] ?: "N/A"}", fontWeight = FontWeight.Bold)
                    Text("Email: ${user["email"] ?: "N/A"}")
                    Text("Gender: ${user["gender"] ?: "N/A"}")
                }
            }
            if (showProfile) {
                AlertDialog(
                    onDismissRequest = { showProfile = false },
                    title = { Text("User Profile") },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Name: ${user["name"] ?: "N/A"}", fontWeight = FontWeight.Bold)
                            Text("Email: ${user["email"] ?: "N/A"}")
                            Text("Gender: ${user["gender"] ?: "N/A"}")
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Services:", fontWeight = FontWeight.Bold)
                            if (services.isNotEmpty()) {
                                services.forEach { service ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(service.serviceName, fontWeight = FontWeight.Bold)
                                            Text(service.description)
                                        }
                                    }
                                }
                            } else {
                                Text("No services posted.")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showProfile = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}