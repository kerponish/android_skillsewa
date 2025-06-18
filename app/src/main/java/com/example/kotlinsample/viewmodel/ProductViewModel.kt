package com.example.kotlinsample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinsample.model.Product
import com.example.kotlinsample.repository.ProductRes

class ProductViewModel(private val repo: ProductRes) : ViewModel() {

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> get() = _product

    private val _productList = MutableLiveData<List<Product?>>()
    val productList: LiveData<List<Product?>> get() = _productList

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> get() = _statusMessage

    fun addPro(
        model: Product,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addPro(model) { success, message ->
            _statusMessage.postValue(message)
            callback(success, message)
        }
    }

    fun delPro(
        proId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.delPro(proId) { success, message ->
            _statusMessage.postValue(message)
            callback(success, message)
        }
    }

    fun getProductById(proId: String) {
        repo.getProductById(proId) { success, message, product ->
            _statusMessage.postValue(message)
            _product.postValue(product)
        }
    }

    fun updateProduct(
        proId: String,
        data: MutableMap<String, Any>?,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateProduct(proId, data) { success, message ->
            _statusMessage.postValue(message)
            callback(success, message)
        }
    }

    fun getAllProduct() {
        repo.getAllProduct { success, message, products ->
            _statusMessage.postValue(message)
            _productList.postValue(products)
        }
    }
}

