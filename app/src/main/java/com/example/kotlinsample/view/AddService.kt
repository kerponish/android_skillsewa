package com.example.kotlinsample.view

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.example.kotlinsample.ui.theme.KotlinsampleTheme
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class AddServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinsampleTheme {
                AddServiceScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceScreen() {
    var serviceName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var professionalType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }

    val professionalTypes = listOf(
        "Carpenter", "Electrician", "Plumber", "Painter",
        "Mason", "Welder", "Mechanic", "Technician", "Other"
    )

    val context = LocalContext.current
    val repository = remember { ServiceResImpl() }
    val userRepo = remember { UserRepositoryImpl() }
    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("service_images")

    // Fetch user name
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userRepo.getProfile(userId) { data ->
                userName = data["name"] as? String ?: "Unknown User"
            }
        }
    }

    // Remove all code related to service image upload, display, and selection
    // Only allow adding service details (name, type, price, etc.) without an image

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Service") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Service Image",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Display a placeholder or message if no image is selected
                        Text("No image selected for this service.")
                    }
                }

                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = { Text("Service Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Professional Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = professionalType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Professional Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        professionalTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    professionalType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (NPR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Estimated Duration (e.g., 2 hours, 1 day)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Service Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contactNumber,
                    onValueChange = { contactNumber = it },
                    label = { Text("Contact Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Service Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Add Service Button
                Button(
                    onClick = {
                        when {
                            serviceName.isBlank() || price.isBlank() || professionalType.isBlank() -> {
                                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                            }
                            price.toDoubleOrNull() == null || price.toDouble() < 0 -> {
                                Toast.makeText(context, "Enter a valid price", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                isUploading = true

                                // Create service without image
                                val service = Service(
                                    userId = userId,
                                    serviceName = serviceName.trim(),
                                    professionalType = professionalType.trim(),
                                    price = price.toDouble(),
                                    description = description.trim(),
                                    duration = duration.trim(),
                                    location = location.trim(),
                                    contactNumber = contactNumber.trim(),
                                    postedBy = userName
                                )

                                repository.addService(service) { success, message ->
                                    isUploading = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        // Reset form
                                        serviceName = ""
                                        price = ""
                                        description = ""
                                        professionalType = ""
                                        duration = ""
                                        location = ""
                                        contactNumber = ""
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Add Service")
                    }
                }
            }
        }
    )
}
