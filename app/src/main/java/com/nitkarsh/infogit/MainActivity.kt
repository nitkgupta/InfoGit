package com.nitkarsh.infogit

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitkarsh.infogit.RestServices.models.SearchResponse
import com.nitkarsh.infogit.adapters.UserListAdapter
import com.nitkarsh.infogit.fragments.ProfileInfoFragment
import com.nitkarsh.infogit.utils.Fonts
import com.nitkarsh.infogit.utils.Utils
import com.nitkarsh.infogit.viewModels.SearchUsersViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), UserListAdapter.CallbackUserAction {
    override fun onUserClick(position: Int) {
        Toast.makeText(this, "Got your position --> ${position}", Toast.LENGTH_SHORT).show()
        loadUnloadFragProfile(true,searchUsersViewModel.searchResponse.value?.usersList!![position].login)
    }

    private lateinit var searchUsersViewModel: SearchUsersViewModel
    private var userListAdapter: UserListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        searchUsersViewModel = ViewModelProviders.of(this).get(SearchUsersViewModel::class.java)

        searchUsersViewModel.searchResponse.observe(this, Observer<SearchResponse> {
            it.usersList.let {
                if (userListAdapter == null) {
                    userListAdapter = UserListAdapter(it!!.toMutableList(), this)
                    rvProfiles.adapter = userListAdapter
                } else {
                    userListAdapter!!.setData(it!!)
                }
            }
            if (it.usersList.isNullOrEmpty()) {
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

        searchUsersViewModel.message.observe(this, Observer {
            Toast.makeText(this,"Error in call to Github with message:  ${it}",Toast.LENGTH_SHORT).show()
        })

        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbar.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD)
        btnSearch.typeface = Fonts.mavenRegular(this)
        etSearchQuery.typeface = Fonts.mavenRegular(this)
        rvProfiles.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvProfiles.itemAnimator = DefaultItemAnimator()

        btnSearch.setOnClickListener {
            if (!etSearchQuery.text.isNullOrEmpty()) {
                searchUsersViewModel.getSearchData(etSearchQuery.text.toString(), 1)
                Utils.hideKeyboard(this)
            } else {
                Toast.makeText(this, getString(R.string.error_please_enter_a_name), Toast.LENGTH_SHORT).show()
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
        } else {
            if (supportFragmentManager.backStackEntryCount > 0
                && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
                supportFragmentManager.popBackStack()
            }
            constraintContainer.visibility = View.GONE
            groupMain.visibility = View.VISIBLE
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
            finishAffinity()
        }
    }
}
