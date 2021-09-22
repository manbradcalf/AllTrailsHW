package com.benmedcalf.alltrailshomework.model

import com.benmedcalf.alltrailshomework.model.local.PlaceDao
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.common.PlaceDetails
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val placeDao: PlaceDao) {
    lateinit var userLocation: LatLng
    private val scope = CoroutineScope(Dispatchers.IO)
    private val service = GooglePlacesService.instance
    private val _searchResults = MutableStateFlow<RepoResponse>(RepoResponse.Loading())
    val searchResults: Flow<RepoResponse> = _searchResults


    private fun mapAPIResponseToRestaurants(response: List<PlaceDetails>): ArrayList<Restaurant> {
        val restaurants = arrayListOf<Restaurant>()
        response.forEach { detail ->
            val restaurant = detail.toRestaurant()
            val isFavorite = !placeDao.getFavorites().none { detail.placeId == it._id }
            restaurant.isFavorite = isFavorite
            restaurants.add(restaurant)
        }
        return restaurants
    }

    private fun formatLatLng(latLng: LatLng): String {
        return "${latLng.latitude},${latLng.longitude}"
    }


    suspend fun searchNearby(query: String) {
        scope.launch {
            val response = service.searchNearby(formatLatLng(userLocation), query)
            if (response.isSuccessful) {
                response.body()?.results?.let {
                    _searchResults.value = RepoResponse.Success(mapAPIResponseToRestaurants(it))
                }
            } else {
                _searchResults.value = RepoResponse.Error(response.errorBody().toString())
            }
        }
    }

    suspend fun updateIsFavorite(place: PlaceEntity) {
        scope.launch {
            // if its not in favoritesv
            val favs = placeDao.getFavorites()

            if (favs.none { place._id == it._id }) {
                // insert it
                placeDao.insert(place)
            } else {
                placeDao.delete(place)
            }
        }
    }

}

sealed class RepoResponse(
    var searchResults: ArrayList<Restaurant>? = null,
    var error: String? = null
) {
    class Success(restaurants: ArrayList<Restaurant>) :
        RepoResponse(searchResults = restaurants)

    class Error(searchResponseError: String?) :
        RepoResponse(error = searchResponseError)

    class Loading : RepoResponse()
}
