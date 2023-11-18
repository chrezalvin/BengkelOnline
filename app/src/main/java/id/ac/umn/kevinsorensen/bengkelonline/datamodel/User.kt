package id.ac.umn.kevinsorensen.bengkelonline.datamodel

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val address: Address?,
    val phone: String,
    val photo: String,
)