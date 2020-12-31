package com.ali74.libkot.utils

import android.content.Context
import androidx.annotation.ColorRes
import com.ali74.libkot.R
import com.ali74.libkot.patternBuilder.ReplaceFontApp

object AppTheme {

    object SnackBar {
        @ColorRes
        var snackMessageColor = R.color.white

        @ColorRes
        var snackBackgroundColor = R.color.black

        @ColorRes
        var snackActionTextColor = R.color.black

        @ColorRes
        var snackActionBackgroundColor = R.color.white
    }

    fun setAppFont(context: Context, font: String) {
        ReplaceFontApp(context, font).execute()
    }

}