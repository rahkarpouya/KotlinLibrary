package com.ali74.libkot.crashlytics

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.ali74.libkot.ui.DefaultErrorActivity
import java.io.PrintWriter
import java.io.Serializable
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipFile
import kotlin.system.exitProcess

object CustomActivityOnCrash {
    private const val TAG = "CustomActivityOnCrash"

    //Extras passed to the error activity
    private const val EXTRA_CONFIG = "EXTRA_CONFIG"
    private const val EXTRA_STACK_TRACE = "EXTRA_STACK_TRACE"
    private const val EXTRA_ACTIVITY_LOG = "EXTRA_ACTIVITY_LOG"

    //General constants
    private const val INTENT_ACTION_ERROR_ACTIVITY = "ERROR"
    private const val INTENT_ACTION_RESTART_ACTIVITY = "RESTART"
    private const val CALC_HANDLER_PACKAGE_NAME = "customActivityOnCrash"
    private const val DEFAULT_HANDLER_PACKAGE_NAME = "com.android.internal.os"
    private const val MAX_STACK_TRACE_SIZE = 131071 //128 KB - 1
    private const val MAX_ACTIVITIES_IN_LOG = 50

    //Shared preferences
    private const val SHARED_PREFERENCES_FILE = "custom_activity_on_crash"
    private const val SHARED_PREFERENCES_FIELD_TIMESTAMP = "last_crash_timestamp"

    //Internal variables
    @SuppressLint("StaticFieldLeak") //This is an application-wide component
    private var application: Application? = null
    private var config = CrashConfig()
    private val activityLog: Deque<String> = ArrayDeque(MAX_ACTIVITIES_IN_LOG)
    private var lastActivityCreated = WeakReference<Activity?>(null)
    private var isInBackground = true

    /**
     * Installs CustomActivityOnCrash on the application using the default error activity.
     *
     * @param context Context to use for obtaining the ApplicationContext. Must not be null.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun install(@Nullable context: Context?) {
        try {
            if (context == null) {
                Log.e(TAG, "Install failed: context is null!")
            } else { //INSTALL!
                val oldHandler = Thread.getDefaultUncaughtExceptionHandler()
                if (oldHandler != null && oldHandler.javaClass.name.startsWith(
                        CALC_HANDLER_PACKAGE_NAME
                    )
                ) {
                    Log.e(TAG, "CustomActivityOnCrash was already installed, doing nothing!")
                } else {
                    if (oldHandler != null && !oldHandler.javaClass.name.startsWith(
                            DEFAULT_HANDLER_PACKAGE_NAME
                        )
                    ) {
                        Log.e(
                            TAG,
                            "IMPORTANT WARNING! You already have an UncaughtExceptionHandler, are you sure this is correct? If you use a custom UncaughtExceptionHandler, you must initialize it AFTER CustomActivityOnCrash! Installing anyway, but your original handler will not be called."
                        )
                    }
                    application = context.applicationContext as Application
                    //We define a default exception handler that does what we want so it can be called from Crashlytics/ACRA
                    Thread.setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler { thread, throwable ->
                        if (config.isEnabled()) {
                            Log.e(
                                TAG,
                                "App has crashed, executing CustomActivityOnCrash's UncaughtExceptionHandler",
                                throwable
                            )
                            if (hasCrashedInTheLastSeconds(application)) {
                                Log.e(
                                    TAG,
                                    "App already crashed recently, not starting custom error activity because we could enter a restart loop. Are you sure that your app does not crash directly on init?",
                                    throwable
                                )
                                if (oldHandler != null) {
                                    oldHandler.uncaughtException(thread, throwable)
                                    return@UncaughtExceptionHandler
                                }
                            } else {
                                setLastCrashTimestamp(application, Date().time)
                                var errorActivityClass = config.getErrorActivityClass()
                                if (errorActivityClass == null)
                                    errorActivityClass = guessErrorActivityClass(application)
                                if (isStackTraceLikelyConflictive(
                                        throwable,
                                        errorActivityClass
                                    )
                                ) {
                                    Log.e(
                                        TAG,
                                        "Your application class or your error activity have crashed, the custom activity will not be launched!"
                                    )
                                    if (oldHandler != null) {
                                        oldHandler.uncaughtException(thread, throwable)
                                        return@UncaughtExceptionHandler
                                    }
                                } else
                                if (config.getBackgroundMode() == CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM || !isInBackground) {
                                    val intent = Intent(application, errorActivityClass)
                                    val sw = StringWriter()
                                    val pw = PrintWriter(sw)
                                    throwable.printStackTrace(pw)
                                    var stackTraceString = sw.toString()
                                    //Reduce data to 128KB so we don't get a TransactionTooLargeException when sending the intent.
                                    //The limit is 1MB on Android but some devices seem to have it lower.
                                    //See: http://developer.android.com/reference/android/os/TransactionTooLargeException.html
                                    //And: http://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception#comment46697371_12809171
                                    if (stackTraceString.length > MAX_STACK_TRACE_SIZE) {
                                        val disclaimer = "[stack trace too large]"
                                        stackTraceString = stackTraceString.substring(
                                            0,
                                            MAX_STACK_TRACE_SIZE - disclaimer.length
                                        ) + disclaimer
                                    }
                                    intent.putExtra(EXTRA_STACK_TRACE, stackTraceString)
                                    if (config.isTrackActivities()) {
                                        val activityLogStringBuilder = StringBuilder()
                                        while (!activityLog.isEmpty())
                                            activityLogStringBuilder.append(activityLog.poll())

                                        intent.putExtra(
                                            EXTRA_ACTIVITY_LOG,
                                            activityLogStringBuilder.toString()
                                        )
                                    }
                                    if (config.isShowRestartButton() && config.getRestartActivityClass() == null)
                                        config.setRestartActivityClass(
                                            guessRestartActivityClass(
                                                application
                                            )
                                        )

                                    intent.putExtra(EXTRA_CONFIG, config)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    if (config.getEventListener() != null)
                                        config.getEventListener()!!.onLaunchErrorActivity()

                                    application!!.startActivity(intent)
                                } else if (config.getBackgroundMode() == CrashConfig.BACKGROUND_MODE_CRASH) {
                                    if (oldHandler != null) {
                                        oldHandler.uncaughtException(thread, throwable)
                                        return@UncaughtExceptionHandler
                                    }
                                    //If it is null (should not be), we let it continue and kill the process or it will be stuck
                                }
                                //Else (BACKGROUND_MODE_SILENT): do nothing and let the following code kill the process
                            }
                            val lastActivity = lastActivityCreated.get()
                            if (lastActivity != null) {
                                //See: https://github.com/ACRA/acra/issues/42
                                lastActivity.finish()
                                lastActivityCreated.clear()
                            }
                            killCurrentProcess()
                        } else oldHandler?.uncaughtException(thread, throwable)
                    })
                    application!!.registerActivityLifecycleCallbacks(
                        object : Application.ActivityLifecycleCallbacks {
                            var currentlyStartedActivities = 0
                            val dateFormat: DateFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

                            override fun onActivityCreated(
                                activity: Activity,
                                savedInstanceState: Bundle?
                            ) {
                                if (activity.javaClass != config.getErrorActivityClass())
                                    lastActivityCreated = WeakReference(activity)

                                if (config.isTrackActivities()) {
                                    activityLog.add(
                                        dateFormat.format(Date()) + ": " + activity.javaClass.simpleName + " created\n"
                                    )
                                }
                            }

                            override fun onActivityStarted(activity: Activity) {
                                currentlyStartedActivities++
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivityResumed(activity: Activity) {
                                if (config.isTrackActivities()) {
                                    activityLog.add(
                                        dateFormat.format(Date()) + ": " + activity.javaClass.simpleName + " resumed\n"
                                    )
                                }
                            }

                            override fun onActivityPaused(activity: Activity) {
                                if (config.isTrackActivities()) {
                                    activityLog.add(
                                        dateFormat.format(Date()) + ": " + activity.javaClass.simpleName + " paused\n"
                                    )
                                }
                            }

                            override fun onActivityStopped(activity: Activity) { //Do nothing
                                currentlyStartedActivities--
                                isInBackground = currentlyStartedActivities == 0
                            }

                            override fun onActivitySaveInstanceState(
                                activity: Activity,
                                p1: Bundle
                            ) {
                            }

                            override fun onActivityDestroyed(activity: Activity) {
                                if (config.isTrackActivities()) {
                                    activityLog.add(
                                        dateFormat.format(Date()) + ": " + activity.javaClass.simpleName + " destroyed\n"
                                    )
                                }
                            }
                        })
                }
                Log.i(TAG, "CustomActivityOnCrash has been installed.")
            }
        } catch (t: Throwable) {
            Log.e(
                TAG,
                "An unknown error occurred while installing CustomActivityOnCrash, it may not have been properly initialized. Please report this as a bug if needed.",
                t
            )
        }
    }

    @Nullable
    fun getStackTraceFromIntent(@NonNull intent: Intent): String? {
        return intent.getStringExtra(EXTRA_STACK_TRACE)
    }


    @Nullable
    fun getConfigFromIntent(@NonNull intent: Intent): CrashConfig {
        val config = intent.getSerializableExtra(EXTRA_CONFIG) as CrashConfig
        if (config.isLogErrorOnRestart()) {
            val stackTrace = getStackTraceFromIntent(intent)
            if (stackTrace != null) {
                Log.e(
                    TAG,
                    "The previous app process crashed. This is the stack trace of the crash:\n" + getStackTraceFromIntent(
                        intent
                    )
                )
            }
        }
        return config
    }

    @Nullable
    fun getActivityLogFromIntent(@NonNull intent: Intent): String? =
        intent.getStringExtra(EXTRA_ACTIVITY_LOG)

    @NonNull
    fun getAllErrorDetailsFromIntent(
        @NonNull context: Context,
        @NonNull intent: Intent
    ): CrashReport {

        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val currentDate = dateFormat.format(Date())
        //Get build date
        val buildDateAsString = getBuildDateAsString(context, dateFormat)
        //Get app version
        val versionName = getVersionName(context)
        //Get User Action
        val activityLog = getActivityLogFromIntent(intent)

        return CrashReport(
            versionName,
            deviceModelName + "_" + Build.BRAND + "_" + Build.DEVICE + "_" + Build.VERSION.SDK_INT,
            getStackTraceFromIntent(intent) ?: "",
            activityLog ?: "",
            buildDateAsString ?: "",
            currentDate
        )
    }

    private fun restartApplicationWithIntent(
        @NonNull activity: Activity,
        @NonNull intent: Intent,
        @NonNull config: CrashConfig
    ) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        if (intent.component != null) {
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
        }
        if (config.getEventListener() != null) {
            config.getEventListener()!!.onRestartAppFromErrorActivity()
        }
        activity.finish()
        activity.startActivity(intent)
        killCurrentProcess()
    }

    fun restartApplication(@NonNull activity: Activity, @NonNull config: CrashConfig) {
        val intent = Intent(activity, config.getRestartActivityClass())
        restartApplicationWithIntent(
            activity,
            intent,
            config
        )
    }

    fun closeApplication(@NonNull activity: Activity, @NonNull config: CrashConfig) {
        if (config.getEventListener() != null)
            config.getEventListener()?.onCloseAppFromErrorActivity()

        activity.finish()
        killCurrentProcess()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @NonNull
    fun getConfig(): CrashConfig = config

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun setConfig(@NonNull config: CrashConfig) {
        CustomActivityOnCrash.config = config
    }

    private fun isStackTraceLikelyConflictive(
        @NonNull th: Throwable,
        @NonNull activityClass: Class<*>?
    ): Boolean {
        var throwable = th
        do {
            val stackTrace = throwable.stackTrace
            for (element in stackTrace) {
                if (element.className == "android.app.ActivityThread" && element.methodName == "handleBindApplication" || element.className == activityClass!!.name) {
                    return true
                }
            }
        } while (throwable.cause.also { throwable = it!! } != null)
        return false
    }

    @Nullable
    private fun getBuildDateAsString(
        @NonNull context: Context,
        @NonNull dateFormat: DateFormat
    ): String? {
        var buildDate: Long
        try {
            val ai =
                context.packageManager.getApplicationInfo(context.packageName, 0)
            val zf = ZipFile(ai.sourceDir)
            //If this failed, try with the old zip method
            val ze = zf.getEntry("classes.dex")
            buildDate = ze.time
            zf.close()
        } catch (e: Exception) {
            buildDate = 0
        }
        return if (buildDate > 312764400000L) {
            dateFormat.format(Date(buildDate))
        } else {
            null
        }
    }

    @NonNull
    private fun getVersionName(context: Context): String {
        return try {
            val packageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }

    @get:NonNull
    private val deviceModelName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer))
                capitalize(model)
            else
                capitalize(manufacturer) + " " + model

        }

    @NonNull
    private fun capitalize(@Nullable s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }

    @Nullable
    private fun guessRestartActivityClass(@NonNull context: Context?): Class<*>? {
        var resolvedActivityClass: Class<*>?
        //If action is defined, use that
        resolvedActivityClass =
            getRestartActivityClassWithIntentFilter(
                context
            )
        //Else, get the default launcher activity
        if (resolvedActivityClass == null) {
            resolvedActivityClass =
                getLauncherActivity(
                    context
                )
        }
        return resolvedActivityClass
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Nullable
    private fun getRestartActivityClassWithIntentFilter(@NonNull context: Context?): Class<*>? {
        val searchedIntent =
            Intent().setAction(INTENT_ACTION_RESTART_ACTIVITY)
                .setPackage(context!!.packageName)
        val resolveInfos =
            context.packageManager.queryIntentActivities(
                searchedIntent,
                PackageManager.GET_RESOLVED_FILTER
            )
        if (resolveInfos.size > 0) {
            val resolveInfo = resolveInfos[0]
            try {
                return Class.forName(resolveInfo.activityInfo.name)
            } catch (e: ClassNotFoundException) { //Should not happen, print it to the log!
                Log.e(
                    TAG,
                    "Failed when resolving the restart activity class via intent filter, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    @Nullable
    private fun getLauncherActivity(@NonNull context: Context?): Class<*>? {
        val intent =
            context!!.packageManager.getLaunchIntentForPackage(context.packageName)
        if (intent != null && intent.component != null) {
            try {
                return Class.forName(intent.component!!.className)
            } catch (e: ClassNotFoundException) { //Should not happen, print it to the log!
                Log.e(
                    TAG,
                    "Failed when resolving the restart activity class via getLaunchIntentForPackage, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    @NonNull
    private fun guessErrorActivityClass(@NonNull context: Context?): Class<*> {
        var resolvedActivityClass: Class<*>?
        //If action is defined, use that
        resolvedActivityClass = getErrorActivityClassWithIntentFilter(context)
        //Else, get the default error activity
        if (resolvedActivityClass == null)
            resolvedActivityClass = DefaultErrorActivity::class.java

        return resolvedActivityClass
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Nullable
    private fun getErrorActivityClassWithIntentFilter(@NonNull context: Context?): Class<*>? {
        val searchedIntent =
            Intent().setAction(INTENT_ACTION_ERROR_ACTIVITY)
                .setPackage(context!!.packageName)
        val resolveInfos =
            context.packageManager.queryIntentActivities(
                searchedIntent,
                PackageManager.GET_RESOLVED_FILTER
            )
        if (resolveInfos.size > 0) {
            val resolveInfo = resolveInfos[0]
            try {
                return Class.forName(resolveInfo.activityInfo.name)
            } catch (e: ClassNotFoundException) { //Should not happen, print it to the log!
                Log.e(
                    TAG,
                    "Failed when resolving the error activity class via intent filter, stack trace follows!",
                    e
                )
            }
        }
        return null
    }

    private fun killCurrentProcess() {
        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    @SuppressLint("ApplySharedPref") //This must be done immediately since we are killing the app
    private fun setLastCrashTimestamp(@NonNull context: Context?, timestamp: Long) {
        context!!.getSharedPreferences(
            SHARED_PREFERENCES_FILE,
            Context.MODE_PRIVATE
        ).edit().putLong(
            SHARED_PREFERENCES_FIELD_TIMESTAMP,
            timestamp
        ).commit()
    }

    private fun getLastCrashTimestamp(@NonNull context: Context?): Long {
        return context!!.getSharedPreferences(
            SHARED_PREFERENCES_FILE,
            Context.MODE_PRIVATE
        ).getLong(
            SHARED_PREFERENCES_FIELD_TIMESTAMP,
            -1
        )
    }

    private fun hasCrashedInTheLastSeconds(@NonNull context: Context?): Boolean {
        val lastTimestamp =
            getLastCrashTimestamp(
                context
            )
        val currentTimestamp = Date().time
        return lastTimestamp <= currentTimestamp && currentTimestamp - lastTimestamp < config.getMinTimeBetweenCrashesMs()
    }

    interface EventListener : Serializable {
        fun onLaunchErrorActivity()
        fun onRestartAppFromErrorActivity()
        fun onCloseAppFromErrorActivity()
    }
}