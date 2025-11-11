package com.example.anfaapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.adapter.UnifiedAdapter
import com.example.anfaapp.model.LeagueTable
import com.example.anfaapp.model.TimelineItem
import com.google.firebase.database.*

class FutureTablesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var rvTables: RecyclerView
    private lateinit var unifiedAdapter: UnifiedAdapter
    private val timelineItems = mutableListOf<TimelineItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_future_tables)
        supportActionBar?.title = "Tablas Futuras"

        rvTables = findViewById(R.id.rvTables)
        rvTables.layoutManager = LinearLayoutManager(this)

        unifiedAdapter = UnifiedAdapter(timelineItems)
        rvTables.adapter = unifiedAdapter

        database = FirebaseDatabase.getInstance().getReference("futureTables")

        fetchTablesAndMatches()
    }

    private fun fetchTablesAndMatches() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                timelineItems.clear()
                for (tableSnapshot in snapshot.children) {
                    val table = tableSnapshot.getValue(LeagueTable::class.java)
                    if (table != null && table.title != null) {
                        timelineItems.add(TimelineItem.TitleItem(table.title))

                        val matches = table.matches.values.toList()
                        if (matches.isNotEmpty()) {
                            matches.forEach { match ->
                                timelineItems.add(TimelineItem.MatchItem(match))
                            }
                        }
                    }
                }
                unifiedAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FutureTablesActivity, "Error al cargar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

