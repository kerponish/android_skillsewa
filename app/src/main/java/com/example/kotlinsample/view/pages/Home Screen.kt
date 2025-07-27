package com.example.kotlinsample.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.viewmodel.ServiceViewModel
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.model.Service
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val viewModel = remember { ServiceViewModel(ServiceResImpl()) }
    var isLoading by remember { mutableStateOf(true) }
    var serviceList by remember { mutableStateOf<List<Service>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    // Load services on launch
    LaunchedEffect(Unit) {
        viewModel.getAllServices()
        viewModel.serviceList.observeForever { list ->
            serviceList = list.filterNotNull().filter { it.userId != currentUserId }
            isLoading = false
        }
    }

    val filteredServices = if (searchQuery.isBlank()) serviceList else serviceList.filter {
        it.serviceName.contains(searchQuery, ignoreCase = true) ||
        it.professionalType.contains(searchQuery, ignoreCase = true) ||
        it.location.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // Search Section
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search services") },
            placeholder = { Text("Search services, professionals, or location...") },
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

        // Services List Section
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (filteredServices.isEmpty()) {
                EmptyStateMessage()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredServices) { service ->
                        ServiceCard(service = service)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: Service) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(service.serviceName, style = MaterialTheme.typography.titleMedium)
            Text("Type: ${service.professionalType}", style = MaterialTheme.typography.bodyMedium)
            Text("Price: Rs. ${service.price}", style = MaterialTheme.typography.bodySmall)
            Text("Location: ${service.location}", style = MaterialTheme.typography.bodySmall)
            Text("Contact: ${service.contactNumber}", style = MaterialTheme.typography.bodySmall)
            Text(service.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EmptyStateMessage() {
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
                text = "No services found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Be the first to post a service!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
