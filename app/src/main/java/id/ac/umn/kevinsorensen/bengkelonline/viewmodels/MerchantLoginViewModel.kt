package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MerchantLoginUiState(
    val error: String = "",
    val usernameError: String = "",
    val passwordError: String = "",
)
class MerchantLoginViewModel(database: Firebase): ViewModel() {
    private val userController = UserController(database)

    private val _uiState = MutableStateFlow(MerchantLoginUiState());
    val uiState = _uiState.asStateFlow();

    var inputEmailOrUsername by mutableStateOf("")
        private set;

    var inputPassword by mutableStateOf("")
        private set;

    var inputPasswordVisibility by mutableStateOf(false)
        private set;

    fun updateEmailOrUsername(username: String) {
        inputEmailOrUsername = username;
    }

    fun updatePassword(password: String) {
        inputPassword = password;
    }

    fun togglePasswordVisibility() {
        inputPasswordVisibility = !inputPasswordVisibility;
    }

    private fun passwordChecking(){
        if(inputPassword.isEmpty()){
            _uiState.value = _uiState.value.copy(passwordError = "Please fill out this field");
        }
        else{
            _uiState.value = _uiState.value.copy(passwordError = "");
        }
    }

    private fun emailOrUsernameChecking(){
        if(inputEmailOrUsername.isEmpty()){
            _uiState.value = _uiState.value.copy(usernameError = "Please fill out this field");
        }
        else{
            _uiState.value = _uiState.value.copy(usernameError = "");
        }
    }

    private fun validateInput(onSuccess: () -> Unit) {
        passwordChecking();
        emailOrUsernameChecking();

        if(
            _uiState.value.usernameError.isEmpty() &&
            _uiState.value.passwordError.isEmpty() &&
            _uiState.value.error.isEmpty()
        ){
            _uiState.value = _uiState.value.copy(error = "", usernameError = "", passwordError = "");
            onSuccess();
        }
    }

    fun submitLogin(onSuccess: (User) -> Unit) {
        validateInput {
            userController.getUser(inputEmailOrUsername, inputPassword){
                if(it == null){
                    _uiState.value = _uiState.value.copy(error = "incorrect Username or password");
                }
                else if(it.role != "merchant"){
                    _uiState.value = _uiState.value.copy(error = "You are not a merchant");
                }
                else{
                    onSuccess(it);
                }
            }

        }
    }
}