package com.ronnie.data.di

import android.app.Application
import android.content.Context
import com.ronnie.commons.BASE_URL
import com.ronnie.commons.CACHE_NAME
import com.ronnie.commons.IMAGE_TYPE
import com.ronnie.commons.KEY
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
import javax.inject.Singleton

/**
 * Hilt Module, used to provide dependencies
 */
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

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesApi(retrofit: Retrofit):PixaBayApi= retrofit.create(PixaBayApi::class.java)

    @Provides
    @Singleton
    fun providesRepository(pixaBayApi: PixaBayApi,pixaBayRoomDb: PixaBayRoomDb, @ApplicationContext context: Context):SearchImagesRepository= SearchImagesRepositoryImpl(pixaBayApi,pixaBayRoomDb,context )

    @Provides
    @Singleton
    fun providesDB(@ApplicationContext appContext: Context): PixaBayRoomDb {
        return PixaBayRoomDb.invoke(appContext)
    }

    private val apiInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
            val originalHttpUrl = chain.request().url
            val url = originalHttpUrl.newBuilder()
                .addQueryParameter(KEY.first, KEY.second)
                .addQueryParameter(IMAGE_TYPE.first, IMAGE_TYPE.second)
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
            File(app.applicationContext.cacheDir, CACHE_NAME),
            10485760L
        )
    }

}