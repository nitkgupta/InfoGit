package com.nitkarsh.infogit.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.adapters.UserListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchPagedDataSource(var text: String) : PageKeyedDataSource<Int, UsersResponse>() {

    val networkState by lazy { MutableLiveData<Int>() }
    val message by lazy { MutableLiveData<String>() }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UsersResponse>) {
        val initialPosition = 1
        if (!text.isEmpty()) {
            requestUsersData(initialPosition) { data, after, before ->
                callback.onResult(data, initialPosition, initialPosition + 1)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UsersResponse>) {
        if (!text.isEmpty()) {
            requestUsersData(params.key) { data, after, before ->
                callback.onResult(data, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UsersResponse>) {

    }

    private fun requestUsersData(page: Int, result: (List<UsersResponse>, Int, Int) -> Unit) {
        networkState.postValue(UserListAdapter.LOADING)
        RestClient.getApiService().searchUsers(text, page).enqueue(object : Callback<SearchResponse> {
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                t.printStackTrace()
                networkState.postValue(UserListAdapter.LOADED)
            }

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                networkState.postValue(UserListAdapter.LOADED)
                if (response.code() == 403) {
                    message.value = response.message()
                }
                if (response.isSuccessful && response.body() != null) {
                    result(response.body()!!.usersList!!, page, page + 1)
                }
            }
        })
    }
}