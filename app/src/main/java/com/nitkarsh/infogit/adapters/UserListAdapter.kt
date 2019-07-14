package com.nitkarsh.infogit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nitkarsh.infogit.R
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.utils.Fonts
import kotlinx.android.synthetic.main.item_user_profiles.view.*

class UserListAdapter(var callback: CallbackUserAction) :
    PagedListAdapter<UsersResponse, RecyclerView.ViewHolder>(USERRESPONSE_DIFF_UTIL) {

    private var networkState = LOADED


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USERS) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_profiles, parent, false)
            return ViewHolderUsers(view, callback)
        } else {
            return LoadingVH(LayoutInflater.from(parent.context).inflate(R.layout.item_loader, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderUsers && position != -1) {
            val context = holder.itemView.context
            holder.tvSiteAdmin.text =
                context.getString(R.string.site_admin, if (getAdapterItem(position).siteAdmin!!) "Yes" else "No")
            holder.tvScore.text = context.getString(R.string.score, getAdapterItem(position).score.toString())
            holder.tvLoginName.text = context.getString(R.string.user_name, getAdapterItem(position).login)
            Glide.with(context).load(getAdapterItem(position).avatarUrl)
                .apply(RequestOptions().circleCrop())
                .error(R.drawable.ic_placeholder_man)
                .into(holder.ivProfileImg)
        }
    }

    private fun isLoading() = networkState == LOADING

    //    maintains and call notify whenever network state changes
    fun setNetworkStatus(state: Int) {
        if (state == networkState) return
        networkState = state
        if (isLoading()) notifyItemInserted(itemCount) else notifyItemRemoved(itemCount)
    }

    private fun getAdapterItem(pos: Int): UsersResponse {
        return if (isLoading() && isLoadingItem(pos)) getItem(pos - 1)!! else getItem(pos)!!
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading() && isLoadingItem(position)) VIEW_TYPE_LOADER else VIEW_TYPE_USERS
    }

    private fun isLoadingItem(pos: Int) = pos == itemCount - 1

    class ViewHolderUsers(view: View, callback: CallbackUserAction) : RecyclerView.ViewHolder(view) {
        var constraintTop = view.constraintTop
        var tvScore = view.tvScore
        var tvSiteAdmin = view.tvSiteAdmin
        var tvLoginName = view.tvLoginName
        var ivProfileImg = view.ivProfileImg

        init {
            tvScore.typeface = Fonts.mavenRegular(view.context)
            tvSiteAdmin.typeface = Fonts.mavenRegular(view.context)
            tvLoginName.typeface = Fonts.mavenRegular(view.context)
            itemView.setOnClickListener {
                callback.onUserClick(adapterPosition)
            }
        }
    }

    class LoadingVH(view: View) : RecyclerView.ViewHolder(view)

    //    callback for getting the itemClick position
    interface CallbackUserAction {
        fun onUserClick(position: Int)
    }

    companion object {
        //        Diff util for comparision and pagedlist adapter
        val USERRESPONSE_DIFF_UTIL = object : DiffUtil.ItemCallback<UsersResponse>() {
            override fun areItemsTheSame(oldItem: UsersResponse, newItem: UsersResponse) = oldItem === newItem

            override fun areContentsTheSame(oldItem: UsersResponse, newItem: UsersResponse) = oldItem == newItem

        }

        const val VIEW_TYPE_LOADER = 0
        const val VIEW_TYPE_USERS = 2

        const val LOADING = 1
        const val LOADED = 2

    }

}