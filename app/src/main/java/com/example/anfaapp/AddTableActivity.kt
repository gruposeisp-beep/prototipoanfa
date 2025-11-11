package com.example.anfaapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.Match
import com.google.firebase.database.*

class AddTableActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etNewTableTitle: EditText
    private lateinit var etTeam1: EditText
    private lateinit var etScore1: EditText
    private lateinit var etTeam2: EditText
    private lateinit var etScore2: EditText
    private lateinit var etMatchTime: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_table)

        supportActionBar?.title = "Nueva Tabla con Partido"

        database = FirebaseDatabase.getInstance().getReference("leagueTables")

        etNewTableTitle = findViewById(R.id.etNewTableTitle)
        etTeam1 = findViewById(R.id.etTeam1)
        etScore1 = findViewById(R.id.etScore1)
        etTeam2 = findViewById(R.id.etTeam2)
        etScore2 = findViewById(R.id.etScore2)
        etMatchTime = findViewById(R.id.etMatchTime)
        val btnSaveNewTable: Button = findViewById(R.id.btnSaveNewTable)

        btnSaveNewTable.setOnClickListener {
            saveTableAndFirstMatch()
        }
    }

    private fun saveTableAndFirstMatch() {
        val title = etNewTableTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "El título de la tabla no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val team1 = etTeam1.text.toString().trim()
        val score1Str = etScore1.text.toString().trim()
        val team2 = etTeam2.text.toString().trim()
        val score2Str = etScore2.text.toString().trim()
        val matchTime = etMatchTime.text.toString().trim()

        if (team1.isEmpty() || score1Str.isEmpty() || team2.isEmpty() || score2Str.isEmpty() || matchTime.isEmpty()) {
            Toast.makeText(this, "Para crear una tabla, debes añadir también su primer partido. Completa todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        database.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@AddTableActivity, "Error: Ya existe una tabla con ese nombre", Toast.LENGTH_SHORT).show()
                } else {
                    createAndSaveData(title, team1, score1Str, team2, score2Str, matchTime)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddTableActivity, "Error de base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createAndSaveData(title: String, team1: String, score1Str: String, team2: String, score2Str: String, matchTime: String) {
        val tableId = database.push().key
        if (tableId == null) {
            Toast.makeText(this, "No se pudo generar un ID para la tabla.", Toast.LENGTH_SHORT).show()
            return
        }

        val tableData = mapOf(
            "id" to tableId,
            "title" to title
        )
        database.child(tableId).updateChildren(tableData)

        val matchId = database.child(tableId).child("matches").push().key ?: return
        val score1 = score1Str.toIntOrNull() ?: 0
        val score2 = score2Str.toIntOrNull() ?: 0
        val firstMatch = Match(matchId, team1, score1, team2, score2, matchTime)

        database.child(tableId).child("matches").child(matchId).setValue(firstMatch)
            .addOnSuccessListener {
                Toast.makeText(this, "Tabla '$title' y su primer partido creados con éxito", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar el partido: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
