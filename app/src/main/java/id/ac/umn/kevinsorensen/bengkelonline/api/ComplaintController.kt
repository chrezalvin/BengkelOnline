package id.ac.umn.kevinsorensen.bengkelonline.api

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Complaint
import java.util.UUID

class ComplaintController(db: Firebase) {
    private val firestore = db.firestore;

    private fun dataValidation(data: DocumentSnapshot): Complaint{
        Log.d(TAG, "Got complaint data: ${data.toString()}");
        val id = data.get("id") as String? ?: "";
        val userId = data.get("userId") as String? ?: "";
        val long = data.get("long");
        val lat = data.get("lat");
        val description = data.get("description") as String? ?: "";
        val photoUris = data.get("photoUris") as List<String>? ?: listOf();
        val videoUri = data.get("videoUri") as String? ?: "";

        return Complaint(
            id,
            userId,
            long as Float,
            lat as Float,
            description,
            photoUris.map { uri -> Uri.parse(uri) },
            Uri.parse(videoUri),
        );
    }

    fun submitComplaint(
        userId: String,
        long: Float,
        lat: Float,
        description: String = "",
        photoUris: List<String>,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        val complaint = Complaint(
            UUID.randomUUID().toString(),
            userId,
            long,
            lat,
            description,
            photoUris.map { uri -> Uri.parse(uri) },
            Uri.parse(""),
        );

        firestore.collection(COLLECTION_NAME)
            .add(complaint)
            .addOnSuccessListener {
                onSuccess();
            }
            .addOnFailureListener { ex ->
                onFailure(ex.message.toString());
            }
    }

    fun getComplaintsFromUser(
        userId: String,
        onSuccess: (List<Complaint>) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    onFailure("Complaint not found");
                    return@addOnSuccessListener;
                }
                else {
                    val complaints = it.documents.map { document -> dataValidation(document) };
                    onSuccess(complaints);
                }
            }
            .addOnFailureListener {
                ex -> onFailure(ex.message.toString());
            }
    }

    fun getNearbyComplaint(
        long: Float,
        lat: Float,
        onSuccess: (List<Complaint>) -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        // radius of 2km from the center
        val radius = 2.0;
        val minLong = long - radius;
        val maxLong = long + radius;

        val minLat = lat - radius;
        val maxLat = lat + radius;

        firestore.collection(COLLECTION_NAME)
            .whereGreaterThan("long", minLong)
            .whereLessThan("long", maxLong)
            .whereGreaterThan("lat", minLat)
            .whereLessThan("lat", maxLat)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    onFailure("Complaint not found");
                    return@addOnSuccessListener;
                }
                else {
                    val complaints = it.documents.map { document -> dataValidation(document) };
                    onSuccess(complaints);
                }
            }
            .addOnFailureListener {
                ex -> onFailure(ex.message.toString());
            }
    }

    companion object {
        const val COLLECTION_NAME: String = "complaints"
        const val TAG: String = "Complaint Controller"
    }
}