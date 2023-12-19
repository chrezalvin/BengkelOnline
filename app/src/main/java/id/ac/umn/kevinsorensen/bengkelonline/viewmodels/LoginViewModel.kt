package id.ac.umn.kevinsorensen.bengkelonline.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.SettingsStore
import id.ac.umn.kevinsorensen.bengkelonline.api.UserController
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val error: String = "",
    val passwordError: String = "",
    val emailOrUsernameError: String = "",
    val pageIndex: Int = 0
)

class LoginViewModel(private val settingsStore: SettingsStore, onUserCached: (User) -> Unit): ViewModel() {
    private val _uiState = MutableStateFlow(LoginState());
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow();

    var inputPassword by mutableStateOf("")
        private set;

    var inputPhoneNumber by mutableStateOf("")
        private set;

    var passwordVisibility by mutableStateOf(false)
        private set;

    var inputEmailOrUsername by mutableStateOf("")
        private set;

    private val userController = UserController(Firebase);

    init {
        resetInputs();

        viewModelScope.launch {
            settingsStore.text.collect {
                if(it != ""){
                    userController.getUserById(it){user ->
                        if(user != null){
                            onUserCached(user);
                        }
                        else
                            // resets this id because it's not valid id
                            resetCachedUserId()
                    }
                }
            }
        }
    }

    fun saveUserId(userId: String){
        viewModelScope.launch {
            settingsStore.saveText(userId);
        }
    }

    fun resetCachedUserId(){
        saveUserId("");
    }

    fun updatePassword(password: String) {
        inputPassword = password;
    }

    fun updateEmailOrUsername(emailOrUsername: String) {
        inputEmailOrUsername = emailOrUsername;
    }

    fun updatePhoneNumber(phoneNumber: String) {
        inputPhoneNumber = phoneNumber;
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
        inputPhoneNumber = "";
    }

    private fun passwordChecking(){
        if(inputPassword.isEmpty()){
            _uiState.value = _uiState.value.copy(passwordError = "Please fill out this field");
        }
        else{
            _uiState.value = _uiState.value.copy(passwordError = "");
        }
    }

    private fun phoneNumberChecking(){
        if(inputPhoneNumber.isEmpty()){
            _uiState.value = _uiState.value.copy(emailOrUsernameError = "Please fill out this field");
        }
        else{
            _uiState.value = _uiState.value.copy(emailOrUsernameError = "");
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

    private fun validateInputsPhone(onSuccess: () -> Unit){
        passwordChecking();
        phoneNumberChecking();

        if(
            _uiState.value.emailOrUsernameError.isEmpty() &&
            _uiState.value.passwordError.isEmpty()
        ){
            _uiState.value = _uiState.value.copy(error = "", emailOrUsernameError = "", passwordError = "");
            onSuccess();
        }
    }

    fun login() {
        validateInputs {
            userController.getUser(inputEmailOrUsername, inputPassword) {
                if (it != null) {
                    resetInputs();
                    // cache the user id
                    saveUserId(it.id);
                } else {
                    _uiState.update { loginState ->
                        loginState.copy(error = "Incorrect username or password!");
                    }
                }
            }
        }
    }

    fun loginPhone(){
        validateInputsPhone {
            userController.getUserFromPhoneNumber(
                inputPhoneNumber,
                inputPassword,
                onSuccess = { user ->
                    if(user != null){
                        resetInputs();
                        // cache the user id
                        saveUserId(user.id);
                    }
                    else{
                        _uiState.update { loginState ->
                            loginState.copy(error = "Incorrect phone number or password!");
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update { loginState ->
                        loginState.copy(error = error);
                    }
                })
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