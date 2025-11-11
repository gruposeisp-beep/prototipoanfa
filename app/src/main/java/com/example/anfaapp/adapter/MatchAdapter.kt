package com.example.anfaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.R
import com.example.anfaapp.model.Match

class MatchAdapter(private val matches: List<Match>) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTeam1: TextView = itemView.findViewById(R.id.tvTeam1)
        val tvTeam2: TextView = itemView.findViewById(R.id.tvTeam2)
        val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        val tvMatchTime: TextView = itemView.findViewById(R.id.tvMatchTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.tvTeam1.text = match.team1
        holder.tvTeam2.text = match.team2
        holder.tvScore.text = "${match.score1} - ${match.score2}"
        holder.tvMatchTime.text = match.matchTime
    }

    override fun getItemCount() = matches.size
}
