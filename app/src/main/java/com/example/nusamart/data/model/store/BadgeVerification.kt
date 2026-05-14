package com.example.nusamart.data.model.store

data class BadgeVerification(
    val idBadge: String,        // PK
    val idStore: String,        // FK (Store)
    val badgeType: BadgeType,
    val requestDate: java.time.LocalDateTime,
    val reviewDate: java.time.LocalDateTime? = null,
    val endDate: java.time.LocalDateTime? = null,
    val status: Status,
    val notes: String? = null
) {
    enum class BadgeType {
        LOCAL
    }

    enum class Status {
        PENDING,
        APPROVED,
        REJECTED,
        EXPIRED
    }
}