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
    var contactNumber: String = "",
    var imageUrl: String = "", // URL for service image
    var postedBy: String = "", // Name of the user who posted the service
    var likes: List<String> = emptyList(), // List of user IDs who liked the service
    var comments: List<Comment> = emptyList(), // List of comments on the service
    var interested: List<String> = emptyList() // List of user IDs who marked as interested
)

data class Comment(
    var commentId: String = "",
    var userId: String = "", // User ID who made the comment
    var userName: String = "", // Name of the user who commented
    var comment: String = "",
    var timestamp: Long = System.currentTimeMillis()
) 