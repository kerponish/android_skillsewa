package com.example.kotlinsample

fun main (){
    // arithmetic Op
    var num1 : Double = 10.6
    var num2 : Double= 5.0
    var result : Double =0.0

    result = num1+ num2
    println("num1 + num2 is $result")
    result = num1+ num2
    println("num1 - num2 is $result")
    result = num1+ num2
    println("num1 * num2 is $result")
    result = num1+ num2
    println("num1 / num2 is $result")
    result = num1+ num2
    println("num1 % num2 is $result")

    //assignment op

    var x:Int=20
    var y : Int=10
    var z: Int = 0

    z=x+y
    println("z=x+y=$z")
    z +=x
    println("z+=x =$z")
    z -=x
    println("z-=x =$z")
    z *=x
    println("z*=x =$z")
    z /=x
    println("z/=x =$z")
    z %=x
    println("z%=x =$z")

// unary op
    var number: Double = 7.6
    var isCheck: Boolean = true;
    println("+number = ${+number}" );
    println("-number = ${-number}"
    );
    println("++number = ${++number}"
    );
    println("--number = ${--number}" );
    println("!isCheck = ${!isCheck}" );
    println("-------------")
    var resu:Double =4.7
    println("resu :$result")
//when the result++ is executed, the original value is evaluated first
//and value of result is increased only after that
    println("result++ :" + result++)

    //equality and relational operators

    var a: Int = 5
    var b:Int = 5
    println("a == b:"+(a == b))
    println("a != b:"+(a != b))
    println("a<b:"+(a<b))
    println("a>b:"+(a>b))
    println("a>=b:"+(a>=b))
    println("a<=b:"+(a<=b))

    //conditional op

    var number1:Int=5
    var number2:Int=8
    var number3:Int=12
    var res: Boolean =false
    res=(number1 > number2) && (number3 > number2)
    println(res)
    res=(number1 > number2) || (number3 > number2)
    println(res)

    //operator procedence
    // bodmas

//BODMAS
    var reso: Int = 5+2*4
    println("Result = "+result)
    reso = (5+2) * 4
    println("Result = "+result)
    var r: Int = 8;
    var u:Int = 4;
    var i:Int = 2;
    var sum: Int = 0;
    sum = r + --u+ --i
    print("r+ --u-i :: $sum")

   //rangeTo()Function and In Operator
    var myCharRange = 'a'.rangeTo( 'j')
    var testCharRange = 'a'.. 'j'
    var check = 'Z' in testCharRange
    println("mycharRange has Z = $check")
    println(myCharRange)
    println(testCharRange)

    //console Input in android app deve

    print("Enter name:: ")
    var name: String? = readln();
// The data user inputs is always a String so,
//type conversion for data type other than String print("Enter age:: ")
    var age: Int = readln().toInt();




}
