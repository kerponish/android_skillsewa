package com.example.kotlinsample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinsample.model.Service
import com.example.kotlinsample.repository.ServiceRes

class ServiceViewModel(private val repo: ServiceRes) : ViewModel() {

    private val _service = MutableLiveData<Service?>()
    val service: LiveData<Service?> get() = _service

    private val _serviceList = MutableLiveData<List<Service?>>()
    val serviceList: LiveData<List<Service?>> get() = _serviceList

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    fun addService(
        model: Service,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addService(model) { success, message ->
            _statusMessage.postValue(message)
            callback(success, message)
        }
    }

    fun delService(
        serviceId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.delService(serviceId) { success, message ->
            _statusMessage.postValue(message)
            callback(success, message)
        }
    }

    fun getServiceById(serviceId: String) {
        repo.getServiceById(serviceId) { success, message, service ->
            _statusMessage.postValue(message)
            _service.postValue(service)
        }
    }

    fun updateService(
        serviceId: String,
        data: MutableMap<String, Any>?,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateService(serviceId, data) { success, message ->
            _statusMessage.postValue(message)
            callback(success, message)
        }
    }

    fun getAllServices() {
        repo.getAllServices { success, message, services ->
            _statusMessage.postValue(message)
            _serviceList.postValue(services)
        }
    }

    fun getServicesByProfessionalType(professionalType: String) {
        repo.getServicesByProfessionalType(professionalType) { success, message, services ->
            _statusMessage.postValue(message)
            _serviceList.postValue(services)
        }
    }
} 