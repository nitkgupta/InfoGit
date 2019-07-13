package com.nitkarsh.infogit.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nitkarsh.infogit.R
import com.nitkarsh.infogit.RestServices.models.Store
import com.nitkarsh.infogit.utils.Fonts
import kotlinx.android.synthetic.main.item_profile_details.view.*

class ProfileDetailsAdapter(var listStore: List<Store>): RecyclerView.Adapter<ProfileDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_details,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (listStore.isNullOrEmpty()) 0 else listStore.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvKey.text = listStore[position].key
        holder.tvValue.text = listStore[position].value
    }

    fun setData(list: List<Store>) {
        this.listStore = list
        notifyDataSetChanged()
    }

    class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val tvKey = view.tvKey
        val tvValue = view.tvValue
        init {
            tvKey.typeface = Fonts.mavenRegular(view.context)
            tvValue.setTypeface(Fonts.mavenRegular(view.context),Typeface.BOLD)
        }
    }
}