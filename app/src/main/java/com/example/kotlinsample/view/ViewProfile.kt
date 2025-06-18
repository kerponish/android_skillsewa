package com.example.kotlinsample.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsample.R
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth

class UserProfileViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ViewUserProfileScreen()
                }
            }
        }
    }
}

@Composable
fun ViewUserProfileScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepositoryImpl() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // User data fields
    var fullName by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var dob by remember { mutableStateOf("Loading...") }
    var gender by remember { mutableStateOf("") }

    // Load data from Firebase
    LaunchedEffect(Unit) {
        repo.getProfile(userId) { data ->
            val firstName = data["firstName"] as? String ?: ""
            val secondName = data["secondName"] as? String ?: ""
            fullName = "$firstName $secondName"
            email = data["email"] as? String ?: "N/A"
            dob = data["dob"] as? String ?: "N/A"
            gender = data["gender"] as? String ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(R.drawable.figo),
            contentDescription = "Profile Icon",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        // Profile Info Card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow("Full Name", fullName)
                InfoRow("Email", email)
                InfoRow("Date of Birth", dob)
                if (gender.isNotEmpty()) InfoRow("Gender", gender)
            }
        }

        Button(
            onClick = {
                context.startActivity(Intent(context, UserProfileActivity::class.java))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007DBA)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Edit Profile", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = {
                    FirebaseAuth.getInstance().currentUser?.delete()
                        ?.addOnSuccessListener {
                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        }
                        ?.addOnFailureListener {
                            Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            ) {
                Text("Delete Account", color = Color.Red)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(label.uppercase(), fontSize = 12.sp, color = Color(0xFF1691C6))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}
