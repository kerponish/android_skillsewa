package com.example.kotlinsample

import androidx.compose.animation.scaleOut

fun main() {
    //println("hello")
    //immutable
    //val age =10
    //age=20

    //mutable
//    var name: String ="kripan"
//    name="ram"
//    var age :Int = 10
//    println("hello i am ${name.uppercase()} and $age years old")
    var male = arrayListOf<String>("10","20","30")
    var female= ArrayList<String>()
    female.add("sita")
    female.add("rijan")
    var fee= ArrayList<Any>()
    fee.add(10)
    fee.add("ram")
    fee.removeAt(0)

    var meaning = mapOf(
        "apple " to "this is fruits",
        "samsung" to "this is mobile"
    )
    println(meaning["apple"])



}
