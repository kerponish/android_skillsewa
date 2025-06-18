package com.example.kotlinsample.model

data class Product(
    var proId : String="",
    var productName : String="",
    var category : String= "",
    var price : Double=0.0,
    var description : String="",
    var image : String=""
)
