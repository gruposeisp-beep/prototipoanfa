package com.example.anfaapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.LeagueTable
import com.example.anfaapp.model.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.random.Random

class TicketPurchaseActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var futureTablesDatabase: DatabaseReference
    private lateinit var ticketsDatabase: DatabaseReference
    private lateinit var btnChooseMatch: Button
    private lateinit var btnConfirmPurchase: Button
    private lateinit var tvSelectedMatch: TextView
    private val futureTablesList = mutableListOf<LeagueTable>()
    private var selectedTable: LeagueTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_purchase)
        supportActionBar?.title = "Venta de Entradas"

        auth = FirebaseAuth.getInstance()
        futureTablesDatabase = FirebaseDatabase.getInstance().getReference("futureTables")
        ticketsDatabase = FirebaseDatabase.getInstance().getReference("tickets")

        tvSelectedMatch = findViewById(R.id.tvSelectedMatch)
        btnChooseMatch = findViewById(R.id.btnChooseMatch)
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase)

        fetchFutureTables()

        btnChooseMatch.setOnClickListener {
            showFutureTablesDialog()
        }

        btnConfirmPurchase.setOnClickListener {
            if (selectedTable != null) {
                generateAndSaveTicket()
            } else {
                Toast.makeText(this, "Por favor, elige un partido primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchFutureTables() {
        futureTablesDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                futureTablesList.clear()
                snapshot.children.forEach { tableSnapshot ->
                    val table = tableSnapshot.getValue(LeagueTable::class.java)
                    if (table != null) {
                        futureTablesList.add(table)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TicketPurchaseActivity, "Error al cargar partidos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showFutureTablesDialog() {
        if (futureTablesList.isEmpty()) {
            Toast.makeText(this, "No hay partidos futuros disponibles.", Toast.LENGTH_SHORT).show()
            return
        }

        val tableTitles = futureTablesList.map { it.title }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Elige un Partido")
            .setItems(tableTitles) { dialog, which ->
                selectedTable = futureTablesList[which]
                tvSelectedMatch.text = selectedTable!!.title
                tvSelectedMatch.setTextColor(resources.getColor(android.R.color.black, null))
                btnConfirmPurchase.isEnabled = true
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun generateAndSaveTicket() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Error: Debes iniciar sesión para comprar.", Toast.LENGTH_LONG).show()
            return
        }
        val userId = currentUser.uid

        val ticketId = ticketsDatabase.child(userId).push().key
        if (ticketId == null) {
            Toast.makeText(this, "Error al generar el ID del ticket.", Toast.LENGTH_SHORT).show()
            return
        }

        val ticketCode = (100000..999999).random().toString()
        val tableTitle = selectedTable?.title ?: "Título no disponible"

        val newTicket = Ticket(
            id = ticketId,
            matchTitle = tableTitle,
            code = ticketCode,
            userId = userId
        )

        ticketsDatabase.child(userId).child(ticketId).setValue(newTicket)
            .addOnSuccessListener {
                showSuccessDialog(ticketCode)
                resetSelection()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al generar el ticket: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showSuccessDialog(ticketCode: String) {
        AlertDialog.Builder(this)
            .setTitle("¡Compra Exitosa!")
            .setMessage("Tu ticket ha sido generado y asociado a tu cuenta.\n\nCódigo de Ticket: $ticketCode")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun resetSelection() {
        selectedTable = null
        tvSelectedMatch.text = "Ningún partido seleccionado"
        tvSelectedMatch.setTextColor(resources.getColor(android.R.color.darker_gray, null))
        btnConfirmPurchase.isEnabled = false
    }
}


