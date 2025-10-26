package com.example.userdomain.service

import com.example.userdomain.domain.Role
import com.example.userdomain.domain.User
import com.example.userdomain.dto.AuthResponse
import com.example.userdomain.dto.LoginRequest
import com.example.userdomain.dto.SignUpRequest
import com.example.userdomain.repository.UserRepository
import com.example.userdomain.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun signUp(request: SignUpRequest): AuthResponse {
        // Check email duplication
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists: ${request.email}")
        }

        // Create user
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            role = Role.USER
        )

        val savedUser = userRepository.save(user)

        // Generate JWT tokens
        val accessToken = jwtTokenProvider.generateToken(savedUser)
        val refreshToken = jwtTokenProvider.generateRefreshToken(savedUser)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = savedUser.email,
            name = savedUser.name
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        // Attempt authentication
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val userDetails = authentication.principal as UserDetails
        val user = userRepository.findByEmail(userDetails.username)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Generate JWT tokens
        val accessToken = jwtTokenProvider.generateToken(userDetails)
        val refreshToken = jwtTokenProvider.generateRefreshToken(userDetails)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            email = user.email,
            name = user.name
        )
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("User not found: $email") }
    }
}