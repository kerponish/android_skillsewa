package com.example.cardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.cardapp.ui.theme.CardAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold { innerpadding ->
                CardAppScreen(innerpadding)
            }


        }
    }
}

@Composable
fun CardAppScreen() {
    val appGreen = Color(0xFF4CD97B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top bar with profile
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Card",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Simple and easy to use app",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Profile Picture
                Image(
                    painter = painterResource(R.drawable.setti),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grid of cards
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    CategoryCard(
//                        modifier = Modifier.weight(1f),
//                        icon = R.drawable.cr,
//                        title = "Text",
//                        itemCount = "0 items sorted"
//                    )
//                    CategoryCard(
//                        modifier = Modifier.weight(1f),
//                        icon = R.drawable.cr,
//                        title = "Address",
//                        itemCount = "0 items sorted"
//                    )
//                }

                // Second row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    CategoryCard(
//                        modifier = Modifier.weight(1f),
//                        icon = R.drawable.cr,
//                        title = "Character",
//                        itemCount = "0 items sorted"
//                    )
//                    CategoryCard(
//                        modifier = Modifier.weight(1f),
//                        icon = R.drawable.cr,
//                        title = "Bank card",
//                        itemCount = "0 items sorted"
//                    )
                }

                // Third row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
//                    CategoryCard(
//                        modifier = Modifier.weight(1f),
//                        icon = R.drawable.cr,
//                        title = "Password",
//                        itemCount = "0 items sorted"
//                    )
//                    CategoryCard(
//                        modifier = Modifier.weight(1f),
//                        icon = R.drawable.cr,
//                        title = "Logistics",
//                        itemCount = "0 items sorted"
//                    )
                }

                // Settings card (full width)
                SettingsCard()
            }
        }
    }
}

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    icon: Int,
    title: String,
    itemCount: String
) {
    Card(
        modifier = modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = itemCount,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SettingsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.setti),
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp)
            )
            )


            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Customize how the app works",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    CardAppScreen(innerPadding =PaddingValues(0.dp))
}