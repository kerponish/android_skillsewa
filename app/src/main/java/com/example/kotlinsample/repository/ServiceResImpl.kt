package com.example.kotlinsample.repository

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
                
                val list = snapshot.children.mapNotNull { childSnapshot ->
                    println("DEBUG: ServiceResImpl - Processing child: ${childSnapshot.key}")
                    val service = childSnapshot.getValue(Service::class.java)
                    if (service != null) {
                        println("DEBUG: ServiceResImpl - Successfully parsed service: ${service.serviceName}")
                    } else {
                        println("DEBUG: ServiceResImpl - Failed to parse service from child: ${childSnapshot.key}")
                    }
                    service
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
                    val list = snapshot.children.mapNotNull { it.getValue(Service::class.java) }
                    callback(true, "Services fetched by type", list)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false, error.message, emptyList())
                }
            })
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