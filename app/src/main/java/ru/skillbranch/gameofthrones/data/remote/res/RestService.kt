package ru.skillbranch.gameofthrones.data.remote.res

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.local.entities.House

interface RestService {
    @GET("houses?pageSize=50")
    suspend fun houses(@Query("page") page:Int = 1): List<HouseRes>

    @GET("characters/{id}")
    suspend fun charter(@Path("id") charterId: String): CharacterRes

    @GET("houses")
    suspend fun houseByName(@Query("name") name: String) : List<HouseRes>
}