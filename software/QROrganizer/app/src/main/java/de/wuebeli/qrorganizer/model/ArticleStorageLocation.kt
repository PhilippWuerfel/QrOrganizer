package de.wuebeli.qrorganizer.model

data class ArticleStorageLocation(
    // used to add storage location in ArticleMaster
    val articleStorageRoom: String,
    val articleStorageBox: String,
    val articleStorageShelf: String
) {
    override fun toString(): String {
        return "Room: $articleStorageRoom Box: $articleStorageBox Shelf: $articleStorageShelf"
    }
}

