package com.example.finalproject_work

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class UsersAdapter(val users: ArrayList<UserData>) : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    private val TAG = "Adapter"

    interface OnItemClickListener {/////
        fun onItemClick(user: UserData)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }////


    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder (itemView){
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        // Each MyViewHolder object keeps a reference to 3 view items in our row_item.xml file
        val name = itemView.findViewById<Button>(R.id.btn_username)
        val id = itemView.findViewById<TextView>(R.id.id_text_view)
        val email = itemView.findViewById<TextView>(R.id.email_textView)
        val post = itemView.findViewById<ImageView>(R.id.post_image_view)
        val profileImage = itemView.findViewById<ImageView>(R.id.profile_image)


        init {/////
            name.setOnClickListener {
                val user = users[adapterPosition]
                val intent = Intent(itemView.context, profileView::class.java)
                intent.putExtra("user", user)
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.MyViewHolder {
        // Inflate a layout from our XML (row_item.XML) and return the holder
        // create a new view
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsersAdapter.MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val currentItem = users[position]
        holder.name.text = "${currentItem.firstName} ${currentItem.lastName}"
        holder.id.text = "${currentItem.firstName}${currentItem.lastName}"
        holder.email.text = "${currentItem.email}"

        val imageUrl = "${currentItem.image.orEmpty()}"
        val profilePicUrl = "${currentItem.profilePic.orEmpty()}"
        Log.d(TAG, "onBindViewHolder: $imageUrl")
        Log.d(TAG, "onBindViewHolder: $profilePicUrl")
        if(imageUrl != null){
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.post)
        }
        if(profilePicUrl != null){
            Glide.with(holder.itemView.context)
                .load(profilePicUrl)
                .circleCrop()
                .into(holder.profileImage)
        }
    }

    override fun getItemCount(): Int {
        // Return the size of your dataset (invoked by the layout manager)
        return users.size
    }
}