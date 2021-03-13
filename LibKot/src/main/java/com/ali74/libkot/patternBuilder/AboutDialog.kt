package com.ali74.libkot.patternBuilder

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.ali74.libkot.R
import com.google.android.material.textview.MaterialTextView

class AboutDialog(private val context: Context) {



    private var mainDesc:String? = null
    private var address:String? = null
    private var phone :String? = null
    private var email :String? = null
    private var appVersion :String? = null

    @DrawableRes
    private var iconToolbar = android.R.drawable.sym_def_app_icon


    fun setDescription(Text: String) =
        apply { this.mainDesc = Text}

    fun setAddress(address: String) =
        apply { this.address = address }

    fun setPhone(phone: String) =
        apply {this.phone = phone}

    fun setEmail(email: String) =
        apply { this.email = email }

    fun setAppVersion(version: String) =
        apply { this.appVersion = version }

    fun setToolbarIcon(@DrawableRes icon: Int) =
        apply { this.iconToolbar = icon }



    fun create(): Dialog = Dialog(context).apply {
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_about)
        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
        setContentView(R.layout.dialog_about)
        setCancelable(false)


        val ivBackAbout by lazy { findViewById<AppCompatImageView>(R.id.ivBackAbout) }
        val titleLogo by lazy { findViewById<AppCompatImageView>(R.id.titleLogo) }
        val txtTel by lazy { findViewById<MaterialTextView>(R.id.txtTel) }
        val txtAddress by lazy { findViewById<MaterialTextView>(R.id.txtAddress) }
        val txtDesc by lazy { findViewById<AppCompatTextView>(R.id.txtDesc) }
        val txtEmail by lazy { findViewById<MaterialTextView>(R.id.txtEmail) }
        val txtVersion by lazy { findViewById<MaterialTextView>(R.id.txtVersion) }
        val linearWebsite by lazy { findViewById<LinearLayoutCompat>(R.id.linearWebsite) }

        ivBackAbout.setOnClickListener { dismiss() }

        txtDesc.text = mainDesc
        txtAddress.text = address
        txtTel.text = phone
        txtEmail.text = email
        txtVersion.text = appVersion
        titleLogo.setImageResource(iconToolbar)


        if (email == null) txtEmail.visibility=View.GONE

        txtTel.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${txtTel.text.toString().trim()}")))
        }

        txtEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            context.startActivity(Intent.createChooser(intent, "ارسال ایمیل با :"))
        }


        linearWebsite.setOnClickListener {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://rahkarpouya.ir/")))
        }


    }





}
