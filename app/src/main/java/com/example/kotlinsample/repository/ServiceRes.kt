package com.example.kotlinsample.repository

import com.example.kotlinsample.model.Service

interface ServiceRes {
    fun addService(model: Service, callback: (Boolean, String) -> Unit)
    fun delService(serviceId: String, callback: (Boolean, String) -> Unit)
    fun getServiceById(serviceId: String, callback: (Boolean, String, Service?) -> Unit)
    fun updateService(serviceId: String, data: MutableMap<String, Any>?, callback: (Boolean, String) -> Unit)
    fun getAllServices(callback: (Boolean, String, List<Service?>) -> Unit)
    fun getServicesByProfessionalType(professionalType: String, callback: (Boolean, String, List<Service?>) -> Unit)
} 