package id.ac.umn.kevinsorensen.bengkelonline.views.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.RegistrationViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase = Firebase;
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                WrapperFunction(
                    this,
                    RegistrationViewModel(firebase)
                );
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

    RegisterUser(
        username = registrationViewModel.inputUsername,
        email = registrationViewModel.inputEmail,
        password = registrationViewModel.inputPassword,
        confirmPassword = registrationViewModel.inputConfirmPassword,
        usernameErrorMessage = registerState.usernameError,
        emailErrorMessage = registerState.emailError,
        passwordErrorMessage = registerState.passwordError,
        confirmPasswordErrorMessage = registerState.confirmPasswordError,
        inputPasswordVisibility = registrationViewModel.inputPasswordVisibility,
        error = registerState.error,
        togglePasswordVisibility = {
            registrationViewModel.togglePasswordVisibility()
        },

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
        onRegister = {
            registrationViewModel.register {
                // finish activity
                activity.finish();
            }
        },
        onRegisterMerchant = {
            registrationViewModel.register("merchant") {
                // finish activity
                activity.finish();
            }
        },
        onBackButton = {
            activity.finish();
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUser(
    password: String = "",
    inputPasswordVisibility: Boolean = false,
    togglePasswordVisibility: () -> Unit,
    username: String = "",
    email: String = "",
    confirmPassword: String = "",

    onBackButton: () -> Unit = {},
    onUsernameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    onRegisterMerchant: () -> Unit = {},

    error: String = "",
    usernameErrorMessage: String = "",
    emailErrorMessage: String = "",
    passwordErrorMessage: String = "",
    confirmPasswordErrorMessage: String = "",
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = {
                onBackButton();
            }
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Go Back")
            }
        }
    )
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Register",
            fontSize = 20.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )
        TextField (
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            isError = usernameErrorMessage.isNotEmpty(),
            supportingText = {
                Text(usernameErrorMessage)
            },
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if(username.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null,
                        Modifier.clickable {
                            onUsernameChange("")
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                focusedLabelColor = Color.Blue,
                focusedLeadingIconColor = Color.Blue,
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Blue,
                unfocusedLabelColor = Color.Blue,
                unfocusedLeadingIconColor = Color.Blue
            ),
            textStyle = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField (
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            isError = emailErrorMessage.isNotEmpty(),
            supportingText = {
                Text(emailErrorMessage)
            },
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_email_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if(email.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null,
                        Modifier.clickable {
                            onEmailChange("")
                        }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                focusedLabelColor = Color.Blue,
                focusedLeadingIconColor = Color.Blue,
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Blue,
                unfocusedLabelColor = Color.Blue,
                unfocusedLeadingIconColor = Color.Blue
            ),
            textStyle = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            isError = passwordErrorMessage.isNotEmpty(),
            supportingText = {
                Text(passwordErrorMessage)
            },
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = if (inputPasswordVisibility) {
                        painterResource(id = R.drawable.baseline_visibility_24)
                    }
                    else {
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    }
                    Icon (
                        painter = visibilityIcon,
                        contentDescription = null,
                        Modifier.clickable {
                            togglePasswordVisibility();
                        }
                    )
                }
            },
            visualTransformation = if (inputPasswordVisibility) {
                VisualTransformation.None
            }
            else {
                PasswordVisualTransformation()
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                focusedLabelColor = Color.Blue,
                focusedLeadingIconColor = Color.Blue,
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Blue,
                unfocusedLabelColor = Color.Blue,
                unfocusedLeadingIconColor = Color.Blue
            ),
            textStyle = TextStyle (
                color = Color.Blue,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.None
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password") },
            isError = confirmPasswordErrorMessage.isNotEmpty(),
            supportingText = {
                Text(confirmPasswordErrorMessage)
            },
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = if (inputPasswordVisibility) {
                        painterResource(id = R.drawable.baseline_visibility_24)
                    }
                    else {
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    }
                    Icon (
                        painter = visibilityIcon,
                        contentDescription = null,
                        Modifier.clickable {
                            togglePasswordVisibility();
                        }
                    )
                }
            },
            visualTransformation = if (inputPasswordVisibility) {
                VisualTransformation.None
            }
            else {
                PasswordVisualTransformation()
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                focusedLabelColor = Color.Blue,
                focusedLeadingIconColor = Color.Blue,
                containerColor = Color.White,
                unfocusedIndicatorColor = Color.Blue,
                unfocusedLabelColor = Color.Blue,
                unfocusedLeadingIconColor = Color.Blue
            ),
            textStyle = TextStyle (
                color = Color.Blue,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.None
            ),
            singleLine = true
        )
        if(error != "")
            Text(error, color = Color.Red);
        Spacer(modifier = Modifier.height(40.dp))
        Button (
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            onClick = onRegister,
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {
            Text(
                text = "Register",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button (
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            onClick = onRegisterMerchant,
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {
            Text(
                text = "Register as Merchant",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Text(
                text = "Already Have an Account?  ",
                color = Color.Gray
            )
            ClickableText(
                text = AnnotatedString("Login"),
                onClick = {
                    onBackButton();
                },
                style = TextStyle(
                    color = Color.Blue,
                    // fontSize = 26.sp,
                    // fontFamily = FontFamily.Cursive
                )
            )
        }
    }
}