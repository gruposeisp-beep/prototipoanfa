package com.example.anfaapp

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class QrDisplayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_display)
        supportActionBar?.title = "Tu Ticket QR"

        val ivQrCode: ImageView = findViewById(R.id.ivQrCode)
        val tvQrMatchTitle: TextView = findViewById(R.id.tvQrMatchTitle)
        val tvQrTicketCode: TextView = findViewById(R.id.tvQrTicketCode)

        val ticketCode = intent.getStringExtra("TICKET_CODE")
        val matchTitle = intent.getStringExtra("MATCH_TITLE")

        if (ticketCode == null || matchTitle == null) {
            Toast.makeText(this, "Error: No se pudo cargar la información del ticket.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        tvQrMatchTitle.text = matchTitle
        tvQrTicketCode.text = "Código: $ticketCode"

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                ticketCode,
                BarcodeFormat.QR_CODE,
                800,
                800
            )
            ivQrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al generar el código QR", Toast.LENGTH_SHORT).show()
        }
    }
}
