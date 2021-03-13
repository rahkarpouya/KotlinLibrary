package com.ali74.libkot.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat


fun String.toMobileFormat() = this.matches("09\\d{9}".toRegex())

fun String.validateMelliCode(): Boolean = ValidateMelliCode.isCode(this)

fun String.convertStringToBase64(): String {
   return if (this.trim().isEmpty()) ""
    else Base64.encodeToString(this.trim().toByteArray(charset("UTF-8")), Base64.DEFAULT)
}


fun String.convertImageToBase64(): String {
    if (this == "") return ""
    val bm = BitmapFactory.decodeFile(this)
    var compress = 75
    val size = bm.byteCount
    //if size more than 1MB use more compress
    if (size.toFloat() / 10000000 > 1.0) compress = 50
    if (size.toFloat() / 10000000 > 2.0) compress = 40
    if (size.toFloat() / 10000000 > 4.0) compress = 10
    val bos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, compress, bos)
    val bytesArray = bos.toByteArray()
    return Base64.encodeToString(bytesArray, Base64.DEFAULT)
}

fun Drawable.drawableToBitmap(): Bitmap? {
    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }
    val bitmap: Bitmap? = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap!!)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

fun Activity.hideKeyboard() {
    Looper.myLooper()?.apply {
        Handler(this).postDelayed({
            val view = this@hideKeyboard.findViewById<View>(android.R.id.content)
            if (view != null) {
                val imm =
                    this@hideKeyboard.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }, 300)
    }

}

fun Activity.showKeyboard() {
    val inputMethodManager =
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Context.copyTextToClipboard(information: String): Boolean {
    if (information.trim().isEmpty())
        return false

    return try {
        val clipboard = this.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Error information", information)
        clipboard.setPrimaryClip(clip)
        true
    } catch (e: java.lang.Exception) {
        Log.i("Error_Clipboard", e.message!!)
        false
    }
}

fun addSeparatorText(text: Any, showCurrency: Boolean = false, currency: String = "تومان"): String =
    if (showCurrency) DecimalFormat("#,###.##").format(text) + " " + currency
    else DecimalFormat("#,###.##").format(text)

fun AppCompatEditText.addSeparatorTextWatcher() {
    this.addTextChangedListener(TextWatcherForThousand(this))
}

fun String.removeSeparatorTextWatcher() {
    TextWatcherForThousand.trimCommaOfString(this.trim())
}

fun isRootedDevice(): Boolean {
    val buildTags = Build.TAGS
    if (buildTags != null && buildTags.contains("test-keys")) return true

    // check if /system/app/Superuser.apk is present
    try {
        val file = File("/system/app/Superuser.apk")
        if (file.exists()) {
            return true
        }
    } catch (e1: java.lang.Exception) {
    }
    // try executing commands
    return (canExecuteCommand("/system/xbin/which su")
            || canExecuteCommand("/system/bin/which su")
            || canExecuteCommand("which su"))

}

private fun canExecuteCommand(command: String): Boolean = try {
    Runtime.getRuntime().exec(command)
    true
} catch (e: java.lang.Exception) {
    false
}

// Check Device Emulator
fun isEmulator(): Boolean = (Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.BOARD === "QC_Reference_Phone" //bluestacks
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.HOST.startsWith("Build") //MSI App Player
        || Build.BRAND.startsWith("generic")
        && Build.DEVICE.startsWith("generic")
        || "google_sdk" === Build.PRODUCT)


fun EditText.trimString():String = this.text.toString().trim()

fun View.setGone() {this.visibility = View.GONE}

fun View.setVisible() {this.visibility = View.VISIBLE}

fun View.setInvisible() {this.visibility = View.INVISIBLE}

fun View.isVisible() :Boolean{ return this.visibility == View.VISIBLE }