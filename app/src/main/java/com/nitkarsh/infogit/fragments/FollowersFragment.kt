package com.nitkarsh.infogit.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.nitkarsh.infogit.R
import com.nitkarsh.infogit.adapters.FollowersAdapter
import com.nitkarsh.infogit.adapters.UserListAdapter
import com.nitkarsh.infogit.utils.Constants
import com.nitkarsh.infogit.viewModels.FollowersViewModel
import kotlinx.android.synthetic.main.fragment_followers.*

class FollowersFragment : Fragment(),UserListAdapter.CallbackUserAction {
    override fun onUserClick(position: Int) {}

    private var loginName:String? = null
    private lateinit var followersViewModel: FollowersViewModel

    companion object {
        fun newInstance(name: String): FollowersFragment {
            val followersFragment = FollowersFragment()
            val bundle = Bundle()
            bundle.putString(Constants.KEY_LOGIN, name)
            followersFragment.arguments = bundle
            return followersFragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments!= null && arguments!!.containsKey(Constants.KEY_LOGIN)) {
            loginName = arguments!!.getString(Constants.KEY_LOGIN)
        }
        if(loginName.isNullOrEmpty()){
            Toast.makeText(context,getString(R.string.some_error_occured),Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        followersViewModel = ViewModelProviders.of(this).get(FollowersViewModel::class.java)
        rvFollowers.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        rvFollowers.itemAnimator = DefaultItemAnimator()

        followersViewModel.followersLiveData.observe(this, Observer {
            if(rvFollowers.adapter == null) {
                rvFollowers.adapter = FollowersAdapter(it,this)
            } else {
                (rvFollowers.adapter as FollowersAdapter).setData(it)
            }
        })

        followersViewModel.getData(loginName!!,context!!)
    }

    override fun onDestroy() {
        System.gc()
        super.onDestroy()
    }
}
