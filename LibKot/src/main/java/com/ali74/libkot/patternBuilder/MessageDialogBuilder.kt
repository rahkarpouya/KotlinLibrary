package com.ali74.libkot.patternBuilder

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.ali74.libkot.R
import com.google.android.material.button.MaterialButton

class MessageDialogBuilder(private val context: Context) {

    private var title = ""
    private var btnTextConfirm = ""
    private var confirm: View.OnClickListener? = null

    @DrawableRes
    private var colorTitle = R.color.black

    @DrawableRes
    private var backgroundToolbar = R.color.white

    @DrawableRes
    private var iconToolbar = android.R.drawable.sym_def_app_icon

    @DrawableRes
    private var btnConfirmColor = R.color.green


    fun setMessage(message: String, @DrawableRes color: Int = R.color.black) = apply {
        title = message
        colorTitle = color
    }

    fun setIconToolbar(@DrawableRes drawable: Int, @DrawableRes toolbarColor: Int = R.color.black) =
        apply {
            iconToolbar = drawable
            backgroundToolbar = toolbarColor
        }

    fun setBtnConfirm(text: String = "", @DrawableRes color: Int) = apply {
        btnTextConfirm = text
        btnConfirmColor = color
    }

    fun setOnclickBtn(confirm: View.OnClickListener? = null) = apply {
        this.confirm = confirm
    }

    fun create(): Dialog = Dialog(context, R.style.CustomDialogTheme).apply {
        setContentView(R.layout.dialog_message)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT

        val ivToolbarM = findViewById<AppCompatImageView>(R.id.ivToolbarM)
        val txtTitleM = findViewById<AppCompatTextView>(R.id.txtTitleM)
        val btnConfirmM = findViewById<MaterialButton>(R.id.btnConfirmM)
        ivToolbarM.setImageResource(iconToolbar)
        ivToolbarM.setBackgroundResource(backgroundToolbar)

        txtTitleM?.text = title
        txtTitleM?.setTextColor(colorTitle)

        btnConfirmM.text = btnTextConfirm
        btnConfirmM.setBackgroundResource(btnConfirmColor)

        if (confirm == null)
            btnConfirmM.setOnClickListener { dismiss() }
        else btnConfirmM.setOnClickListener {
            confirm?.onClick(it)
            dismiss()
        }
    }

}