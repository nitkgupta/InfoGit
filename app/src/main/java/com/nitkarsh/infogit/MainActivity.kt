package com.nitkarsh.infogit

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.adapters.UserListAdapter
import com.nitkarsh.infogit.fragments.ProfileInfoFragment
import com.nitkarsh.infogit.utils.Fonts
import com.nitkarsh.infogit.utils.Utils
import com.nitkarsh.infogit.viewModels.SearchUsersViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UserListAdapter.CallbackUserAction {
    override fun onUserClick(position: Int) {
        Toast.makeText(this, "Got your position --> ${position}", Toast.LENGTH_SHORT).show()
        loadUnloadFragProfile(true,searchUsersViewModel.userResponseLiveData.value!![position]!!.login)
    }

    private lateinit var searchUsersViewModel: SearchUsersViewModel
    private val userListAdapter by lazy { UserListAdapter(this) }
    private var backPressCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        searchUsersViewModel = ViewModelProviders.of(this).get(SearchUsersViewModel::class.java)

        searchUsersViewModel.userResponseLiveData.observe(this, Observer<PagedList<UsersResponse>> {
            it?.let { userListAdapter.submitList(it) }
        })

        searchUsersViewModel.message.observe(this, Observer {
            Toast.makeText(this,"Error in call to Github with message:  ${it}",Toast.LENGTH_SHORT).show()
        })

        searchUsersViewModel.networkState.observe(this, Observer {
            it?.let { userListAdapter.setNetworkStatus(it) }
        })

        searchUsersViewModel.listLimit.observe(this, Observer {
            if(it == 0) {
                rvProfiles.visibility = View.GONE
                ivSearchImg.visibility = View.VISIBLE
                tvInfoSearch.visibility = View.VISIBLE
                tvInfoSearch.text = getString(R.string.no_result_found)
            } else {
                rvProfiles.visibility = View.VISIBLE
                ivSearchImg.visibility = View.GONE
                tvInfoSearch.visibility = View.GONE
            }
        })

        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbar.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD)
        btnSearch.typeface = Fonts.mavenRegular(this)
        etSearchQuery.typeface = Fonts.mavenRegular(this)
        rvProfiles.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvProfiles.itemAnimator = DefaultItemAnimator()
        rvProfiles.adapter = userListAdapter

        btnSearch.setOnClickListener {
            if (!etSearchQuery.text.isNullOrEmpty()) {
                searchUsersViewModel.setQuery(etSearchQuery.text.toString())
                searchUsersViewModel.searchPagedDataSourceFactory.searchPagedDataSource.invalidate()
                Utils.hideKeyboard(this)
            } else {
                Toast.makeText(this, getString(R.string.error_please_enter_a_name), Toast.LENGTH_SHORT).show()
            }
        }
        if(savedInstanceState != null) {
            if (supportFragmentManager.backStackEntryCount > 0
                && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
                constraintContainer.visibility = View.VISIBLE
                groupMain.visibility = View.GONE
                setTitleText(getString(R.string.profile_details),false)
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun loadUnloadFragProfile(load: Boolean,login: String) {
        if (load) {
            Utils.hideKeyboard(this)
            if (supportFragmentManager.backStackEntryCount > 0
                && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null
            ) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                    .replace(R.id.constraintContainer, ProfileInfoFragment.newInstance(login), ProfileInfoFragment::class.java.name)
                    .addToBackStack(ProfileInfoFragment::class.java.name)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                    .add(R.id.constraintContainer, ProfileInfoFragment.newInstance(login), ProfileInfoFragment::class.java.name)
                    .addToBackStack(ProfileInfoFragment::class.java.name)
                    .commit()
            }
            constraintContainer.visibility = View.VISIBLE
            groupMain.visibility = View.GONE
            setTitleText(getString(R.string.profile_details),false)
        } else {
            if (supportFragmentManager.backStackEntryCount > 0
                && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
                supportFragmentManager.popBackStack()
            }
            constraintContainer.visibility = View.GONE
            groupMain.visibility = View.VISIBLE
            setTitleText(getString(R.string.search_github),true)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (supportFragmentManager.backStackEntryCount == 1 && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
                loadUnloadFragProfile(false,"")
            } else {
                super.onBackPressed()
            }
        } else {
            finishAfterTwo()
        }
    }

    override fun onResume() {
        super.onResume()
        if(supportFragmentManager.backStackEntryCount == 1 && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
            setTitleText(getString(R.string.profile_details),false)
        } else {
            setTitleText(getString(R.string.search_github),true)
        }
    }

    public fun setTitleText(title: String, setIcon: Boolean) {
        tvToolbar.text = title
        if(setIcon) {
            tvToolbar.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_worker,0)
        } else {
            tvToolbar.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        }
    }
    private fun finishAfterTwo() {
        backPressCount++
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000)
            backPressCount = 0
        }
        if(backPressCount == 2) {
            finishAffinity()
        } else {
            Toast.makeText(this,getString(R.string.press_back_again_to_exit),Toast.LENGTH_SHORT).show()
        }
    }
}