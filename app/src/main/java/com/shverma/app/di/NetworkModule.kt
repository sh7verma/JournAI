package com.shverma.app.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.shverma.app.data.network.ApiService
import com.shverma.app.data.preference.DataStoreHelper
import com.shverma.app.utils.OffsetDateTimeAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.threeten.bp.OffsetDateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://journai-backend-pcyy.onrender.com/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        dataStoreHelper: DataStoreHelper
    ): AuthInterceptor = AuthInterceptor(
        getToken = { runBlocking { dataStoreHelper.accessToken.firstOrNull() } },
        getTokenType = { runBlocking { dataStoreHelper.tokenType.firstOrNull() } }
    )

    @Provides
    @Singleton
    fun provideCacheOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}


class AuthInterceptor(
    private val getToken: () -> String?,
    private val getTokenType: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = getToken()
        val tokenType = getTokenType()

        return if (!token.isNullOrBlank() && !tokenType.isNullOrBlank()) {
            val authenticatedRequest = request.newBuilder()
                .addHeader("Authorization", "$tokenType $token")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            chain.proceed(request)
        }
    }
}
