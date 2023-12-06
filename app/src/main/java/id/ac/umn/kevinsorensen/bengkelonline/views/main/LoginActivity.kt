package id.ac.umn.kevinsorensen.bengkelonline.views.main

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import id.ac.umn.kevinsorensen.bengkelonline.R
import id.ac.umn.kevinsorensen.bengkelonline.datamodel.User
import id.ac.umn.kevinsorensen.bengkelonline.viewmodels.LoginViewModel
import id.ac.umn.kevinsorensen.bengkelonline.views.user.HomeUser

@Composable
fun LoginActivity(activity: Activity) {
    Column (
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxWidth()
            .height(267.dp)
    ) {
        Image (
            painter = painterResource(id = R.drawable.main_pict),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ){
        TabLayout(activity)
    }
}

@Composable
fun TabLayout(
    activity: Activity,
    loginViewModel: LoginViewModel = viewModel()
) {
    val loginState by loginViewModel.uiState.collectAsState();

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 275.dp)
            .background(Color.White)
    ) {
        LoginUser(
            onRegisterUser = {
                val intent = Intent(activity, RegisterActivity::class.java);
                activity.startActivity(intent);
            },
            onLogin = {
                loginViewModel.login() {
                    val intent = Intent(activity, HomeUser::class.java)
                        .putExtra("userId", it.id);

                    activity.startActivity(
                        intent
                    )
                }
            },
            onMerchantLogin = {
                loginViewModel.merchantLogin {
                    val intent = Intent(activity, LoginMerchantActivity::class.java)
                        .putExtra("userId", it.id);
                    activity.startActivity(intent);
                }
            },
            onForgotPassword = {
                val intent = Intent(activity, ForgotPassActivity::class.java);
                activity.startActivity(intent);
            },
            emailOrUsername = loginViewModel.inputEmailOrUsername,
            password = loginViewModel.inputPassword,
            passwordVisible = loginViewModel.passwordVisibility,
            togglePasswordVisibility = { loginViewModel.togglePasswordVisibility() },
            updateEmailOrUsername = { loginViewModel.updateEmailOrUsername(it) },
            updatePassword = { loginViewModel.updatePassword(it) },
            errorMessage = loginState.error,
            passwordError = loginState.passwordError,
            emailOrUsernameError = loginState.emailOrUsernameError,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUser(
    emailOrUsername: String = "",
    password: String = "",
    passwordVisible: Boolean = true,
    updateEmailOrUsername: (String) -> Unit = {},
    updatePassword: (String) -> Unit = {},
    togglePasswordVisibility: () -> Unit = {},
    onLogin: () -> Unit = {},
    onRegisterUser: () -> Unit = {},
    onMerchantLogin: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    errorMessage: String = "",
    passwordError: String = "",
    emailOrUsernameError: String = ""
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Login",
            fontSize = 20.sp,
            color = Color.Blue,
            fontWeight = Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        TextField (
            value = emailOrUsername,
            onValueChange = {
                updateEmailOrUsername(it)
            },
            label = {
                Text(text = "Username")
            },
            isError = emailOrUsernameError.isNotEmpty(),
            supportingText = {
                Text(text = emailOrUsernameError)
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
                imeAction = ImeAction.None
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            ClickableText(
                text = AnnotatedString("Forgot Password"),
                onClick = {
                    onForgotPassword();
                },
                style = TextStyle(
                    color = Color.Gray,
                    // fontSize = 26.sp,
                    // fontFamily = FontFamily.Cursive
                )

            )
            Spacer(modifier = Modifier.width(50.dp))
            ClickableText(
                text = AnnotatedString("Log in As Merchant"),
                onClick = {
                    onMerchantLogin();
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
                text = "New User?  ",
                color = Color.Gray
            )
            ClickableText(
                text = AnnotatedString("Register Account"),
                onClick = {
                    onRegisterUser();
                },
                style = TextStyle(
                    color = Color.Blue,
                    // fontSize = 26.sp,
                    // fontFamily = FontFamily.Cursive
                )
            )
        }
        if(errorMessage.isNotEmpty())
            Text(text = errorMessage, color = Color.Red);
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginMerchant() {
    var showDialog by remember { mutableStateOf(false) }
    val emty by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val mContext = LocalContext.current
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        TextField (
            value = name,
            onValueChange = {
                name = it
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
                if(name.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = null,
                        Modifier.clickable { name = emty.toString() }
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
                password = it
            },
            label = {
                Text(text="Password")
            },
            leadingIcon = {
                Icon (
                    painter = painterResource(id = R.drawable.baseline_lock_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (password.isNotEmpty()) {
                    val visibilityIcon = if (passwordVisibility) {
                        painterResource(id = R.drawable.baseline_visibility_24)
                    }
                    else {
                        painterResource(id = R.drawable.baseline_visibility_off_24)
                    }
                    Icon (
                        painter = visibilityIcon,
                        contentDescription = null,
                        Modifier.clickable {
                            passwordVisibility =! passwordVisibility
                        }
                    )
                }
            },
            visualTransformation = if (passwordVisibility) {
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
        Spacer(modifier = Modifier.height(40.dp))
        Button (
            modifier = Modifier
                .height(50.dp)
                .width(200.dp),
            onClick = {
                mContext.startActivity(Intent(mContext, HomeMerchant::class.java))
            },
            colors = ButtonDefaults.buttonColors(
                Color.Blue
            )
        ) {

        }
    }
}

 */
