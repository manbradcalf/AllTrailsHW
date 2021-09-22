package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.RepoSearchResults
import com.benmedcalf.alltrailshomework.model.RepoSearchResults.Error
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.benmedcalf.alltrailshomework.model.local.PlaceEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel
@Inject constructor(private val repository: PlacesRepository) : BaseViewModel<RepoSearchResults>() {
    var onFavoriteClick: ((Restaurant) -> Unit)? = { restaurant ->
        CoroutineScope(Dispatchers.IO).launch {
            //TODO("only save id")
            val entity = PlaceEntity(restaurant.placeId, restaurant.name)
            repository.updateIsFavorite(entity)
        }
    }

    init {
        viewModelScope.launch {
            repository.searchResults.collect { results ->
                when (results) {
                    is RepoSearchResults.Success -> {
                        val newState = UIState.Success<RepoSearchResults>(results)
                        _uiState.value = newState
                    }
                    is Error -> {
                        val newState =
                            UIState.Error<RepoSearchResults>(errorMessage = results.error!!)
                        _uiState.value = newState
                    }

                    is RepoSearchResults.Loading -> TODO()
                }
            }
        }
    }
}