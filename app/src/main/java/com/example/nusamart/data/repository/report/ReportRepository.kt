package com.example.nusamart.data.repository.report

// HAPUS import android.R.attr.type yang bikin error!
import android.content.Context
import com.example.nusamart.data.model.report.Report
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// 1. TAMBAHKAN referenceID di JSON Model agar sinkron dengan model utama
data class ReportJson(
    val idReport: String,
    val reporterId: String,
    val reason: String,
    val status: String,
    val adminNote: String? = null,
    val createAt: String,
    val updateAt: String? = null,
    val type: String,
    val referenceID: String // <-- Tambahan wajib
)

@Singleton
class ReportRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val reportFile = "reports.json"

    private inline fun <reified T> readJson(fileName: String): MutableList<T> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    val json = inputStream.bufferedReader().readText()
                    file.writeText(json)
                }
            } catch (e: Exception) {
                return mutableListOf()
            }
        }
        val json = file.readText()
        if (json.isBlank()) return mutableListOf() // Cegah crash jika file kosong
        val type = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun <T> writeJson(fileName: String, data: List<T>) {
        val file = File(context.filesDir, fileName)
        file.writeText(gson.toJson(data))
    }

    // 2. MAPPER JUGA HARUS MENGAMBIL referenceID
    private fun ReportJson.toReport() = Report(
        idReport = idReport,
        reporterId = reporterId,
        reason = reason,
        status = Report.ReportStatus.valueOf(status),
        adminNote = adminNote,
        createAt = LocalDateTime.parse(createAt),
        updateAt = updateAt?.let { LocalDateTime.parse(it) },
        type = Report.ReferenceType.valueOf(type),
        referenceID = referenceID // <-- Tambahan wajib
    )

    // Operasi
    fun submitReport(
        reporterId: String,
        reason: String,
        reportedUserId: String? = null,
        reportedProductId: String? = null,
        reportedReviewId: String? = null
    ): Report {
        require(reportedUserId != null || reportedProductId != null || reportedReviewId != null) {
            "Harus ada target laporan (user, product, atau review)"
        }

        val reports = readJson<ReportJson>(reportFile)

        // 3. LOGIKA PENENTUAN TIPE & ID REFERENSI
        val (determinedType, determinedRefId) = when {
            reportedUserId != null -> Report.ReferenceType.USER.name to reportedUserId
            reportedProductId != null -> Report.ReferenceType.PRODUCT.name to reportedProductId
            reportedReviewId != null -> Report.ReferenceType.REVIEW.name to reportedReviewId
            else -> Report.ReferenceType.OTHERS.name to "UNKNOWN"
        }

        val newReport = ReportJson(
            idReport = "rep-${UUID.randomUUID()}",
            reporterId = reporterId,
            reason = reason,
            status = Report.ReportStatus.OPEN.name,
            createAt = LocalDateTime.now().toString(),
            type = determinedType, // <-- Menggunakan variabel yang sudah dihitung
            referenceID = determinedRefId // <-- Menggunakan ID yang sudah dihitung
        )

        reports.add(newReport)
        writeJson(reportFile, reports)

        return newReport.toReport()
    }

    fun getMyReports(userId: String): List<Report> {
        return readJson<ReportJson>(reportFile)
            .filter { it.reporterId == userId }
            .map { it.toReport() }
            .sortedByDescending { it.createAt }
    }

    fun getReportById(reportId: String): Report? {
        return readJson<ReportJson>(reportFile)
            .find { it.idReport == reportId }
            ?.toReport()
    }
}