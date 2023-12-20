package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegistrationUiState(
    val error: String = "",
    val usernameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val confirmPasswordError: String = "",
)
class RegistrationViewModel(db: Firebase): ViewModel() {
    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState());
    val uiState = _uiState.asStateFlow();

    private val userController = UserController(db);

    var inputUsername by mutableStateOf("")
        private set;

    var inputEmail by mutableStateOf("")
        private set;

    var inputPassword by mutableStateOf("")
        private set;
    var inputPasswordVisibility by mutableStateOf(false)
        private set;

    var inputConfirmPassword by mutableStateOf("")
        private set;

    var isMerchant by mutableStateOf(false)
        private set;

    fun updateUsername(username: String) {
        inputUsername = username;
    }

    fun updateEmail(email: String) {
        inputEmail = email;
    }

    fun updatePassword(password: String) {
        inputPassword = password;
    }

    fun updateConfirmPassword(confirmPassword: String) {
        inputConfirmPassword = confirmPassword;
    }

    fun toggleIsMerchant() {
        isMerchant = !isMerchant;
    }

    fun togglePasswordVisibility() {
        inputPasswordVisibility = !inputPasswordVisibility;
    }

    fun resetInputs() {
        inputUsername = "";
        inputEmail = "";
        inputPassword = "";
        inputConfirmPassword = "";
        inputPasswordVisibility = false;
    }

    private fun validateUsername(){
        if(inputUsername.isEmpty())
            _uiState.value = _uiState.value.copy(usernameError = "Please fill out the username first!");
        else
            _uiState.value = _uiState.value.copy(usernameError = "");
    }

    private fun validateEmail(){
        if(inputEmail.isEmpty())
            _uiState.value = _uiState.value.copy(emailError = "Please fill out the email first!");
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches())
            _uiState.value = _uiState.value.copy(emailError = "Please fill out the email correctly!");
        else
            _uiState.value = _uiState.value.copy(emailError = "");
    }

    private fun validatePassword(){
        if(inputPassword.isEmpty())
            _uiState.value = _uiState.value.copy(passwordError = "Please fill out the password first!");
        else
            _uiState.value = _uiState.value.copy(passwordError = "");
    }

    private fun validateConfirmPassword(){
        if(inputConfirmPassword.isEmpty())
            _uiState.value = _uiState.value.copy(confirmPasswordError = "Please fill out the confirm password first!");
        else if(inputConfirmPassword != inputPassword)
            _uiState.value = _uiState.value.copy(confirmPasswordError = "Confirm password does not match!");
        else
            _uiState.value = _uiState.value.copy(confirmPasswordError = "");
    }

    private fun validateInputs(onSuccess: () -> Unit) {
        validateUsername();
        validateEmail();
        validatePassword();
        validateConfirmPassword();

        if (
            _uiState.value.usernameError.isEmpty() &&
            _uiState.value.emailError.isEmpty() &&
            _uiState.value.passwordError.isEmpty() &&
            _uiState.value.confirmPasswordError.isEmpty()
        )
            onSuccess();
    }

    fun register(userRole: String = "user", onSuccessRegistration: () -> Unit) {
        validateInputs(){
            val role = if (userRole == "merchant") "merchant" else "user";
            userController.addUser(
                inputEmail,
                inputUsername,
                inputPassword,
                role
            ) { success, message ->
                if (success) {
                    resetInputs();
                    _uiState.value = _uiState.value.copy(error = "");
                    onSuccessRegistration();
                } else
                    _uiState.value = _uiState.value.copy(error = message ?: "");
            }
        }
    }

}