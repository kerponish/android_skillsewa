package com.example.kotlinsample.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kotlinsample.model.Product
import com.example.kotlinsample.repository.ProductResImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetProductsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetProductsScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GetProductsScreen() {
    val context = LocalContext.current
    val repo = remember { ProductResImpl() }

    var productList by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun loadProducts(showSnackbar: Boolean = false) {
        isLoading = true
        repo.getAllProduct { success, message, list ->
            (context as? ComponentActivity)?.runOnUiThread {
                isLoading = false
                if (success) {
                    productList = list.filterNotNull()
                    if (showSnackbar) {
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Product list refreshed")
                        }
                    }
                } else {
                    Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
                actions = {
                    IconButton(onClick = { loadProducts(showSnackbar = true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                productList.isEmpty() -> {
                    Text("No products available", modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(productList) { product ->
                            ProductCard(
                                product = product,
                                onDelete = {
                                    repo.delPro(product.proId) { success, message ->
                                        (context as? ComponentActivity)?.runOnUiThread {
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            if (success) {
                                                loadProducts()
                                            }
                                        }
                                    }
                                },
                                onUpdate = {
                                    val intent = Intent(context, UpdateProductActivity::class.java).apply {
                                        putExtra("proId", product.proId)
                                        putExtra("productName", product.productName)
                                        putExtra("category", product.category)
                                        putExtra("price", product.price)
                                        putExtra("description", product.description)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onDelete: () -> Unit, onUpdate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Name: ${product.productName}", style = MaterialTheme.typography.titleMedium)
            Text("Price: Rs. ${product.price}", style = MaterialTheme.typography.bodyMedium)
            Text("Category: ${product.category}", style = MaterialTheme.typography.bodySmall)
            Text("Description: ${product.description}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }

                Button(
                    onClick = onUpdate,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
            }
        }
    }
}
