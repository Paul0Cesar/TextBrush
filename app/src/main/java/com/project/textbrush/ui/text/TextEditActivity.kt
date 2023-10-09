package com.project.textbrush.ui.text

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.project.textbrush.R
import com.project.textbrush.databinding.ActivityTextEditBinding
import com.project.textbrush.ui.adapters.ColorAdapter
import com.project.textbrush.ui.adapters.FamilyFontAdapter


class TextEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTextEditBinding
    private val currentColor = MutableLiveData(Color.BLACK)
    private val currentFamilyFont = MutableLiveData("sans-serif")

    private val colors = arrayOf(Color.BLACK, Color.RED, Color.BLUE, Color.GREEN)
    private val familyFonts = arrayOf(
        "sans-serif",
        "sans-serif-light",
        "sans-serif-condensed",
        "sans-serif-black",
        "sans-serif-thin",
        "sans-serif-medium"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_text_edit)

        currentColor.observe(this) { textColor ->
            textColor?.let {
                binding.textInputEditLayout.setTextColor(it)
            }

        }
        currentFamilyFont.observe(this) { familyFont ->
            familyFont?.let {
                binding.textInputEditLayout.typeface = Typeface.create(
                    it,
                    Typeface.NORMAL
                )
            }
        }
        binding.recyclerViewColors.adapter = ColorAdapter(colors, currentColor)
        binding.recyclerViewTextFamilyFont.adapter =
            FamilyFontAdapter(familyFonts, currentFamilyFont)

        binding.actionBtnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnDone.setOnClickListener {
            val data = Intent()
            val bundle = Bundle().apply {
                putString("font", currentFamilyFont.value)
                putString("text", binding.textInputEditLayout.text.toString())
                currentColor.value?.let {
                    putInt("color", it)
                }
            }
            data.putExtras(bundle)
            setResult(RESULT_OK, data)
            finish()
        }
    }
}