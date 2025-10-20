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
        // Wire buttons to open the respective screens
        findViewById<android.widget.Button>(R.id.btnUpdate).setOnClickListener {
            startActivity(android.content.Intent(this, UpdateActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnAudit).setOnClickListener {
            startActivity(android.content.Intent(this, AuditActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnSearch).setOnClickListener {
            startActivity(android.content.Intent(this, SearchActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnProperties).setOnClickListener {
            startActivity(android.content.Intent(this, PropertiesActivity::class.java))
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
