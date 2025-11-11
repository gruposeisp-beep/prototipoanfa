package com.example.anfaapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.model.Match
import com.google.firebase.database.*

class EditLiveMatchListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var rvEditLiveMatches: RecyclerView
    private lateinit var matchesAdapter: EditLiveMatchListAdapter
    private val matchList = mutableListOf<Match>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_live_match_list)
        supportActionBar?.title = "Editar Partido en Vivo"

        database = FirebaseDatabase.getInstance().getReference("liveMatches")

        rvEditLiveMatches = findViewById(R.id.rvEditLiveMatches)
        rvEditLiveMatches.layoutManager = LinearLayoutManager(this)

        matchesAdapter = EditLiveMatchListAdapter(matchList) { match ->
            val intent = Intent(this, EditLiveMatchFormActivity::class.java)
            intent.putExtra("MATCH_ID", match.id)
            startActivity(intent)
        }
        rvEditLiveMatches.adapter = matchesAdapter

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
                if (matchList.isEmpty()) {
                    Toast.makeText(this@EditLiveMatchListActivity, "No hay partidos para editar.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditLiveMatchListActivity, "Error al cargar partidos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class EditLiveMatchListAdapter(
    private val matches: List<Match>,
    private val onItemClick: (Match) -> Unit
) : RecyclerView.Adapter<EditLiveMatchListAdapter.MatchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_edit_live_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    override fun getItemCount() = matches.size

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val matchDescriptionTextView: TextView = itemView.findViewById(R.id.tvMatchToEdit)

        fun bind(match: Match) {
            matchDescriptionTextView.text = "${match.team1} vs ${match.team2}"
            itemView.setOnClickListener { onItemClick(match) }
        }
    }
}
