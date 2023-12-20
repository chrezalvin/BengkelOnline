package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.ComplaintController
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryState(
    val error: String = "",
    val titleError: String = "",
    val descriptionError: String = "",
)

class HistoryViewModel : ViewModel(){
    private val database = Firebase;
    private val userController = UserController(database);
    private val complaintController = ComplaintController(database);

    private val _uiState = MutableStateFlow(HistoryState());
    val uiState = _uiState.asStateFlow();
}