package com.cemsarikaya.kotlinphotoapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.cemsarikaya.kotlinphotoapp.R
import com.cemsarikaya.kotlinphotoapp.adapter.GalleryImageAdapter
import com.cemsarikaya.kotlinphotoapp.databinding.ActivityGalleryBinding
import com.cemsarikaya.kotlinphotoapp.model.MySingleton


class GalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding
    private lateinit var imagesAdapter : GalleryImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        binding = ActivityGalleryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
      /*  val mArrayUri = intent.getSerializableExtra("photos") as ArrayList<*>

        println(mArrayUri)
        */
        createdGridView()




    }

    override fun onResume() {
        super.onResume()
        createdGridView()
    }


    fun createdGridView(){

        val gridLayout = GridLayoutManager(this,2)
        imagesAdapter = GalleryImageAdapter(MySingleton.mArrayUri!!,this)

        binding.galleryRecyclerView.layoutManager = gridLayout
        binding.galleryRecyclerView.adapter = imagesAdapter

    }




}