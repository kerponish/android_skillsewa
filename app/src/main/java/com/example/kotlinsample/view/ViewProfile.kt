package com.example.kotlinsample.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth

class UserProfileViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            finish()
            return
        }

        setContent {
            MaterialTheme {
                ViewUserProfileScreen(userId = currentUserId)
            }
        }
    }
}

@Composable
fun ViewUserProfileScreen(userId: String) {
    val repo = remember { UserRepositoryImpl() }
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var dob by remember { mutableStateOf("Loading...") }
    var gender by remember { mutableStateOf("Loading...") }

    LaunchedEffect(userId) {
        repo.getProfile(userId) { data ->
            fullName = data["name"] as? String ?: "N/A"
            email = data["email"] as? String ?: "N/A"
            dob = data["dob"] as? String ?: "N/A"
            gender = data["gender"] as? String ?: "N/A"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                )
            )
            .padding(16.dp)
    ) {
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Profile Details",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                )

                InfoRow(label = "Full Name", value = fullName)
                InfoRow(label = "Email", value = email)
                InfoRow(label = "Date of Birth", value = dob)
                InfoRow(label = "Gender", value = gender)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        context.startActivity(Intent(context, UserProfileActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) {
                    Text("Edit Profile", color = Color.White)
                }

                Button(
                    onClick = {
                        logoutAndReturnToLogin(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

fun logoutAndReturnToLogin(context: Context) {
    FirebaseAuth.getInstance().signOut()
    val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()

    val intent = Intent(context, SkillsLoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}
