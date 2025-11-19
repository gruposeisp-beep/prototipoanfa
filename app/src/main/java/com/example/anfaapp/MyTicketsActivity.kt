package com.example.anfaapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.adapter.MyTicketsAdapter
import com.example.anfaapp.model.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyTicketsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var rvMyTickets: RecyclerView
    private lateinit var tvNoTickets: TextView
    private lateinit var ticketsAdapter: MyTicketsAdapter
    private val ticketList = mutableListOf<Ticket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tickets)
        supportActionBar?.title = "Mis Tickets"

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("tickets")

        rvMyTickets = findViewById(R.id.rvMyTickets)
        tvNoTickets = findViewById(R.id.tvNoTickets)
        rvMyTickets.layoutManager = LinearLayoutManager(this)

        ticketsAdapter = MyTicketsAdapter(ticketList)
        rvMyTickets.adapter = ticketsAdapter

        fetchUserTickets()
    }

    private fun fetchUserTickets() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Debes iniciar sesión para ver tus tickets.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val userId = currentUser.uid
        val userTicketsRef = database.child(userId)

        userTicketsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ticketList.clear()
                if (snapshot.exists()) {
                    for (ticketSnapshot in snapshot.children) {
                        val ticket = ticketSnapshot.getValue(Ticket::class.java)
                        if (ticket != null) {
                            ticketList.add(ticket)
                        }
                    }
                    tvNoTickets.visibility = View.GONE
                    rvMyTickets.visibility = View.VISIBLE
                } else {
                    tvNoTickets.visibility = View.VISIBLE
                    rvMyTickets.visibility = View.GONE
                }
                ticketsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyTicketsActivity, "Error al cargar tickets: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
