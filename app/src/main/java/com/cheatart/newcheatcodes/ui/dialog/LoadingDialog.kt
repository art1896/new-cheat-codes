package com.cheatart.newcheatcodes.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.cheatart.newcheatcodes.R


class LoadingDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)

        return inflater.inflate(R.layout.loading_dialog, container, false)
    }


    override fun onResume() {
        super.onResume()
        val width = RelativeLayout.LayoutParams.WRAP_CONTENT
        val height = RelativeLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
    }

    fun dismissDialog() {
        dialog?.let { dismiss() }
    }
}