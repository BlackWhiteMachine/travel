package com.example.travel.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View

fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
    if (width > 0 && height > 0) {
        view.measure(View.MeasureSpec.makeMeasureSpec(convertDpToPixels(width.toFloat()), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(convertDpToPixels(height.toFloat()), View.MeasureSpec.EXACTLY))
    }
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)

    val bitmap = Bitmap.createBitmap(view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val background = view.background

    background?.draw(canvas)
    view.draw(canvas)

    return bitmap
}

private fun convertDpToPixels(dp: Float): Int {
    return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp, Resources.getSystem().displayMetrics))
}
