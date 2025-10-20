package com.example.siddhi

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Wire buttons to open the respective screens
        findViewById<android.widget.Button>(R.id.btn_update).setOnClickListener {
            startActivity(android.content.Intent(this, UpdateActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btn_audit).setOnClickListener {
            startActivity(android.content.Intent(this, AuditActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btn_search).setOnClickListener {
            startActivity(android.content.Intent(this, SearchActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btn_properties).setOnClickListener {
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
