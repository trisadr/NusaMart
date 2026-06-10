package com.example.nusamart.data.network

import com.example.nusamart.data.dto.common.BaseResponse
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(
    maxRetry: Int = 3,
    call: suspend () -> BaseResponse<T>
): ApiResult<T> {
    var attempt = 0
    while (attempt < maxRetry) {
        try {
            val response = call()
            return if (response.code in 200..299 && response.data != null) {
                ApiResult.Success(response.data)
            } else {
                ApiResult.Error(response.code, response.message)
            }
        } catch (e: HttpException) {
            return ApiResult.Error(e.code(), e.message())
        } catch (e: IOException) {
            attempt++
            if (attempt == maxRetry) {
                return ApiResult.Error(-1, "Gagal setelah $maxRetry percobaan")
            }
            delay(1000L * attempt)
        } catch (e: Exception) {
            return ApiResult.Error(-1, e.message ?: "Terjadi kesalahan")
        }
    }
    return ApiResult.Error(-1, "Unknown error")
}