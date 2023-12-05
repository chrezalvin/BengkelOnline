package id.ac.umn.kevinsorensen.bengkelonline.api

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

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

    companion object {
        private const val PATH_TO_IMAGES = "images/";
        private const val PATH_TO_PROFILE_PICTURES = "profilePhotos/"
        private const val TAG = "ResourceCollector";
    }
}