package com.example.kotlinsample.repository

import com.example.kotlinsample.model.Comment
import com.example.kotlinsample.model.Service
import com.google.firebase.database.*

class ServiceResImpl : ServiceRes {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("services")

    override fun addService(model: Service, callback: (Boolean, String) -> Unit) {
        println("DEBUG: ServiceResImpl - Starting addService")
        println("DEBUG: ServiceResImpl - Service to add: ${model.serviceName}")
        val id = ref.push().key ?: return callback(false, "Failed to generate ID")
        model.serviceId = id
        println("DEBUG: ServiceResImpl - Generated ID: $id")
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                println("DEBUG: ServiceResImpl - Service added successfully with ID: $id")
                callback(true, "Service added successfully")
            } else {
                println("DEBUG: ServiceResImpl - Failed to add service: ${it.exception?.message}")
                callback(false, it.exception?.message ?: "Error adding service")
            }
        }
    }

    override fun delService(serviceId: String, callback: (Boolean, String) -> Unit) {
        ref.child(serviceId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Service deleted successfully")
            else callback(false, it.exception?.message ?: "Failed to delete service")
        }
    }

    override fun getServiceById(serviceId: String, callback: (Boolean, String, Service?) -> Unit) {
        ref.child(serviceId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val service = snapshot.getValue(Service::class.java)
                if (service != null) callback(true, "Service fetched", service)
                else callback(false, "Service not found", null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun updateService(serviceId: String, data: MutableMap<String, Any>?, callback: (Boolean, String) -> Unit) {
        if (data == null) return callback(false, "No data to update")
        ref.child(serviceId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Service updated successfully")
            else callback(false, it.exception?.message ?: "Failed to update service")
        }
    }

    override fun getAllServices(callback: (Boolean, String, List<Service?>) -> Unit) {
        println("DEBUG: ServiceResImpl - Starting getAllServices")
        println("DEBUG: ServiceResImpl - Database reference: ${ref.toString()}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println("DEBUG: ServiceResImpl - onDataChange called")
                println("DEBUG: ServiceResImpl - Snapshot exists: ${snapshot.exists()}")
                println("DEBUG: ServiceResImpl - Snapshot children count: ${snapshot.childrenCount}")
                
                val list = mutableListOf<Service>()
                for (childSnapshot in snapshot.children) {
                    try {
                        println("DEBUG: ServiceResImpl - Processing child: ${childSnapshot.key}")
                        val service = childSnapshot.getValue(Service::class.java)
                        if (service != null) {
                            println("DEBUG: ServiceResImpl - Successfully parsed service: ${service.serviceName}")
                            list.add(service)
                        } else {
                            println("DEBUG: ServiceResImpl - Failed to parse service from child: ${childSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        println("DEBUG: ServiceResImpl - Skipping non-service node: ${childSnapshot.key}, error: ${e.message}")
                    }
                }
                println("DEBUG: ServiceResImpl - Final list size: ${list.size}")
                callback(true, "Services fetched", list)
            }

            override fun onCancelled(error: DatabaseError) {
                println("DEBUG: ServiceResImpl - onCancelled called with error: ${error.message}")
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getServicesByProfessionalType(professionalType: String, callback: (Boolean, String, List<Service?>) -> Unit) {
        ref.orderByChild("professionalType").equalTo(professionalType)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Service>()
                    for (childSnapshot in snapshot.children) {
                        try {
                            val service = childSnapshot.getValue(Service::class.java)
                            if (service != null) {
                                list.add(service)
                            }
                        } catch (e: Exception) {
                            println("DEBUG: ServiceResImpl - Skipping non-service node in getServicesByProfessionalType: ${childSnapshot.key}, error: ${e.message}")
                        }
                    }
                    callback(true, "Services fetched by type", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun getServicesByUserId(userId: String, callback: (Boolean, String, List<Service?>) -> Unit) {
        ref.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Service>()
                    for (childSnapshot in snapshot.children) {
                        try {
                            val service = childSnapshot.getValue(Service::class.java)
                            if (service != null) {
                                list.add(service)
                            }
                        } catch (e: Exception) {
                            println("DEBUG: ServiceResImpl - Skipping non-service node in getServicesByUserId: ${childSnapshot.key}, error: ${e.message}")
                        }
                    }
                    callback(true, "User's services fetched", list)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
    }

    override fun editService(serviceId: String, updatedService: Service, callback: (Boolean, String) -> Unit) {
        ref.child(serviceId).setValue(updatedService).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Service updated successfully")
            else callback(false, it.exception?.message ?: "Failed to update service")
        }
    }

    override fun likeService(serviceId: String, userId: String, callback: (Boolean, String) -> Unit) {
        ref.child(serviceId).child("likes").get().addOnSuccessListener { snapshot ->
            val currentLikes = snapshot.getValue(object : com.google.firebase.database.GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            val updatedLikes = if (userId in currentLikes) currentLikes else currentLikes + userId
            
            ref.child(serviceId).child("likes").setValue(updatedLikes).addOnCompleteListener {
                if (it.isSuccessful) callback(true, "Service liked successfully")
                else callback(false, it.exception?.message ?: "Failed to like service")
            }
        }.addOnFailureListener {
            callback(false, it.message ?: "Failed to like service")
        }
    }

    override fun unlikeService(serviceId: String, userId: String, callback: (Boolean, String) -> Unit) {
        ref.child(serviceId).child("likes").get().addOnSuccessListener { snapshot ->
            val currentLikes = snapshot.getValue(object : com.google.firebase.database.GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            val updatedLikes = currentLikes.filter { it != userId }
            
            ref.child(serviceId).child("likes").setValue(updatedLikes).addOnCompleteListener {
                if (it.isSuccessful) callback(true, "Service unliked successfully")
                else callback(false, it.exception?.message ?: "Failed to unlike service")
            }
        }.addOnFailureListener {
            callback(false, it.message ?: "Failed to unlike service")
        }
    }

    override fun addComment(
        serviceId: String,
        comment: Comment,
        callback: (Boolean, String) -> Unit
    ) {
        val commentId = ref.child(serviceId).child("comments").push().key ?: return callback(false, "Failed to generate comment ID")
        comment.commentId = commentId

        ref.child(serviceId).child("comments").child(commentId).setValue(comment).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Comment added successfully")
            else callback(false, it.exception?.message ?: "Failed to add comment")
        }
    }



    override fun deleteComment(serviceId: String, commentId: String, callback: (Boolean, String) -> Unit) {
        ref.child(serviceId).child("comments").child(commentId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Comment deleted successfully")
            else callback(false, it.exception?.message ?: "Failed to delete comment")
        }
    }

    override fun markAsInterested(serviceId: String, userId: String, posterId: String, callback: (Boolean, String) -> Unit) {
        ref.child(serviceId).child("interested").get().addOnSuccessListener { snapshot ->
            val currentInterested = snapshot.getValue(object : com.google.firebase.database.GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            if (userId in currentInterested) {
                callback(false, "Already marked as interested")
                return@addOnSuccessListener
            }
            val updatedInterested = currentInterested + userId
            ref.child(serviceId).child("interested").setValue(updatedInterested).addOnCompleteListener {
                if (it.isSuccessful) {
                    // Add notification for the poster
                    val notificationRef = database.reference.child("notifications").child(posterId).push()
                    val notificationData = mapOf(
                        "type" to "interested",
                        "serviceId" to serviceId,
                        "fromUserId" to userId,
                        "timestamp" to System.currentTimeMillis()
                    )
                    notificationRef.setValue(notificationData)
                    callback(true, "Marked as interested. Poster will be notified in real time.")
                } else {
                    callback(false, it.exception?.message ?: "Failed to mark as interested")
                }
            }
        }.addOnFailureListener {
            callback(false, it.message ?: "Failed to mark as interested")
        }
    }

    // Test function to check Firebase connectivity
    fun testFirebaseConnection(callback: (Boolean, String) -> Unit) {
        println("DEBUG: ServiceResImpl - Testing Firebase connection")
        ref.child("test").setValue("test_value").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("DEBUG: ServiceResImpl - Firebase connection successful")
                // Clean up test data
                ref.child("test").removeValue()
                callback(true, "Firebase connection successful")
            } else {
                println("DEBUG: ServiceResImpl - Firebase connection failed: ${task.exception?.message}")
                callback(false, "Firebase connection failed: ${task.exception?.message}")
            }
        }
    }
} 