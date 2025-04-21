package com.example.kotlinsample

import kotlin.random.Random

fun main(){
    //if else statement

    print("Please enter a number : ")
    var number: Any = readln()!!.toInt()
    if (number.toString().toInt() % 2 == 0){ println("$number is even")
    }
    else{
        println("$number is odd")
    }

    //if else if ladder

    print("Please enter your age :: ")
    var yourAge: Int = readln().toInt()
    if (yourAge < 13) {
        print("You are a child")
    } else if (yourAge < 19) {
        print("You are a teenager")
    } else {
        if (yourAge < 50) {
            print("You are an adult")
        } else {
            print("You are a senior")
        }
    }

    //nested if

    println("Please enter 3 numbers: ")
    var number1: Int = readln()!!.toInt()
    var number2: Int = readln()!!.toInt()
    var number3: Int = readln()!!.toInt()
    var largestNumber: Int
    if (number1 >= number2) {

        if (number1 >= number3) {
            largestNumber = number1
        } else {
            largestNumber = number3
        }
    } else {
        if (number2 >= number3) {
            largestNumber = number2
        } else {
            largestNumber = number3
        }
    }
    println("The largest number is $largestNumber")

    // when case

    print("Please enter a day number of week :-")
    var dayNumber: Int = readln().toInt()
    var day: String
    when (dayNumber) {

        1 -> day = "Sunday"
        2 -> day = "Monday"
        3 -> day = "Tuesday"
        4 -> day = "Wednesday"
        5 -> day = "Thursday"
        6 -> day = "Friday"
        7 -> day = "Saturday"
        else -> day = "Invalid day choice"
    }
    println(day)

    //for loop
    for (i in 1 .. 9){
        println(i)
    }
    var nig: Int=0
    for (x in 0 .. 5){
        println(x)
        nig +=x
    }


    //while loop
    var i :Int=0;
    while(i<5){
        println(i)
        i++
    }

    //infinite loop

    var nu = Random.nextInt(0,10000)
    println("Please enter any number from 0 to 10000: - ")
    while (2>1) {
        var userGuess: Int = readln().toInt()
        if(userGuess == nu){
            println("congratulations!!!!, you won")
            break
        }else if(userGuess <nu){
            println("Increase your guess")
        }else{
        }
        println("Decrease your guess")

        // do while loop
        var n:Int=1
        do{
            println(n)
            n++
        }
        while(n<14)

    }
}