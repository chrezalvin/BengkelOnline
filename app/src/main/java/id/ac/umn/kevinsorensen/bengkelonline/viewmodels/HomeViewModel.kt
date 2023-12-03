package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.ProductController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Product
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

data class HomeState(
    val products: List<Product> = listOf(),
    val user: User? = null,
    val error: String? = null,
    val profilePhoto: Uri = Uri.parse(""),
);

class HomeViewModel(profileId: String): ViewModel(){
    private val _uiState = MutableStateFlow(HomeState());
    val uiState = _uiState.asStateFlow();

    private val productController = ProductController(Firebase.firestore);
    private val userController = UserController(Firebase);

    init {
        userController.getUserById(profileId){ user ->
            _uiState.update {
                it.copy(user = user, error = if(user == null) "User not found" else null);
            }
        }

        userController.getProfilePhoto(profileId){ uri ->
            _uiState.update {
                it.copy(profilePhoto = uri);
            }
        }

        runBlocking {
            emitData().collect(){
                _uiState.update{state ->
                    state.copy(products = state.products)
                }
            }

            productController.getAllProduct { products ->
                _uiState.update{
                    it.copy(products = products);
                }
            }
        }
    }

    private fun emitData(): Flow<List<Product>> = callbackFlow {
        productController.getAllProduct { products ->
            trySend(products);
        }
    }

}