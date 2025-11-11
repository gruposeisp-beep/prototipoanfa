package com.example.anfaapp.model

data class Match(val id: String? = null,
                 val team1: String? = null,
                 val score1: Int? = null,
                 val team2: String? = null,
                 val score2: Int? = null,
                 val matchTime: String? = null
) {
    constructor() : this("", "", 0, "", 0, "")
}
