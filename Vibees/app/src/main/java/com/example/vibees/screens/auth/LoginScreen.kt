package com.example.vibees.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonElevation
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import com.example.vibees.GlobalAppState
import com.example.vibees.R
import com.example.vibees.graphs.PartyScreen
import com.example.vibees.utils.hashToUUID
import com.example.vibees.screens.home.MaterialIconDimension


@Composable
fun LoginTextField(
    modifier: Modifier = Modifier,
    text: String,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    onChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(0.90f)
            .padding(10.dp)
            .background(Color.White),
        value = text,
        onValueChange = onChange,
        leadingIcon = leadingIcon,
        textStyle = TextStyle(fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black
        ),
        placeholder = {
            androidx.compose.material.Text(
                text = placeholder,
                style = TextStyle(fontSize = 18.sp, color = Color.DarkGray)
            )
        }
    )
}

@Composable
fun HalfCutCircle() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(bottomStart = 100.dp, bottomEnd = 100.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(70.dp)
    ) {
        Text(
            text = "Half Cut Circle",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun LoginScreen(
    onClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    var userID by GlobalAppState::UserID
    var userName by GlobalAppState::UserName
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_black_yellow),
            contentDescription = "Vibees Logo",
            modifier = Modifier.width(400.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.saly_7),
                contentDescription = "Welcome Illustration",
            )

            Spacer(modifier = Modifier.height(40.dp))

//            Text(text = "Let's get Vibees",
//                style = MaterialTheme.typography.headlineSmall,
//                color = MaterialTheme.colorScheme.tertiary,
//            textAlign = TextAlign.Center
//            )

            OutlinedButton(
                onClick = { onCreateAccountClick() },
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "Google Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                    )
                    Text(text = "Continue with Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }


            }


//            LoginTextField(
//                text = username,
//                placeholder = "Username*",
//                onChange = {
//                    username = it
//                },
//                imeAction = ImeAction.Next,//Show next as IME button
//                keyboardType = KeyboardType.Text, //Plain text keyboard
//                keyBoardActions = KeyboardActions(
//                    onNext = {
//                        focusManager.moveFocus(FocusDirection.Down)
//                    }
//                )
//            )
//
//            LoginTextField(
//                text = password,
//                placeholder = "Password*",
//                onChange = {
//                    password = it
//                },
//                imeAction = ImeAction.Next,//Show next as IME button
//                keyboardType = KeyboardType.Text, //Plain text keyboard
//                keyBoardActions = KeyboardActions(
//                    onNext = {
//                        focusManager.moveFocus(FocusDirection.Down)
//                    }
//                )
//            )

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth(0.90f)
//                    .padding(10.dp),
//                horizontalArrangement = Arrangement.End
//            ) {
//                Text(
//                    modifier = Modifier.clickable { onForgotClick() },
//                    text = "Forgot Password?",
//                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                    fontWeight = FontWeight.Medium,
//                    color = Color.White
//                )
//            }

//            androidx.compose.material.Button(
//                onClick = { onSignUpClick() },
//                modifier = Modifier,
//                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
//            ) {
//                androidx.compose.material.Text(
//                    text = "Sign In",
//                    color = Color.Black,
//                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            Text(
//                text = "OR",
//                color = MaterialTheme.colorScheme.primary,
//                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                fontWeight = FontWeight.Bold
//            )
//
//            androidx.compose.material.Button(
//                onClick = { onCreateAccountClick() },
//                modifier = Modifier,
//                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
//            ) {
//                androidx.compose.material.Text(
//                    text = "Create an account",
//                    color = Color.Black,
//                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                androidx.compose.material.Button(
//                    onClick = {
//                        userID = "6515c9f8-57f1-406f-8707-20033dcd764e"
//                        userName = "Christian"
//                        onClick()
//                    },
//                    modifier = Modifier.padding(20.dp),
//                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
//                ) {
//                    androidx.compose.material.Text(
//                        text = "Christian User",
//                        color = Color.Black,
//                    )
//                }
//
//                androidx.compose.material.Button(
//                    onClick = {
//                        userID = "5bdfc21f-ea15-43b3-9654-093f15d63ba7"
//                        userName = "Shawn"
//                        onClick()
//                    },
//                    modifier = Modifier.padding(20.dp),
//                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.primary)
//                ) {
//                    androidx.compose.material.Text(
//                        text = "Shawn User",
//                        color = Color.Black
//                    )
//                }
//            }
        }
    }
}