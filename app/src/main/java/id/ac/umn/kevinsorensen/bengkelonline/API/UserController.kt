package id.ac.umn.kevinsorensen.bengkelonline.API

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Address
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import java.security.MessageDigest

class UserController(val database: FirebaseFirestore){
    private fun hash(str: String): String {
        val bytes = str.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun addUser(user: User, callback: (Boolean) -> Unit){
        database.collection(COLLECTION_NAME)
            .add(user)
            .addOnSuccessListener {
                callback(true);
            }
            .addOnFailureListener{
                callback(false);
            }
    }

    fun deleteUser(id: String, callback: (Boolean) -> Unit){
        // TODO (must implement password checking before delete)

        database.collection(COLLECTION_NAME)
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
        database.collection(COLLECTION_NAME)
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

                if(user != null){
                    callback(user);
                }
                else{
                    callback(null);
                }
            }
            .addOnFailureListener{
                throw it;
            }
    }

    fun getUser(emailOrUsername: String, password: String, callback: (User?) -> Unit) {
        // get user
        database.collection(COLLECTION_NAME)
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

    private fun dataValidation(document: DocumentSnapshot?): User? {
        if(document != null) {
            val id = document.get("id");
            val username = document.get("username");
            val email = document.get("email");
            val password = document.get("password");
            val role = document.get("role");
            val address = document.get("address");
            val phone = document.get("phoneNumber");
            val photo = document.get("photo");

            if (id != null && username != null && email != null && password != null && role != null && phone != null && photo != null) {
                if(address is Map<*, *>){
                    val lat = address["lat"];
                    val long = address["long"];
                    val addressName = address["name"];
                    val desc = address["description"];

                    if(lat != null && long != null && addressName != null && desc != null){
                        if(lat is Number && long is Number){
                            val add = Address(
                                addressName as String,
                                lat.toFloat(),
                                long.toFloat(),
                                desc as String,
                            )
                        }
                    }
                }

                return User(
                    id as String,
                    username as String,
                    email as String,
                    password as String,
                    role as String,
                    null,
                    phone as String,
                    photo as String,
                );
            }
        }

        return null;
    }

    fun updateUser(){

    }

    companion object {
        const val COLLECTION_NAME = "users"
        const val TAG = "User Controller"
    }

}