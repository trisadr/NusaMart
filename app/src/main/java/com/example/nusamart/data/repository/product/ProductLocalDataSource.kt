package com.example.nusamart.data.repository.product

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

// ── JSON Models ───────────────────────────────────────────────────────────────

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
    val productStatus: String,
    val createAt: String,
    val updateAt: String,
    val avgRating: Double? = null
)

data class ProductImageJson(
    val idImage: String,
    val idProduct: String,
    val imageURL: Int,
    val isPrimary: Boolean
)

data class ProductItemJson(
    val idItem: String,
    val idProduct: String,
    val sku: String? = null,
    val stock: Int,
    val price: Double,
    val isActive: Boolean
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

// ── Wrapper untuk tiap file JSON ──────────────────────────────────────────────

data class CategoryDatabase(val categories: MutableList<CategoryJson> = mutableListOf())
data class SubCategoryDatabase(val subcategories: MutableList<SubCategoryJson> = mutableListOf())
data class ProductDatabase(val products: MutableList<ProductJson> = mutableListOf())
data class ProductImageDatabase(val product_images: MutableList<ProductImageJson> = mutableListOf())
data class ProductItemDatabase(val product_items: MutableList<ProductItemJson> = mutableListOf())
data class ProductVariationDatabase(val product_variations: MutableList<ProductVariationJson> = mutableListOf())
data class ProductSubcategoryDatabase(val product_subcategories: MutableList<ProductSubcategoryJson> = mutableListOf())

// ── Local Data Source ─────────────────────────────────────────────────────────

class ProductLocalDataSource(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    // Helper: copy dari assets ke filesDir kalau belum ada
    private fun initFile(fileName: String): File {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            val seed = context.assets.open(fileName).bufferedReader().readText()
            file.writeText(seed)
        }
        return file
    }

    // ── Category ──────────────────────────────────────────────────────────────
    fun readCategories(): CategoryDatabase {
        val json = initFile("categories.json").readText()
        return gson.fromJson(json, CategoryDatabase::class.java) ?: CategoryDatabase()
    }
    fun writeCategories(db: CategoryDatabase) {
        initFile("categories.json").writeText(gson.toJson(db))
    }

    // ── SubCategory ───────────────────────────────────────────────────────────
    fun readSubCategories(): SubCategoryDatabase {
        val json = initFile("subcategories.json").readText()
        return gson.fromJson(json, SubCategoryDatabase::class.java) ?: SubCategoryDatabase()
    }
    fun writeSubCategories(db: SubCategoryDatabase) {
        initFile("subcategories.json").writeText(gson.toJson(db))
    }

    // ── Product ───────────────────────────────────────────────────────────────
    fun readProducts(): ProductDatabase {
        val json = initFile("products.json").readText()
        return gson.fromJson(json, ProductDatabase::class.java) ?: ProductDatabase()
    }
    fun writeProducts(db: ProductDatabase) {
        initFile("products.json").writeText(gson.toJson(db))
    }

    // ── ProductImage ──────────────────────────────────────────────────────────
    fun readProductImages(): ProductImageDatabase {
        val json = initFile("product_images.json").readText()
        return gson.fromJson(json, ProductImageDatabase::class.java) ?: ProductImageDatabase()
    }
    fun writeProductImages(db: ProductImageDatabase) {
        initFile("product_images.json").writeText(gson.toJson(db))
    }

    // ── ProductItem ───────────────────────────────────────────────────────────
    fun readProductItems(): ProductItemDatabase {
        val json = initFile("product_items.json").readText()
        return gson.fromJson(json, ProductItemDatabase::class.java) ?: ProductItemDatabase()
    }
    fun writeProductItems(db: ProductItemDatabase) {
        initFile("product_items.json").writeText(gson.toJson(db))
    }

    // ── ProductVariation ──────────────────────────────────────────────────────
    fun readProductVariations(): ProductVariationDatabase {
        val json = initFile("product_variations.json").readText()
        return gson.fromJson(json, ProductVariationDatabase::class.java) ?: ProductVariationDatabase()
    }
    fun writeProductVariations(db: ProductVariationDatabase) {
        initFile("product_variations.json").writeText(gson.toJson(db))
    }

    // ── ProductSubcategory ────────────────────────────────────────────────────
    fun readProductSubcategories(): ProductSubcategoryDatabase {
        val json = initFile("product_subcategories.json").readText()
        return gson.fromJson(json, ProductSubcategoryDatabase::class.java) ?: ProductSubcategoryDatabase()
    }
    fun writeProductSubcategories(db: ProductSubcategoryDatabase) {
        initFile("product_subcategories.json").writeText(gson.toJson(db))
    }
}