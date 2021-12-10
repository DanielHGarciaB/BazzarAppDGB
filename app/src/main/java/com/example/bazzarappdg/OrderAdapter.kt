package com.example.bazzarappdg

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.Drawable
import android.widget.Toast
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.net.URL


public  class OrderAdapter(private val dataSet: MutableList<OrderEntity>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var orderEntity = dataSet[position]

        holder.titleItems.text = orderEntity.title;
        holder.desItem.text = orderEntity.des;

    }

    override fun getItemCount():Int{
        return dataSet.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val titleItems :TextView = itemView.findViewById<TextView>(R.id.titleItem)
        val desItem :TextView = itemView.findViewById<TextView>(R.id.desItem)
    }


}