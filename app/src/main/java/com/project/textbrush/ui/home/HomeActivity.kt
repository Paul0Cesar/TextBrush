package com.project.textbrush.ui.home

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.project.textbrush.R
import com.project.textbrush.databinding.ActivityHomeBinding
import com.project.textbrush.ui.drawing.DrawingActivity
import com.project.textbrush.utils.hasPermissions


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val imageData = MutableLiveData<Uri>()
    private var pictureUri: Uri? = null

    companion object {
        private const val REQUEST_PERMISSION_CODE = 123
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageData.value = uri
        }

    private var takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it && pictureUri != null) {
            imageData.value = pictureUri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        var permission = arrayOf(
            Manifest.permission.CAMERA
        )
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission.plus(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permission.plus(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.btnOpenCamera.setOnClickListener {
            if (hasPermissions(this, permission)) {
                openCamera()
            } else {
                requestPermissions(permission, REQUEST_PERMISSION_CODE)
            }
        }

        binding.btnTakeAPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        imageData.observe(this) {
            it?.let {
                val intent = Intent(this, DrawingActivity::class.java).apply {
                    val bundle = Bundle()
                    bundle.putParcelable("uri", it)
                    putExtras(bundle)
                }
                startActivity(intent)
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        pictureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        takePicture.launch(pictureUri)
    }
}

