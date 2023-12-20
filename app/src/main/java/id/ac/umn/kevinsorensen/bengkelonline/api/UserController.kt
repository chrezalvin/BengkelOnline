package id.ac.umn.kevinsorensen.bengkelonline.api

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Address
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Complaint
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import java.security.MessageDigest
import java.util.UUID

class UserController(private val database: Firebase){
    private val firestore = database.firestore;
    private val storage = database.storage;
    private val complaintController = ComplaintController(database);

    private fun hash(str: String): String {
        val bytes = str.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun getUserFromPhoneNumber(
        phoneNumber: String,
        password: String,
        onSuccess: (User?) -> Unit = {},
        onFailure: (reason: String) -> Unit = {}
    ){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("phoneNumber", phoneNumber)

            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    onFailure("Incorrect Username or Password");
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                try {
                    val user = dataValidation(document);

                    if(user.password != hash(password)){
                        onFailure("Incorrect Username or Password");
                    }
                    else {
                        Log.d(TAG, "Got user data: ${user}");
                        onSuccess(user);
                    }
                }
                catch (_: Exception){
                    onFailure("Incorrect Username or Password");
                }
            }
            .addOnFailureListener{
                throw it;
            }
    }

    fun getUserFromId(id: String, callback: (User?) -> Unit){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(null);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                try {
                    val user = dataValidation(document);

                    Log.d(TAG, "Got user data: ${user}");
                    callback(user);
                }
                catch (_: Exception){
                    callback(null);
                }
            }
            .addOnFailureListener{
                throw it;
            }
    }

    fun changePasswordFromPhoneNumber(
        phoneNumber: String,
        newPassword: String,
        onSuccess: () -> Unit = {},
        onFailure: (reason: String) -> Unit = {}){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    onFailure("User not found");
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                try {
                    val user = dataValidation(document);

                    Log.d(TAG, "Got user data: $user");

                    document.reference.update("password", hash(newPassword))
                        .addOnSuccessListener {
                            onSuccess();
                        }
                        .addOnFailureListener {
                            ex -> onFailure(ex.message.toString());
                        }
                }
                catch (_: Exception){
                    onFailure("User not found");
                }
            }
            .addOnFailureListener{
                onFailure(it.message.toString());
            }
    }

    fun changePassword(
        usernameOrEmail: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (reason: String) -> Unit
    ){
        // get user
        firestore.collection(COLLECTION_NAME)
            .where(
                Filter.or(
                    Filter.equalTo("email", usernameOrEmail),
                    Filter.equalTo("username", usernameOrEmail),
                ),
            )
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    onFailure("User not found");
                    return@addOnSuccessListener;
                }
                else {
                    val document = it.documents[0];
                    val user = dataValidation(document);

                    Log.d(TAG, "Got user data: ${user.toString()}");

                    if (user != null) {
                        // update password
                        document.reference.update("password", hash(newPassword))
                            .addOnSuccessListener {
                                onSuccess();
                            }
                            .addOnFailureListener {
                                ex -> onFailure(ex.message.toString());
                            }
                    } else {
                        onFailure("User not found");
                    }
                }
            }
            .addOnFailureListener{
                onFailure(it.message.toString());
            }
    }

    fun addUser(
        email: String,
        username: String,
        password: String,
        role: String = "user",
        phoneNumber: String? = null,
        callback: (Boolean, String?) -> Unit,
    ){
        // check if user exist within database
        this.getUser(email, password){userByEmail ->
            if(userByEmail != null){
                callback(false, "User already exist");
                return@getUser;
            }
            else{
                this.getUser(username, password){userByPassword ->
                    if(userByPassword != null){
                        callback(false, "User already exist");
                    }
                    else{
                        var uuid = UUID.randomUUID().toString();

                        // check if uuid exist within database
                        getUserById(uuid){ user ->
                            if(user != null){
                                // if exist, generate new uuid
                                uuid = UUID.randomUUID().toString();
                            }
                        }

                        val user = User(
                            uuid,
                            username,
                            email,
                            hash(password),
                            if(role == "user" || role == "merchant") role else "user",
                            phoneNumber,
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
                }
            }
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

    fun updateUserLocation(
        userId: String,
        long: Float,
        lat: Float,
        callback: (Boolean) -> Unit
    ){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(false);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                document.reference.update("long", long, "lat", lat)
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

    private fun dataValidation(document: DocumentSnapshot): User {
        // these are strings
        val id = document.get("id") as String? ?: throw Exception("id is null");
        val username = document.get("username") as String? ?: "";
        val email = document.get("email") as String? ?: "";
        val password = document.get("password") as String? ?: "";
        val role = document.get("role") as String? ?: "user";
        val phone = document.get("phoneNumber") as String?;
        val photo = document.get("photo") as String?;
        var long = document.get("long") as Number?;
        var lat = document.get("lat") as Number?;
        val complaintId = document.get("complaintId") as String?;

        long = long?.toFloat() ?: 0.0f;
        lat = lat?.toFloat() ?: 0.0f;

        Log.d(TAG, "Got user: id: $id, username: $username, email: $email, password: $password, role: $role, phone: $phone, photo: $photo");

        return User(
            id,
            username,
            email,
            password,
            role,
            phone,
            photo,
            long as Float,
            lat as Float,
            complaintId,
        );
    }

    fun removeUserComplaint(userId: String, callback: (Boolean) -> Unit){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(false);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                document.reference.update("complaintId", null)
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

    fun updateUserComplaint(
        userId: String,
        long: Float,
        lat: Float,
        description: String = "",
        photoUris: List<String>,
        callback: (complaintId: String?) -> Unit
    ){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    callback(null);
                    return@addOnSuccessListener;
                }

                val document = it.documents[0];
                complaintController.submitComplaint(
                    userId,
                    long,
                    lat,
                    description,
                    photoUris,
                    onSuccess = { complaintId ->
                        if(complaintId != null){
                            document.reference.update("complaintId", complaintId)
                                .addOnSuccessListener {
                                    callback(complaintId);
                                }
                                .addOnFailureListener{
                                    callback(null);
                                }
                        }
                        else{
                            callback(null);
                        }
                    }
                )

            }
            .addOnFailureListener{
                Log.d(TAG, "updateUserComplaint: failed");
            }
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