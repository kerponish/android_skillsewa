package com.example.kotlinsample.view.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(color= Color.LightGray)
    ){
        Text("this is the home screen")
    }
}