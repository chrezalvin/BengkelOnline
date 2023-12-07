package id.ac.umn.kevinsorensen.bengkelonline.api

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ResourceCollector(private val database: FirebaseStorage){
    private fun getResourceUri(path: String, fileName: String, callback: (Uri) -> Unit){
        database.reference.child("$path$fileName").downloadUrl.addOnSuccessListener {
            Log.d(TAG, "uri: $it");
            callback(it);
        }

    }

    private fun putIntoStorage(path: String, fileName: String, callback: (Boolean) -> Unit){
        // TODO put into storage (images)
    }

    fun getImageResource(name: String, callback: (Uri) -> Unit) {
        getResourceUri(PATH_TO_IMAGES, name, callback);
    }

    fun getProfilePhoto(name: String, callback: (Uri) -> Unit) {
        getResourceUri(PATH_TO_PROFILE_PICTURES, name, callback);
    }

    fun getComplaintPhoto(name: String, callback: (Uri) -> Unit) {
        getResourceUri(PATH_TO_COMPLAINT_PHOTOS, name, callback);
    }

    fun getComplaintVideo(name: String, callback: (Uri) -> Unit) {
        getResourceUri(PATH_TO_COMPLAINT_VIDEOS, name, callback);
    }

    fun submitProfilePhoto(imgPath: String, onSuccess: () -> Unit){
        val storageRef = database.reference.child(PATH_TO_PROFILE_PICTURES);
        storageRef.putFile(Uri.parse(imgPath))
            .addOnSuccessListener {
                onSuccess();
            }
            .addOnFailureListener {
                ex -> Log.d(TAG, ex.message.toString());
            }
    }

    fun submitComplaintPhoto(imgPath: String, onSuccess: () -> Unit){
        val storageRef = database.reference.child(PATH_TO_COMPLAINT_PHOTOS);
        storageRef.putFile(Uri.parse(imgPath))
            .addOnSuccessListener {
                onSuccess();
            }
            .addOnFailureListener {
                ex -> Log.d(TAG, ex.message.toString());
            }
    }

    companion object {
        private const val PATH_TO_IMAGES = "images/";
        private const val PATH_TO_PROFILE_PICTURES = "profilePhotos/"
        private const val PATH_TO_COMPLAINT_PHOTOS = "complaintPhotos/"
        private const val PATH_TO_COMPLAINT_VIDEOS = "complaintVideos/"
        private const val TAG = "ResourceCollector";
    }
}