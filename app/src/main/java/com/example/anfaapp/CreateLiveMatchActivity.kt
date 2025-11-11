package com.example.anfaapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.Match
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateLiveMatchActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etLiveTeam1: TextInputEditText
    private lateinit var etLiveScore1: TextInputEditText
    private lateinit var etLiveTeam2: TextInputEditText
    private lateinit var etLiveScore2: TextInputEditText
    private lateinit var etLiveMatchTime: TextInputEditText
    private lateinit var btnSaveLiveMatch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_live_match)
        supportActionBar?.title = "Crear Partido en Vivo"

        database = FirebaseDatabase.getInstance().getReference("liveMatches")

        etLiveTeam1 = findViewById(R.id.etLiveTeam1)
        etLiveScore1 = findViewById(R.id.etLiveScore1)
        etLiveTeam2 = findViewById(R.id.etLiveTeam2)
        etLiveScore2 = findViewById(R.id.etLiveScore2)
        etLiveMatchTime = findViewById(R.id.etLiveMatchTime)
        btnSaveLiveMatch = findViewById(R.id.btnSaveLiveMatch)

        btnSaveLiveMatch.setOnClickListener {
            saveLiveMatch()
        }
    }

    private fun saveLiveMatch() {
        val team1 = etLiveTeam1.text.toString().trim()
        val score1Str = etLiveScore1.text.toString().trim()
        val team2 = etLiveTeam2.text.toString().trim()
        val score2Str = etLiveScore2.text.toString().trim()
        val matchTime = etLiveMatchTime.text.toString().trim()

        if (team1.isEmpty() || score1Str.isEmpty() || team2.isEmpty() || score2Str.isEmpty() || matchTime.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val matchId = database.push().key
        if (matchId == null) {
            Toast.makeText(this, "Error al generar ID para el partido", Toast.LENGTH_SHORT).show()
            return
        }

        val score1 = score1Str.toIntOrNull() ?: 0
        val score2 = score2Str.toIntOrNull() ?: 0
        val liveMatch = Match(matchId, team1, score1, team2, score2, matchTime)

        database.child(matchId).setValue(liveMatch)
            .addOnSuccessListener {
                Toast.makeText(this, "Partido en vivo guardado con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar el partido: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
    