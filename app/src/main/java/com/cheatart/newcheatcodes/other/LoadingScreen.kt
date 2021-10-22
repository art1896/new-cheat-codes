package com.cheatart.newcheatcodes.other

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.cheatart.newcheatcodes.R


object LoadingScreen {
    var dialog: Dialog? = null
    fun displayLoading(context: Context?) {
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.layout_loading_screen)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
        try {
            dialog!!.show()
        } catch (e: Exception) {
        }
    }

    fun hideLoading() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }
}