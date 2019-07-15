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

class UserDetailViewModel: ViewModel() {

    public var usersResponse = MutableLiveData<UsersResponse>()
    public var message = MutableLiveData<String>()

//    newtwork call to get a user details
    public fun getData(login: String,context: Context) {
    if(context is MainActivity) {
        context.showLoadingDialog()
    }
        RestClient.getApiService().getUserData(login).enqueue(object :Callback<UsersResponse> {
            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                if(context is MainActivity) {
                    context.dismissLoadingDialog()
                }
            }

            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                if(context is MainActivity) {
                    context.dismissLoadingDialog()
                }
                if(response.code() == 403) {
                    message.value = response.message()
                    return
                }
                response.body().let { usersResponse.value = response.body() }
            }
        })
    }
}