package com.nitkarsh.infogit.restservices

import com.nitkarsh.infogit.restservices.models.SearchResponse
import com.nitkarsh.infogit.restservices.models.UsersResponse
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
    fun getFollowers(@Path(value = "userName",encoded = true) userName: String): Call<List<UsersResponse>>
}