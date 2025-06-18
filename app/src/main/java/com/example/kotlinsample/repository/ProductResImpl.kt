package com.example.kotlinsample.repository

import android.content.Context
import android.net.Uri
import com.example.kotlinsample.model.Product
import com.google.firebase.database.*

class ProductResImpl : ProductRes {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.reference.child("products")

    override fun addPro(model: Product, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: return callback(false, "Failed to generate ID")
        model.proId = id
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product added")
            else callback(false, it.exception?.message ?: "Error adding product")
        }
    }

    override fun delPro(proId: String, callback: (Boolean, String) -> Unit) {
        ref.child(proId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product deleted")
            else callback(false, it.exception?.message ?: "Failed to delete")
        }
    }

    override fun getProductById(proId: String, callback: (Boolean, String, Product?) -> Unit) {
        ref.child(proId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                if (product != null) callback(true, "Product fetched", product)
                else callback(false, "Product not found", null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun updateProduct(proId: String, data: MutableMap<String, Any>?, callback: (Boolean, String) -> Unit) {
        if (data == null) return callback(false, "No data to update")
        ref.child(proId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product updated")
            else callback(false, it.exception?.message ?: "Failed to update")
        }
    }

    override fun getAllProduct(callback: (Boolean, String, List<Product?>) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                callback(true, "Products fetched", list)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun getFileNameFromUri(
        context: Context,
        uri: Uri
    ): String? {
        TODO("Not yet implemented")
    }
}
