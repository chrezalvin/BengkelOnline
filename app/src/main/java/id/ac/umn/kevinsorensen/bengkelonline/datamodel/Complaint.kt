package id.ac.umn.kevinsorensen.bengkelonline.datamodel

import android.net.Uri
import com.google.firebase.Timestamp

data class Complaint (
    val id: String,
    val userId: String,
    val long: Float,
    val lat: Float,
    val description: String,
    val photoUris: List<Uri>,
    val videoUri: Uri,
    val date: Timestamp = Timestamp.now(),
)