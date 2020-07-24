package ru.skillbranch.gameofthrones

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.skillbranch.gameofthrones.repositories.RootRepository

class MainViewModel(val app: Application) : AndroidViewModel(app) {
    private val repository = RootRepository
    fun syncData() : LiveData<LoadResult<Boolean>> {
        val result: MutableLiveData<LoadResult<Boolean>> = MutableLiveData(LoadResult.Loading(false))
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.isNeedUpdate()) {
                if (!isNetworkEnable(app.applicationContext)) {
                    result.postValue(LoadResult.Error("Нет интернет соединения"))
                    return@launch
                }
                repository.syncData()
                result.postValue(LoadResult.Success(true))
            } else{
                delay(5000)
                result.postValue(LoadResult.Success(true))
            }
        }
        return result;
    }
}

sealed class LoadResult<T> (
    val data: T?, val error: String? = null) {
    class Success<T>(data:T): LoadResult<T>(data)
    class Loading<T>(data:T? = null): LoadResult<T>(data)
    class Error<T>(message:String, data: T? = null): LoadResult<T>(data, message)
}

fun isNetworkEnable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
    }
    return false
}

