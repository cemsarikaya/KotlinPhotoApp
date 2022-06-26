package com.cemsarikaya.kotlinphotoapp.view
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cemsarikaya.kotlinphotoapp.R
import com.cemsarikaya.kotlinphotoapp.adapter.ThumbnailAdapter
import com.cemsarikaya.kotlinphotoapp.databinding.ActivityMainBinding
import com.cemsarikaya.kotlinphotoapp.model.MySingleton
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imagesAdapter : ThumbnailAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()
        MySingleton.mArrayUri = ArrayList()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
        }


        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            System.currentTimeMillis().toString() + ".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    MySingleton.mArrayUri!!.add(savedUri)
                    createLinearLayoutView()


                }

            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()
            val cameraBackSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val cameraFrontSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            var bool = true

            //ön, arka kamera değiştirme
            binding.frontBackButton.setOnClickListener {
                bool = !bool
                if (bool == true){
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this, cameraBackSelector, preview, imageCapture
                    )
                }else{
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this, cameraFrontSelector, preview, imageCapture)
                }
            }
            try {
                cameraProvider.unbindAll()
                //ilk açılış
                cameraProvider.bindToLifecycle(
                    this, cameraBackSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    }

    fun goToGalleryButton(view:View){
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)

    }
    fun createLinearLayoutView(){
        val gridLayout = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL,false)
        imagesAdapter = ThumbnailAdapter(MySingleton.mArrayUri!!,baseContext)
        binding.recyclerView.layoutManager = gridLayout
        binding.recyclerView.adapter = imagesAdapter
    }

    companion object {
        private const val TAG = "CameraXGFG"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onResume() {
        super.onResume()
        createLinearLayoutView()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
