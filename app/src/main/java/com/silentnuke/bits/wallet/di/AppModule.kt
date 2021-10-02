package com.silentnuke.bits.wallet.di

import android.content.Context
import androidx.room.Room
import com.silentnuke.bits.wallet.data.source.CardsDataSource
import com.silentnuke.bits.wallet.data.source.CardsRepository
import com.silentnuke.bits.wallet.data.source.DefaultCardsRepository
import com.silentnuke.bits.wallet.data.source.local.CardsLocalDataSource
import com.silentnuke.bits.wallet.data.source.local.WalletDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class LocalCardsDataSource

    @Singleton
    @LocalCardsDataSource
    @Provides
    fun provideCardsLocalDataSource(
        database: WalletDatabase,
        ioDispatcher: CoroutineDispatcher
    ): CardsDataSource {
        return CardsLocalDataSource(database.cardsDao(), ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): WalletDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WalletDatabase::class.java,
            "Cards.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

/**
 * The binding for TasksRepository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(SingletonComponent::class)
object TasksRepositoryModule {

    @Singleton
    @Provides
    fun provideTasksRepository(
        @AppModule.LocalCardsDataSource localCardsDataSource: CardsDataSource,
        ioDispatcher: CoroutineDispatcher
    ): CardsRepository {
        return DefaultCardsRepository(localCardsDataSource, ioDispatcher)
    }
}
