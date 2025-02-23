package com.radiax.app.repository

import com.google.gson.JsonSyntaxException
import com.radiax.app.models.AnalysisResponse
import com.radiax.app.network.RetrofitClient
import com.radiax.app.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException

class AnalysisRepository {
    suspend fun analyzeImage(imageBytes: ByteArray): NetworkResult<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)

                println("📤 Отправка изображения на сервер. Размер: ${imageBytes.size} байт")

                val response = RetrofitClient.instance.analyzeImage(body)

                val rawResponse = response.body()?.toString() ?: "null"
                println("📥 Ответ от сервера в эмуляторе: $rawResponse")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()

                    // ✅ Берём `result` вместо `messages[]`
                    val content = responseBody?.result

                    return@withContext if (!content.isNullOrEmpty()) {
                        println("✅ Итоговый анализ: $content")
                        NetworkResult.Success(content)
                    } else {
                        println("⚠️ Сервер вернул пустой ответ")
                        NetworkResult.Error("Сервер вернул пустой ответ")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    println("❌ Ошибка сервера: ${response.code()} - $errorBody")
                    return@withContext NetworkResult.Error("Ошибка сервера: ${response.code()} - $errorBody")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext NetworkResult.Error("Ошибка сети: Проверьте подключение к интернету")
            } catch (e: HttpException) {
                println("❌ Ошибка сервера: ${e.code()} - ${e.message()}")
                return@withContext NetworkResult.Error("Ошибка сервера: ${e.code()} - ${e.message()}")
            } catch (e: JsonSyntaxException) {
                println("❌ Ошибка парсинга JSON: ${e.message}")
                return@withContext NetworkResult.Error("Ошибка обработки ответа сервера")
            } catch (e: Exception) {
                println("❗ Неизвестная ошибка: ${e.localizedMessage}")
                return@withContext NetworkResult.Error("Неизвестная ошибка: ${e.localizedMessage}")
            }
        }
    }
}