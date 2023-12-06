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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.ForgotPasswordViewModel
import id.ac.umn.kevinsorensen.bengkelonline.views.MainActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.main.ui.theme.BengkelOnlineTheme
import id.ac.umn.kevinsorensen.bengkelonline.views.user.HomeUser

class ForgotPassActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebase = Firebase
        val activity = this;
        setContent {
            ForgotPassWrapper(this, forgotPasswordViewModel = ForgotPasswordViewModel(firebase));
        }
    }
}

@Composable
fun ForgotPassWrapper(
    activity: Activity,
    forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
){
    val uiState by forgotPasswordViewModel.uiState.collectAsState();

    ForgotPass(
        emailOrUsername = forgotPasswordViewModel.emailOrUsername,
        password = forgotPasswordViewModel.newPassword,
        updateEmailOrUsername = {forgotPasswordViewModel.updateEmailOrUsername(it)} ,
        updatePassword = {forgotPasswordViewModel.updateNewPassword(it)},
        passwordVisible = forgotPasswordViewModel.passwordVisibility,
        togglePasswordVisibility = { forgotPasswordViewModel.togglePasswordVisibility() },
        onLogin = {
              forgotPasswordViewModel.submitForgotPassword {
                  activity.finish()
              }
        },
        confirmPassword = forgotPasswordViewModel.confirmPassword,
        updateConfirmPassword = {forgotPasswordViewModel.updateConfirmPassword(it)},
        errorMessage = uiState.error,
        emailOrUsernameError = uiState.emailOrUsernameError,
        passwordError = uiState.passwordError,
        confirmPasswordError = uiState.confirmPasswordError,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPass(
    errorMessage: String = "",
    emailOrUsernameError: String = "",
    passwordError: String = "",
    confirmPasswordError: String = "",
    user: User? = null,
    emailOrUsername: String = "",
    password: String = "",
    passwordVisible: Boolean = false,
    confirmPassword: String = "",
    updateEmailOrUsername: (String) -> Unit,
    updatePassword: (String) -> Unit,
    togglePasswordVisibility: () -> Unit,
    updateConfirmPassword: (String) -> Unit,
    onLogin: () -> Unit,
) {
    val mContext = LocalContext.current

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
            text = "Reset Password",
            fontSize = 20.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )
        TextField(
            value = emailOrUsername,
            onValueChange = {
                updateEmailOrUsername(it)
            },
            label = {
                Text(text = "Email or Username")
            },
            isError = emailOrUsernameError.isNotEmpty(),
            supportingText = {
                Text(text = emailOrUsernameError)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (emailOrUsername.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null,
                        Modifier.clickable {
                            updateEmailOrUsername("")
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
            onValueChange = {
                updatePassword(it)
            },
            label = {
                Text(text = "New Password")
            },
            supportingText = {
                Text(text = passwordError)
            },
            isError = passwordError.isNotEmpty(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = if (passwordVisible) {
                        painterResource(id = R.drawable.baseline_visibility_24)
                    } else {
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    }
                    Icon(
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
            } else {
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
            textStyle = TextStyle(
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
            onValueChange = {
                updateConfirmPassword(it)
            },
            label = {
                Text(text = "Confirm Password")
            },
            isError = confirmPasswordError.isNotEmpty(),
            supportingText = {
                Text(text = confirmPasswordError)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = if (passwordVisible) {
                        painterResource(id = R.drawable.baseline_visibility_24)
                    } else {
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    }
                    Icon(
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
            } else {
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
            textStyle = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.None
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            modifier = Modifier
                .height(50.dp)
                .width(300.dp),
            onClick = {
                onLogin();
            },
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {
            Text(
                text = "Change Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if(errorMessage.isNotEmpty()){
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 16.sp,
            );
        }
    }
}