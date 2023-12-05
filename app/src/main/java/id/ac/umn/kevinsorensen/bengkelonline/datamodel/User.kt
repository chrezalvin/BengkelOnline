package id.ac.umn.kevinsorensen.bengkelonline.datamodel

data class User(
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val role: String = "user",
    val address: Address? = null,
    val phoneNumber: String? = null,
    val photo: String? = null,
)