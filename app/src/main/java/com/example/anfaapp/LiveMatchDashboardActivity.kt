package com.example.anfaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LiveMatchDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_match_dashboard)
        supportActionBar?.title = "Gestión de Partidos en Vivo"

        val btnGoToCreateLiveMatch: Button = findViewById(R.id.btnGoToCreateLiveMatch)
        val btnGoToViewLiveMatches: Button = findViewById(R.id.btnGoToViewLiveMatches)
        val btnGoToEditLiveMatch: Button = findViewById(R.id.btnGoToEditLiveMatch)
        val btnGoToDeleteLiveMatch: Button = findViewById(R.id.btnGoToDeleteLiveMatch)

        btnGoToCreateLiveMatch.setOnClickListener {
            startActivity(Intent(this, CreateLiveMatchActivity::class.java))
        }

        btnGoToViewLiveMatches.setOnClickListener {
            startActivity(Intent(this, ViewLiveMatchesActivity::class.java))
        }

        btnGoToEditLiveMatch.setOnClickListener {
            val intent = Intent(this, EditLiveMatchListActivity::class.java)
            startActivity(intent)
        }

        btnGoToDeleteLiveMatch.setOnClickListener {
            val intent = Intent(this, DeleteLiveMatchActivity::class.java)
            startActivity(intent)
        }
    }
}



