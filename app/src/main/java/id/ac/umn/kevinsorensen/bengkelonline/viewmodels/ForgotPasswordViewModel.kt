package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ForgotPasswordState(
    val emailOrUsernameError: String = "",
    val passwordError: String = "",
    val error: String = "",

)
class ForgotPasswordViewModel(database: Firebase): ViewModel() {
    private val userController = UserController(database);

    private val _uiState = MutableStateFlow(ForgotPasswordState());
    val uiState = _uiState.asStateFlow();

    var passwordVisibility by mutableStateOf(false)
        private set;
    var emailOrUsername by mutableStateOf("")
        private set;

    var newPassword by mutableStateOf("")
        private set;

    var confirmPassword by mutableStateOf("")
        private set;

    fun submitForgotPassword(onSuccess: () -> Unit){
        emailChecking();
        passwordChecking();

        if(uiState.value.passwordError.isNotEmpty() || uiState.value.emailOrUsernameError.isNotEmpty()){
            userController.changePassword(emailOrUsername, newPassword, onSuccess){
                _uiState.value = _uiState.value.copy(error = it);
            }
        }
    }

    fun updateEmailOrUsername(emailOrUsername: String){
        this.emailOrUsername = emailOrUsername;
    }

    fun updateNewPassword(newPassword: String){
        this.newPassword = newPassword;
    }

    fun updateConfirmPassword(confirmPassword: String){
        this.confirmPassword = confirmPassword;
    }

    fun togglePasswordVisibility(){
        passwordVisibility = !passwordVisibility;
    }

    fun emailChecking(){
        if(emailOrUsername.isEmpty()){
            _uiState.value = _uiState.value.copy(emailOrUsernameError = "Email or Username must be filled");
            return;
        }
    }

    fun passwordChecking(){
        if(newPassword.isEmpty()){
            _uiState.value = _uiState.value.copy(passwordError = "Password must be filled");
        }
        else if(newPassword != confirmPassword){
            _uiState.value = _uiState.value.copy(passwordError = "Password must be same");
        }
    }

    fun resetInput(){
        emailOrUsername = "";
        newPassword = "";
        confirmPassword = "";
    }
}