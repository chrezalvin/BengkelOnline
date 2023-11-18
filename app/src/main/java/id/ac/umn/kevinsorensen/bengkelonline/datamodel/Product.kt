package id.ac.umn.kevinsorensen.bengkelonline.datamodel

data class Product (
    val id: String,
    val name: String,
    val price: Int,
    val stock: Int = 0,
    val description:String = "",
    val discountFlat: Int? = null,
    val discountPercentage: Int? = null,
    val imageUrl: String? = null,
    val vendorId: String = "...",
)