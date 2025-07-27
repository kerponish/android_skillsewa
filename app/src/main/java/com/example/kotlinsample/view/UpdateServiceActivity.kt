package com.example.kotlinsample.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.ui.theme.KotlinsampleTheme

class UpdateServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinsampleTheme {
                UpdateServiceScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateServiceScreen() {
    val context = LocalContext.current
    val repository = remember { ServiceResImpl() }

    var serviceName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var professionalType by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var serviceId by remember { mutableStateOf("") }

    val professionalTypes = listOf(
        "Carpenter", "Electrician", "Plumber", "Painter", 
        "Mason", "Welder", "Mechanic", "Technician", "Other"
    )

    // Get data from intent
    LaunchedEffect(Unit) {
        val intent = (context as? UpdateServiceActivity)?.intent
        intent?.let {
            serviceId = it.getStringExtra("serviceId") ?: ""
            serviceName = it.getStringExtra("serviceName") ?: ""
            professionalType = it.getStringExtra("professionalType") ?: ""
            price = it.getDoubleExtra("price", 0.0).toString()
            description = it.getStringExtra("description") ?: ""
            duration = it.getStringExtra("duration") ?: ""
            location = it.getStringExtra("location") ?: ""
            contactNumber = it.getStringExtra("contactNumber") ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Service") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                val updateData = mutableMapOf<String, Any>(
                                    "serviceName" to serviceName.trim(),
                                    "professionalType" to professionalType.trim(),
                                    "price" to price.toDouble(),
                                    "description" to description.trim(),
                                    "duration" to duration.trim(),
                                    "location" to location.trim(),
                                    "contactNumber" to contactNumber.trim()
                                )

                                repository.updateService(serviceId, updateData) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        (context as? UpdateServiceActivity)?.finish()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Service")
                }
            }
        }
    )
} 