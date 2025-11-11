package com.example.anfaapp.model

data class LeagueTable(
    val id: String? = null,
    val title: String? = null,
    val matches: Map<String, Match> = emptyMap()
) {
    constructor() : this("", "", emptyMap())
}

