package com.example.anfaapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.QrDisplayActivity
import com.example.anfaapp.R
import com.example.anfaapp.model.Ticket

class MyTicketsAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<MyTicketsAdapter.TicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.bind(ticket)
    }

    override fun getItemCount() = tickets.size

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val matchTitleTextView: TextView = itemView.findViewById(R.id.tvMatchTitle)
        private val ticketCodeTextView: TextView = itemView.findViewById(R.id.tvTicketCode)
        private val showQrButton: ImageButton = itemView.findViewById(R.id.btnShowQr)

        fun bind(ticket: Ticket) {
            matchTitleTextView.text = ticket.matchTitle
            ticketCodeTextView.text = "Código: ${ticket.code}"

            showQrButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, QrDisplayActivity::class.java).apply {
                    putExtra("TICKET_CODE", ticket.code)
                    putExtra("MATCH_TITLE", ticket.matchTitle)
                }
                context.startActivity(intent)
            }
        }
    }
}

