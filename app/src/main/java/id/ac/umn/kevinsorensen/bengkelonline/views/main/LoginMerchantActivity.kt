package id.ac.umn.kevinsorensen.bengkelonline.views.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.MerchantLoginViewModel
import id.ac.umn.kevinsorensen.bengkelonline.views.MainActivity
import id.ac.umn.kevinsorensen.bengkelonline.views.merchant.HomeMerchant

class LoginMerchantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginMerchantWrapper(this, MerchantLoginViewModel(Firebase))
        }
    }
}

@Composable
fun LoginMerchantWrapper(
    activity: Activity,
    loginMerchantLoginViewModel: MerchantLoginViewModel = viewModel()
)
{
    val uiState by loginMerchantLoginViewModel.uiState.collectAsState();

    LoginMerchant(
        updateEmailOrUsername = {
            loginMerchantLoginViewModel.updateEmailOrUsername(it);
        },
        emailOrUsername = loginMerchantLoginViewModel.inputEmailOrUsername,
        togglePasswordVisibility = {
            loginMerchantLoginViewModel.togglePasswordVisibility();
        },
        passwordVisible = loginMerchantLoginViewModel.inputPasswordVisibility,
        password = loginMerchantLoginViewModel.inputPassword,
        updatePassword = {
            loginMerchantLoginViewModel.updatePassword(it);
        },
        onLogin = {
            loginMerchantLoginViewModel.submitLogin {
                if(it != null) {
                    val intent = Intent(activity, HomeMerchant::class.java)
                    activity.startActivity(intent)
                }
            }
        },
        emailOrUsernameError = uiState.usernameError,
        passwordError = uiState.passwordError,
        errorMessage = uiState.error,
    );
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginMerchant(
    errorMessage: String = "",
    emailOrUsernameError: String = "",
    passwordError: String = "",
    user: User? = null,
    emailOrUsername: String = "",
    updatePassword: (String) -> Unit,
    password: String = "",
    passwordVisible: Boolean = true,
    updateEmailOrUsername: (String) -> Unit,
    togglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit,
) {
    val mContext = LocalContext.current

    // if user defined, immediately switch activity
    if(user != null){
        val intent = Intent(LocalContext.current, HomeMerchant::class.java)

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
            text = "Login Merchant",
            fontSize = 20.sp,
            color = Color.Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 40.dp)
        )
        TextField (
            value = emailOrUsername,
            onValueChange = {
                updateEmailOrUsername(it)
            },
            isError = emailOrUsernameError.isNotEmpty(),
            supportingText = {
                Text(text = emailOrUsernameError)
            },
            label = {
                Text(text = "Username")
            },
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if(emailOrUsername.isNotEmpty()) {
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
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
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
                Text(text="Password")
            },
            isError = passwordError.isNotEmpty(),
            supportingText = {
                Text(text = passwordError)
            },
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
                imeAction = androidx.compose.ui.text.input.ImeAction.None
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            ClickableText(
                text = AnnotatedString("Forgot Password"),
                onClick = {
                    mContext.startActivity(Intent(mContext, ForgotPassActivity::class.java))
                },
                style = TextStyle(
                    color = Color.Gray,
                    // fontSize = 26.sp,
                    // fontFamily = FontFamily.Cursive
                )

            )
            Spacer(modifier = Modifier.width(75.dp))
            ClickableText(
                text = AnnotatedString("Log in As User"),
                onClick = {
                    mContext.startActivity(Intent(mContext, MainActivity::class.java))
                },
                style = TextStyle(
                    color = Color.Gray,
                    // fontSize = 26.sp,
                    // fontFamily = FontFamily.Cursive
                )
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Button (
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            onClick = {
                onLogin();
            },
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {
            Text(
                text = "Log in",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Text(
                text = "Want to Become Our Merchant?  ",
                color = Color.Gray
            )
            ClickableText(
                text = AnnotatedString("Register Account"),
                onClick = {
                    mContext.startActivity(Intent(mContext, RegisterMerchantActivity()::class.java))
                },
                style = TextStyle(
                    color = Color.Blue,
                    // fontSize = 26.sp,
                    // fontFamily = FontFamily.Cursive
                )
            )
        }

        if(errorMessage.isNotEmpty()){
            Text(
                text = errorMessage,
                color = Color.Red
            )
        }
    }
}