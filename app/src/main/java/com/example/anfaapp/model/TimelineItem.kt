package com.example.anfaapp.model

sealed interface TimelineItem {
    data class TitleItem(val title: String) : TimelineItem
    data class MatchItem(val match: Match) : TimelineItem
}
