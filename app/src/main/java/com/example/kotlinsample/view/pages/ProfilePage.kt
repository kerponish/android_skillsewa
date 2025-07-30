package com.example.kotlinsample.view.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Edit
import com.google.firebase.database.*
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Cake
import com.example.kotlinsample.repository.ServiceResImpl
import com.example.kotlinsample.model.Service

@SuppressLint("UnrememberedMutableState")
@Composable
fun ProfileScr() {
    val context = LocalContext.current
    val repo = remember { com.example.kotlinsample.repository.UserRepositoryImpl() }
    val serviceRepo = remember { ServiceResImpl() }
    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val snackbarHostState = remember { SnackbarHostState() }
    var lastNotificationTimestamp by mutableLongStateOf(0L)
    var name by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedGender by remember { mutableStateOf("") }
    var editedDob by remember { mutableStateOf("") }
    var services by remember { mutableStateOf<List<Service>>(emptyList()) }

    // Remove all code related to profile image upload, display, and edit
    // Only show name, email, gender, and date of birth in the profile page

    // Fetch user info and services on launch
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            repo.getProfile(userId) { data ->
                name = data["name"] as? String ?: ""
                dateOfBirth = data["dob"] as? String ?: ""
                gender = data["gender"] as? String ?: ""
                profilePicture = data["profilePicture"] as? String ?: ""
                editedName = name
                editedGender = gender
                editedDob = dateOfBirth
                isLoading = false
            }
            serviceRepo.getServicesByUserId(userId) { ok, msg, serviceList ->
                if (ok && serviceList != null) {
                    services = serviceList.filterNotNull()
                }
            }
        }
    }

    // Real-time notification listener
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            val notificationRef = FirebaseDatabase.getInstance().reference.child("notifications").child(userId)
            notificationRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val type = snapshot.child("type").getValue(String::class.java)
                    val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                    if (timestamp > lastNotificationTimestamp) {
                        lastNotificationTimestamp = timestamp
                        if (type == "interested") {
                            snackbarMessage = "Someone is interested in your service!"
                            showSnackbar = true
                        }
                    }
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
    if (showSnackbar) {
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2196F3), Color(0xFFE3F2FD))
                )
            )
    ) {
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.TopCenter))
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with border and shadow
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(5.dp, Color(0xFF2196F3), CircleShape)
                        .shadow(12.dp, CircleShape)
                        .clickable {
                            // Removed image picker launcher
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePicture.isNotEmpty()) {
                        AsyncImage(
                            model = profilePicture,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile Picture",
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF2196F3)
                        )
                    }
                    // Edit icon overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Picture",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Name
                if (isEditing) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = name.ifBlank { "User Name" },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(currentUser?.email ?: "N/A")
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            if (isEditing) {
                                OutlinedTextField(
                                    value = editedGender,
                                    onValueChange = { editedGender = it },
                                    label = { Text("Gender") },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Text(gender.ifBlank { "Not specified" })
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Cake, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            if (isEditing) {
                                OutlinedTextField(
                                    value = editedDob,
                                    onValueChange = { editedDob = it },
                                    label = { Text("Date of Birth") },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Text(dateOfBirth.ifBlank { "Not specified" })
                            }
                        }
                    }
                }
                // Edit/Save/Cancel Buttons
                if (isEditing) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            val updateData = mapOf(
                                "name" to editedName,
                                "gender" to editedGender,
                                "dob" to editedDob
                            )
                            repo.editProfile(userId, updateData) { success, message ->
                                if (success) {
                                    name = editedName
                                    gender = editedGender
                                    dateOfBirth = editedDob
                                    isEditing = false
                                    snackbarMessage = "Profile updated successfully"
                                    showSnackbar = true
                                } else {
                                    snackbarMessage = message
                                    showSnackbar = true
                                }
                            }
                        }) { Text("Save") }
                        Button(onClick = { isEditing = false }) { Text("Cancel") }
                    }
                } else {
                    FloatingActionButton(
                        onClick = { isEditing = true },
                        containerColor = Color(0xFF2196F3),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
                // User's Services
                Spacer(Modifier.height(24.dp))
                Text("Your Services", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2196F3))
                if (services.isEmpty()) {
                    Text("You have not posted any services yet.", color = Color.Gray)
                } else {
                    Column(Modifier.fillMaxWidth()) {
                        services.forEach { service ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(service.serviceName, fontWeight = FontWeight.Bold)
                                    Text(service.description, color = Color.Gray)
                                    Text("Price: NPR ${service.price}", color = Color(0xFF2196F3))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 