package id.ac.umn.kevinsorensen.bengkelonline.datamodel

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user",
    val phoneNumber: String? = null,
    val photo: String? = null,
    val long: Float = 0.0f,
    val lat: Float = 0.0f,
    val complaintId: String? = null,
)