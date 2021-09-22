package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.viewModelScope
import com.benmedcalf.alltrailshomework.model.RepoResponse
import com.benmedcalf.alltrailshomework.model.RepoResponse.Error
import com.benmedcalf.alltrailshomework.model.Repository
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
@Inject constructor(private val repository: Repository) : BaseViewModel<RepoResponse>() {
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
                    is RepoResponse.Success -> {
                        val newState = UIState.Success<RepoResponse>(results)
                        _uiState.value = newState
                    }
                    is Error -> {
                        val newState =
                            UIState.Error<RepoResponse>(errorMessage = results.error!!)
                        _uiState.value = newState
                    }

                    is RepoResponse.Loading -> TODO()
                }
            }
        }
    }
}