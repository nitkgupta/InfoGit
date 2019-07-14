package com.nitkarsh.infogit.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel: ViewModel() {

    public var usersResponse = MutableLiveData<UsersResponse>()
    public var message = MutableLiveData<String>()

//    newtwork call to get a user details
    public fun getData(login: String,context: Context) {
        Utils.showLoadingDialog(context)
        RestClient.getApiService().getUserData(login).enqueue(object :Callback<UsersResponse> {
            override fun onFailure(call: Call<UsersResponse>, t: Throwable) {
                Utils.dismissLoadingDialog()
            }

            override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
                Utils.dismissLoadingDialog()
                if(response.code() == 403) {
                    message.value = response.message()
                    return
                }
                response.body().let { usersResponse.value = response.body() }
            }
        })
    }
}