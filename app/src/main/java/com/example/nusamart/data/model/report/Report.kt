package com.example.nusamart.data.model.report

data class Report(
    val idReport: String,               // PK
    val reporterId: String,             // FK (User)
    val reportedUserId: String? = null, // FK (User)
    val reportedProductId: String? = null, // FK (Product)
    val reportedReviewId: String? = null,  // FK (Review)
    val reason: String,
    val status: ReportStatus,
    val adminNote: String? = null,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime? = null
) {
    enum class ReportStatus {
        OPEN,
        REVIEWED,
        RESOLVED,
        DISMISSED
    }
}
