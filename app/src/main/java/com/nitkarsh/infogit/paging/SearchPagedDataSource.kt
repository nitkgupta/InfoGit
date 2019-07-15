package com.nitkarsh.infogit.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.nitkarsh.infogit.restservices.RestClient
import com.nitkarsh.infogit.restservices.models.SearchResponse
import com.nitkarsh.infogit.restservices.models.UsersResponse
import com.nitkarsh.infogit.adapter.UserListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
 * holds chunks of data which is later loaded into pagedlist
 */
class SearchPagedDataSource(var text: String) : PageKeyedDataSource<Int, UsersResponse>() {

    val networkState by lazy { MutableLiveData<UserListAdapter.NetworkState>() }
    val message by lazy { MutableLiveData<String>() }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UsersResponse>) {
        val initialPosition = 1
        if (!text.isEmpty()) {
            requestUsersData(initialPosition) { data, after, before ->
                callback.onResult(data, initialPosition, initialPosition + 1)
            }
        }
    }

    /*
     * call when chunks of data has to be loaded
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UsersResponse>) {
        if (!text.isEmpty()) {
            requestUsersData(params.key) { data, after, before ->
                callback.onResult(data, params.key + 1)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UsersResponse>) {

    }

//    call when data is to be fetched and setvalue to livedata for observing
    private fun requestUsersData(page: Int, result: (List<UsersResponse>, Int, Int) -> Unit) {
        networkState.postValue(UserListAdapter.NetworkState.LOADING)
        RestClient.getApiService().searchUsers(text, page).enqueue(object : Callback<SearchResponse> {
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                t.printStackTrace()
                networkState.postValue(UserListAdapter.NetworkState.LOADED)
            }

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                networkState.postValue(UserListAdapter.NetworkState.LOADED)
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