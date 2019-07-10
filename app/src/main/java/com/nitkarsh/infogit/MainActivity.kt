package com.nitkarsh.infogit

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.nitkarsh.infogit.RestServices.ApiService
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.UsersResponse

import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var call:Call<UsersResponse>
    private val callback: Callback<UsersResponse> by lazy { object :Callback<UsersResponse> {
        override fun onFailure(call: Call<UsersResponse>, t: Throwable) {

        }

        override fun onResponse(call: Call<UsersResponse>, response: Response<UsersResponse>) {
            Log.e("RESULT------->>>>>",response.body()!!.name)
        }
    } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        call.enqueue(callback)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
