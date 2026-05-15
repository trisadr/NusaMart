package com.example.nusamart.data.repository.product

import android.content.Context
import com.example.nusamart.data.model.product.Product
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

// JSON-Friendly Models

data class CategoryJson(
    val idCategory: String,
    val categoryName: String,
    val iconURL: Int? = null,
    val isActive: Boolean
)

data class SubCategoryJson(
    val idSubCategory: String,
    val idCategory: String,
    val subCategoryName: String,
    val description: String? = null
)

data class ProductJson(
    val idProduct: String,
    val idStore: String,
    val productName: String,
    val description: String? = null,
    val weightGram: Double,
    val productStatus: String, // String mapping untuk enum
    val createAt: String,
    val updateAt: String,
    val avgRating: Double? = null
)

data class ProductItemJson(
    val idItem: String,
    val idProduct: String,
    val sku: String? = null,
    val stock: Int,
    val price: Double,
    val isActive: Boolean
)

data class ProductImageJson(
    val idImage: String,
    val idProduct: String,
    val imageURL: Int,
    val isPrimary: Boolean
)

data class ProductVariationJson(
    val idVariation: String,
    val idItem: String,
    val typeVariation: String,
    val value: String
)

data class ProductSubcategoryJson(
    val idProductSubCat: String,
    val idProduct: String,
    val idSubCategory: String
)

// Hasil Operasi

sealed class ProductResult {
    data class Success(val productId: String) : ProductResult()
    data class Error(val message: String) : ProductResult()
}

// Repository

class ProductRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private val categoryFile = "category.json"
    private val subCategoryFile = "sub_category.json"
    private val productFile = "product.json"
    private val productItemFile = "product_item.json"
    private val productImageFile = "product_image.json"
    private val productVariationFile = "product_variation.json"
    private val productSubcatFile = "product_subcategory.json"

    // Helper Baca/Tulis JSON

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            // Jika file belum ada di internal storage, coba ambil dari assets (json sebagai data awal)
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                // Abaikan jika tidak ada file awal di assets
                return mutableListOf()
            }
        }
        val json = file.readText()
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }


    // KATEGORI & SUB KATEGORI

    suspend fun getAllProducts(): List<ProductJson> = withContext(Dispatchers.IO) {
        return@withContext readJson<ProductJson>(productFile).filter { it.productStatus == "ACTIVE" }
    }

    suspend fun getAllCategories(): List<CategoryJson> = withContext(Dispatchers.IO) {
        return@withContext readJson<CategoryJson>(categoryFile).filter { it.isActive }
    }

    suspend fun getSubCategoriesByCategory(categoryId: String): List<SubCategoryJson> = withContext(Dispatchers.IO) {
        return@withContext readJson<SubCategoryJson>(subCategoryFile).filter { it.idCategory == categoryId }
    }


    // MANAJEMEN PRODUK

    suspend fun getProductsByStore(storeId: String): List<ProductJson> = withContext(Dispatchers.IO) {
        val products = readJson<ProductJson>(productFile)
        return@withContext products.filter { it.idStore == storeId }
    }
    suspend fun getProductItemsByItemId(itemId: String): ProductItemJson? = withContext(Dispatchers.IO) {
        // Gunakan nama variabel file JSON-mu, biasanya "product_item.json" atau variabel productItemFile
        val items = readJson<ProductItemJson>("product_item.json")
        return@withContext items.find { it.idItem == itemId }
    }
    suspend fun getProductItems(productId: String): List<ProductItemJson> = withContext(Dispatchers.IO) {
        val items = readJson<ProductItemJson>(productItemFile)
        return@withContext items.filter { it.idProduct == productId }
    }

    suspend fun getProductImages(productId: String): List<ProductImageJson> = withContext(Dispatchers.IO) {
        val images = readJson<ProductImageJson>(productImageFile)
        return@withContext images.filter { it.idProduct == productId }
    }

    suspend fun getProductVariations(itemId: String): List<ProductVariationJson> = withContext(Dispatchers.IO) {
        val variations = readJson<ProductVariationJson>(productVariationFile)
        return@withContext variations.filter { it.idItem == itemId }
    }


    // TAMBAH PRODUK BARU (KOMPLEKS)

    suspend fun addProduct(
        storeId: String,
        productName: String,
        description: String,
        weightGram: Double,
        subCategoryIds: List<String>,
        basePrice: Double,
        baseStock: Int,
        primaryImageRes: Int
    ): ProductResult = withContext(Dispatchers.IO) {
        delay(500) // Simulasi loading jaringan

        val products = readJson<ProductJson>(productFile)

        // Generate Product ID (PRD-000001)
        val maxPrdNum = products.maxOfOrNull { it.idProduct.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newProductId = String.format("PRD-%06d", maxPrdNum + 1)
        val now = LocalDateTime.now().toString()

        val newProduct = ProductJson(
            idProduct = newProductId,
            idStore = storeId,
            productName = productName.trim(),
            description = description.trim(),
            weightGram = weightGram,
            productStatus = Product.ProductStatus.ACTIVE.name,
            createAt = now,
            updateAt = now,
            avgRating = null
        )
        products.add(newProduct)
        writeJson(productFile, products)

        // Simpan Relasi Subkategori (Many-to-Many)
        if (subCategoryIds.isNotEmpty()) {
            val productSubcats = readJson<ProductSubcategoryJson>(productSubcatFile)
            var maxPscNum = productSubcats.maxOfOrNull { it.idProductSubCat.substringAfter("-").toIntOrNull() ?: 0 } ?: 0

            subCategoryIds.forEach { subCatId ->
                maxPscNum++
                productSubcats.add(
                    ProductSubcategoryJson(
                        idProductSubCat = String.format("PSC-%06d", maxPscNum),
                        idProduct = newProductId,
                        idSubCategory = subCatId
                    )
                )
            }
            writeJson(productSubcatFile, productSubcats)
        }

        // Simpan Base Item (Harga & Stok utama)
        val items = readJson<ProductItemJson>(productItemFile)
        val maxItmNum = items.maxOfOrNull { it.idItem.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newItemId = String.format("ITM-%06d", maxItmNum + 1)

        val newItem = ProductItemJson(
            idItem = newItemId,
            idProduct = newProductId,
            sku = "SKU-${newProductId}",
            stock = baseStock,
            price = basePrice,
            isActive = true
        )
        items.add(newItem)
        writeJson(productItemFile, items)

        // Simpan Gambar Utama
        val images = readJson<ProductImageJson>(productImageFile)
        val maxImgNum = images.maxOfOrNull { it.idImage.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newImageId = String.format("IMG-%06d", maxImgNum + 1)

        val newImage = ProductImageJson(
            idImage = newImageId,
            idProduct = newProductId,
            imageURL = primaryImageRes,
            isPrimary = true
        )
        images.add(newImage)
        writeJson(productImageFile, images)

        return@withContext ProductResult.Success(newProductId)
    }


    // TAMBAH VARIASI PRODUK

    suspend fun addProductVariation(
        itemId: String,
        typeVariation: String,
        value: String
    ) = withContext(Dispatchers.IO) {
        val variations = readJson<ProductVariationJson>(productVariationFile)
        val maxVarNum = variations.maxOfOrNull { it.idVariation.substringAfter("-").toIntOrNull() ?: 0 } ?: 0
        val newVarId = String.format("VAR-%06d", maxVarNum + 1)

        variations.add(
            ProductVariationJson(
                idVariation = newVarId,
                idItem = itemId,
                typeVariation = typeVariation,
                value = value
            )
        )
        writeJson(productVariationFile, variations)
    }
}