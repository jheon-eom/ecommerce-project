package com.example.userdomain.service

import com.example.userdomain.domain.Role
import com.example.userdomain.domain.User
import com.example.userdomain.dto.LoginRequest
import com.example.userdomain.dto.SignUpRequest
import com.example.userdomain.repository.UserRepository
import com.example.userdomain.security.JwtTokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        passwordEncoder = mockk()
        jwtTokenProvider = mockk()
        authenticationManager = mockk()
        userService = UserService(userRepository, passwordEncoder, jwtTokenProvider, authenticationManager)
    }

    @Test
    fun `should successfully sign up new user`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User"
        )

        val encodedPassword = "encodedPassword123"
        val user = User(
            id = 1L,
            email = signUpRequest.email,
            password = encodedPassword,
            name = signUpRequest.name,
            role = Role.USER
        )

        every { userRepository.existsByEmail(signUpRequest.email) } returns false
        every { passwordEncoder.encode(signUpRequest.password) } returns encodedPassword
        every { userRepository.save(any()) } returns user
        every { jwtTokenProvider.generateToken(user) } returns "accessToken"
        every { jwtTokenProvider.generateRefreshToken(user) } returns "refreshToken"

        // when
        val response = userService.signUp(signUpRequest)

        // then
        assertNotNull(response)
        assertEquals("accessToken", response.accessToken)
        assertEquals("refreshToken", response.refreshToken)
        assertEquals(signUpRequest.email, response.email)
        assertEquals(signUpRequest.name, response.name)

        verify(exactly = 1) { userRepository.existsByEmail(signUpRequest.email) }
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun `should throw exception when email already exists`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User"
        )

        every { userRepository.existsByEmail(signUpRequest.email) } returns true

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.signUp(signUpRequest)
        }

        assertEquals("Email already exists: ${signUpRequest.email}", exception.message)
        verify(exactly = 1) { userRepository.existsByEmail(signUpRequest.email) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should successfully login with valid credentials`() {
        // given
        val loginRequest = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        val user = User(
            id = 1L,
            email = loginRequest.email,
            password = "encodedPassword",
            name = "Test User",
            role = Role.USER
        )

        val authentication: Authentication = mockk()
        every { authentication.principal } returns user

        every { authenticationManager.authenticate(any()) } returns authentication
        every { userRepository.findByEmail(loginRequest.email) } returns Optional.of(user)
        every { jwtTokenProvider.generateToken(user) } returns "accessToken"
        every { jwtTokenProvider.generateRefreshToken(user) } returns "refreshToken"

        // when
        val response = userService.login(loginRequest)

        // then
        assertNotNull(response)
        assertEquals("accessToken", response.accessToken)
        assertEquals("refreshToken", response.refreshToken)
        assertEquals(loginRequest.email, response.email)
        assertEquals(user.name, response.name)

        verify(exactly = 1) { authenticationManager.authenticate(any()) }
        verify(exactly = 1) { userRepository.findByEmail(loginRequest.email) }
    }

    @Test
    fun `should successfully get user by email`() {
        // given
        val email = "test@example.com"
        val user = User(
            id = 1L,
            email = email,
            password = "encodedPassword",
            name = "Test User",
            role = Role.USER
        )

        every { userRepository.findByEmail(email) } returns Optional.of(user)

        // when
        val foundUser = userService.getUserByEmail(email)

        // then
        assertNotNull(foundUser)
        assertEquals(email, foundUser.email)
        assertEquals(user.name, foundUser.name)

        verify(exactly = 1) { userRepository.findByEmail(email) }
    }

    @Test
    fun `should throw exception when user not found by email`() {
        // given
        val email = "nonexistent@example.com"

        every { userRepository.findByEmail(email) } returns Optional.empty()

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            userService.getUserByEmail(email)
        }

        assertEquals("User not found: $email", exception.message)
        verify(exactly = 1) { userRepository.findByEmail(email) }
    }
}