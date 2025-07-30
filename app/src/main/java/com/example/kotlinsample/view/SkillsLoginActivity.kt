package com.example.kotlinsample.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlinsample.R
import com.example.kotlinsample.repository.UserRepositoryImpl
import com.example.kotlinsample.ui.theme.KotlinsampleTheme
import com.example.kotlinsample.viewmodel.UserViewModel
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.clip
import com.google.firebase.auth.FirebaseAuth

class SkillsLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinsampleTheme {
                SkillsSewaLoginScreen {
                    startActivity(Intent(this, signup::class.java))
                }
            }
        }
    }
}

@Composable
fun SkillsSewaLoginScreen(
    onSignupClick: () -> Unit = {}
) {
    val repo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repo) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        val savedEmail = sharedPref.getString("email", "") ?: ""
        val savedPassword = sharedPref.getString("password", "") ?: ""

        // Make sure FirebaseAuth does NOT already have a user
        val rememberMePref = sharedPref.getBoolean("remember_me", false)
        if (rememberMePref && savedEmail.isNotEmpty() && savedPassword.isNotEmpty())  {

            email = savedEmail
            password = savedPassword
            rememberMe = true
            isLoading = true

            userViewModel.login(savedEmail, savedPassword) { success, message ->
                isLoading = false
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    context.startActivity(Intent(context, NavigationActivity::class.java))
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0077B6))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(45.dp))

        Text("Skills Sewa", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Text("“तपाईंको सीप आवश्यक हातमा”", fontSize = 17.sp, color = Color.White)
        Spacer(modifier = Modifier.height(28.dp))
        Text("Login", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("USERNAME") },
            placeholder = { Text("username") },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedContainerColor = Color(0xFF5A64EA),
                unfocusedContainerColor = Color(0xFF5A64EA)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("PASSWORD") },
            placeholder = { Text("*********") },
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(icon, contentDescription = if (showPassword) "Hide Password" else "Show Password", tint = Color.White)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedContainerColor = Color(0xFF5A64EA),
                unfocusedContainerColor = Color(0xFF5A64EA)
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = 8.dp)
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White
                )
            )
            Text("Remember Me", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Button(
                onClick = {
                    isLoading = true
                    userViewModel.login(email, password) { success, message ->
                        isLoading = false
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            if (rememberMe) {
                                sharedPref.edit().putString("email", email)
                                    .putString("password", password)
                                    .putBoolean("remember_me", true)
                                    .apply()
                            } else {
                                sharedPref.edit().clear().apply()
                            }
                            context.startActivity(Intent(context, NavigationActivity::class.java))
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp)
            ) {
                Text("Login", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Skills Sewa Logo",
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(24.dp)) // increase dp for rounder corners
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("New user? ", color = Color.White)
            Text(
                text = "Signup here",
                color = Color.Yellow,
                modifier = Modifier.clickable(onClick = onSignupClick)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Forgot Password?",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable {
                    val intent = Intent(context, ForgetPassword::class.java)
                    context.startActivity(intent)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreviewBody() {
    KotlinsampleTheme {
        SkillsSewaLoginScreen()
    }
}
