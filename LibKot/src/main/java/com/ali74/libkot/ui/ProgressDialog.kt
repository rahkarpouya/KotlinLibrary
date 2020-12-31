package com.ali74.libkot.ui

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.ali74.libkot.R

class ProgressDialog(context: Context, private val title: String = "لطفا منتظر بمانید ...") :
    Dialog(context, R.style.CustomDialogTheme) {

    private val constrainProgress by lazy { findViewById<ConstraintLayout>(R.id.constrainProgress) }
    private val cardProgress by lazy { findViewById<CardView>(R.id.cardProgress) }
    private val prbProgress by lazy { findViewById<ProgressBar>(R.id.prbProgress) }
    private val txtProgress by lazy { findViewById<AppCompatTextView>(R.id.txtProgress) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        setCancelable(false)

        constrainProgress.setBackgroundColor(Color.parseColor("#60000000")) //Background Color
        cardProgress.setCardBackgroundColor(Color.parseColor("#70000000")) //Box Color

        txtProgress.text = title
        txtProgress.setTextColor(Color.WHITE)

        setColorFilter(
            prbProgress.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
        ) //Progress Bar Color

    }

    private fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        }
    }

    override fun onBackPressed() {
        dismiss()
    }
}