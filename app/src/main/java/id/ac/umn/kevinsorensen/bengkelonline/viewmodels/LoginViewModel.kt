package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val error: String = "",
    val passwordError: String = "",
    val emailOrUsernameError: String = "",
    val pageIndex: Int = 0
)

class LoginViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(LoginState());
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow();

    var inputPassword by mutableStateOf("")
        private set;

    var passwordVisibility by mutableStateOf(false)
        private set;

    var inputEmailOrUsername by mutableStateOf("")
        private set;

    private val userController = UserController(Firebase);

    init {
        resetInputs();
    }

    fun updatePassword(password: String) {
        inputPassword = password;
    }

    fun updateEmailOrUsername(emailOrUsername: String) {
        inputEmailOrUsername = emailOrUsername;
    }

    fun togglePasswordVisibility() {
        passwordVisibility = !passwordVisibility;
    }

    fun changePage(index: Int) {
        _uiState.update {
            it.copy(pageIndex = index)
        }
    }

    private fun resetInputs() {
        inputPassword = "";
        inputEmailOrUsername = "";
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
            _uiState.value = _uiState.value.copy(emailOrUsernameError = "Please fill out this field");
        }
        else{
            _uiState.value = _uiState.value.copy(emailOrUsernameError = "");
        }
    }

    private fun validateInputs(onSuccess: () -> Unit){
        passwordChecking();
        emailOrUsernameChecking();

        if(
            _uiState.value.emailOrUsernameError.isEmpty() &&
            _uiState.value.passwordError.isEmpty()
        ){
            _uiState.value = _uiState.value.copy(error = "", emailOrUsernameError = "", passwordError = "");
            onSuccess();
        }
    }

    fun login(onSuccess: (User) -> Unit) {
        validateInputs {
            userController.getUser(inputEmailOrUsername, inputPassword) {
                if (it != null) {
                    resetInputs();
                    onSuccess(it);
                } else {
                    _uiState.update { loginState ->
                        loginState.copy(error = "Incorrect username or password!");
                    }
                }
            }
        }
    }

    fun merchantLogin(onSuccess: (User) -> Unit){
        validateInputs {
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