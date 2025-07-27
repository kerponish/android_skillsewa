package com.example.kotlinsample.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.ui.theme.KotlinsampleTheme

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
    var professionalType by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    
    var professionalTypeExpanded by remember { mutableStateOf(false) }

    val professionalTypes = listOf(
        "Carpenter", "Electrician", "Plumber", "Painter", 
        "Mason", "Welder", "Mechanic", "Technician", "Other"
    )

    val context = LocalContext.current
    val repository = remember { ServiceResImpl() }

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
                    expanded = professionalTypeExpanded,
                    onExpandedChange = { professionalTypeExpanded = !professionalTypeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = professionalType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Professional Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionalTypeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = professionalTypeExpanded,
                        onDismissRequest = { professionalTypeExpanded = false }
                    ) {
                        professionalTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    professionalType = type
                                    professionalTypeExpanded = false
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
                        .height(120.dp)
                )

                Button(
                    onClick = {
                        when {
                            serviceName.isBlank() || professionalType.isBlank() || price.isBlank() -> {
                                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                            }
                            price.toDoubleOrNull() == null || price.toDouble() < 0 -> {
                                Toast.makeText(context, "Enter a valid price", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val service = Service(
                                    serviceName = serviceName.trim(),
                                    professionalType = professionalType.trim(),
                                    price = price.toDouble(),
                                    description = description.trim(),
                                    duration = duration.trim(),
                                    location = location.trim(),
                                    contactNumber = contactNumber.trim()
                                )

                                repository.addService(service) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        serviceName = ""
                                        professionalType = ""
                                        price = ""
                                        description = ""
                                        duration = ""
                                        location = ""
                                        contactNumber = ""
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Service")
                }
            }
        }
    )
} 