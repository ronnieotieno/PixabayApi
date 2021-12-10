package com.ronnie.domain.di

import com.ronnie.domain.repositories.SearchImagesRepository
import com.ronnie.domain.usecases.SearchUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    @Singleton
    fun provideLogInUseCase(searchImagesRepository: SearchImagesRepository) =
        SearchUseCase(searchImagesRepository)
}