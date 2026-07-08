package com.company.planet.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PhoneRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val collection = firestore.collection("phones")

    fun observePhones(): Flow<List<Phone>> = callbackFlow {
        val listener = collection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val phones = snapshot?.documents?.mapNotNull { doc ->
                    doc.toPhone()
                } ?: emptyList()
                trySend(phones)
            }
        awaitClose { listener.remove() }
    }

    suspend fun savePhone(phone: Phone) {
        collection.document(phone.id).set(phone.toMap()).await()
    }

    suspend fun deletePhone(id: String) {
        collection.document(id).delete().await()
    }

    private fun Phone.toMap(): Map<String, Any?> = mapOf(
        "company" to company,
        "model" to model,
        "detail" to detail,
        "imei1" to imei1,
        "imei2" to imei2,
        "type" to type,
        "pta" to pta,
        "storage" to storage,
        "colour" to colour,
        "purchasePrice" to purchasePrice,
        "salePrice" to salePrice,
        "toldPrice" to toldPrice,
        "withdrawn" to withdrawn,
        "sold" to sold,
        "createdAt" to createdAt
    )

    private fun com.google.firebase.firestore.DocumentSnapshot.toPhone(): Phone? {
        if (!exists()) return null
        return Phone(
            id = id,
            company = getString("company") ?: "",
            model = getString("model") ?: "",
            detail = getString("detail") ?: "",
            imei1 = getString("imei1") ?: "",
            imei2 = getString("imei2") ?: "",
            type = getString("type") ?: "JV",
            pta = getString("pta") ?: "Non PTA",
            storage = getString("storage") ?: "",
            colour = getString("colour") ?: "",
            purchasePrice = getDouble("purchasePrice") ?: 0.0,
            salePrice = getDouble("salePrice") ?: 0.0,
            toldPrice = getDouble("toldPrice") ?: 0.0,
            withdrawn = getDouble("withdrawn") ?: 0.0,
            sold = getBoolean("sold") ?: false,
            createdAt = getLong("createdAt") ?: System.currentTimeMillis()
        )
    }
}
