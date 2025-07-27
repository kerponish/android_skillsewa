package com.example.kotlinsample

import com.example.kotlinsample.model.Service
import org.junit.Test
import org.junit.Assert.*

class ServiceTest {
    
    @Test
    fun testServiceCreation() {
        val service = Service(
            serviceId = "test123",
            serviceName = "Electrical Repair",
            professionalType = "Electrician",
            price = 1500.0,
            description = "Fix electrical issues in home",
            duration = "2 hours",
            location = "Kathmandu",
            contactNumber = "9841234567"
        )
        
        assertEquals("test123", service.serviceId)
        assertEquals("Electrical Repair", service.serviceName)
        assertEquals("Electrician", service.professionalType)
        assertEquals(1500.0, service.price, 0.01)
        assertEquals("Fix electrical issues in home", service.description)
        assertEquals("2 hours", service.duration)
        assertEquals("Kathmandu", service.location)
        assertEquals("9841234567", service.contactNumber)
    }
    
    @Test
    fun testServiceDefaultValues() {
        val service = Service()
        
        assertEquals("", service.serviceId)
        assertEquals("", service.serviceName)
        assertEquals("", service.professionalType)
        assertEquals(0.0, service.price, 0.01)
        assertEquals("", service.description)
        assertEquals("", service.duration)
        assertEquals("", service.location)
        assertEquals("", service.contactNumber)
    }
} 