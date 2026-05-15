package com.example.nusamart.data.repository.report

import android.content.Context
import com.example.nusamart.data.model.report.Report
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.time.LocalDateTime
import java.util.UUID
// class repository ini belum dipakai, kalau mau makai mungin bisa dicek ulang nantinya, soalnya strukturnya aga beda dari yang lain
// JSON model

data class ReportJson(
    val idReport: String,
    val reporterId: String,
    val reportedUserId: String? = null,
    val reportedProductId: String? = null,
    val reportedReviewId: String? = null,
    val reason: String,
    val status: String,
    val adminNote: String? = null,
    val createAt: String,
    val updateAt: String? = null
)

data class ReportDatabase(
    val reports: MutableList<ReportJson> = mutableListOf()
)

// Local Data Source

class ReportLocalDataSource(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val fileName = "reports.json"

    private val localFile: File
        get() = File(context.filesDir, fileName)

    fun initializeIfNeeded() {
        if (!localFile.exists()) {
            val seedJson = context.assets.open(fileName).bufferedReader().readText()
            localFile.writeText(seedJson)
        }
    }

    fun readDatabase(): ReportDatabase {
        initializeIfNeeded()
        val json = localFile.readText()
        return gson.fromJson(json, ReportDatabase::class.java) ?: ReportDatabase()
    }

    fun writeDatabase(db: ReportDatabase) {
        localFile.writeText(gson.toJson(db))
    }
}

// Repository

class ReportRepository(private val dataSource: ReportLocalDataSource) {

    // Mapper

    private fun ReportJson.toReport() = Report(
        idReport = idReport,
        reporterId = reporterId,
        reportedUserId = reportedUserId,
        reportedProductId = reportedProductId,
        reportedReviewId = reportedReviewId,
        reason = reason,
        status = Report.ReportStatus.valueOf(status),
        adminNote = adminNote,
        createAt = LocalDateTime.parse(createAt),
        updateAt = updateAt?.let { LocalDateTime.parse(it) }
    )

    private fun Report.toJson() = ReportJson(
        idReport = idReport,
        reporterId = reporterId,
        reportedUserId = reportedUserId,
        reportedProductId = reportedProductId,
        reportedReviewId = reportedReviewId,
        reason = reason,
        status = status.name,
        adminNote = adminNote,
        createAt = createAt.toString(),
        updateAt = updateAt?.toString()
    )

    // Operasi User

    // Kirim laporan baru
    fun submitReport(
        reporterId: String,
        reason: String,
        reportedUserId: String? = null,
        reportedProductId: String? = null,
        reportedReviewId: String? = null
    ): Report {
        // Minimal salah satu target harus diisi
        require(reportedUserId != null || reportedProductId != null || reportedReviewId != null) {
            "Harus ada target laporan (user, product, atau review)"
        }

        val db = dataSource.readDatabase()

        val newReport = ReportJson(
            idReport = "rep-${UUID.randomUUID()}",
            reporterId = reporterId,
            reportedUserId = reportedUserId,
            reportedProductId = reportedProductId,
            reportedReviewId = reportedReviewId,
            reason = reason,
            status = Report.ReportStatus.OPEN.name,
            createAt = LocalDateTime.now().toString()
        )

        db.reports.add(newReport)
        dataSource.writeDatabase(db)

        return newReport.toReport()
    }

    // Semua laporan milik user tertentu
    fun getMyReports(userId: String): List<Report> {
        return dataSource.readDatabase().reports
            .filter { it.reporterId == userId }
            .map { it.toReport() }
            .sortedByDescending { it.createAt }   // terbaru duluan
    }

    // Detail satu laporan
    fun getReportById(reportId: String): Report? {
        return dataSource.readDatabase().reports
            .find { it.idReport == reportId }
            ?.toReport()
    }
}