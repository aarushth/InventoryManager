package com.leopardseal.inventorymanagerapp.ui.main.box.expanded


import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.repositories.BoxRepository
import com.leopardseal.inventorymanagerapp.data.repositories.ImageRepository
import com.leopardseal.inventorymanagerapp.data.responses.Boxes
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BoxExpandedViewModel @Inject constructor(
    private val repository: BoxRepository,
    private val imageRepository : ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val boxId: Long = savedStateHandle["box_id"] ?: -1L

    private val _box = MutableStateFlow<Boxes?>(repository.getCachedBoxById(boxId))
    val box: StateFlow<Boxes?>
        get() = _box

    private val _updateResponse = MutableStateFlow<Resource<SaveResponse>>(Resource.Init)
    val updateResponse: StateFlow<Resource<SaveResponse>>
        get() = _updateResponse

    init {
        _box.value = repository.getCachedBoxById(boxId)
        if(boxId >= 0 ) {
            viewModelScope.launch {

                val response = repository.fetchBoxById(boxId)
                if (response is Resource.Success) {
                    _box.value = response.value
                }

            }
        }

    }
    fun saveOrUpdateBox(updatedBox: Boxes, imageChanged : Boolean) = viewModelScope.launch {
        _updateResponse.value = Resource.Loading

        try {
            _updateResponse.value = repository.updateBox(updatedBox, imageChanged)

            if (_updateResponse.value is Resource.Success) {
                updatedBox.id = (_updateResponse.value as Resource.Success<SaveResponse>).value.id
                _box.value = updatedBox
            }

        } catch (e: Exception) {
            _updateResponse.value = Resource.Failure(isNetworkError = false, errorCode = null)
        }
    }
    private val _uploadResult = MutableStateFlow<Resource<Unit>>(Resource.Init)
    val uploadResult: StateFlow<Resource<Unit>>
        get() = _uploadResult

    fun uploadImage(sasUrl: String, imageFile: File) {
        resetUpdateResponse()
        _uploadResult.value = Resource.Loading
        viewModelScope.launch {
            _uploadResult.value = imageRepository.uploadImage(sasUrl, imageFile)
        }
    }
    fun resetUploadFlag(){
        _uploadResult.value = Resource.Init
    }
    fun resetUpdateResponse(){
        _updateResponse.value = Resource.Init
    }

}