package com.example.anfaapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anfaapp.model.Ticket
import com.google.firebase.database.*
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ValidateTicketActivity : AppCompatActivity() {

    private lateinit var etTicketCode: EditText
    private lateinit var btnValidateTicket: Button
    private lateinit var tvValidationResult: TextView
    private lateinit var ticketsRef: DatabaseReference
    private lateinit var btnScanQr: Button

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->

        if (result.contents != null) {
            etTicketCode.setText(result.contents)
            Toast.makeText(this, "Código escaneado: ${result.contents}", Toast.LENGTH_SHORT).show()
            findTicketByCode(result.contents)
        } else {
            Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_validate_ticket)
        supportActionBar?.title = "Validación de Tickets"

        etTicketCode = findViewById(R.id.etTicketCode)
        btnValidateTicket = findViewById(R.id.btnValidateTicket)
        tvValidationResult = findViewById(R.id.tvValidationResult)
        btnScanQr = findViewById(R.id.btnScanQr)

        ticketsRef = FirebaseDatabase.getInstance().getReference("tickets")

        btnValidateTicket.setOnClickListener {
            val code = etTicketCode.text.toString().trim()
            if (code.length == 6) {
                findTicketByCode(code)
            } else {
                Toast.makeText(this, "Por favor, ingrese un código de 6 dígitos.", Toast.LENGTH_SHORT).show()
            }
        }

        btnScanQr.setOnClickListener {
            launchScanner()
        }
    }

    private fun launchScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Escanea el código QR del ticket")
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        barcodeLauncher.launch(options)
    }

    private fun findTicketByCode(code: String) {
        tvValidationResult.text = "Buscando..."
        tvValidationResult.setTextColor(Color.BLACK)

        ticketsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ticketFound: Ticket? = null
                var ticketPath: DatabaseReference? = null
                var found = false

                for (userSnapshot in snapshot.children) {
                    for (ticketSnapshot in userSnapshot.children) {
                        val ticket = ticketSnapshot.getValue(Ticket::class.java)
                        if (ticket?.code == code) {
                            ticketFound = ticket
                            ticketPath = ticketSnapshot.ref
                            found = true
                            break
                        }
                    }
                    if (found) break
                }

                if (ticketFound != null && ticketPath != null) {
                    showValidationSuccessDialog(ticketFound, ticketPath)
                } else {
                    tvValidationResult.text = "Ticket no encontrado o inválido."
                    tvValidationResult.setTextColor(Color.RED)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                tvValidationResult.text = "Error en la búsqueda."
                tvValidationResult.setTextColor(Color.RED)
                Toast.makeText(this@ValidateTicketActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showValidationSuccessDialog(ticket: Ticket, ticketRef: DatabaseReference) {
        tvValidationResult.text = "¡Ticket Válido!"
        tvValidationResult.setTextColor(Color.parseColor("#4CAF50"))

        AlertDialog.Builder(this)
            .setTitle("Ticket Válido")
            .setMessage("El ticket es válido.\n\nPartido: ${ticket.matchTitle}\nCódigo: ${ticket.code}\nUsuario ID: ${ticket.userId}")
            .setPositiveButton("Aceptar y Canjear Ticket") { dialog, _ ->
                deleteTicket(ticketRef)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .setCancelable(false)
            .show()
    }

    private fun deleteTicket(ticketRef: DatabaseReference) {
        ticketRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Ticket validado y canjeado correctamente.", Toast.LENGTH_LONG).show()
                etTicketCode.text.clear()
                tvValidationResult.text = "El ticket fue canjeado. Puede escanear el siguiente."
                tvValidationResult.setTextColor(Color.DKGRAY)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al canjear el ticket: ${e.message}", Toast.LENGTH_SHORT).show()
                tvValidationResult.text = "Error al canjear."
                tvValidationResult.setTextColor(Color.RED)
            }
    }
}


