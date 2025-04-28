package com.example.kotlinsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.ui.theme.KotlinsampleTheme

class lololo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinsampleTheme {
                Scaffold { innerPadding ->
                    lololo(innerPadding)
                }
            }
        }
    }
}

@Composable
fun lololo(innerPaddingValues: PaddingValues = PaddingValues(0.dp)) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember{mutableStateOf(false)}

    val isInPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
            .background(Color.White)
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter =painterResource(R.drawable.figo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Buy Meth From Heisenberg",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                label = {Text("Email")},
                placeholder = { Text("abc@gmail.com") },
                value = username,
                onValueChange = { username = it }
            )}
        Spacer(modifier = Modifier.height(16.dp))
Row{
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                label = {Text("Password")},
                placeholder = { Text("******") },

                value = password,
                onValueChange = { password = it },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val iconRes =
                        if (passwordVisible) android.R.drawable.ic_menu_view
                        else android.R.drawable.ic_secure
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ){
            Row ( modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start

        ){
            Checkbox(
                checked = rememberMe,
                onCheckedChange = {
                    rememberMe = !rememberMe
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Green,
                    checkmarkColor = Color.White
                )

            )
            Text(text = "remember me")
        }
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Text(modifier = Modifier.clickable{},
                    text = "forget password"
                )
            }
        }
        Row(){
            Spacer(modifier = Modifier.height(20.dp))
            ElevatedButton(onClick ={},
                modifier = Modifier.width(200.dp).height(70.dp)
                    .padding(20.dp,0.dp)) {
                Text(text = "login")
            }



        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(modifier = Modifier.clickable{},
                text = "New User Register Now"
            )


        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text="------------------other options-------------------")

        }
        Spacer(modifier = Modifier.height(16.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(painter = painterResource(R.drawable.ggogle),
                contentDescription = null,
                modifier = Modifier.height(50.dp).width(50.dp))
            Image(painter =painterResource(R.drawable.fbb),
                contentDescription = null,
                modifier = Modifier.height(50.dp).width(50.dp))
            Image(painter = painterResource(R.drawable.tor),
                contentDescription = null,
                modifier = Modifier.height(70.dp).width(70.dp)
            )

        }




    }
}

@Preview
@Composable
fun MyComposablePreview() {
    KotlinsampleTheme {
        lololo(innerPaddingValues =  PaddingValues(0.dp))
    }
}
