package com.benmedcalf.alltrailshomework.model.remote

import com.benmedcalf.alltrailshomework.BuildConfig
import com.benmedcalf.alltrailshomework.model.remote.nearbySearch.SearchResponse
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object GooglePlacesService {
    private const val baseURL = "https://maps.googleapis.com/maps/api/place/"
    private const val key = BuildConfig.apikey

    val instance: PlacesAPI by lazy {
        val okHttpBuilder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val gson: Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val client = okHttpBuilder
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val originalHttpUrl = chain.request().url
                val url = originalHttpUrl.newBuilder().addQueryParameter("key", key).build()
                request.url(url)
                return@addInterceptor chain.proceed(request.build())
            }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        retrofit.create(PlacesAPI::class.java)
    }

    interface PlacesAPI {
        @GET("details/json")
        suspend fun getPlaceDetails(
            @Query("fields") fields: String,
            @Query("place_id") placeId: String
        ): Response<PlaceDetailsResponse>

        @GET("nearbysearch/json?radius=50000&type=restaurant")
        suspend fun searchNearby(
            @Query("location") location: String,
            @Query("keyword") name: String
        ): Response<SearchResponse>
    }
}