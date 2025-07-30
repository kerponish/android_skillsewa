package com.example.kotlinsample.repository

import com.example.kotlinsample.model.Service
import com.example.kotlinsample.model.Comment

interface ServiceRes {
    fun addService(model: Service, callback: (Boolean, String) -> Unit)
    fun delService(serviceId: String, callback: (Boolean, String) -> Unit)
    fun getServiceById(serviceId: String, callback: (Boolean, String, Service?) -> Unit)
    fun updateService(serviceId: String, data: MutableMap<String, Any>?, callback: (Boolean, String) -> Unit)
    fun getAllServices(callback: (Boolean, String, List<Service?>) -> Unit)
    fun getServicesByProfessionalType(professionalType: String, callback: (Boolean, String, List<Service?>) -> Unit)
    fun getServicesByUserId(userId: String, callback: (Boolean, String, List<Service?>) -> Unit)
    fun likeService(serviceId: String, userId: String, callback: (Boolean, String) -> Unit)
    fun unlikeService(serviceId: String, userId: String, callback: (Boolean, String) -> Unit)
    fun addComment(serviceId: String, comment: Comment, callback: (Boolean, String) -> Unit)
    fun deleteComment(serviceId: String, commentId: String, callback: (Boolean, String) -> Unit)
    fun editService(serviceId: String, updatedService: Service, callback: (Boolean, String) -> Unit)
    fun markAsInterested(serviceId: String, userId: String, posterId: String, callback: (Boolean, String) -> Unit)
} 