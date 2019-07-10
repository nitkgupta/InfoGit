package com.nitkarsh.infogit.RestServices

import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/users/{userName}")
    fun getUserData(@Path(value = "userName",encoded = true) userName: String): Call<UsersResponse>

    @GET("/search/users")
    fun searchUsers(@Query("q") text: String, @Query("page") page: Int) : Call<SearchResponse>

    @GET("/users/{userName}/followers")
    fun getFollowers(@Path(value = "userName",encoded = true) userName: String): Call<MutableList<UsersResponse>>
}