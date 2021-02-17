package com.lediya.infosys.communication


import com.google.gson.Gson
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * object class for declaring RestClient properties used in this application
 */
object RestClient {
    private const val BASE_URL="https://dl.dropboxusercontent.com/s/2iodh4vg0eortkl/"
    private const val CONNECTION_TIME_OUT = 100L
    private const val READ_TIME_OUT = 200L
    private val okHttpClient by lazy { OkHttpClient() }
    /**Create retrofit */
    private val retrofit: Retrofit by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client: OkHttpClient = okHttpClient.newBuilder()
            .connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .dispatcher(dispatcher)
            .build()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.client(client).build()
    }
    /**
     * Create the instance of retrofit */
    fun getInstance(): ApiEndPointService {
        return retrofit.create(ApiEndPointService::class.java)
    }

}