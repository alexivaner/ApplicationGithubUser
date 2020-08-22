package com.example.applicationgithubuser.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.applicationgithubuser.R
import kotlin.collections.ArrayList


class MyAdapter(private val listData: ArrayList<UserGithub>) :
    RecyclerView.Adapter<MyAdapter.ListViewHolder>() {

    var listDataFilter = ArrayList<UserGithub>()


    init {
        listDataFilter = listData
    }

    private lateinit var context: Context


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        context = viewGroup.context
        return ListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.user_item, viewGroup, false)

        )


    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listDataFilter[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .apply(RequestOptions().override(100, 100))
            .into(holder.tvavatar)
        holder.tvUsername.text = user.username
        holder.tvUserid.text = user.userid

        holder.itemView.setOnClickListener {
            Toast.makeText(context, user.username, Toast.LENGTH_SHORT).show()
            val moveWithIntent = Intent(context, MyDetail::class.java)
            moveWithIntent.putExtra(MyDetail.EXTRA_USER, user)
            context.startActivity(moveWithIntent)
        }
    }

    override fun getItemCount(): Int {
        return listDataFilter.size
    }


    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.username)
        var tvUserid: TextView = itemView.findViewById(R.id.userid)
        var tvavatar: ImageView = itemView.findViewById(R.id.avatar)
    }


}