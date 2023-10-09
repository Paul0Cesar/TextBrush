package com.project.textbrush.ui.drawing

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.project.textbrush.R
import com.project.textbrush.databinding.ActivityDrawingBinding
import com.project.textbrush.ui.text.TextEditActivity
import com.project.textbrush.utils.ImageUtils
import com.project.textbrush.utils.gone
import com.project.textbrush.utils.parcelable
import com.project.textbrush.utils.visible


class DrawingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawingBinding
    private val currentColor = MutableLiveData(Color.BLACK)
    private val currentText = MutableLiveData("")
    private val currentFamilyFont = MutableLiveData("sans-serif")

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.extras?.let {
                    it.getString("font")?.let { font ->
                        currentFamilyFont.value = font
                    }
                    it.getString("text")?.let { text ->
                        currentText.value = text
                    }
                    currentColor.value = it.getInt("color")
                }
            }
            binding.actionBtnOpenTextEdit.visible()
            binding.actionBtnShare.visible()
            binding.actionBtnUndoAction.visible()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_drawing)

        intent.extras?.let {
            val uri = it.parcelable<Uri>("uri")
            binding.imageViewBackground.setImageURI(uri)
        }

        binding.drawView.actionCounter.observe(this) {
            if (it != null && it > 0) {
                binding.actionBtnUndoAction.visible()
            } else {
                binding.actionBtnUndoAction.gone()
            }
        }

        currentText.observe(this) { text ->
            if (text.isNotEmpty()) {
                binding.drawView.setText(text)
            }
        }

        currentColor.observe(this) { color ->
            if (color != null) {
                binding.drawView.setTextColor(color)
            }
        }

        currentFamilyFont.observe(this) { font ->
            if (font != null && font.isNotEmpty()) {
                binding.drawView.setFamilyFont(font)
            }
        }

        binding.actionBtnOpenTextEdit.setOnClickListener {
            binding.actionBtnOpenTextEdit.gone()
            binding.actionBtnShare.gone()
            binding.actionBtnUndoAction.gone()
            startForResult.launch(Intent(this, TextEditActivity::class.java))
        }

        binding.actionBtnUndoAction.setOnClickListener {
            binding.drawView.undoLast()
        }

        binding.actionBtnShare.setOnClickListener {
            val bitmap = ImageUtils.mergeViews(binding.imageViewBackground, binding.drawView)
            shareImage(bitmap)
        }
    }

    private fun shareImage(icon: Bitmap) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"

        val values = ContentValues()
        values.put(Images.Media.TITLE, R.string.app_name)
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        val uri = contentResolver.insert(
            Images.Media.EXTERNAL_CONTENT_URI,
            values
        )

        if (uri == null) {
            Toast.makeText(this, R.string.activity_drawing_share_fail_message, Toast.LENGTH_LONG)
                .show()
            return
        }

        try {
            contentResolver.openOutputStream(uri)?.let { outStream ->
                icon.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.close()
            }
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, getString(R.string.activity_drawing_share_title)))
    }
}
