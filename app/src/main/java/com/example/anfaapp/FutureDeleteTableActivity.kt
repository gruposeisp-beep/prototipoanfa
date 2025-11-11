package com.example.anfaapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.model.LeagueTable
import com.google.firebase.database.*

class FutureDeleteTableActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var rvDeleteTables: RecyclerView
    private lateinit var tablesAdapter: DeleteTableAdapter
    private val tableList = mutableListOf<LeagueTable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_delete_table)
        supportActionBar?.title = "Eliminar Tabla Futura"

        database = FirebaseDatabase.getInstance().getReference("futureTables")

        rvDeleteTables = findViewById(R.id.rvDeleteTables)
        rvDeleteTables.layoutManager = LinearLayoutManager(this)

        tablesAdapter = DeleteTableAdapter(tableList) { table ->
            showConfirmationDialog(table)
        }
        rvDeleteTables.adapter = tablesAdapter

        fetchTables()
    }

    private fun fetchTables() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tableList.clear()
                for (tableSnapshot in snapshot.children) {
                    val table = tableSnapshot.getValue(LeagueTable::class.java)
                    if (table != null && table.id != null) {
                        tableList.add(table)
                    }
                }
                tablesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FutureDeleteTableActivity, "Error al cargar las tablas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showConfirmationDialog(table: LeagueTable) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar la tabla futura '${table.title}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteTable(table)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteTable(table: LeagueTable) {
        if (table.id == null) return

        database.child(table.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Tabla futura '${table.title}' eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar la tabla", Toast.LENGTH_SHORT).show()
            }
    }
}

