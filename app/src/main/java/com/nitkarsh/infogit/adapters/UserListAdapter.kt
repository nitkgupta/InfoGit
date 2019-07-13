package com.nitkarsh.infogit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nitkarsh.infogit.R
import com.nitkarsh.infogit.RestServices.models.UsersResponse
import com.nitkarsh.infogit.utils.Fonts
import kotlinx.android.synthetic.main.item_user_profiles.view.*

class UserListAdapter(var userListResponse: List<UsersResponse>, var callback: CallbackUserAction): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_profiles,parent,false)
        return ViewHolder(view,callback)
    }

    override fun getItemCount(): Int {
        return if (userListResponse.isNullOrEmpty()) 0 else userListResponse.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.tvSiteAdmin.text = context.getString(R.string.site_admin,if (userListResponse[position].siteAdmin!!) "Yes" else "No")
        holder.tvScore.text = context.getString(R.string.score,userListResponse[position].score.toString())
        holder.tvLoginName.text = context.getString(R.string.user_name,userListResponse[position].login)
        Glide.with(context).load(userListResponse[position].avatarUrl)
            .apply(RequestOptions().circleCrop())
            .error(R.drawable.ic_placeholder_man)
            .into(holder.ivProfileImg)
    }

    fun setData(list: List<UsersResponse>) {
        this.userListResponse = list
        notifyDataSetChanged()
    }

    class ViewHolder(view:View,callback: CallbackUserAction) : RecyclerView.ViewHolder(view) {
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

    interface CallbackUserAction {
        fun onUserClick(position: Int)
    }
}