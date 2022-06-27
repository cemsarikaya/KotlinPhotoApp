package com.cemsarikaya.kotlinphotoapp.adapter
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cemsarikaya.kotlinphotoapp.databinding.GalleryRowBinding
import java.io.File


class GalleryImageAdapter(val postArrayList: ArrayList<Uri>, private val context: Context): RecyclerView.Adapter<GalleryImageAdapter.ViewHolder>() {

    class ViewHolder(val binding: GalleryRowBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GalleryRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return  ViewHolder(binding)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var pathName  = postArrayList[position].lastPathSegment
        val uri = Uri.fromFile(postArrayList[position].path?.let { File(it) })
        val fdelete = File(uri.path.toString())
        var bool = false
        Glide.with(context)
            .load(postArrayList[position])
            .centerCrop()
            .into(holder.binding.galleryImageView)

           holder.binding.galleryImageText.text=pathName


        holder.binding.galleryImageView.setOnClickListener {
            bool = !bool
            if (bool == true){
                holder.binding.deleteImageButton.visibility = View.VISIBLE
                holder.binding.deleteImageButton.setOnClickListener {
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Toast.makeText(context, "file Deleted :$pathName", Toast.LENGTH_SHORT).show()


                        } else {
                            Toast.makeText(context, "file not Deleted :$pathName", Toast.LENGTH_SHORT).show()

                        }
                        postArrayList.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    }
                }

            }else{
                holder.binding.deleteImageButton.visibility = View.GONE
            }
        }
        holder.binding.renameButton.setOnClickListener {
            holder.binding.galleryImageText.visibility = View.GONE
            holder.binding.renameText.visibility = View.VISIBLE
            holder.binding.renameButton.visibility =View.GONE
            holder.binding.renameYesButton.visibility =View.VISIBLE
        }
        holder.binding.renameYesButton.setOnClickListener {
            holder.binding.galleryImageText.text = holder.binding.renameText.text
            holder.binding.renameButton.visibility =View.VISIBLE
            holder.binding.renameYesButton.visibility =View.GONE
            holder.binding.galleryImageText.visibility = View.VISIBLE
            holder.binding.renameText.visibility = View.GONE
            pathName = holder.binding.renameText.text.toString()
            holder.binding.galleryImageText.text = pathName


        }
    }
    override fun getItemCount(): Int {
        return  postArrayList.size

    }

}

