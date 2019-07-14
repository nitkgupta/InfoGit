package com.nitkarsh.infogit.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersViewModel: ViewModel(){

    public var followersLiveData = MutableLiveData<List<UsersResponse>>()
    public var message = MutableLiveData<String>()

//    network call to fetch followers
    public fun getData(login: String,context: Context) {
        Utils.showLoadingDialog(context)
        RestClient.getApiService().getFollowers(login).enqueue(object : Callback<List<UsersResponse>> {
            override fun onFailure(call: Call<List<UsersResponse>>, t: Throwable) {
                Utils.dismissLoadingDialog()
            }

            override fun onResponse(call: Call<List<UsersResponse>>, response: Response<List<UsersResponse>>) {
                Utils.dismissLoadingDialog()
                if(response.code() == 403) {
                    message.value = response.message()
                    return
                }
                if(response.isSuccessful && response.body() != null) {
                    response.body().let { followersLiveData.value = response.body() }
                }
            }
        })
    }
}