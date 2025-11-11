package com.example.anfaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "ANFA App"

        auth = FirebaseAuth.getInstance()

        val btnGoToTables: Button = findViewById(R.id.btnGoToTables)
        val btnGoToFutureTables: Button = findViewById(R.id.btnGoToFutureTables)
        val btnGoToLiveMatches: Button = findViewById(R.id.btnGoToLiveMatches)
        val btnLogout: Button = findViewById(R.id.btnLogout)


        btnGoToTables.setOnClickListener {
            startActivity(Intent(this, TablesActivity::class.java))
        }

        btnGoToFutureTables.setOnClickListener {
            startActivity(Intent(this, FutureTablesActivity::class.java))
        }

        btnGoToLiveMatches.setOnClickListener {
            startActivity(Intent(this, ViewLiveMatchesActivity::class.java))
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}




