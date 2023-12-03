package id.ac.umn.kevinsorensen.bengkelonline.myapi

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Address
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import java.security.MessageDigest
import java.util.UUID

class UserController(private val database: Firebase){
    private val firestore = database.firestore;
    private val storage = database.storage;

    private fun hash(str: String): String {
        val bytes = str.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun addUser(
        email: String,
        username: String,
        password: String,
        role: String = "user",
        callback: (Boolean, String?) -> Unit
    ){
        val user = User(
            UUID.fromString(username).toString(),
            username,
            email,
            hash(password),
            if(role == "user" || role == "merchant") role else "user",
            null,
            null,
            null,
        );

        firestore.collection(COLLECTION_NAME)
            .add(user)
            .addOnSuccessListener {
                callback(true, null);
            }
            .addOnFailureListener{
                callback(false, it.message);
            }
    }

    fun deleteUser(id: String, callback: (Boolean) -> Unit){
        // TODO (must implement password checking before delete)

        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(false);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                document.reference.delete()
                    .addOnSuccessListener {
                        callback(true);
                    }
                    .addOnFailureListener{
                        callback(false);
                    }
            }
            .addOnFailureListener{
                callback(false);
            }
    }

    fun getUserById(id: String, callback: (User?) -> Unit){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(null);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                val user = dataValidation(document);

                Log.d(TAG, "Got user data: ${user.toString()}");

                callback(user);
            }
            .addOnFailureListener{
                throw it;
            }
    }

    fun getUser(emailOrUsername: String, password: String, callback: (User?) -> Unit) {
        // get user
        firestore.collection(COLLECTION_NAME)
            .where(
                Filter.or(
                    Filter.equalTo("email", emailOrUsername),
                    Filter.equalTo("username", emailOrUsername),
                ),
            )
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(null);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                val user = dataValidation(document);

                Log.d(TAG, "Got user data: ${user.toString()}");

                if(user != null){
                    // check password
                    if(user.password == hash(password))
                        callback(user);
                    else
                        callback(null);
                }
                else{
                    callback(null);
                }
            }
            .addOnFailureListener{
                throw it;
            }

    }

    private fun dataValidation(document: DocumentSnapshot): User {
        // these are strings
        val id = document.get("id") as String? ?: throw Exception("id is null");
        val username = document.get("username") as String? ?: "";
        val email = document.get("email") as String? ?: "";
        val password = document.get("password") as String? ?: "";
        val role = document.get("role") as String? ?: "user";
        val phone = document.get("phoneNumber") as String?;
        val photo = document.get("photo") as String?;

        Log.d(TAG, "Got user: id: $id, username: $username, email: $email, password: $password, role: $role, phone: $phone, photo: $photo");

        // nullable string or object
        var address = document.get("address");

        /*if(address != null){
            if(address is Map<*, *>){
                val lat = address["lat"];
                val long = address["long"];
                val addressName = address["name"];
                val desc = address["description"];

                if(lat == null || long == null || addressName == null || desc == null){
                    if(lat == null)
                        Log.d(TAG, "lat is null");
                    if(long == null)
                        Log.d(TAG, "long is null");
                    if(addressName == null)
                        Log.d(TAG, "addressName is null");
                    if(desc == null)
                        Log.d(TAG, "desc is null");

                    throw Exception("One of the mentioned variable is null");
                }
                else{
                    if(lat is Number && long is Number){
                        address = Address(
                            addressName as String,
                            lat as Float,
                            long as Float,
                            desc as String,
                        )
                    }
                    else
                        throw Exception("Error on reading address data");
                }
            }
            else
                throw Exception("Error on reading address data");
        }*/

        return User(
            id,
            username,
            email,
            password,
            role,
            null,
            phone,
            photo,
        );
    }

    fun updateUser(){

    }

//    fun getProfilePhoto(profile: String, onSuccess: (Uri) -> Unit){
//        ResourceCollector(storage).getProfilePhoto(profile){
//            onSuccess(it);
//        }
//    }

    companion object {
        const val COLLECTION_NAME = "users"
        const val TAG = "User Controller"
    }

}