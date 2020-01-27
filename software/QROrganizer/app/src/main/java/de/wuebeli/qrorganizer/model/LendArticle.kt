package de.wuebeli.qrorganizer.model

data class LendArticle (
    // used for list of all articles which were lend
    val article_name : String,
    val article_id : String,
    val article_lending : ArticleLending
)