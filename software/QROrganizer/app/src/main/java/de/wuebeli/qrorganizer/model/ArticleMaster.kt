package de.wuebeli.qrorganizer.model

import java.util.*

data class ArticleMaster (
    // represents the article document in MongoDB
    val articleId : String, // will be created automatically on QR Code generation: articleName + uuid
    val articleName: String,
    val lastUserId: String,
    val lastChangeTime: Date,
    val articlePrice: Double,
    val articleStorageLocation: ArticleStorageLocation,
    val articleCurrentStockAmount: Int,
    val articleMinimumStockAmount: Int,
    val articleLendingAmount: Int,
    val articleWhereOrdered: String
)