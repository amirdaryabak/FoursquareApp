package com.amirdaryabak.foursquareapp.util

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.amirdaryabak.foursquareapp.R
import kotlinx.android.synthetic.main.dialog_loading.*

fun showLoading(context: Context, text: String = "Please wait"): Dialog {
    val dialog = Dialog(context, R.style.AppTheme)
    try {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.dialog_text.text = text
        val window = dialog.window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setDimAmount(0.32F)
        }
    } catch (e: Exception) {

    }
    return dialog
}
