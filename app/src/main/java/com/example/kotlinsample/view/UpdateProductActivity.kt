package com.example.kotlinsample.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType

class UpdateProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpdateProductScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductScreen() {
    val context = LocalContext.current
    val repo = remember { ProductResImpl() }

    val activity = context as? UpdateProductActivity

    val proId = activity?.intent?.getStringExtra("proId") ?: ""
    val initialName = activity?.intent?.getStringExtra("productName") ?: ""
    val initialCategory = activity?.intent?.getStringExtra("category") ?: ""
    val initialPrice = activity?.intent?.getStringExtra("price") ?: ""
    val initialDescription = activity?.intent?.getStringExtra("description") ?: ""

    var productName by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var price by remember { mutableStateOf(initialPrice) }
    var description by remember { mutableStateOf(initialDescription) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Update Product") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val priceInt = price.toIntOrNull()
                        if (priceInt == null) {
                            Toast.makeText(context, "Please enter a valid number for price", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val updateMap = mutableMapOf<String, Any>()
                        updateMap["productName"] = productName
                        updateMap["category"] = category
                        updateMap["price"] = priceInt
                        updateMap["description"] = description

                        repo.updateProduct(proId, updateMap) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) {
                                activity?.finish()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Product")
                }
            }
        }
    )
}
