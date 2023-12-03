package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.myapi.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val user: User? = null,
    val error: String = "",
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

    fun resetInputs() {
        inputPassword = "";
        inputEmailOrUsername = "";
    }

    fun login() {
        if (inputEmailOrUsername.isEmpty() || inputPassword.isEmpty()) {
            _uiState.update { loginState ->
                loginState.copy(error = "Please fill out the username and password first!");
            }
        }

        userController.getUser(inputEmailOrUsername, inputPassword) {
            if (it != null) {
                resetInputs();
                _uiState.update { loginState ->
                    Log.d("LoginViewModel", "user: $it");
                    loginState.copy(user = it)
                }
            } else {
                _uiState.update { loginState ->
                    loginState.copy(error = "Incorrect username or password!");
                }
            }
        }
    }
}