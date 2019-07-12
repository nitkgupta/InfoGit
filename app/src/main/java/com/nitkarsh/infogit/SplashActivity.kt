package com.nitkarsh.infogit

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.utils.Fonts
import com.nitkarsh.infogit.viewModels.SearchUsersViewModel
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var searchUsersViewModel:SearchUsersViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        searchUsersViewModel = ViewModelProviders.of(this).get(SearchUsersViewModel::class.java)

        tvWelcome.typeface = Fonts.avenirNext(this)
        btnSearch.typeface = Fonts.mavenRegular(this)
        edtQuery.typeface = Fonts.mavenRegular(this)

        searchUsersViewModel.searchResponse.observe(this, Observer<SearchResponse> {
            Toast.makeText(this,it.totalCount.toString(),Toast.LENGTH_SHORT).show()
        })

        btnSearch.setOnClickListener {
            if(!edtQuery.text.isNullOrEmpty()) {
                searchUsersViewModel.getSearchData(edtQuery.text.toString(), 1)
            } else {
                Toast.makeText(this,getString(R.string.error_please_enter_a_name),Toast.LENGTH_SHORT).show()
            }
        }
    }
}
