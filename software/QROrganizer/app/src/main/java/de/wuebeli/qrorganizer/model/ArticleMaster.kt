package de.wuebeli.qrorganizer.model

import java.util.*

data class ArticleMaster (
    val articleId : String, // will be created by QR Code generation with articleQrCode data
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