package com.example.anfaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FutureTablesDashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_screen)
        supportActionBar?.title = "Gestión de Tablas Futuras"

        val btnGoToCreateFutureTable: Button = findViewById(R.id.btnGoToCreateFutureTable)
        val btnGoToAddFutureMatch: Button = findViewById(R.id.btnGoToAddFutureMatch)
        val btnGoToTables: Button = findViewById(R.id.btnGoToTables)
        val btnGoToDeleteFutureTable: Button = findViewById(R.id.btnGoToDeleteFutureTable)

        btnGoToCreateFutureTable.setOnClickListener {
            startActivity(Intent(this, FutureAddTableActivity::class.java))
        }

        btnGoToAddFutureMatch.setOnClickListener {
            startActivity(Intent(this, FutureAddMatchActivity::class.java))
        }

        btnGoToDeleteFutureTable.setOnClickListener {
            startActivity(Intent(this, FutureDeleteTableActivity::class.java))
        }

        btnGoToTables.setOnClickListener {
            startActivity(Intent(this, FutureTablesActivity::class.java))
        }
    }
}
