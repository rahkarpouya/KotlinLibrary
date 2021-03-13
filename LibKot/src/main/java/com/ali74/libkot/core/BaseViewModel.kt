package com.ali74.libkot.core

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    val message = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    private val parentJob = Job()
    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            GlobalScope.launch {
                throwable.message?.apply {
                    Log.i("Response_Error", this)
                    loading.postValue(false)
                    message.postValue(this)
                }
            }
        }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + parentJob + coroutineExceptionHandler

    override fun onCleared() {
        parentJob.complete()
        super.onCleared()
    }


    suspend fun <T> apiLaunchIO(block: suspend () -> T): T = withContext(Dispatchers.IO) {
        loading.postValue(true)
        block.invoke()
    }

    fun <T> emit(res: BaseResult<T>, data: (T) -> Unit) {
        when (res.status) {
            BaseResult.Status.ERROR -> {
                loading.postValue(false)
                message.value = res.message ?: "اشکال در انجام عملیات"
            }
            BaseResult.Status.SUCCESS -> {
                loading.postValue(false)
                res.data?.apply { data.invoke(this) }
            }
        }
    }



}