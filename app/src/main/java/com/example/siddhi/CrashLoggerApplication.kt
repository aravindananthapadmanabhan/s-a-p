package com.example.siddhi

import android.app.Application
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CrashLoggerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Save the previous handler so we can delegate to it later (avoid recursion)
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val log = sw.toString()
                val f = File(filesDir, "crash.log")
                f.writeText(log)
            } catch (_: Exception) {
                // swallow
            }
            // delegate to previous handler to allow system to handle the crash
            previousHandler?.uncaughtException(thread, throwable)
        }
    }
}
