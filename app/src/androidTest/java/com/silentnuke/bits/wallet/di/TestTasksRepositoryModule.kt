package com.silentnuke.bits.wallet.di

import com.silentnuke.bits.wallet.data.source.CardsRepository
import com.silentnuke.bits.wallet.data.source.FakeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [TasksRepositoryModule::class]
)
abstract class TestTasksRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRepository(repo: FakeRepository): CardsRepository
}
