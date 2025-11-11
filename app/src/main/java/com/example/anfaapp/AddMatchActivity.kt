package com.example.anfaapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.LeagueTable
import com.example.anfaapp.model.Match
import com.google.firebase.database.*

class AddMatchActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var spinnerTables: Spinner
    private lateinit var etTeam1: EditText
    private lateinit var etScore1: EditText
    private lateinit var etTeam2: EditText
    private lateinit var etScore2: EditText
    private lateinit var etMatchTime: EditText
    private lateinit var btnSaveMatch: Button

    private val tableList = mutableListOf<LeagueTable>()
    private var selectedTable: LeagueTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_match)
        supportActionBar?.title = "Añadir Partido"

        database = FirebaseDatabase.getInstance().getReference("leagueTables")

        spinnerTables = findViewById(R.id.spinnerTables)
        etTeam1 = findViewById(R.id.etTeam1)
        etScore1 = findViewById(R.id.etScore1)
        etTeam2 = findViewById(R.id.etTeam2)
        etScore2 = findViewById(R.id.etScore2)
        etMatchTime = findViewById(R.id.etMatchTime)
        btnSaveMatch = findViewById(R.id.btnSaveMatch)

        setupSpinner()
        fetchTablesForSpinner()

        btnSaveMatch.setOnClickListener {
            saveMatchToSelectedTable()
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tableList.map { it.title })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTables.adapter = adapter

        spinnerTables.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position < tableList.size) {
                    selectedTable = tableList[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedTable = null
            }
        }
    }

    private fun fetchTablesForSpinner() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tableList.clear()
                val tableTitles = mutableListOf<String>()

                for (tableSnapshot in snapshot.children) {
                    val table = tableSnapshot.getValue(LeagueTable::class.java)
                    if (table != null && table.id != null && table.title != null) {
                        tableList.add(table)
                        tableTitles.add(table.title)
                    }
                }


                val adapter = ArrayAdapter(this@AddMatchActivity, android.R.layout.simple_spinner_item, tableTitles)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTables.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddMatchActivity, "Error al cargar tablas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveMatchToSelectedTable() {
        if (selectedTable == null) {
            Toast.makeText(this, "Por favor, selecciona una tabla", Toast.LENGTH_SHORT).show()
            return
        }
        val tableId = selectedTable!!.id!!
        val team1 = etTeam1.text.toString().trim()
        val score1Str = etScore1.text.toString().trim()
        val team2 = etTeam2.text.toString().trim()
        val score2Str = etScore2.text.toString().trim()
        val matchTime = etMatchTime.text.toString().trim()

        if (team1.isEmpty() || score1Str.isEmpty() || team2.isEmpty() || score2Str.isEmpty() || matchTime.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos del partido", Toast.LENGTH_SHORT).show()
            return
        }

        val matchId = database.child(tableId).child("matches").push().key ?: return
        val score1 = score1Str.toIntOrNull() ?: 0
        val score2 = score2Str.toIntOrNull() ?: 0
        val newMatch = Match(matchId, team1, score1, team2, score2, matchTime)

        database.child(tableId).child("matches").child(matchId).setValue(newMatch)
            .addOnSuccessListener {
                Toast.makeText(this, "Partido añadido a '${selectedTable!!.title}'", Toast.LENGTH_SHORT).show()
                clearMatchFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al guardar partido: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearMatchFields() {
        etTeam1.text.clear()
        etScore1.text.clear()
        etTeam2.text.clear()
        etScore2.text.clear()
        etMatchTime.text.clear()
        etTeam1.requestFocus()
    }
}
