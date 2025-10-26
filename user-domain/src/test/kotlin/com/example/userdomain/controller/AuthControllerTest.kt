package com.example.userdomain.controller

import com.example.userdomain.dto.LoginRequest
import com.example.userdomain.dto.SignUpRequest
import com.example.userdomain.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun `should successfully sign up new user`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User"
        )

        // when & then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
    }

    @Test
    fun `should fail when signing up with duplicate email`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User"
        )

        // First signup
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isCreated)

        // when & then - retry with same email
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `should fail when signing up with invalid email format`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "invalid-email",
            password = "password123",
            name = "Test User"
        )

        // when & then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `should fail when signing up with short password`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "short",
            name = "Test User"
        )

        // when & then
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `should successfully login with valid credentials`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User"
        )

        // Sign up first
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isCreated)

        val loginRequest = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        // when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"))
    }

    @Test
    fun `should fail when logging in with wrong password`() {
        // given
        val signUpRequest = SignUpRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User"
        )

        // Sign up first
        mockMvc.perform(
            post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        )
            .andExpect(status().isCreated)

        val loginRequest = LoginRequest(
            email = "test@example.com",
            password = "wrongpassword"
        )

        // when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should fail when logging in with non-existent user`() {
        // given
        val loginRequest = LoginRequest(
            email = "nonexistent@example.com",
            password = "password123"
        )

        // when & then
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }
}