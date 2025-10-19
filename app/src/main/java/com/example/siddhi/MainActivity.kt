package com.example.siddhi

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If a crash log exists from a previous run, show it so we can debug.
        try {
            val f = File(filesDir, "crash.log")
            if (f.exists()) {
                val text = f.readText()
                showCrashDialog(text)
                f.delete()
            }
        } catch (_: Exception) {
        }
    }

    private fun showCrashDialog(text: String) {
        val tv = TextView(this).apply {
            setTextIsSelectable(true)
            setPadding(16, 16, 16, 16)
            textSize = 12f
            setText(text)
        }
        val sv = ScrollView(this).apply { addView(tv) }

        AlertDialog.Builder(this)
            .setTitle("Previous crash log")
            .setView(sv)
            .setPositiveButton("OK", null)
            .show()
    }
}
