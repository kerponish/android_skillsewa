package com.example.kotlinsample.repository

import android.content.Context
import android.net.Uri
import com.example.kotlinsample.model.Product

interface ProductRes {
    fun addPro(model: Product, callback: (Boolean, String) -> Unit)

    fun delPro(proId: String, callback: (Boolean, String) -> Unit)

    fun getProductById(proId: String, callback: (Boolean, String, Product?) -> Unit)

    fun updateProduct(proId: String, data: MutableMap<String, Any>?, callback: (Boolean, String) -> Unit)

    fun getAllProduct(callback: (Boolean, String, List<Product?>) -> Unit)

    //fun uploadImage(context: Context,imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context,uri: Uri): String?
}
