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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat


fun formatMobile(mobile: String) = mobile.matches("09\\d{9}".toRegex())

fun validateMelliCode(nationalCode: String): Boolean = ValidateMelliCode.isCode(nationalCode)

fun convertStringToBase64(string: String): String =
    if (string.trim().isEmpty()) ""
    else Base64.encodeToString(string.trim().toByteArray(charset("UTF-8")), Base64.DEFAULT)

fun convertImageToBase64(filePath: String): String {
    if (filePath == "") return ""
    val bm = BitmapFactory.decodeFile(filePath)
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

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }
    val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap!!)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun hideKeyboard(activity: Activity) {
    Looper.myLooper()?.apply {
        Handler(this).postDelayed({
            val view = activity.findViewById<View>(android.R.id.content)
            if (view != null) {
                val imm =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }, 300)
    }

}

fun showKeyboard(activity: Activity) {
    val inputMethodManager =
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun copyTextToClipboard(context: Context, information: String): Boolean {
    if (information.trim().isEmpty())
        return false

    return try {
        val clipboard =
            context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Error information", information)
        clipboard.setPrimaryClip(clip)
        true
    } catch (e: java.lang.Exception) {
        Log.i("Error_Clipboard", e.message!!)
        false
    }
}

fun addSeparatorText(
    text: Any,
    showCurrency: Boolean = false,
    currency: String = "تومان"
): String =
    if (showCurrency) DecimalFormat("#,###.##").format(text) + " " + currency
    else DecimalFormat("#,###.##").format(text)

fun addSeparatorTextWatcher(editText: AppCompatEditText) {
    editText.addTextChangedListener(TextWatcherForThousand(editText))
}

fun removeSeparatorTextWatcher(text: String) {
    TextWatcherForThousand.trimCommaOfString(text.trim())
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
