package com.example.anfaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)
        supportActionBar?.title = "Panel de Admin"

        val btnGoToCreateTable: Button = findViewById(R.id.btnGoToCreateTable)
        val btnGoToAddMatch: Button = findViewById(R.id.btnGoToAddMatch)
        val btnGoToTables: Button = findViewById(R.id.btnGoToTables)
        val btnGoToDeleteTable: Button = findViewById(R.id.btnGoToDeleteTable)

        btnGoToCreateTable.setOnClickListener {
            startActivity(Intent(this, AddTableActivity::class.java))
        }

        btnGoToAddMatch.setOnClickListener {
            startActivity(Intent(this, AddMatchActivity::class.java))
        }

        btnGoToTables.setOnClickListener {
            startActivity(Intent(this, TablesActivity::class.java))
        }

        btnGoToDeleteTable.setOnClickListener {
            startActivity(Intent(this, DeleteTableActivity::class.java))
        }
    }
}
