package de.wuebeli.qrorganizer.util

import android.util.Log
import com.mongodb.BasicDBObject
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions
import de.wuebeli.qrorganizer.model.ArticleLending
import de.wuebeli.qrorganizer.model.ArticleMaster
import de.wuebeli.qrorganizer.model.ArticleStorageLocation
import de.wuebeli.qrorganizer.model.LendArticle
import org.bson.Document
import java.util.*

object MongoDBStitchManager {
    init {
        Stitch.initializeDefaultAppClient("qrstitchapp-khqnn") // ToDo connect this to ressource file
    }

    private val stitchAppClient: StitchAppClient = Stitch.getDefaultAppClient()
    private val remoteMongoClient: RemoteMongoClient =
        stitchAppClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
    private val remoteQrStorageCollection: RemoteMongoCollection<Document> =
        remoteMongoClient.getDatabase("qr_organizer_database")
            .getCollection("ilr_organizer_storage") // ToDo connect this later to string.xml

//    fun doSomething(context: Context) {
//        stitchAppClient.auth.loginWithCredential(AnonymousCredential()).addOnSuccessListener {
//            val mongoClient = stitchAppClient.getServiceClient(
//                RemoteMongoClient.factory, "mongodb-atlas"
//            )
//            val myCollection = mongoClient
//                .getDatabase("fabulous_testdata")
//                .getCollection("fabulous_collection")
//
//            // testing insertion of some document data
//            val myFirstDocument = Document()
//            myFirstDocument["user_id"] = it.id
//            myFirstDocument["time"] = Date().time
//
//            myCollection.insertOne(myFirstDocument)
//                .addOnSuccessListener {
//                    Log.d("STITCH", "Document successfully inserted")
//                }
//            // testing queries
//            val query = myCollection.find()
//                .sort(Document("time", -1))
//                .limit(10)
//            val result = mutableListOf<Document>()
//            query.into(result).addOnSuccessListener {
//                val output = StringBuilder("You opened this app: \n\n")
//
//                // Loop through the results
//                result.forEach {
//                    output.append(
//                        DateUtils.getRelativeDateTimeString(
//                            context,
//                            it["time"] as Long, // Get value of 'time' field
//                            DateUtils.SECOND_IN_MILLIS,
//                            DateUtils.WEEK_IN_MILLIS,
//                            0
//                        )
//                    ).append("\n")
//                }
//
//                // Show result of query on app screen as toast
//                Toast.makeText(context, output, Toast.LENGTH_LONG).show()
//            }
//
//        }
//    }

    fun createArticle(article: ArticleMaster, uploadCallback: UploadCallback.UploadCallbackInterface) {
        stitchAppClient.auth.loginWithCredential(AnonymousCredential()).addOnSuccessListener {
            Log.d("createArticle", "In Success")

            // filterDoc represents the filter to detect the document on which will be updated/created
            val filterDoc = Document()
            filterDoc["article_id"] = article.articleId

            // updateDoc contains ArticleMaster items
            val updateDoc = Document()
            updateDoc["article_id"] = article.articleId
            updateDoc["article_name"] = article.articleName
            updateDoc["last_user_id"] = it.id
            updateDoc["last_change_time"] = article.lastChangeTime
            updateDoc["article_price"] = article.articlePrice

            // article_storage_location will contain multiple fields and will be added as Document
            val articleStorageLocationDoc = Document()
            articleStorageLocationDoc["Room"] = article.articleStorageLocation.articleStorageRoom
            articleStorageLocationDoc["Box"] = article.articleStorageLocation.articleStorageBox
            articleStorageLocationDoc["Shelf"] = article.articleStorageLocation.articleStorageShelf
            updateDoc["article_storage_location"] = articleStorageLocationDoc

            updateDoc["article_current_stock_amount"] = article.articleCurrentStockAmount
            updateDoc["article_minimum_stock_amount"] = article.articleMinimumStockAmount
            updateDoc["article_lending_amount"] = article.articleLendingAmount
            updateDoc["article_where_ordered"] = article.articleWhereOrdered
            // upsert: create new if it does not exist
            val options = RemoteUpdateOptions().upsert(true)

            remoteQrStorageCollection.updateOne(filterDoc, updateDoc, options)
                .addOnSuccessListener {
                    Log.d("STITCH", "Document successfully inserted")
                }
                .addOnFailureListener {
                    // updateOne was not successful
                    Log.e("STITCH", "Document query failed")
                    uploadCallback.onError()
                }
                .addOnCanceledListener {
                    // updateOne was not successful
                    Log.e("STITCH", "Document query failed")
                    uploadCallback.onError()
                }
                .addOnCompleteListener {
                    Log.d("STITCH", "Document successfully inserted")
                    uploadCallback.onFinish()
                }
        }
            .addOnFailureListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                uploadCallback.onError()
            }
            .addOnCanceledListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                uploadCallback.onError()
            }
    }

    fun updateArticle(article: ArticleMaster) {

        stitchAppClient.auth.loginWithCredential(AnonymousCredential()).addOnSuccessListener {
            Log.d("updateArticle", "In Success")

            // filterDoc represents the filter to detect the document on which will be updated/created
            val filterDoc = Document()
            filterDoc["article_id"] = article.articleId

            // updateDoc contains ArticleMaster items
            val updateDoc = Document()
            updateDoc["article_id"] = article.articleId
            updateDoc["article_name"] = article.articleName
            updateDoc["last_user_id"] = it.id
            updateDoc["last_change_time"] = article.lastChangeTime
            updateDoc["article_price"] = article.articlePrice
            updateDoc["article_storage_location"] = article.articleStorageLocation.toString()
            updateDoc["article_current_stock_amount"] = article.articleCurrentStockAmount
            updateDoc["article_minimum_stock_amount"] = article.articleMinimumStockAmount
            updateDoc["article_lending_amount"] = article.articleLendingAmount
            updateDoc["article_where_ordered"] = article.articleWhereOrdered

            // upsert = true -> create new if it does not exist
            val options = RemoteUpdateOptions().upsert(false)

            remoteQrStorageCollection.updateOne(filterDoc, updateDoc, options)
                .addOnSuccessListener {
                    Log.d("STITCH", "Document successfully inserted")
                }
        }
    }

    fun lendArticle(
        articleId: String,
        lending: ArticleLending,
        uploadCallback: UploadCallback.UploadCallbackInterface
    ) {

        /**
         *
         * called when checkBox "wear part" ticked false
         *
         *  On Update:
         *   decrease articleCurrentStockAmount (amount can get negative, user checks manually in dataset overview)
         *   increase articleLendingAmount
         */

        stitchAppClient.auth.loginWithCredential(AnonymousCredential()).addOnSuccessListener {

            Log.d("lendArticle", "In Success")

            // filterDoc represents the filter to detect the document on which will be updated/created
            val filterDoc = Document()
            filterDoc["article_id"] = articleId

            // updateDoc contains ArticleLending items
            val updateDoc = Document()
            val lendingDoc = Document()
            lendingDoc["lending_id"] = lending.lending_id
            lendingDoc["lending_who"] = lending.lending_who
            lendingDoc["lending_amount"] = lending.lending_amount
            lendingDoc["lending_return_date"] = lending.lending_return_date
            lendingDoc["lending_is_wear_part"] = lending.lending_is_wear_part

            // add Lending as subdocument in MongoDB
            updateDoc.append("\$push", Document().append("lending", lendingDoc))

            // decrease article_current_stock_amount in MongoDB
            // increase article_lending_amount in MongoDB
            updateDoc.append(
                "\$inc",
                Document().append(
                    "article_current_stock_amount",
                    (-1) * lending.lending_amount
                ).append("article_lending_amount", lending.lending_amount)
            )

            // only add Lending if Article exists
            val options = RemoteUpdateOptions().upsert(false)

            remoteQrStorageCollection.updateOne(filterDoc, updateDoc, options)
                .addOnSuccessListener {
                    // Log.d("STITCH", "Document successfully inserted")
                }
                .addOnFailureListener {
                    // updateOne was not successful
                    Log.e("STITCH", "Document query failed")
                    uploadCallback.onError()
                }
                .addOnCanceledListener {
                    // updateOne was not successful
                    Log.e("STITCH", "Document query failed")
                    uploadCallback.onError()
                }
                .addOnCompleteListener {
                    Log.d("STITCH", "Document successfully inserted")
                    uploadCallback.onFinish()
                }
        }
            .addOnFailureListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                uploadCallback.onError()
            }
            .addOnCanceledListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                uploadCallback.onError()
            }
    }

    fun returnArticle(article: ArticleMaster, lending: ArticleLending) {

        // ToDo TBD

//        stitchAppClient.auth.loginWithCredential(AnonymousCredential()).addOnSuccessListener {
//
//            Log.d("returnArticle", "In Success")
//
//            // filterDoc represents the filter to detect the document on which will be updated/created
//            val filterDoc = Document()
//            filterDoc["article_id"] = article.articleId
//
//            // updateDoc contains ArticleLending items
//            val updateDoc = Document()
//            val lendingDoc = Document()
//            lendingDoc["lending_id"] = lending.lending_id
//            lendingDoc["lending_who"] = lending.lending_who
//            lendingDoc["lending_amount"] = lending.lending_amount
//            lendingDoc["lending_return_date"] = lending.lending_return_date
//
//            updateDoc.append("\$pull", Document().append("lending", lendingDoc))
//            // upsert: create new if it does not exist
//            // val options = RemoteUpdateOptions().upsert(true)
//
//            remoteQrStorageCollection.updateOne(filterDoc, updateDoc)
//                .addOnSuccessListener {
//                    Log.d("STITCH", "Document successfully inserted")
//                }
//        }
    }

    fun takeArticleForever(
        articleId: String,
        lending: ArticleLending,
        uploadCallback: UploadCallback.UploadCallbackInterface
    ) {

        /**
         *  called when checkBox "wear part" ticked true
         *
         *  On Update:
         *   decrease articleCurrentStockAmount (amount can get negative, user checks manually in dataset overview)
         *   increase articleLendingAmount
         *
         *   no return_date will be attached to lending_object
         */

        stitchAppClient.auth.loginWithCredential(AnonymousCredential()).addOnSuccessListener {

            Log.d("takeArticleForever", "In Success")

            // filterDoc represents the filter to detect the document on which will be updated/created
            val filterDoc = Document()
            filterDoc["article_id"] = articleId

            // updateDoc contains ArticleLending items
            val updateDoc = Document()
            val lendingDoc = Document()
            lendingDoc["lending_id"] = lending.lending_id
            lendingDoc["lending_who"] = lending.lending_who
            lendingDoc["lending_amount"] = lending.lending_amount
            lendingDoc["lending_return_date"] = lending.lending_return_date
            lendingDoc["lending_is_wear_part"] = lending.lending_is_wear_part

            // add Lending as subdocument in MongoDB
            updateDoc.append("\$push", Document().append("lending", lendingDoc))

            // decrease article_current_stock_amount in MongoDB
            updateDoc.append(
                "\$inc",
                Document().append(
                    "article_current_stock_amount",
                    (-1) * lending.lending_amount
                ).append("article_lending_amount", lending.lending_amount)
            )

            // only add Lending if Article exists
            val options = RemoteUpdateOptions().upsert(false)

            remoteQrStorageCollection.updateOne(filterDoc, updateDoc, options)
                .addOnSuccessListener {
                    // Log.d("STITCH", "Document successfully inserted")
                }

                .addOnFailureListener {
                    // updateOne was not successful
                    Log.e("STITCH", "Document query failed")
                    uploadCallback.onError()
                }
                .addOnCanceledListener {
                    // updateOne was not successful
                    Log.e("STITCH", "Document query failed")
                    uploadCallback.onError()
                }
                .addOnCompleteListener {
                    Log.d("STITCH", "Document successfully inserted")
                    uploadCallback.onFinish()
                }
        }
            .addOnFailureListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                uploadCallback.onError()
            }
            .addOnCanceledListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                uploadCallback.onError()
            }
    }

    fun downloadLendArticleList(
        articleId: String,
        downloadCallback: DownloadCallbackLendArticleList.DownloadCallbackInterface
    ) {

        val lendArticleList = mutableListOf<LendArticle>()

        stitchAppClient.auth.loginWithCredential(AnonymousCredential())
            .addOnSuccessListener {
                Log.d("STITCH", "loginWithCredential successful")

                // check/filter if element "lending" contained in article
                val filterDBObj = BasicDBObject("lending", BasicDBObject("\$exists", true))
//                filterDBObj["article_id"] = articleId
                filterDBObj.append("article_id", articleId)
                // query: find all documents with lending elements
                val query = remoteQrStorageCollection.find(filterDBObj)
                val result = mutableListOf<Document>()
                query.into(result).addOnSuccessListener {
                    var articleName: String
                    var articleId: String
                    // Subdocuments of lendings are packed into a ArrayList with elements of type Document
                    var resultLendingArrayList: ArrayList<Document>
                    var articleLending: ArticleLending

                    result.forEach {
                        try {
                            articleName = it["article_name"] as String
                            articleId = it["article_id"] as String

                            // Subdocuments of lendings are packed into a ArrayList with elements of type Document
                            resultLendingArrayList = it["lending"] as ArrayList<Document>

                            resultLendingArrayList.forEach {
                                articleLending =
                                    ArticleLending(
                                        it["lending_id"] as String,
                                        it["lending_who"] as String,
                                        it["lending_amount"] as Int,
                                        it["lending_return_date"] as Date,
                                        it["lending_is_wear_part"] as Boolean
                                    )

                                lendArticleList.add(
                                    LendArticle(
                                        articleName,
                                        articleId,
                                        articleLending
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("STITCH", "Document might miss some data: " + e.message)

                            downloadCallback.onError()
                        }
                    }
                }
                    .addOnFailureListener {
                        // query.into(result) was not successful
                        Log.e("STITCH", "Document query failed")
                        downloadCallback.onError()
                    }
                    .addOnCanceledListener {
                        // query.into(result) was not successful
                        Log.e("STITCH", "Document query failed")
                        downloadCallback.onError()
                    }
                    .addOnCompleteListener {
                        if (lendArticleList.isEmpty()) {
                            // query.into(result) was not successful
                            Log.e("STITCH", "Document query results in empty list")
                            downloadCallback.onError()
                        } else {
                            Log.d("STITCH", "Document successfully queried")
                            Log.d("Result ArrayList", lendArticleList.toString())
                            downloadCallback.onFinish(lendArticleList)
                        }
                    }
            }
            .addOnFailureListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                downloadCallback.onError()
            }
            .addOnCanceledListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                downloadCallback.onError()
            }

    }

    fun downloadArticleList(downloadCallback: DownloadCallbackArticleList.DownloadCallbackInterface) {
        val articleList = mutableListOf<ArticleMaster>()

        stitchAppClient.auth.loginWithCredential(AnonymousCredential())
            .addOnSuccessListener {
                Log.d("STITCH", "loginWithCredential successful")

                // check/filter if element "lending" contained in article
                val filterDBObj = BasicDBObject("article_id", BasicDBObject("\$exists", true))
                // query: find all article documents
                val query = remoteQrStorageCollection.find(filterDBObj)
                val result = mutableListOf<Document>()
                query.into(result).addOnSuccessListener {
                    var articleId: String
                    var articleName: String
                    var lastUserId: String
                    var lastChangeTime: Date
                    var articlePrice: Double
                    var articleStorageLocation: ArticleStorageLocation
                    var articleCurrentStockAmount: Int
                    var articleMinimumStockAmount: Int
                    var articleBorrowedAmount: Int
                    var articleWhereOrdered: String

                    // article_storage_location contains multiple fields and will be received as Document
                    var articleStorageLocationDoc = Document()

                    result.forEach {
                        try {
                            articleId = it["article_id"] as String
                            articleName = it["article_name"] as String
                            lastUserId = it["last_user_id"] as String
                            lastChangeTime = it["last_change_time"] as Date
                            articlePrice = it["article_price"] as Double

                            // article_storage_location contains multiple fields and will be received as Document
                            if (it["article_storage_location"] != null) {
                                articleStorageLocationDoc =
                                    it["article_storage_location"] as Document
                                articleStorageLocation = ArticleStorageLocation(
                                    articleStorageLocationDoc["Room"] as String,
                                    articleStorageLocationDoc["Box"] as String,
                                    articleStorageLocationDoc["Shelf"] as String
                                )
                            } else {
                                articleStorageLocation = ArticleStorageLocation(
                                    "NA",
                                    "NA",
                                    "NA"
                                )
                            }

                            articleCurrentStockAmount = it["article_current_stock_amount"] as Int
                            articleMinimumStockAmount = it["article_minimum_stock_amount"] as Int
                            articleBorrowedAmount = it["article_lending_amount"] as Int
                            articleWhereOrdered = it["article_where_ordered"] as String

                            articleList.add(
                                ArticleMaster(
                                    articleId,
                                    articleName,
                                    lastUserId,
                                    lastChangeTime,
                                    articlePrice,
                                    articleStorageLocation,
                                    articleCurrentStockAmount,
                                    articleMinimumStockAmount,
                                    articleBorrowedAmount,
                                    articleWhereOrdered
                                )
                            )

                        } catch (e: Exception) {
                            Log.e("STITCH", "Document might miss some data: " + e.message)

                            downloadCallback.onError()
                        }
                    }
                }
                    .addOnFailureListener {
                        // query.into(result) was not successful
                        Log.e("STITCH", "Document query failed")
                        downloadCallback.onError()
                    }
                    .addOnCanceledListener {
                        // query.into(result) was not successful
                        Log.e("STITCH", "Document query failed")
                        downloadCallback.onError()
                    }
                    .addOnCompleteListener {
                        if (articleList.isEmpty()) {
                            // query.into(result) was not successful
                            Log.e("STITCH", "Document query results in empty list")
                            downloadCallback.onError()
                        } else {
                            Log.d("STITCH", "Document successfully queried")
                            Log.d("Result ArrayList", articleList.toString())
                            downloadCallback.onFinish(articleList)
                        }
                    }
            }
            .addOnFailureListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                downloadCallback.onError()
            }
            .addOnCanceledListener {
                //  stitchAppClient.auth.loginWithCredential was not successful
                Log.e("STITCH", "Login failed")
                downloadCallback.onError()
            }

    }
}