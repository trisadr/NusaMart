package com.example.nusamart.data.model.report

data class Report(
    val idReport: String,               // PK
    val reporterId: String,             // FK (User)
    val reason: String,
    val status: ReportStatus,
    val adminNote: String? = null,
    val createAt: java.time.LocalDateTime,
    val updateAt: java.time.LocalDateTime? = null,
    val type: ReferenceType,
    val referenceID: String
) {
    enum class ReportStatus {
        OPEN,
        REVIEWED,
        RESOLVED,
        DISMISSED
    }

    enum class ReferenceType {
        USER,
        PRODUCT,
        REVIEW,
        OTHERS
    }
}
