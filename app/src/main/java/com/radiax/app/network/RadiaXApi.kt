package com.radiax.app.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import com.radiax.app.models.AnalysisResponse


interface RadiaXApi {
    @Multipart
    @POST("api/analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part
    ): Response<AnalysisResponse>
}
