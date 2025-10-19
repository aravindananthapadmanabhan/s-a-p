package com.example.siddhi

import android.app.Application
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class CrashLoggerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

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
            // delegate to default handler to allow system to handle the crash
            val default = Thread.getDefaultUncaughtExceptionHandler()
            default?.uncaughtException(thread, throwable)
        }
    }
}
