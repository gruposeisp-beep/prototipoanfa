package com.example.anfaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anfaapp.R
import com.example.anfaapp.model.TimelineItem

class UnifiedAdapter(private val items: List<TimelineItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TITLE = 1
        private const val VIEW_TYPE_MATCH = 2
    }

    class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTableTitle: TextView = view.findViewById(R.id.tvTableTitle)
    }

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTeam1: TextView = view.findViewById(R.id.tvTeam1)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvTeam2: TextView = view.findViewById(R.id.tvTeam2)
        val tvMatchTime: TextView = view.findViewById(R.id.tvMatchTime)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TimelineItem.TitleItem -> VIEW_TYPE_TITLE
            is TimelineItem.MatchItem -> VIEW_TYPE_MATCH
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TITLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
                TitleViewHolder(view)
            }
            VIEW_TYPE_MATCH -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
                MatchViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TimelineItem.TitleItem -> {
                val titleHolder = holder as TitleViewHolder
                titleHolder.tvTableTitle.text = item.title
            }
            is TimelineItem.MatchItem -> {
                val matchHolder = holder as MatchViewHolder
                val match = item.match
                matchHolder.tvTeam1.text = match.team1
                matchHolder.tvScore.text = "${match.score1} - ${match.score2}"
                matchHolder.tvTeam2.text = match.team2
                matchHolder.tvMatchTime.text = match.matchTime
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
