package com.cheatart.newcheatcodes.other

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Insets
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Size
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
        finish()
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}


fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.makeSnackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).show()
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("Retry") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}


fun makeToast(text: String, context: Context) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT)
        .show()
}


fun Context.getImageUri(inImage: Bitmap?, gameName: String?): Uri? {
    val cachePath = File(externalCacheDir, "my_images/")
    cachePath.mkdirs()
    val file = File(cachePath, gameName)
    val fileOutputStream: FileOutputStream
    try {
        fileOutputStream = FileOutputStream(file)
        inImage?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)
}

fun TextView.setClickableText(text: String?, color: Int, what: Any) {
    val spanTxt = SpannableStringBuilder(
        text
    )
    spanTxt.setSpan(
        what,
        0,
        spanTxt.length,
        0
    )
    spanTxt.setSpan(
        ForegroundColorSpan(color),
        0,
        spanTxt.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    movementMethod = LinkMovementMethod.getInstance()
    setText(spanTxt, TextView.BufferType.SPANNABLE)

}


fun Context.getDisplayDimensions(): Pair<Int, Int> {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val width = display.width
        val height = display.height
        return Pair(width, height)
    } else {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics: WindowMetrics = wm.currentWindowMetrics
        val windowInsets = metrics.windowInsets
        val insets: Insets = windowInsets.getInsetsIgnoringVisibility(
            WindowInsets.Type.navigationBars()
                    or WindowInsets.Type.displayCutout()
        )

        val insetsWidth: Int = insets.right + insets.left
        val insetsHeight: Int = insets.top + insets.bottom

        val bounds: Rect = metrics.bounds
        val legacySize = Size(
            bounds.width() - insetsWidth,
            bounds.height() - insetsHeight
        )
        return Pair(legacySize.width, legacySize.height)
    }
}

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()


