package com.digitalsignage.androidplayer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digitalsignage.androidplayer.utils.LOADER_STATE

open class BaseViewModel: ViewModel() {

    //Variable to store current loading Status
    private val _loading = MutableLiveData<LOADER_STATE>()
    val loading: LiveData<LOADER_STATE>
        get() = _loading

    //Variable to store Error Messages when an error occurs
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _image = MutableLiveData<String>()
    val image: LiveData<String>
        get() = _image

    /**
     * Method to set values/Loading State to [_loading] & [_error]
     */
    fun setLoadingState(value: LOADER_STATE, errorMsg: String = "", image: String = "") {
        _image.postValue(image)
        _error.postValue(errorMsg)
        _loading.postValue(value)
    }
}