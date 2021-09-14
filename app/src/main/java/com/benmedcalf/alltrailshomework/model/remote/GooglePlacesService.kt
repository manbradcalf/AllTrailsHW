package com.benmedcalf.alltrailshomework.model.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object GooglePlacesService {
    //TODO: Move to a config
    private const val baseURL = "https://maps.googleapis.com/maps/api/place"

    // Singleton PlacesAPI instance
    val instance: PlacesAPI by lazy {
        // Create the logging interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PlacesAPI::class.java)
    }

    interface PlacesAPI {
        /*
        DOCS: https://developers.google.com/maps/documentation/places/web-service/details
        */
        @GET("/place/json")
        suspend fun getPlaceDetails(@Query("place_id") placeId: String): Response<PlaceDetailsResponse>

        /*
        DOCS: https://developers.google.com/maps/documentation/places/web-service/search
        */
        // TODO: We need to specify restaurants here
        @GET("/nearbysearch")
        suspend fun searchPlaces(@Query("radius") radius: Int)
    }
}