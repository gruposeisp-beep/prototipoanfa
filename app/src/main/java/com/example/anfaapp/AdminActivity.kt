package com.example.anfaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        supportActionBar?.title = "Acceso de Administrador"

        auth = FirebaseAuth.getInstance()

        val btnGoToAdminDashboard: Button = findViewById(R.id.btnGoToAdminDashboard)
        val btnGoToNewScreen: Button = findViewById(R.id.btnGoToNewScreen)
        val btnGoToAddLiveMatch: Button = findViewById(R.id.btnGoToAddLiveMatch)
        val btnLogoutAdmin: Button = findViewById(R.id.btnLogoutAdmin)


        btnGoToAdminDashboard.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        }

        btnGoToNewScreen.setOnClickListener {
            startActivity(Intent(this, FutureTablesDashboard::class.java))
        }

        btnGoToAddLiveMatch.setOnClickListener {
            startActivity(Intent(this, LiveMatchDashboardActivity::class.java))
        }

        btnLogoutAdmin.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}











