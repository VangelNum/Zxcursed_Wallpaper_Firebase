package com.zxcursed.wallpaper.feature_main.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zxcursed.wallpaper.core.common.Resource
import com.zxcursed.wallpaper.feature_main.domain.use_cases.GetAllPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelMain @Inject constructor(
    private val getAllPhotosUseCase: GetAllPhotosUseCase,
) : ViewModel() {

    private val _allPhotos = mutableStateOf(AllPhotosState())
    val allPhotos: State<AllPhotosState> = _allPhotos

    init {
        getAllPhotos()
    }

    fun getAllPhotos() {
        viewModelScope.launch {
            getAllPhotosUseCase().collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _allPhotos.value = AllPhotosState(error = resource.message.toString())
                    }
                    is Resource.Loading -> {
                        _allPhotos.value = AllPhotosState(isLoading = true)
                    }
                    is Resource.Success -> {
                        _allPhotos.value = AllPhotosState(isLoading = false)
                        _allPhotos.value = AllPhotosState(data = resource.data)
                    }
                }
            }
        }

    }
}