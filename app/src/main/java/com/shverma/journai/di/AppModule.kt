package com.shverma.journai.di

import android.content.Context
import com.shverma.journai.data.repository.AiRepository
import com.shverma.journai.data.repository.AiRepositoryImpl
import com.shverma.journai.data.repository.AuthRepository
import com.shverma.journai.data.repository.AuthRepositoryImpl
import com.shverma.journai.data.repository.JournalRepository
import com.shverma.journai.data.repository.JournalRepositoryImpl
import com.shverma.journai.data.preference.DataStoreHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton



@InstallIn(SingletonComponent::class)
@Module
object DispatchersModule {
    @Provides
    fun provideIODispatcher() = Dispatchers.IO
}


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context) = DataStoreHelper(context)
}


@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        journalRepositoryImpl: JournalRepositoryImpl
    ): JournalRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        aiRepositoryImpl: AiRepositoryImpl
    ): AiRepository
}
