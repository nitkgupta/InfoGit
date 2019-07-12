package com.nitkarsh.infogit.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitkarsh.infogit.RestServices.ApiService
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchUsersViewModel: ViewModel() {

    var searchResponse = MutableLiveData<SearchResponse>()

    fun getSearchData(query: String,page: Int) {
        RestClient.getApiService().searchUsers(query,page).enqueue(object : Callback<SearchResponse>{
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                searchResponse.value = response.body()
            }
        })
    }

}