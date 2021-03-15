package com.ali74.libkot.patternBuilder

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.ali74.libkot.R
import com.ali74.libkot.utils.AppTheme
import com.google.android.material.snackbar.Snackbar

class SnackBarBuilder(private var message: String) {

    private var snackBar: Snackbar? = null

    private var actionText = ""
    private var snackFont = ""
    private var action: View.OnClickListener? = null
    private var duration = Snackbar.LENGTH_SHORT
    private var animation = Snackbar.ANIMATION_MODE_FADE
    private var anchor_View :View ? = null

    @ColorRes
    private var snackBackgroundColor = AppTheme.SnackBar.snackBackgroundColor

    @ColorRes
    private var snackMessageColor = AppTheme.SnackBar.snackMessageColor

    @ColorRes
    private var snackActionTextColor = AppTheme.SnackBar.snackActionTextColor

    @ColorRes
    private var snackActionBackgroundColor = AppTheme.SnackBar.snackActionBackgroundColor

    fun setMessage(message: String, @DrawableRes color: Int = AppTheme.SnackBar.snackMessageColor) =
        apply {
            this.message = message
            snackMessageColor = color
        }

    fun setActionText(text: String = "", @DrawableRes color: Int) = apply {
        actionText = text
        snackActionTextColor = color
    }

    fun setAction(action: View.OnClickListener? = null) = apply {
        this.action = action
    }

    fun setDuration(duration: Int) = apply {
        this.duration = duration
    }

    fun setFont(font: String) = apply {
        this.snackFont = font
    }

    fun setAnimation(animation: Int) = apply {
        this.animation = animation
    }

    fun setAnchorView(view:View) = apply {
        this.anchor_View = view
    }

    fun show(activity: Activity) {
        snackBar = if (action != null)
            Snackbar.make(
                activity.findViewById(android.R.id.content), message, duration
            ).setAction(actionText, action)
                .setAnimationMode(animation)
                .setBackgroundTint(ContextCompat.getColor(activity, snackBackgroundColor))
        else Snackbar.make(
            activity.findViewById(android.R.id.content), message, duration
        ).setAnimationMode(animation)
            .setBackgroundTint(ContextCompat.getColor(activity, snackBackgroundColor))

        snackBar?.apply {
            val text: TextView = view.findViewById(R.id.snackbar_text)
            val action: TextView = view.findViewById(R.id.snackbar_action)
            if (snackFont != "") {
                text.typeface = Typeface.createFromAsset(activity.assets, snackFont)
                action.setTypeface(
                    Typeface.createFromAsset(activity.assets, snackFont),
                    Typeface.BOLD
                )
            }
            action.setTextColor(ContextCompat.getColor(activity, snackActionTextColor))
            action.setBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    snackActionBackgroundColor
                )
            )

            text.setTextColor(ContextCompat.getColor(activity, snackMessageColor))
           if (anchorView != null ) anchorView = anchor_View


        }

        snackBar?.apply {
            if (!isShown)
                show()
        }
    }

    fun show(view: View) {
        snackBar = if (action != null){
            Snackbar.make(view, message, duration).setAction(actionText, action)
                .setAnimationMode(animation)
                .setBackgroundTint(ContextCompat.getColor(view.context, snackBackgroundColor))

        } else{
            Snackbar.make(view, message, duration).setAnimationMode(animation)
            .setBackgroundTint(ContextCompat.getColor(view.context, snackBackgroundColor))
        }



        snackBar?.apply {
            val text: TextView = view.findViewById(R.id.snackbar_text)
            val action: TextView = view.findViewById(R.id.snackbar_action)
            if (snackFont != "") {
                text.typeface = Typeface.createFromAsset(view.context.assets, snackFont)
                action.setTypeface(
                    Typeface.createFromAsset(view.context.assets, snackFont),
                    Typeface.BOLD
                )
            }
            action.setTextColor(ContextCompat.getColor(view.context, snackActionTextColor))
            action.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    snackActionBackgroundColor
                )
            )

            text.setTextColor(ContextCompat.getColor(view.context, snackMessageColor))
            if (anchorView != null ) anchorView = anchor_View

        }

        snackBar?.apply {
            if (!isShown)
                show()
        }
    }
}