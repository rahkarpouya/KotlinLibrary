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

class QuestionDialogBuilder(private val context: Context) {

    private var title = ""
    private var btnTextConfirm = ""
    private var btnTextCancel = ""
    private var confirm: View.OnClickListener? = null
    private var cancel: View.OnClickListener? = null

    @DrawableRes
    private var colorTitle = R.color.black

    @DrawableRes
    private var backgroundToolbar = R.color.white

    @DrawableRes
    private var iconToolbar = android.R.drawable.sym_def_app_icon

    @DrawableRes
    private var btnConfirmColor = R.color.green

    @DrawableRes
    private var btnCancelColor = R.color.red

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

    fun setBtnCancel(text: String = "", @DrawableRes color: Int) = apply {
        btnTextCancel = text
        btnCancelColor = color
    }

    fun setOnclickBtn(confirm: View.OnClickListener, cancel: View.OnClickListener? = null) = apply {
        this.confirm = confirm
        this.cancel = cancel
    }

    fun create(): Dialog = Dialog(context, R.style.CustomDialogTheme).apply {
        setContentView(R.layout.dialog_question)
        setCancelable(false)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT

        val ivToolbarQ = findViewById<AppCompatImageView>(R.id.ivToolbarQ)
        val txtTitleQ = findViewById<AppCompatTextView>(R.id.txtTitleQ)
        val btnCancelQ = findViewById<MaterialButton>(R.id.btnCancelQ)
        val btnConfirmQ = findViewById<MaterialButton>(R.id.btnConfirmQ)
        ivToolbarQ.setImageResource(iconToolbar)
        ivToolbarQ.setBackgroundResource(backgroundToolbar)

        txtTitleQ.text = title
        txtTitleQ.setTextColor(colorTitle)

        btnCancelQ.text = btnTextCancel
        btnCancelQ.setBackgroundResource(btnCancelColor)
        if (cancel == null)
            btnCancelQ.setOnClickListener { dismiss() }
        else btnCancelQ.setOnClickListener {
            cancel?.onClick(it)
            dismiss()
        }

        btnConfirmQ.text = btnTextConfirm
        btnConfirmQ.setBackgroundResource(btnConfirmColor)
        confirm?.apply {
            btnConfirmQ.setOnClickListener {
                this.onClick(it)
                dismiss()
            }
        }
    }

}