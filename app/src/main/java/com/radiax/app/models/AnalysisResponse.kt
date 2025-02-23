package com.radiax.app.models

import com.google.gson.annotations.SerializedName

data class AnalysisResponse(
    @SerializedName("result") val result: String?,
    @SerializedName("message") val message: String?
)
data class AIMessage(
    @SerializedName("content") val content: String?
)