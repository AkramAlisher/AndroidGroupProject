package com.example.movieapp30.model

import com.google.gson.annotations.SerializedName

data class GetRequestTokenResponse (
    @SerializedName("success") val success: Boolean,
    @SerializedName("expires_at") val expires_at: String,
    @SerializedName("request_token") val request_token: String
)