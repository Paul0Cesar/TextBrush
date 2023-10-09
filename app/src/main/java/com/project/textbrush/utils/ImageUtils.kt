package com.project.textbrush.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View


object ImageUtils {


    private fun loadBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        view.draw(canvas)
        return returnedBitmap
    }

    fun mergeViews(backgroundImage: View, canvasImage: View): Bitmap {
        val background: Bitmap = loadBitmapFromView(backgroundImage)
        val canvas: Bitmap = loadBitmapFromView(canvasImage)
        val bitmap = Bitmap.createBitmap(
            backgroundImage.width,
            backgroundImage.height,
            Bitmap.Config.RGB_565
        )
        val payload = Canvas(bitmap)
        payload.drawBitmap(background, 0f, 0f, null)
        payload.drawBitmap(canvas, 0f, 0f, null)

        return bitmap
    }
}