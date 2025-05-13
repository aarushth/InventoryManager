package com.leopardseal.inventorymanagerapp.data.repositories

import com.leopardseal.inventorymanagerapp.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseRepository {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ) : Resource<T> {
        return withContext(Dispatchers.IO){
            try{
                val response = apiCall()
                if (response.isSuccessful) {
                    Resource.Success(response.body() ?: Unit as T) // if you expect no body
                } else {
                    Resource.Failure(false, response.code())
                }
            }catch(throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(false, throwable.code())
                    }
                    else ->{
                        Resource.Failure(true, null)
                    }
                }
            }
        }
    }
}