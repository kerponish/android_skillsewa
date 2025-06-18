package com.example.kotlinsample.view


import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.unit.sp
import java.util.*

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.InputStream

val CLOUD_NAME = "dfxerqfg"
val UPLOAD_PRESET = "your_unsigned_preset"

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        setContent {
            MaterialTheme {
                EditUserProfileScreen(
                    userId = currentUserId,
                    onProfileUpdated = { finish() }
                )
            }
        }
    }
}
@Composable
fun EditUserProfileScreen(userId: String, onProfileUpdated: () -> Unit) {
    val repo = remember { UserRepositoryImpl() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                uploadToCloudinary(context, it) { cloudUrl ->
                    uploadedImageUrl = cloudUrl
                    repo.editProfile(userId, mapOf("profileImage" to cloudUrl)) { _, _ -> }
                }
            }
        }
    )



    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    var showSuccess by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> dob = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val genderOptions = listOf("Male", "Female", "Other")

    LaunchedEffect(userId) {
        repo.getProfile(userId) { data ->
            name = data["name"] as? String ?: ""
            email = data["email"] as? String ?: ""
            dob = data["dob"] as? String ?: ""
            gender = data["gender"] as? String ?: ""
            uploadedImageUrl = data["profileImage"] as? String ?: ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                )
            )
            .padding(16.dp)
    ) {
        uploadedImageUrl.takeIf { it.isNotEmpty() }?.let { url ->
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { imageLauncher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
        } ?: Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { imageLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Text("Add Image", color = Color.White)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Edit Profile",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = Color.Black) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    label = { Text("Email", color = Color.Black) },
                    singleLine = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    label = { Text("Date of Birth", color = Color.Black) },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color(0xFF0D47A1),
                        disabledContainerColor = Color(0xFFE3F2FD),
                        disabledLabelColor = Color(0xFF1565C0),
                        disabledBorderColor = Color(0xFF1565C0)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Select Gender",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    genderOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = gender == option,
                                    onClick = { gender = option },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = gender == option, onClick = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(option, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || dob.isBlank() || gender.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val updateData: Map<String, Any> = mapOf(
                            "name" to name,
                            "dob" to dob,
                            "gender" to gender
                        )

                        repo.editProfile(userId, updateData) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                showSuccess = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Save Changes", color = Color.White, style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // ✅ Success animation overlay
        if (showSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Profile Updated!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
fun uploadToCloudinary(context: android.content.Context, uri: Uri, onComplete: (String) -> Unit) {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val requestBody = inputStream?.readBytes()?.let { bytes ->
        MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "image.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), bytes))
            .addFormDataPart("upload_preset", UPLOAD_PRESET)
            .build()
    }

    val request = Request.Builder()
        .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
        .post(requestBody!!)
        .build()

    val client = OkHttpClient()
    Thread {
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        val imageUrl = Regex("\"url\":\"(.*?)\"").find(body.orEmpty())?.groupValues?.get(1)?.replace("\\/", "/")
        imageUrl?.let { onComplete(it) }
    }.start()
}

