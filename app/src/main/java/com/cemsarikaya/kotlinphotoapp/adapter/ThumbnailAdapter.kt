package com.cemsarikaya.kotlinphotoapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cemsarikaya.kotlinphotoapp.databinding.PhotoRowBinding


class ThumbnailAdapter(val postArrayList: ArrayList<Uri>,private val context: Context): RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

        class ViewHolder(val binding: PhotoRowBinding) : RecyclerView.ViewHolder(binding.root) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = PhotoRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return  ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

                Glide.with(context)
                    .load(postArrayList.get(position))
                    .centerCrop()
                    .into(holder.binding.photoImageView)


        }

        override fun getItemCount(): Int {


            return postArrayList.size

        }


}
