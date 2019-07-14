package com.nitkarsh.infogit.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nitkarsh.infogit.MainActivity

import com.nitkarsh.infogit.R
import com.nitkarsh.infogit.RestServices.models.Store
import com.nitkarsh.infogit.adapters.ProfileDetailsAdapter
import com.nitkarsh.infogit.utils.Constants
import com.nitkarsh.infogit.utils.Utils
import com.nitkarsh.infogit.viewModels.UserDetailViewModel
import kotlinx.android.synthetic.main.fragment_profile_info.*

class ProfileInfoFragment : Fragment() {

    private lateinit var userDetailViewModel:UserDetailViewModel
    private var listData =  ArrayList<Store>()
    private var browseUrl: String? = null
    private var login: String? = null

    companion object {
        fun newInstance(login: String): ProfileInfoFragment {
            val profileInfoFragment = ProfileInfoFragment()
            val bundle = Bundle()
            bundle.putString(Constants.KEY_LOGIN,login)
            profileInfoFragment.arguments = bundle
            return profileInfoFragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDetailViewModel = ViewModelProviders.of(this).get(UserDetailViewModel::class.java)
        if(arguments!= null && arguments!!.containsKey(Constants.KEY_LOGIN)) {
            login = arguments!!.getString(Constants.KEY_LOGIN)
            if(login.isNullOrEmpty()) {
                Toast.makeText(context,getString(R.string.some_error_occured),Toast.LENGTH_SHORT).show()
                (activity as MainActivity).loadUnloadFragProfile(false,"")
                return
            }
        } else {
            Toast.makeText(context,getString(R.string.some_error_occured),Toast.LENGTH_SHORT).show()
            (activity as MainActivity).loadUnloadFragProfile(false,"")
            return
        }
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDetailViewModel.message.observe(this, Observer {
            Toast.makeText(context,"Error in call to Github with message:  ${it}",Toast.LENGTH_SHORT).show()
        })
        rvProfileDetails.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        rvProfileDetails.itemAnimator = DefaultItemAnimator()
        rvProfileDetails.isNestedScrollingEnabled = false

        tvBrowse.setOnClickListener {
            if(!browseUrl.isNullOrEmpty()) {
                Utils.openUrl(context!!,browseUrl!!)
            } else {
                Toast.makeText(context,getString(R.string.error_empty_browse_url),Toast.LENGTH_SHORT).show()
            }
        }
        if(!login.isNullOrEmpty()) {
            userDetailViewModel.getData(login!!,context!!)
        }

        tvFollowers.setOnClickListener {
            (activity as MainActivity).loadFollowersAdapter(login!!)
        }

        userDetailViewModel.usersResponse.observe(this, Observer {
            it?.let {
                listData.clear()
                if(!it.name.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.name),it.name!!))
                }
                if(it.login.isEmpty()) {
                    listData.add(Store(getString(R.string.login_name),it.login))
                }
                if(!it.company.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.company),it.company!!))
                }
                if(!it.location.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.location),it.location!!))
                }
                if(!it.bio.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.bio),it.bio!!))
                }
                if(!it.email.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.email),it.email!!))
                }
                if(!it.createdAt.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.account_creation_date),Utils.convertDateViaFormatTZ(it.createdAt!!)))
                }
                if(!it.updatedAt.isNullOrEmpty()) {
                    listData.add(Store(getString(R.string.lat_updated),Utils.convertDateViaFormatTZ(it.updatedAt!!)))
                }
                if(it.publicRepos != null) {
                    listData.add(Store(getString(R.string.public_repos),it.publicRepos!!.toString()))
                }
                if(it.publicGists != null) {
                    listData.add(Store(getString(R.string.public_gists),it.publicGists!!.toString()))
                }
                if(it.followers != null) {
                    listData.add(Store(getString(R.string.followers),it.followers.toString()))
                }
                if(it.following != null) {
                    listData.add(Store(getString(R.string.following),it.following.toString()))
                }
                if(it.hireable != null) {
                    if(it.hireable!!) {
                        listData.add(Store(getString(R.string.hireable),"Yes"))
                    } else {
                        listData.add(Store(getString(R.string.hireable),"No"))
                    }
                }
            }
            if(rvProfileDetails.adapter == null) {
                rvProfileDetails.adapter = ProfileDetailsAdapter(listData)
            } else {
                (rvProfileDetails.adapter as ProfileDetailsAdapter).setData(listData)
            }
            Glide.with(this)
                .load(it.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.ic_placeholder_man)
                .into(ivAvatar)

            if(!it.htmlUrl.isNullOrEmpty()) {
                browseUrl = it.htmlUrl
                tvBrowse.visibility = View.VISIBLE
            }
            if(it.followers != null && it.followers!! > 0) {
                tvFollowers.visibility = View.VISIBLE
            }
        })
    }

    override fun onDestroy() {
        System.gc()
        super.onDestroy()
    }
}
