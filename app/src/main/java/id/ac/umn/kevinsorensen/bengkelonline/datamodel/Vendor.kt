package id.ac.umn.kevinsorensen.bengkelonline.datamodel

data class Vendor(
    val productList: List<Product>,
    val id: String,
    val name: String,
    val email: String,
    val password: String,
)