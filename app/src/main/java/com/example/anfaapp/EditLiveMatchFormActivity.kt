package com.example.anfaapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.Match
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class EditLiveMatchFormActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var matchId: String? = null
    private lateinit var etEditTeam1: TextInputEditText
    private lateinit var etEditTeam2: TextInputEditText
    private lateinit var etEditScore1: TextInputEditText
    private lateinit var etEditScore2: TextInputEditText
    private lateinit var etEditMatchTime: TextInputEditText
    private lateinit var btnSaveChanges: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_live_match_form)
        supportActionBar?.title = "Formulario de Edición"

        matchId = intent.getStringExtra("MATCH_ID")
        if (matchId == null) {
            Toast.makeText(this, "Error: No se encontró el ID del partido.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("liveMatches").child(matchId!!)
        
        etEditTeam1 = findViewById(R.id.etEditTeam1)
        etEditTeam2 = findViewById(R.id.etEditTeam2)
        etEditScore1 = findViewById(R.id.etEditScore1)
        etEditScore2 = findViewById(R.id.etEditScore2)
        etEditMatchTime = findViewById(R.id.etEditMatchTime)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

        loadMatchData()

        btnSaveChanges.setOnClickListener {
            saveMatchChanges()
        }
    }

    private fun loadMatchData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val match = snapshot.getValue(Match::class.java)
                if (match != null) {
                    etEditTeam1.setText(match.team1)
                    etEditTeam2.setText(match.team2)
                    etEditScore1.setText(match.score1.toString())
                    etEditScore2.setText(match.score2.toString())
                    etEditMatchTime.setText(match.matchTime)
                } else {
                    Toast.makeText(this@EditLiveMatchFormActivity, "No se pudieron cargar los datos del partido.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditLiveMatchFormActivity, "Error al cargar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveMatchChanges() {
        val team1 = etEditTeam1.text.toString().trim()
        val team2 = etEditTeam2.text.toString().trim()
        val score1 = etEditScore1.text.toString().toIntOrNull() ?: 0
        val score2 = etEditScore2.text.toString().toIntOrNull() ?: 0
        val matchTime = etEditMatchTime.text.toString().trim()

        if (team1.isEmpty() || team2.isEmpty() || matchTime.isEmpty()) {
            Toast.makeText(this, "Los nombres de equipo y el tiempo no pueden estar vacíos.", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "team1" to team1,
            "team2" to team2,
            "score1" to score1,
            "score2" to score2,
            "matchTime" to matchTime
        )

        database.updateChildren(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Partido actualizado con éxito.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
