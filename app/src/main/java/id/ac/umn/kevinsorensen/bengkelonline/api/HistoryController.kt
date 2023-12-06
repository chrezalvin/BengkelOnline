package id.ac.umn.kevinsorensen.bengkelonline.api

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoryController(db: Firebase){
    private val firestore: FirebaseFirestore = db.firestore;
    private fun dataValidation(data: DocumentSnapshot){

    }

    fun getAllHistories(userId: String) {
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    // onFailure("User not found");
                    return@addOnSuccessListener;
                }
                else {
                    val document = it.documents[0];
                    val user = dataValidation(document);

                    // Log.d(TAG, "Got user data: ${user.toString()}");

                    if (user != null) {
                        // update password
                        // document.reference.update("password", hash(newPassword))
                        //     .addOnSuccessListener {
                        //         onSuccess();
                        //     }
                        //     .addOnFailureListener {
                        //         ex -> onFailure(ex.message.toString());
                        //     }
                    } else {
                        // onFailure("User not found");
                    }
                }
            }
            .addOnFailureListener {
                // ex -> onFailure(ex.message.toString());
            }
    }

    companion object {
        val COLLECTION_NAME: String = "histories"
        val TAG: String = "History Controller"
    }
}