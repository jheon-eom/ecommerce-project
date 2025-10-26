package com.example.userdomain.dto

data class AuthResponse(

    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val email: String,
    val name: String
)