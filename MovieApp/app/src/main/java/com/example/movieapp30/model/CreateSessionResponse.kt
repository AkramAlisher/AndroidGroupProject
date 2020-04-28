package com.example.movieapp30.model

import com.google.gson.annotations.SerializedName

data class CreateSessionResponse (
    @SerializedName("success") val success: Boolean,
    @SerializedName("session_id") val session_id: String
)