package com.example.kotlinsample.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF1F1F1)
                ) {
                    EditUserProfileScreen()
                }
            }
        }
    }
}

@Composable
fun EditUserProfileScreen() {
    val context = LocalContext.current
    val repo = remember { UserRepositoryImpl() }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("hello@reallygreatsite.com") }
    var dateOfBirth by remember { mutableStateOf("Select") }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Edit Your\nProfile",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            LabelText("Full Name")
            RoundedInputField(value = name, onValueChange = { name = it })

            LabelText("Email")
            RoundedInputField(value = email, onValueChange = { email = it })

            LabelText("Date of Birth")
            RoundedInputField(
                value = dateOfBirth,
                onValueChange = {},
                readOnly = true,
                onClick = { datePicker.show() }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                if (userId.isEmpty()) {
                    Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val userMap = mutableMapOf<String, Any>(
                    "name" to name,
                    "email" to email,
                    "dob" to dateOfBirth
                )

                repo.editProfile(userId, userMap) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) (context as? ComponentActivity)?.finish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007DBA)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text("UPDATE", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        color = Color(0xFF1691C6),
        letterSpacing = 1.5.sp
    )
}

@Composable
fun RoundedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() }
                else Modifier
            )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            enabled = true,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxSize(),
            textStyle = TextStyle(fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}
