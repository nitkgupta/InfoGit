package com.nitkarsh.infogit.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel: ViewModel() {

    public var usersResponse = MutableLiveData<UsersResponse>()
    public var message = MutableLiveData<String>()

    public fun getData(login: String) {
        RestClient.getApiService().getUserData(login).enqueue(object :Callback<UsersResponse> {
            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                if(response.code() == 403) {
                    message.value = response.message()
                    return
                }
                response.body().let { usersResponse.value = response.body() }
            }
        })
    }
}