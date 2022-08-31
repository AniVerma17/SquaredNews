package com.example.squarednews.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.squarednews.data.NewsApiService
import com.example.squarednews.data.db.NewsDb
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MainModule {

    @Provides
    @Singleton
    fun provideApiService(): NewsApiService = Retrofit.Builder()
        .baseUrl("https://api.newscatcherapi.com")
        .client(OkHttpClient.Builder()
            .addInterceptor {
                return@addInterceptor it.proceed(
                    it.request().newBuilder()
                        .header("X-API-KEY", Firebase.remoteConfig.getString("newscatcher_api_key"))
                        .build()
                )
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        )
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(NewsApiService::class.java)

    @Provides
    fun provideLocalNewsDb(@ApplicationContext context: Context): NewsDb =
        Room.databaseBuilder(
            context, NewsDb::class.java, "news-db"
        ).build()

    @Provides
    @Singleton
    fun provideNewsPrefsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile("news_prefs") }
}