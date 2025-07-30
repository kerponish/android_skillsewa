package com.example.kotlinsample.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.view.pages.HomeScreen
import com.example.kotlinsample.view.pages.SearchScreen

class NavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationBody()
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBody() {
    HomeScreen()
}

/**
 * Handles navigation to Profile Activity in a Composable-safe way
 */
@Composable
fun ProfileNavigationHandler() {
    val context = LocalContext.current
    val inPreview = LocalInspectionMode.current

    if (!inPreview) {
        LaunchedEffect(Unit) {
            val intent = Intent(context, Profile::class.java)
            context.startActivity(intent)
        }
    } else {
        Text("Profile Screen (Preview)")
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBodyPreview() {
    NavigationBody()
}
