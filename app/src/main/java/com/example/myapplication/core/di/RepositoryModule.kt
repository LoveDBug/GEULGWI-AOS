package com.example.myapplication.core.di

import com.example.myapplication.core.data.repository.fake.FakeGlimRepositoryImpl
import com.example.myapplication.core.domain.repository.FakeGlimRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindGlimRepository(
        repository: FakeGlimRepositoryImpl
    ): FakeGlimRepository
}