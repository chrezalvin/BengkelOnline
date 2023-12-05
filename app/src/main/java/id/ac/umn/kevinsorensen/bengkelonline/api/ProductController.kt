package id.ac.umn.kevinsorensen.bengkelonline.api

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product

class ProductController(private val database: FirebaseFirestore) {
    fun addProduct(product: Product, callback: (Boolean) -> Unit): Unit  {
        database.collection(COLLECTION_NAME)
            .add(product)
            .addOnSuccessListener {
                callback(true);
            }
            .addOnFailureListener{
                callback(true);
            }
    }

    private fun dataValidation(document: DocumentSnapshot): Product?{
        val id = document.get("id");
        val name = document.get("name");
        var price = document.get("price");
        var stock = document.get("stock");
        var description = document.get("description");
        var discountFlat = document.get("discountFlat");
        var discountPercentage = document.get("discountPercentage");
        var imageUrl = document.get("imgUrl");
        // var vendor = document.get("vendorId") as String?;

        Log.d(TAG, "id: $id\nname: $name\nprice: $price\nstock: $stock\ndescription: $description\ndiscountFlat: $discountFlat\ndiscountPercentage: $discountPercentage\nimageUrl: $imageUrl\nvendor: ...");

        if(discountFlat != null && discountFlat is Number)
            discountFlat = discountFlat.toInt();
        if(discountPercentage != null && discountPercentage is Number)
            discountPercentage = discountPercentage.toInt();

        if(id != null && name != null && price != null && stock != null && description != null){
            if(price is Number && stock is Number) {
                Log.d(TAG, "price and stock is number")
                price = price.toInt();
                stock = stock.toInt();
            }
            else return null

            return Product(
                id as String,
                name as String,
                price,
                stock,
                description as String,
                discountFlat as Int?,
                discountPercentage as Int?,
                imageUrl as String?,
                "..."
            );
        }
        else return null;
    }

    fun getProduct(id: String, callback: (Product) -> Unit){
        database.collection(COLLECTION_NAME)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "snapshot data ${it.documents[0].data}")
                val document = it.documents[0];

                val product = dataValidation(document);

                if(product != null)
                    callback(product);
            }
            .addOnFailureListener{
                throw it;

            }
    }

    fun reduceQuantity(id: String, amount: Int, callback: (Product) -> Unit){
        database.collection(COLLECTION_NAME)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener {

            }
            .addOnFailureListener{
                throw it;
            }
    }

    fun getAllProduct(callback: (ArrayList<Product>) -> Unit){
        database.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener {
                val products = ArrayList<Product>();
                for(document in it.documents){
                    val product = dataValidation(document);
                    Log.d(TAG, "got product ${product}")
                    if(product != null)
                        products.add(product);
                }
                callback(products);
            }
            .addOnFailureListener{
                throw it;
            }
    }

    companion object{
        const val COLLECTION_NAME =  "products";
        const val TAG = "Product";
    }
}