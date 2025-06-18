package com.example.kotlinsample.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.R
import com.example.kotlinsample.ui.theme.KotlinsampleTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class Splash : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinsampleTheme {
                Scaffold { innerPadding ->
                    SplashScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun SplashScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val activity = context as Activity

    var hasNavigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(3000L)

        val sharedPref = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", "")
        val savedPassword = sharedPref.getString("password", "")
        val currentUser = FirebaseAuth.getInstance().currentUser

        Log.d("Splash", "SavedEmail: $savedEmail | currentUser: $currentUser")

        if (!hasNavigated) {
            hasNavigated = true
            when {
                currentUser != null -> {
                    // Firebase says user is already authenticated
                    context.startActivity(Intent(context, NavigationActivity::class.java))
                }
                !savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty() -> {
                    // Credentials are remembered, go to login screen which auto-fills & logs in
                    context.startActivity(Intent(context, SkillsLoginActivity::class.java))
                }
                else -> {
                    // No user, go to login screen
                    context.startActivity(Intent(context, SkillsLoginActivity::class.java))
                }
            }
            activity.finish()
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App Logo"
        )
        Spacer(modifier = Modifier.height(20.dp))
        CircularProgressIndicator()
    }
}
