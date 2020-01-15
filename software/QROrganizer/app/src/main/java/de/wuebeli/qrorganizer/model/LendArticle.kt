package de.wuebeli.qrorganizer.model

data class LendArticle (
    // list of all articles which were lend to someone
    val article_name : String,
    val article_id : String,
    val article_lending : ArticleLending
)