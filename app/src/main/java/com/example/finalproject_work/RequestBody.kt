package com.example.finalproject_work

import com.google.gson.annotations.SerializedName

data class RequestBody(
    @SerializedName("model")
    val model: String,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    @SerializedName("temperature")
    val temperature: Double
)
data class ResponseBody(
    @SerializedName("choices")
    val choices: List<Choice>
)
data class Choice(
    @SerializedName("text")
    val text: String
)