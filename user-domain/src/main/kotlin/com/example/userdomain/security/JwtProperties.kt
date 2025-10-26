package com.example.userdomain.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(

    var secret: String = "",
    var expiration: Long = 0,
    var refreshExpiration: Long = 0
)