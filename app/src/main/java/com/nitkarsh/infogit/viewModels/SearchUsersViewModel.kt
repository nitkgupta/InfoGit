package com.nitkarsh.infogit.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchUsersViewModel : ViewModel() {

    var searchResponse = MutableLiveData<SearchResponse>()
    var message = MutableLiveData<String>()

    fun getSearchData(query: String, page: Int, context: Context) {
        Utils.showLoadingDialog(context)
        RestClient.getApiService().searchUsers(query, page).enqueue(object : Callback<SearchResponse> {
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Utils.dismissLoadingDialog()
                t.printStackTrace()
            }

            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                Utils.dismissLoadingDialog()
                if (response.code() == 403) {
                    message.value = response.message()
                    return
                }
                (response.body()).let { searchResponse.value = response.body() }


            }
        })
    }

}