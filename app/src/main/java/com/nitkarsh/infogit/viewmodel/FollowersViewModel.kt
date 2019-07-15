package com.nitkarsh.infogit.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitkarsh.infogit.MainActivity
import com.nitkarsh.infogit.restservices.RestClient
import com.nitkarsh.infogit.restservices.models.UsersResponse
import com.nitkarsh.infogit.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersViewModel: ViewModel(){

    public var followersLiveData = MutableLiveData<List<UsersResponse>>()
    public var message = MutableLiveData<String>()

//    network call to fetch followers
    public fun getData(login: String,context: Context) {
    if(context is MainActivity) {
        context.showLoadingDialog()
    }
        RestClient.getApiService().getFollowers(login).enqueue(object : Callback<List<UsersResponse>> {
            override fun onFailure(call: Call<List<UsersResponse>>, t: Throwable) {
                if(context is MainActivity) {
                    context.dismissLoadingDialog()
                }
            }

            override fun onResponse(call: Call<List<UsersResponse>>, response: Response<List<UsersResponse>>) {
                if(context is MainActivity) {
                    context.dismissLoadingDialog()
                }
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