package com.nitkarsh.infogit

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitkarsh.infogit.RestServices.ApiService
import com.nitkarsh.infogit.RestServices.RestClient
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.utils.Constants
import com.nitkarsh.infogit.utils.Fonts
import com.nitkarsh.infogit.viewModels.SearchUsersViewModel

import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var searchUsersViewModel: SearchUsersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        searchUsersViewModel = ViewModelProviders.of(this).get(SearchUsersViewModel::class.java)

        searchUsersViewModel.searchResponse.observe(this, Observer<SearchResponse> {
            Toast.makeText(this,it.totalCount.toString(), Toast.LENGTH_SHORT).show()
            it.usersList.let {

            }
        })

        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbar.setTypeface(Fonts.mavenRegular(this),Typeface.BOLD)
        btnSearch.typeface = Fonts.mavenRegular(this)
        etSearchQuery.typeface = Fonts.mavenRegular(this)
        rvProfiles.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        rvProfiles.itemAnimator = DefaultItemAnimator()

        btnSearch.setOnClickListener {
            if(!etSearchQuery.text.isNullOrEmpty()) {
                searchUsersViewModel.getSearchData(etSearchQuery.text.toString(),1)
            } else {
                Toast.makeText(this, getString(R.string.error_please_enter_a_name), Toast.LENGTH_SHORT).show()
            }
        }
    }

}
