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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.RegistrationViewModel
import id.ac.umn.kevinsorensen.bengkelonline.views.MainActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.user.HomeUser

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
        inputPasswordVisibility = registrationViewModel.inputPasswordVisibility,
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
        togglePasswordVisibility = {
            registrationViewModel.togglePasswordVisibility()
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUser(
    errorMessage: String = "",
    user: User? = null,
    emailOrUsername: String = "",
    password: String = "",
    inputPasswordVisibility: Boolean = false,
    passwordVisible: Boolean = true,
    togglePasswordVisibility: () -> Unit,
    username: String = "",
    email: String = "",
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
    val mContext = LocalContext.current
    // placeholder for real error, don't use toast
    if(errorMessage.isNotEmpty()){
        Toast.makeText(LocalContext.current, "error: $errorMessage", Toast.LENGTH_LONG);
    }

    // if user defined, immediately switch activity
    if(user != null){
        val intent = Intent(LocalContext.current, HomeUser::class.java)
            .putExtra("userId", user.id)
            .putExtra("username", user.username);

        LocalContext.current.startActivity(
            intent
        )

    }
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = {
                mContext.startActivity(Intent(mContext, MainActivity()::class.java))
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
            label = { Text(usernameErrorMessage ?: "Username") },
            isError = usernameErrorMessage != null,
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
            label = { Text(emailErrorMessage ?: "Email") },
            isError = emailErrorMessage != null,
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
            label = { Text(passwordErrorMessage ?: "Password") },
            isError = passwordErrorMessage != null,
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
            label = { Text(confirmPasswordErrorMessage ?: "Confirm Password") },
            isError = confirmPasswordErrorMessage != null,
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = if (passwordVisible) {
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
            visualTransformation = if (passwordVisible) {
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
        if(error != null)
            Text(error);
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
        Row {
            Text(
                text = "Already Have an Account?  ",
                color = Color.Gray
            )
            ClickableText(
                text = AnnotatedString("Login"),
                onClick = {
                    mContext.startActivity(Intent(mContext, MainActivity()::class.java))
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