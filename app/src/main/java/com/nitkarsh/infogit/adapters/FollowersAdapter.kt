package com.nitkarsh.infogit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nitkarsh.infogit.R
import com.nitkarsh.infogit.RestServices.models.UsersResponse

class FollowersAdapter(var listFollowers: List<UsersResponse>, var callback : UserListAdapter.CallbackUserAction) : RecyclerView.Adapter<UserListAdapter.ViewHolderUsers>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolderUsers {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_profiles,parent,false)
        return UserListAdapter.ViewHolderUsers(view,callback)
    }

    override fun getItemCount(): Int {
        return if(listFollowers.isNullOrEmpty()) 0 else listFollowers.size
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolderUsers, position: Int) {
        val context = holder.itemView.context
        holder.tvSiteAdmin.text =
            context.getString(R.string.site_admin, if (listFollowers[position].siteAdmin!!) "Yes" else "No")
        holder.tvScore.visibility = View.GONE
        holder.tvLoginName.text = context.getString(R.string.user_name, listFollowers[position].login)
        Glide.with(context).load(listFollowers[position].avatarUrl)
            .apply(RequestOptions().circleCrop())
            .error(R.drawable.ic_placeholder_man)
            .into(holder.ivProfileImg)

        ConstraintSet().apply {
            clone(holder.constraintTop)
            connect(holder.tvSiteAdmin.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        }.applyTo(holder.constraintTop)
    }

    fun setData(list: List<UsersResponse>) {
        this.listFollowers = list
        notifyDataSetChanged()
    }
}