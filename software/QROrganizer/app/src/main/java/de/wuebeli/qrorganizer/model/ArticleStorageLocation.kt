package de.wuebeli.qrorganizer.model

data class ArticleStorageLocation(
    val articleStorageRoom: String,
    val articleStorageBox: String,
    val articleStorageShelf: String
) {
    override fun toString(): String {
        return "Room: $articleStorageRoom Box: $articleStorageBox Shelf: $articleStorageShelf"
    }
}

