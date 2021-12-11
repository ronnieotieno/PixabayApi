package com.ronnie.data.di

import android.app.Application
import android.content.Context
import com.ronnie.data.BuildConfig
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.data.repository.SearchImagesRepositoryImpl
import com.ronnie.domain.repositories.SearchImagesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private val loggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun providesOkHttpClient(cache:Cache): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(apiInterceptor)
            .addInterceptor (cacheInterceptor)
            .cache(cache)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) okHttpClient.addInterceptor(loggingInterceptor)
        return okHttpClient.build()
    }
    //TODO MOVE BASE URL
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://pixabay.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit):PixaBayApi= retrofit.create(PixaBayApi::class.java)

    @Provides
    @Singleton
    fun providesRepository(pixaBayApi: PixaBayApi,pixaBayRoomDb: PixaBayRoomDb):SearchImagesRepository= SearchImagesRepositoryImpl(pixaBayApi)

    @Provides
    @Singleton
    fun providesDB(@ApplicationContext appContext: Context): PixaBayRoomDb {
        return PixaBayRoomDb.invoke(appContext)
    }

    private val apiInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
            val originalHttpUrl = chain.request().url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("key", "12990678-12462fcc2da6261905f9a3a04")
                .addQueryParameter("image_type","photo")
                .build()
            request.url(url)
            chain.proceed(request.build())
        }

    private val cacheInterceptor = Interceptor { chain ->
        val response: Response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(30, TimeUnit.DAYS)
            .build()
        response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .build()
    }
    @Provides
    @Singleton
    fun provideCache(app: Application): Cache {
        return Cache(
            File(app.applicationContext.cacheDir, "pixabay_cache"),
            10485760L
        )
    }

}