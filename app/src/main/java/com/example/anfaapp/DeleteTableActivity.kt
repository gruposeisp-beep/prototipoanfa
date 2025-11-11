package com.example.anfaapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.model.LeagueTable
import com.google.firebase.database.*

class DeleteTableActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var rvDeleteTables: RecyclerView
    private lateinit var tablesAdapter: DeleteTableAdapter
    private val tableList = mutableListOf<LeagueTable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_table)
        supportActionBar?.title = "Eliminar Tabla"

        database = FirebaseDatabase.getInstance().getReference("leagueTables")
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
                Toast.makeText(this@DeleteTableActivity, "Error al cargar las tablas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showConfirmationDialog(table: LeagueTable) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar la tabla '${table.title}'? Esta acción no se puede deshacer.")
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
                Toast.makeText(this, "Tabla '${table.title}' eliminada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar la tabla", Toast.LENGTH_SHORT).show()
            }
    }
}

class DeleteTableAdapter(
    private val tables: List<LeagueTable>,
    private val onDeleteClick: (LeagueTable) -> Unit
) : RecyclerView.Adapter<DeleteTableAdapter.TableViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delete_table, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tables[position]
        holder.bind(table)
    }

    override fun getItemCount() = tables.size

    inner class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTableTitle)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(table: LeagueTable) {
            titleTextView.text = table.title
            deleteButton.setOnClickListener { onDeleteClick(table) }
        }
    }
}
