package com.ali74.libkot.core

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    val _loading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _loading

    private val parentJob = Job()
    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            GlobalScope.launch {
                throwable.message?.apply {
                    Log.i("Response_Error", this)
                    _loading.postValue(false)
                    _message.postValue(this)
                }
            }
        }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob + coroutineExceptionHandler

    override fun onCleared() {
        parentJob.complete()
        super.onCleared()
    }
}