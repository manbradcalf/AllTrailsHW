package com.benmedcalf.alltrailshomework.model.remote

import com.benmedcalf.alltrailshomework.BuildConfig
import com.benmedcalf.alltrailshomework.model.remote.nearbySearch.NearbySearchResponse
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
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

        val client = okHttpBuilder
            // For logging
            .addInterceptor(httpLoggingInterceptor)
            // For adding key to every request as query param per maps docs
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
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PlacesAPI::class.java)
    }

    interface PlacesAPI {
        // https://developers.google.com/maps/documentation/places/web-service/details
        @GET("details/json")
        suspend fun getPlaceDetails(
            @Query("fields") fields: String,
            @Query("place_id") placeId: String
        ): Response<PlaceDetailsResponse>

        // https://developers.google.com/maps/documentation/places/web-service/search
        @GET("nearbysearch/json")
        suspend fun searchPlaces(@Query("radius") radius: Int): Response<NearbySearchResponse>

        @GET("findplacefromtext/json")
        suspend fun searchByName(
            @Query("input") input: String,
            @Query("fields") fields: String
        ): Response<PlaceDetailsResponse>
    }
}