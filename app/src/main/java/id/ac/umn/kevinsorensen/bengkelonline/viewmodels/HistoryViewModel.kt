package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.Complaint
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.History
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryState(
    val onGoing: Complaint? = null,
    val done: List<History> = listOf(),
    val cancelled: List<History> = listOf(),

    val error: String = "",
    val titleError: String = "",
    val descriptionError: String = "",
)
class HistoryViewModel : ViewModel(){
    private val database = Firebase;
    private val userController = UserController(database);

    private val _uiState = MutableStateFlow(HistoryState());
    val uiState = _uiState.asStateFlow();

    fun initialize(userId: String){
        userController.getUserById(userId){user ->
            if(user != null) {
                _uiState.value = _uiState.value.copy(user = user);
            }
        }
    }
}