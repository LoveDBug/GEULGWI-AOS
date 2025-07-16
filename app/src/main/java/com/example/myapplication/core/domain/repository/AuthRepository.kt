package com.example.myapplication.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun login(): Flow<Unit>

    fun signUp(): Flow<Unit>

    fun sendVerificationCode(): Flow<Unit>

}