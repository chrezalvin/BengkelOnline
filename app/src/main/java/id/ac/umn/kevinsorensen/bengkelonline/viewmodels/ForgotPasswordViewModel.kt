package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

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
    val confirmPasswordError: String = "",
)

class ForgotPasswordViewModel(database: Firebase): ViewModel() {
    private val userController = UserController(database);

    private val _uiState = MutableStateFlow(ForgotPasswordState());
    val uiState = _uiState.asStateFlow();

    init {
        resetInput();
    }

    var passwordVisibility by mutableStateOf(false)
        private set;
    var emailOrUsername by mutableStateOf("")
        private set;

    var newPassword by mutableStateOf("")
        private set;

    var confirmPassword by mutableStateOf("")
        private set;

    fun submitForgotPassword(onSuccess: () -> Unit){
        validate{
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
        }
        else
            _uiState.value = _uiState.value.copy(emailOrUsernameError = "");
    }

    fun passwordChecking(){
        if(newPassword.isEmpty()){
            _uiState.value = _uiState.value.copy(passwordError = "please fill out this field!");
        }
        else
            _uiState.value = _uiState.value.copy(passwordError = "");
    }

    fun confirmPasswordChecking(){
        if(confirmPassword.isEmpty()){
            _uiState.value = _uiState.value.copy(confirmPasswordError = "please fill out this field!");
        }
        else if(newPassword != confirmPassword){
            _uiState.value = _uiState.value.copy(confirmPasswordError = "Confirm password does not match!");
        }
        else
            _uiState.value = _uiState.value.copy(confirmPasswordError = "");
    }

    private fun validate(onSuccess: () -> Unit){
        emailChecking();
        passwordChecking();
        confirmPasswordChecking();

        if(
            _uiState.value.emailOrUsernameError.isEmpty() &&
            _uiState.value.passwordError.isEmpty() &&
            _uiState.value.confirmPasswordError.isEmpty()
        ){
            onSuccess();
        }
    }

    fun resetInput(){
        emailOrUsername = "";
        newPassword = "";
        confirmPassword = "";
    }
}