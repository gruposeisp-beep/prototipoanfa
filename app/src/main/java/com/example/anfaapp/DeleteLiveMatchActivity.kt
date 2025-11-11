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
import com.example.anfaapp.model.Match
import com.google.firebase.database.*

class DeleteLiveMatchActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var rvDeleteLiveMatches: RecyclerView
    private lateinit var matchesAdapter: DeleteLiveMatchAdapter
    private val matchList = mutableListOf<Match>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_live_match)
        supportActionBar?.title = "Eliminar Partido en Vivo"

        database = FirebaseDatabase.getInstance().getReference("liveMatches")
        rvDeleteLiveMatches = findViewById(R.id.rvDeleteLiveMatches)
        rvDeleteLiveMatches.layoutManager = LinearLayoutManager(this)

        matchesAdapter = DeleteLiveMatchAdapter(matchList) { match ->
            showConfirmationDialog(match)
        }
        rvDeleteLiveMatches.adapter = matchesAdapter

        fetchLiveMatches()
    }

    private fun fetchLiveMatches() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                matchList.clear()
                for (matchSnapshot in snapshot.children) {
                    val match = matchSnapshot.getValue(Match::class.java)
                    if (match != null && match.id != null) {
                        matchList.add(match)
                    }
                }
                matchesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DeleteLiveMatchActivity, "Error al cargar partidos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showConfirmationDialog(match: Match) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el partido '${match.team1} vs ${match.team2}'? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                deleteMatch(match)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteMatch(match: Match) {
        if (match.id == null) return

        database.child(match.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Partido '${match.team1} vs ${match.team2}' eliminado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar el partido", Toast.LENGTH_SHORT).show()
            }
    }
}

class DeleteLiveMatchAdapter(
    private val matches: List<Match>,
    private val onDeleteClick: (Match) -> Unit
) : RecyclerView.Adapter<DeleteLiveMatchAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_delete_live_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        holder.bind(match)
    }

    override fun getItemCount() = matches.size

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tvMatchDescription)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(match: Match) {
            descriptionTextView.text = "${match.team1} vs ${match.team2}"
            deleteButton.setOnClickListener { onDeleteClick(match) }
        }
    }
}
