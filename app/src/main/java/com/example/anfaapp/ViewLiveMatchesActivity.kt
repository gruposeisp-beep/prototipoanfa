package com.example.anfaapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.adapter.UnifiedAdapter
import com.example.anfaapp.model.Match
import com.example.anfaapp.model.TimelineItem
import com.google.firebase.database.*

class ViewLiveMatchesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var rvLiveMatches: RecyclerView
    private lateinit var unifiedAdapter: UnifiedAdapter
    private val timelineItems = mutableListOf<TimelineItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_live_matches)
        supportActionBar?.title = "Partidos en Vivo"

        database = FirebaseDatabase.getInstance().getReference("liveMatches")

        rvLiveMatches = findViewById(R.id.rvLiveMatches)
        rvLiveMatches.layoutManager = LinearLayoutManager(this)

        unifiedAdapter = UnifiedAdapter(timelineItems)
        rvLiveMatches.adapter = unifiedAdapter

        fetchLiveMatches()
    }

    private fun fetchLiveMatches() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                timelineItems.clear()
                for (matchSnapshot in snapshot.children) {
                    val match = matchSnapshot.getValue(Match::class.java)
                    if (match != null) {
                        timelineItems.add(TimelineItem.MatchItem(match))
                    }
                }
                unifiedAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewLiveMatchesActivity, "Error al cargar partidos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
