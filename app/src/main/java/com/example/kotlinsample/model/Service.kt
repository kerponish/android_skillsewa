package com.example.kotlinsample.model

data class Service(
    var serviceId: String = "",
    var userId: String = "", // The UID of the user who created/owns this service
    var serviceName: String = "",
    var professionalType: String = "", // carpenter, electrician, plumber, etc.
    var price: Double = 0.0,
    var description: String = "",
    var duration: String = "", // estimated time to complete
    var location: String = "",
    var contactNumber: String = ""
) 