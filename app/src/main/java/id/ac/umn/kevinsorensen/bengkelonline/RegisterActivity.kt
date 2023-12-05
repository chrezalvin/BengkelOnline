package id.ac.umn.kevinsorensen.bengkelonline

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.ui.theme.BengkelOnlineTheme
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.RegistrationViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebase = Firebase;

        setContent {
            BengkelOnlineTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WrapperFunction(this, RegistrationViewModel(firebase));
                }
            }
        }
    }
}

@Composable
fun WrapperFunction(
    activity: Activity,
    registrationViewModel: RegistrationViewModel = viewModel()
){
    val registerState by registrationViewModel.uiState.collectAsState();

    RegisterUsers(
        username = registrationViewModel.inputUsername,
        email = registrationViewModel.inputEmail,
        password = registrationViewModel.inputPassword,
        confirmPassword = registrationViewModel.inputConfirmPassword,
        onUsernameChange = {
            registrationViewModel.updateUsername(it);
        },
        onEmailChange = {
            registrationViewModel.updateEmail(it);
        },
        onPasswordChange = {
            registrationViewModel.updatePassword(it);
        },
        onConfirmPasswordChange = {
            registrationViewModel.updateConfirmPassword(it);
        },
        usernameErrorMessage = registerState.usernameError,
        emailErrorMessage = registerState.emailError,
        passwordErrorMessage = registerState.passwordError,
        confirmPasswordErrorMessage = registerState.confirmPasswordError,
        onRegister = {
            registrationViewModel.register {
                // finish activity
                if(registerState.error == null)
                    return@register;
                else
                    activity.finish();
            }
        },
        error = registerState.error,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUsers(
    username: String = "",
    email: String = "",
    password: String = "",
    confirmPassword: String = "",
    onUsernameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    usernameErrorMessage: String? = null,
    emailErrorMessage: String? = null,
    passwordErrorMessage: String? = null,
    confirmPasswordErrorMessage: String? = null,
    onRegister: () -> Unit = {},
    error: String? = null,
) {
    Text("Hello Register User")
    Column {
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(usernameErrorMessage ?: "Username") },
            isError = usernameErrorMessage != null,
        )

        TextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(emailErrorMessage ?: "Email") },
            isError = emailErrorMessage != null,
        )

        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(passwordErrorMessage ?: "Password") },
            isError = passwordErrorMessage != null,
        )

        TextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text(confirmPasswordErrorMessage ?: "Confirm Password") },
            isError = confirmPasswordErrorMessage != null,
        )

        if(error != null)
            Text(error);

        Button(onClick = onRegister) {
            Text("Register")
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BengkelOnlineTheme {
        Greeting("Android")
    }
}