package de.wuebeli.qrorganizer.model

import java.util.*

data class ArticleLending (
    // holds all data necessary while lending an article
    val lending_id : String,
    val lending_who : String,
    val lending_amount : Int,
    val lending_return_date : Date
)