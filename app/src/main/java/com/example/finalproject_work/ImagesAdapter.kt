package com.example.finalproject_work

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImagesAdapter(private val images: ArrayList<String?>) : RecyclerView.Adapter<ImagesAdapter.MyViewHolder>(){

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder (itemView){
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.post_image_view_profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAdapter.MyViewHolder{
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photos_posted_dynamic, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagesAdapter.MyViewHolder, position: Int){
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int{
        return images.size
    }
}
