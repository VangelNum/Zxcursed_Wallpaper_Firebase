package com.zxcursed.wallpaper.feature_main.di

import com.zxcursed.wallpaper.feature_main.data.repository.MainRepositoryImpl
import com.zxcursed.wallpaper.feature_main.domain.repository.MainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class MainRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindMainRepository(
        mainRepositoryImpl: MainRepositoryImpl
    ): MainRepository
}