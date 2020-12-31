package com.ali74.libkot.ui

import android.os.Bundle
import androidx.annotation.Nullable
import com.ali74.libkot.BindingActivity
import com.ali74.libkot.R
import com.ali74.libkot.crashlytics.CustomActivityOnCrash
import com.ali74.libkot.databinding.CrashBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DefaultErrorActivity : BindingActivity<CrashBinding>() {

    override fun getLayoutResId(): Int = R.layout.activity_default_error

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = CustomActivityOnCrash.getConfigFromIntent(intent)

        binding.btnRestartCrashReport.setOnClickListener {
            CustomActivityOnCrash.restartApplication(this, config)
        }

        binding.btnCloseCrashReport.setOnClickListener {
            CustomActivityOnCrash.closeApplication(this, config)
        }

        val error = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, intent)

        MaterialAlertDialogBuilder(this)
            .setTitle("AppError")
            .setNeutralButton("close") { dialog, _ -> dialog.dismiss() }
            .setMessage(error.UserAction + "\n\n" + error.StackTrace)
            .create()
            .show()

    }


}
