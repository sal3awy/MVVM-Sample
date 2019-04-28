package com.sal3awy.thed.dagger.App

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.sal3awy.thed.networking.NetworkInterceptor
import com.sal3awy.thed.utils.AppConstants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Module
class NetworkModule(val context : Context) {

    @Provides
    @AppScope
    fun context(): Context {
        return this.context
    }

    @Provides
    @AppScope
    fun loggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }


    @Provides
    @AppScope
    fun okHttpClient(loggingInterceptor: HttpLoggingInterceptor, context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(NetworkInterceptor(context))
            .addInterceptor { chain ->
                val request: Request
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.addHeader("Accept", "application/json")
                requestBuilder.addHeader("lang", Locale.getDefault().language)
                request = requestBuilder.build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }


    @Provides
    @AppScope
    fun gson(): Gson {
        return GsonBuilder()
            .setLenient()
            .setDateFormat("yyyy-MM-dd")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    @AppScope
    fun retrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @AppScope
    internal fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

}