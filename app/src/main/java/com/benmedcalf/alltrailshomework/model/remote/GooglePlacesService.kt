package com.benmedcalf.alltrailshomework.model.remote

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
        TODO: How to edit fields queryParam
        See Common.Result class for available fields. fields queryParam is comma delimited list of property names
        */
        @GET("/details/json")
        suspend fun getPlaceDetails(
            @Query("fields") fields: String,
            @Query("place_id") placeId: String,
            @Query("key") key: String
        ): Response<PlaceDetailsResponse>

        /*
        DOCS: https://developers.google.com/maps/documentation/places/web-service/search
        */
        // TODO: We need to specify restaurants here / update params
        @GET("/nearbysearch/json")
        suspend fun searchPlaces(@Query("radius") radius: Int): Response<NearbySearchResponse>

        /*
         https://maps.googleapis.com/maps/api/place/findplacefromtext/json
         ?fields=formatted_address%2Cname%2Crating%2Copening_hours%2Cgeometry&input=Museum%20of%20Contemporary%20Art%20Australia
         &inputtype=textquery
         &key=YOURAPIKEY
         */
        @GET("/findplacefromtext/json")
        suspend fun searchByName(@Query("input") input: String): Response<PlaceDetailsResponse>
    }
}