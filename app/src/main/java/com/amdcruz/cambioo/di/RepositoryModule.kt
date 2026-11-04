package com.amdcruz.cambioo.di

import com.amdcruz.cambioo.data.DefaultRatesRepository
import com.amdcruz.cambioo.data.RatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRatesRepository(
        impl: DefaultRatesRepository,
    ): RatesRepository
}
