package com.ali74.libkot.crashlytics

import android.app.Activity
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize
import java.lang.reflect.Modifier

@Parcelize
class CrashConfig : Parcelable {

    @IntDef(BACKGROUND_MODE_CRASH, BACKGROUND_MODE_SHOW_CUSTOM, BACKGROUND_MODE_SILENT)

    @Retention(AnnotationRetention.SOURCE)

    private annotation class BackgroundMode

    internal var backgroundMode = BACKGROUND_MODE_SHOW_CUSTOM
    private var enabled = true
    private var showErrorDetails = true
    private var showRestartButton = true
    private var logErrorOnRestart = true
    private var trackActivities = true
    internal var minTimeBetweenCrashesMs = 3000
    private var errorDrawable: Int? = null
    internal var errorActivityClass: Class<*>? = null
    internal var restartActivityClass: Class<*>? = null
    internal var eventListener: CustomActivityOnCrash.EventListener? = null

    @BackgroundMode
    fun getBackgroundMode(): Int = backgroundMode

    fun setBackgroundMode(@BackgroundMode backgroundMode: Int) {
        this.backgroundMode = backgroundMode
    }

    fun isEnabled(): Boolean = enabled

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isShowErrorDetails(): Boolean = showErrorDetails

    fun setShowErrorDetails(showErrorDetails: Boolean) {
        this.showErrorDetails = showErrorDetails
    }

    fun isShowRestartButton(): Boolean = showRestartButton

    fun setShowRestartButton(showRestartButton: Boolean) {
        this.showRestartButton = showRestartButton
    }

    fun isLogErrorOnRestart(): Boolean = logErrorOnRestart

    fun setLogErrorOnRestart(logErrorOnRestart: Boolean) {
        this.logErrorOnRestart = logErrorOnRestart
    }

    fun isTrackActivities(): Boolean = trackActivities

    fun setTrackActivities(trackActivities: Boolean) {
        this.trackActivities = trackActivities
    }

    fun getMinTimeBetweenCrashesMs(): Int = minTimeBetweenCrashesMs

    fun setMinTimeBetweenCrashesMs(minTimeBetweenCrashesMs: Int) {
        this.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs
    }

    @Nullable
    @DrawableRes
    fun getErrorDrawable(): Int? = errorDrawable

    fun setErrorDrawable(@Nullable @DrawableRes errorDrawable: Int?) {
        this.errorDrawable = errorDrawable
    }

    @Nullable
    fun getErrorActivityClass(): Class<*>? = errorActivityClass

    fun setErrorActivityClass(@Nullable errorActivityClass: Class<out Activity?>?) {
        this.errorActivityClass = errorActivityClass
    }

    @Nullable
    fun getRestartActivityClass(): Class<*>? = restartActivityClass

    fun setRestartActivityClass(@Nullable restartActivityClass: Class<*>?) {
        this.restartActivityClass = restartActivityClass
    }

    @Nullable
    fun getEventListener(): CustomActivityOnCrash.EventListener? = eventListener

    fun setEventListener(@Nullable eventListener: CustomActivityOnCrash.EventListener?) {
        this.eventListener = eventListener
    }

    class Builder {
        private var config: CrashConfig? = null

        @NonNull
        fun backgroundMode(@BackgroundMode backgroundMode: Int): Builder {
            config!!.backgroundMode = backgroundMode
            return this
        }

        @NonNull
        fun enabled(enabled: Boolean): Builder {
            config!!.enabled = enabled
            return this
        }

        @NonNull
        fun showErrorDetails(showErrorDetails: Boolean): Builder {
            config!!.showErrorDetails = showErrorDetails
            return this
        }

        @NonNull
        fun showRestartButton(showRestartButton: Boolean): Builder {
            config!!.showRestartButton = showRestartButton
            return this
        }

        @NonNull
        fun logErrorOnRestart(logErrorOnRestart: Boolean): Builder {
            config!!.logErrorOnRestart = logErrorOnRestart
            return this
        }

        @NonNull
        fun trackActivities(trackActivities: Boolean): Builder {
            config!!.trackActivities = trackActivities
            return this
        }

        @NonNull
        fun minTimeBetweenCrashesMs(minTimeBetweenCrashesMs: Int): Builder {
            config!!.minTimeBetweenCrashesMs = minTimeBetweenCrashesMs
            return this
        }

        @NonNull
        fun errorDrawable(@Nullable @DrawableRes errorDrawable: Int?): Builder {
            config!!.errorDrawable = errorDrawable
            return this
        }

        @NonNull
        fun errorActivity(@Nullable errorActivityClass: Class<out Activity?>?): Builder {
            config!!.errorActivityClass = errorActivityClass
            return this
        }

        @NonNull
        fun restartActivity(@Nullable restartActivityClass: Class<out Activity?>?): Builder {
            config!!.restartActivityClass = restartActivityClass
            return this
        }

        @NonNull
        fun eventListener(@Nullable eventListener: CustomActivityOnCrash.EventListener?): Builder {
            require(
                !(eventListener != null && eventListener.javaClass.enclosingClass != null && !Modifier.isStatic(
                    eventListener.javaClass.modifiers
                ))
            ) { "The event listener cannot be an inner or anonymous class, because it will need to be serialized. Change it to a class of its own, or make it a static inner class." }
            config!!.eventListener = eventListener
            return this
        }

        @NonNull
        fun get(): CrashConfig? = config

        fun apply() {
            CustomActivityOnCrash.setConfig(config!!)
        }

        companion object {
            @NonNull
            fun create(): Builder {
                val builder =
                    Builder()
                val currentConfig =
                    CustomActivityOnCrash.getConfig()
                val config = CrashConfig()
                config.backgroundMode = currentConfig.backgroundMode
                config.enabled = currentConfig.enabled
                config.showErrorDetails = currentConfig.showErrorDetails
                config.showRestartButton = currentConfig.showRestartButton
                config.logErrorOnRestart = currentConfig.logErrorOnRestart
                config.trackActivities = currentConfig.trackActivities
                config.minTimeBetweenCrashesMs = currentConfig.minTimeBetweenCrashesMs
                config.errorDrawable = currentConfig.errorDrawable
                config.errorActivityClass = currentConfig.errorActivityClass
                config.restartActivityClass = currentConfig.restartActivityClass
                config.eventListener = currentConfig.eventListener
                builder.config = config
                return builder
            }
        }
    }

    companion object {
        const val BACKGROUND_MODE_SILENT: Int = 0
        const val BACKGROUND_MODE_SHOW_CUSTOM: Int = 1
        const val BACKGROUND_MODE_CRASH = 2
    }
}