package id.ac.umn.kevinsorensen.bengkelonline.api

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Complaint

class HistoryController(db: Firebase){
    private val firestore: FirebaseFirestore = db.firestore;
    private val complaintController = ComplaintController(db);
    private val userController = UserController(db);

    companion object {
        val COLLECTION_NAME: String = "histories"
        val TAG: String = "History Controller"
    }
}