package com.brokenprotocol.firebaseauthdemo.di

import com.brokenprotocol.firebaseauthdemo.network.DjangoApiInterface
import com.brokenprotocol.firebaseauthdemo.network.DjangoApiService
import com.brokenprotocol.firebaseauthdemo.network.FirebaseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.0.14:8000/") // Update this for your Django server
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideDjangoApiInterface(retrofit: Retrofit): DjangoApiInterface {
        return retrofit.create(DjangoApiInterface::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDjangoApiService(djangoApiInterface: DjangoApiInterface): DjangoApiService {
        return DjangoApiService(djangoApiInterface)
    }
    
    @Provides
    @Singleton
    fun provideFirebaseApiService(): FirebaseApiService {
        return FirebaseApiService()
    }
} 