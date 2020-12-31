package com.ali74.libkot.extension

import androidx.databinding.BindingAdapter
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ali74.libkot.recyclerview.RVDividerItemDecoration
import com.ali74.libkot.utils.addSeparatorText
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object CustomDataBinding {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun AppCompatImageView.loadImage(Picture: String?) {
        if (!Picture.isNullOrEmpty()) {
            Glide.with(this.context).load(Picture)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).into(this)
        }
    }

    @JvmStatic
    @BindingAdapter("paintFlag")
    fun AppCompatTextView.setPaintFlag(use: Boolean = false) {
        if (use)
            this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    @JvmStatic
    @BindingAdapter("onOkInSoftKeyboard")
    fun AppCompatEditText.setOnOkInSoftKeyboardListener(listener: View.OnClickListener) {
        this.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                listener.onClick(v)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["price", "showCurrency", "currency"])
    fun AppCompatTextView.textBySeparator(price: Any, showRial: Boolean = false, currency: String) {
        this.text = addSeparatorText(price, showRial, currency)
    }

    @JvmStatic
    @BindingAdapter("tintColorText")
    fun AppCompatImageView.tintData(data: String) {
        this.setColorFilter(Color.parseColor(data))
    }

    @JvmStatic
    @BindingAdapter(value = ["itemDecoration", "marginDecoration"])
    fun RecyclerView.itemDecoration(statue: String, margin: Int = 0) {
        val orientation = if (statue == "vertical")
            LinearLayoutManager.VERTICAL
        else LinearLayoutManager.HORIZONTAL

        this.addItemDecoration(
            RVDividerItemDecoration(
                orientation,
                margin,
                margin,
            )
        )
    }
}