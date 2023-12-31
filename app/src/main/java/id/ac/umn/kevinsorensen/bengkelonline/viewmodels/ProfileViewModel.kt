package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import id.ac.umn.kevinsorensen.bengkelonline.api.ResourceCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UiState(
    val user: User? = null,
    val error: String? = null,
    val profilePhoto: Uri = Uri.parse(""),
)
class ProfileViewModel(): ViewModel(){
    private val userController = UserController(Firebase);
    private val resourceCollector = ResourceCollector(Firebase.storage);

    private val _uiState = MutableStateFlow(UiState());
    val uiState = _uiState.asStateFlow();

    fun initializeProfile(profileId: String){
        if(_uiState.value.user != null) return;

        userController.getUserById(profileId){ user ->
            _uiState.update {
                it.copy(user = user, error = if(user == null) "User not found" else null);
            }
        }

        resourceCollector.getProfilePhoto(profileId){ uri ->
            _uiState.update {
                it.copy(profilePhoto = uri);
            }
        }
    }
}