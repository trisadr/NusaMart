package com.example.nusamart.data.repository.product

import com.example.nusamart.data.model.product.Category
import com.example.nusamart.data.model.product.Product
import com.example.nusamart.data.model.product.ProductImage
import com.example.nusamart.data.model.product.ProductItem
import com.example.nusamart.data.model.product.ProductSubcategory
import com.example.nusamart.data.model.product.ProductVariation
import com.example.nusamart.data.model.product.SubCategory
import java.time.LocalDateTime
import java.util.UUID

class ProductRepository(private val dataSource: ProductLocalDataSource) {

    // ── Mappers: JSON → Data Class ────────────────────────────────────────────

    private fun CategoryJson.toCategory() = Category(
        idCategory = idCategory,
        categoryName = categoryName,
        iconURL = iconURL,
        isActive = isActive
    )

    private fun SubCategoryJson.toSubCategory() = SubCategory(
        idSubCategory = idSubCategory,
        idCategory = idCategory,
        subCategoryName = subCategoryName,
        description = description
    )

    private fun ProductJson.toProduct() = Product(
        idProduct = idProduct,
        idStore = idStore,
        productName = productName,
        description = description,
        weightGram = weightGram,
        productStatus = Product.ProductStatus.valueOf(productStatus),
        createAt = LocalDateTime.parse(createAt),
        updateAt = LocalDateTime.parse(updateAt),
        avgRating = avgRating
    )

    private fun Product.toJson() = ProductJson(
        idProduct = idProduct,
        idStore = idStore,
        productName = productName,
        description = description,
        weightGram = weightGram,
        productStatus = productStatus.name,
        createAt = createAt.toString(),
        updateAt = updateAt.toString(),
        avgRating = avgRating
    )

    private fun ProductImageJson.toImage() = ProductImage(
        idImage = idImage,
        idProduct = idProduct,
        imageURL = imageURL,
        isPrimary = isPrimary
    )

    private fun ProductItemJson.toItem() = ProductItem(
        idItem = idItem,
        idProduct = idProduct,
        sku = sku,
        stock = stock,
        price = price,
        isActive = isActive
    )

    private fun ProductItem.toJson() = ProductItemJson(
        idItem = idItem,
        idProduct = idProduct,
        sku = sku,
        stock = stock,
        price = price,
        isActive = isActive
    )

    private fun ProductVariationJson.toVariation() = ProductVariation(
        idVariation = idVariation,
        idItem = idItem,
        typeVariation = typeVariation,
        value = value
    )

    private fun ProductSubcategoryJson.toProductSubcategory() = ProductSubcategory(
        idProductSubCat = idProductSubCat,
        idProduct = idProduct,
        idSubCategory = idSubCategory
    )

    // ── CATEGORY ──────────────────────────────────────────────────────────────

    fun getCategories(): List<Category> {
        return dataSource.readCategories().categories
            .filter { it.isActive }
            .map { it.toCategory() }
    }

    fun getCategoryById(categoryId: String): Category? {
        return dataSource.readCategories().categories
            .find { it.idCategory == categoryId }
            ?.toCategory()
    }

    // ── SUBCATEGORY ───────────────────────────────────────────────────────────

    fun getSubCategories(): List<SubCategory> {
        return dataSource.readSubCategories().subcategories
            .map { it.toSubCategory() }
    }

    fun getSubCategoriesByCategory(categoryId: String): List<SubCategory> {
        return dataSource.readSubCategories().subcategories
            .filter { it.idCategory == categoryId }
            .map { it.toSubCategory() }
    }

    // ── PRODUCT ───────────────────────────────────────────────────────────────

    fun getProducts(): List<Product> {
        return dataSource.readProducts().products
            .filter { it.productStatus == Product.ProductStatus.ACTIVE.name }
            .map { it.toProduct() }
    }

    fun getProductById(productId: String): Product? {
        return dataSource.readProducts().products
            .find { it.idProduct == productId }
            ?.toProduct()
    }

    fun getProductsByStore(storeId: String): List<Product> {
        return dataSource.readProducts().products
            .filter { it.idStore == storeId }
            .map { it.toProduct() }
    }

    fun getProductsBySubCategory(subCategoryId: String): List<Product> {
        // Cari idProduct dari tabel junction ProductSubcategory
        val productIds = dataSource.readProductSubcategories().product_subcategories
            .filter { it.idSubCategory == subCategoryId }
            .map { it.idProduct }

        return dataSource.readProducts().products
            .filter { it.idProduct in productIds }
            .map { it.toProduct() }
    }

    fun searchProducts(query: String): List<Product> {
        return dataSource.readProducts().products
            .filter {
                it.productStatus == Product.ProductStatus.ACTIVE.name &&
                        it.productName.contains(query, ignoreCase = true)
            }
            .map { it.toProduct() }
    }

    fun addProduct(product: Product): Product {
        val db = dataSource.readProducts()
        db.products.add(product.toJson())
        dataSource.writeProducts(db)
        return product
    }

    fun updateProduct(updatedProduct: Product): Boolean {
        val db = dataSource.readProducts()
        val index = db.products.indexOfFirst { it.idProduct == updatedProduct.idProduct }
        if (index == -1) return false

        db.products[index] = updatedProduct
            .copy(updateAt = LocalDateTime.now())
            .toJson()
        dataSource.writeProducts(db)
        return true
    }

    fun deleteProduct(productId: String): Boolean {
        // Soft delete — ubah status jadi INACTIVE
        val db = dataSource.readProducts()
        val index = db.products.indexOfFirst { it.idProduct == productId }
        if (index == -1) return false

        db.products[index] = db.products[index].copy(
            productStatus = Product.ProductStatus.INACTIVE.name,
            updateAt = LocalDateTime.now().toString()
        )
        dataSource.writeProducts(db)
        return true
    }

    // ── PRODUCT IMAGE ─────────────────────────────────────────────────────────

    fun getImagesByProduct(productId: String): List<ProductImage> {
        return dataSource.readProductImages().product_images
            .filter { it.idProduct == productId }
            .map { it.toImage() }
            .sortedByDescending { it.isPrimary }   // primary image duluan
    }

    fun getPrimaryImage(productId: String): ProductImage? {
        return dataSource.readProductImages().product_images
            .find { it.idProduct == productId && it.isPrimary }
            ?.toImage()
    }

    fun addProductImage(image: ProductImage): Boolean {
        val db = dataSource.readProductImages()

        // Kalau isPrimary, reset primary lain untuk product ini
        if (image.isPrimary) {
            val indices = db.product_images.indices
                .filter { db.product_images[it].idProduct == image.idProduct }
            indices.forEach { db.product_images[it] = db.product_images[it].copy(isPrimary = false) }
        }

        db.product_images.add(
            ProductImageJson(
                idImage = "img-${UUID.randomUUID()}",
                idProduct = image.idProduct,
                imageURL = image.imageURL,
                isPrimary = image.isPrimary
            )
        )
        dataSource.writeProductImages(db)
        return true
    }

    // ── PRODUCT ITEM ──────────────────────────────────────────────────────────

    fun getItemsByProduct(productId: String): List<ProductItem> {
        return dataSource.readProductItems().product_items
            .filter { it.idProduct == productId && it.isActive }
            .map { it.toItem() }
    }

    fun getItemById(itemId: String): ProductItem? {
        return dataSource.readProductItems().product_items
            .find { it.idItem == itemId }
            ?.toItem()
    }

    fun updateStock(itemId: String, newStock: Int): Boolean {
        val db = dataSource.readProductItems()
        val index = db.product_items.indexOfFirst { it.idItem == itemId }
        if (index == -1) return false

        db.product_items[index] = db.product_items[index].copy(stock = newStock)
        dataSource.writeProductItems(db)
        return true
    }

    fun reduceStock(itemId: String, quantity: Int): Boolean {
        val db = dataSource.readProductItems()
        val index = db.product_items.indexOfFirst { it.idItem == itemId }
        if (index == -1) return false

        val currentStock = db.product_items[index].stock
        if (currentStock < quantity) return false   // stok tidak cukup

        db.product_items[index] = db.product_items[index].copy(stock = currentStock - quantity)
        dataSource.writeProductItems(db)
        return true
    }

    // ── PRODUCT VARIATION ─────────────────────────────────────────────────────

    fun getVariationsByItem(itemId: String): List<ProductVariation> {
        return dataSource.readProductVariations().product_variations
            .filter { it.idItem == itemId }
            .map { it.toVariation() }
    }

    // Ambil semua variasi untuk satu product sekaligus (grouped by item)
    fun getVariationsByProduct(productId: String): Map<String, List<ProductVariation>> {
        val itemIds = dataSource.readProductItems().product_items
            .filter { it.idProduct == productId }
            .map { it.idItem }

        return dataSource.readProductVariations().product_variations
            .filter { it.idItem in itemIds }
            .map { it.toVariation() }
            .groupBy { it.idItem }
    }

    // ── PRODUCT SUBCATEGORY ───────────────────────────────────────────────────

    fun getSubCategoriesByProduct(productId: String): List<SubCategory> {
        val subCatIds = dataSource.readProductSubcategories().product_subcategories
            .filter { it.idProduct == productId }
            .map { it.idSubCategory }

        return dataSource.readSubCategories().subcategories
            .filter { it.idSubCategory in subCatIds }
            .map { it.toSubCategory() }
    }

    fun linkProductToSubCategory(productId: String, subCategoryId: String): Boolean {
        val db = dataSource.readProductSubcategories()

        // Cek duplikat
        val exists = db.product_subcategories.any {
            it.idProduct == productId && it.idSubCategory == subCategoryId
        }
        if (exists) return false

        db.product_subcategories.add(
            ProductSubcategoryJson(
                idProductSubCat = "psc-${UUID.randomUUID()}",
                idProduct = productId,
                idSubCategory = subCategoryId
            )
        )
        dataSource.writeProductSubcategories(db)
        return true
    }
}