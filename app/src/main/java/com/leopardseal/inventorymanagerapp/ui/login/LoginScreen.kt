package com.leopardseal.inventorymanagerapp.ui.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.ui.main.MainActivity


@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val loginResponse by viewModel.loginResponse.collectAsState()
    val tokenSaved by viewModel.tokenSaved.collectAsState()

    LaunchedEffect(tokenSaved) {
        if (tokenSaved) {
            viewModel.resetTokenSavedFlag()
            context.startActivity(
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
    }
    val email = viewModel.getEmail()

    LaunchedEffect(email) {
        Log.e("Login", "email  $email")
        Log.e("Login", "response $loginResponse")
        if(loginResponse is Resource.Init){
            Log.e("Login", "response $loginResponse")
            viewModel.triggerLogin(context, !email.isNullOrEmpty())
        }
    }
    LaunchedEffect(loginResponse) {
        when (val result = loginResponse) {
            is Resource.Failure -> {
                when {
                    result.isNetworkError -> {
                        Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
                        viewModel.resetLoginResponse()
                    }
                    result.errorCode == HttpStatus.SC_UNAUTHORIZED -> {
                        navController.navigate("fail")
                    }
                    else -> {
                        Toast.makeText(context, "An error occurred, please try again later", Toast.LENGTH_LONG).show()
                        viewModel.resetLoginResponse()
                    }
                }
            }
            else -> Unit
        }
    }
    LoginContent(
        onSignInClicked = { viewModel.triggerLogin(context, false) },
        isLoading = loginResponse is Resource.Loading
    )
}

@Composable
fun LoginContent(onSignInClicked: () -> Unit, isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = "Inventory Manager", modifier = Modifier.fillMaxWidth().padding(10.dp), textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(20.dp))
            SignInWithGoogleButton(onClick = { onSignInClicked()}, isLoading = isLoading)
            if (isLoading) {
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun SignInWithGoogleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Sign in with Google",
    isLoading: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        enabled = !isLoading
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google logo",
            modifier = Modifier.width(24.dp).height(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            fontSize = 20.sp
        )

    }
}