package com.nitkarsh.infogit

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitkarsh.infogit.restservices.models.UsersResponse
import com.nitkarsh.infogit.adapter.UserListAdapter
import com.nitkarsh.infogit.fragment.FollowersFragment
import com.nitkarsh.infogit.fragment.ProfileInfoFragment
import com.nitkarsh.infogit.utils.Fonts
import com.nitkarsh.infogit.utils.Utils
import com.nitkarsh.infogit.viewmodel.SearchUsersViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UserListAdapter.CallbackUserAction {

    private lateinit var searchUsersViewModel: SearchUsersViewModel
    private val userListAdapter by lazy { UserListAdapter(this) }
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        /*
         *ViewModel to store and manage UI related data in lifecycle conscious way
         */
        searchUsersViewModel = ViewModelProviders.of(this).get(SearchUsersViewModel::class.java)

        /*
         *Observing the livedata for list change and pass pagedList to adapter
         */
        searchUsersViewModel.userResponseLiveData.observe(this, Observer<PagedList<UsersResponse>> {
            it?.let {
                if(it.size > 0) {
                    setUiVisibility(View.GONE)
                }
                userListAdapter.submitList(it)
            }
        })

        /*
         *Observer for api error message if any
         */
        searchUsersViewModel.message.observe(this, Observer {
            Toast.makeText(this,"Error in call to Github with message:  ${it}",Toast.LENGTH_SHORT).show()
        })

        /*
         *Observing the network status to manage the paging ui while fetching data
         */
        searchUsersViewModel.networkState.observe(this, Observer {
            if(searchUsersViewModel.isFirstCall && UserListAdapter.NetworkState.LOADING == it) {
                showLoadingDialog()
            } else if (searchUsersViewModel.isFirstCall && UserListAdapter.NetworkState.LOADED == it) {
                searchUsersViewModel.isFirstCall = false
                dismissLoadingDialog()
            }
            it?.let { userListAdapter.setNetworkStatus(it) }
        })

//        Observer for observing if no item fetched in pagedlist from network call
        searchUsersViewModel.listLimit.observe(this, Observer {
            if(it == 0) {
                setUiVisibility(View.VISIBLE)
                tvInfoSearch.text = getString(R.string.no_result_found)
            } else {
                setUiVisibility(View.GONE)
            }
        })

        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvToolbar.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD)
        btnSearch.typeface = Fonts.mavenRegular(this)
        etSearchQuery.typeface = Fonts.mavenRegular(this)
        rvProfiles.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvProfiles.itemAnimator = DefaultItemAnimator()
        rvProfiles.adapter = userListAdapter

        etSearchQuery.setOnEditorActionListener { textView, i, keyEvent -> btnSearch.performClick()}

        btnSearch.setOnClickListener {
            if (!etSearchQuery.text.isNullOrEmpty()) {
                if (!searchUsersViewModel.queryText.equals(etSearchQuery.text.toString().trim())) { /*checks if call is made again for same text*/
                    searchUsersViewModel.isFirstCall = true
                    setUiVisibility(View.GONE)
                    searchUsersViewModel.queryText = etSearchQuery.text.toString().trim()
                    searchUsersViewModel.setQuery(etSearchQuery.text.toString().trim())
                    searchUsersViewModel.searchPagedDataSourceFactory.searchPagedDataSource.invalidate()
                    Utils.hideKeyboard(this)
                } else {
                    Toast.makeText(this,getString(R.string.please_enter_a_different_name),Toast.LENGTH_SHORT).show()
                }
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

    fun setUiVisibility(visibility: Int) {
        rvProfiles.visibility = if(visibility == View.GONE) View.VISIBLE else View.GONE
        ivSearchImg.visibility = visibility
        tvInfoSearch.visibility = visibility
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onUserClick(position: Int) {
        loadUnloadFragProfile(true,searchUsersViewModel.userResponseLiveData.value!![position]!!.login)
    }

     /*
      *function for managing the fragment loading and unloading from fragment manager
      */
    fun loadUnloadFragProfile(load: Boolean,login: String) {
        if (load) {
            Utils.hideKeyboard(this)
            if (supportFragmentManager.backStackEntryCount > 0
                && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
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
                setTitleText(getString(R.string.profile_details),false)
            }
        } else {
            finishAfterTwo()
        }
    }

    override fun onResume() {
        super.onResume()
        if(supportFragmentManager.backStackEntryCount == 1 && supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name) != null) {
            setTitleText(getString(R.string.profile_details),false)
        } else if(supportFragmentManager.backStackEntryCount == 2 && supportFragmentManager.findFragmentByTag(FollowersFragment::class.java.name) != null) {
            setTitleText(getString(R.string.followers),false)
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

    /*
     *function to prevent accidental back button press
     *finish activity if back button pressed two times within 2 sec
     */

    private fun finishAfterTwo() {
        searchUsersViewModel.backPressCount++

//       corotines launch  on background thread reset the counter
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000)
            searchUsersViewModel.backPressCount = 0
        }
        if(searchUsersViewModel.backPressCount == 2) {
            finishAffinity()
        } else {
            Toast.makeText(this,getString(R.string.press_back_again_to_exit),Toast.LENGTH_SHORT).show()
        }
    }

    public fun loadFollowersFragment(login: String){
        // replaces if Followers fragment alredy exits
        if (supportFragmentManager.backStackEntryCount > 0
            && supportFragmentManager.findFragmentByTag(FollowersFragment::class.java.name) != null) {
            supportFragmentManager.beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name)!!)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.constraintContainer, FollowersFragment.newInstance(login), FollowersFragment::class.java.name)
                .addToBackStack(ProfileInfoFragment::class.java.name)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(ProfileInfoFragment::class.java.name)!!)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                .add(R.id.constraintContainer, FollowersFragment.newInstance(login), FollowersFragment::class.java.name)
                .addToBackStack(ProfileInfoFragment::class.java.name)
                .commit()
        }
        setTitleText(getString(R.string.followers),false)
    }

    /*
     *loads a loading dialog generally during a network calls and loading
     */
    public fun showLoadingDialog() {
        try {
            dialog?.let {
                if (dialog!!.isShowing) {
                    dialog?.dismiss()
                }
            }
            if (this.isFinishing) {
                return
            }
            dialog = Dialog(this)
            val layoutParams = dialog!!.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog?.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog?.setCancelable(false)
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.setContentView(com.nitkarsh.infogit.R.layout.dialog_loading)
            dialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //    dismiss the laoding dialog
    public fun dismissLoadingDialog() {
        try {
            dialog?.let {
                dialog?.dismiss()
            }
            dialog = null
        } catch (e: Exception) {
            Log.e("e", "=$e")
        }

    }

}