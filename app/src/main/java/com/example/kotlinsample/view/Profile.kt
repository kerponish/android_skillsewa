package com.example.kotlinsample.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF1F1F1)
                ) {
                    ProfileScre()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScre() {
    val context = LocalContext.current
    val repo = remember { UserRepositoryImpl() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val currentUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var dateOfBirth by remember { mutableStateOf("Select") }
    var gender by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch user info on launch
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            repo.getProfile(userId) { data ->
                name = data["name"] as? String ?: ""
                dateOfBirth = data["dob"] as? String ?: "Select"
                gender = data["gender"] as? String ?: ""
                isLoading = false
            }
        }
    }

    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dateOfBirth = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { (context as? ComponentActivity)?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF1F1F1))
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Name
                            Text(
                                text = name.ifBlank { "No Name" },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF222222)
                            )
                            // Email
                            Text(
                                text = email,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            // DOB & Gender
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("DOB", fontSize = 12.sp, color = Color.Gray)
                                    Text(dateOfBirth, fontSize = 14.sp, color = Color(0xFF1976D2))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Gender", fontSize = 12.sp, color = Color.Gray)
                                    Text(gender.ifBlank { "-" }, fontSize = 14.sp, color = Color(0xFF1976D2))
                                }
                            }
                            // Edit Button
                            if (!isEditMode) {
                                OutlinedButton(
                                    onClick = { isEditMode = true },
                                    modifier = Modifier.padding(top = 12.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Edit Profile")
                                }
                            }
                        }
                    }
                    // Edit Mode
                    if (isEditMode) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Full Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = dateOfBirth,
                                    onValueChange = {},
                                    label = { Text("Date of Birth") },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { datePicker.show() }
                                )
                                // Gender Dropdown
                                var genderExpanded by remember { mutableStateOf(false) }
                                val genderOptions = listOf("Male", "Female", "Other")
                                ExposedDropdownMenuBox(
                                    expanded = genderExpanded,
                                    onExpandedChange = { genderExpanded = !genderExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = gender,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Gender") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = genderExpanded,
                                        onDismissRequest = { genderExpanded = false }
                                    ) {
                                        genderOptions.forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    gender = option
                                                    genderExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedButton(
                                        onClick = { isEditMode = false },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Cancel")
                                    }
                                    Button(
                                        onClick = {
                                            if (userId.isEmpty()) {
                                                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val userMap = mutableMapOf<String, Any>()
                                            if (name.isNotBlank()) userMap["name"] = name
                                            if (dateOfBirth != "Select") userMap["dob"] = dateOfBirth
                                            if (gender.isNotBlank()) userMap["gender"] = gender
                                            repo.editProfile(userId, userMap) { success, message ->
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                                if (success) isEditMode = false
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Save")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
