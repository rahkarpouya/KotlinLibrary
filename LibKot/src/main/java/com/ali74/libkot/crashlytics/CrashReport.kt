package com.ali74.libkot.crashlytics

data class CrashReport(
    var BuildVersion: String,
    var Device: String,
    var StackTrace: String,
    var UserAction: String,
    var BuildDate: String,
    var CurrentDate: String,
)