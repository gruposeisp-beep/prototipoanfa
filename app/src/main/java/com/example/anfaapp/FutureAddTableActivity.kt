package com.example.anfaapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.LeagueTable
import com.example.anfaapp.model.Match
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FutureAddTableActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etTableTitle: EditText
    private lateinit var etTeam1: EditText
    private lateinit var etScore1: EditText
    private lateinit var etTeam2: EditText
    private lateinit var etScore2: EditText
    private lateinit var etMatchTime: EditText
    private lateinit var btnCreateTable: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_add_table)
        supportActionBar?.title = "Crear Tabla Futura"

        database = FirebaseDatabase.getInstance().getReference("futureTables")

        etTableTitle = findViewById(R.id.etTableTitle)
        etTeam1 = findViewById(R.id.etTeam1)
        etScore1 = findViewById(R.id.etScore1)
        etTeam2 = findViewById(R.id.etTeam2)
        etScore2 = findViewById(R.id.etScore2)
        etMatchTime = findViewById(R.id.etMatchTime)
        btnCreateTable = findViewById(R.id.btnCreateTable)

        btnCreateTable.setOnClickListener {
            createTableWithFirstMatch()
        }
    }

    private fun createTableWithFirstMatch() {
        val title = etTableTitle.text.toString().trim()
        val team1 = etTeam1.text.toString().trim()
        val score1Str = etScore1.text.toString().trim()
        val team2 = etTeam2.text.toString().trim()
        val score2Str = etScore2.text.toString().trim()
        val matchTime = etMatchTime.text.toString().trim()

        if (title.isEmpty() || team1.isEmpty() || score1Str.isEmpty() || team2.isEmpty() || score2Str.isEmpty() || matchTime.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val tableId = database.push().key
        if (tableId == null) {
            Toast.makeText(this, "Error al crear ID para la tabla", Toast.LENGTH_SHORT).show()
            return
        }

        val matchId = database.child(tableId).child("matches").push().key ?: return
        val score1 = score1Str.toIntOrNull() ?: 0
        val score2 = score2Str.toIntOrNull() ?: 0
        val firstMatch = Match(matchId, team1, score1, team2, score2, matchTime)

        val newTable = LeagueTable(
            id = tableId,
            title = title,
            matches = mutableMapOf(matchId to firstMatch)
        )

        database.child(tableId).setValue(newTable)
            .addOnSuccessListener {
                Toast.makeText(this, "Tabla futura '$title' creada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al crear la tabla: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

