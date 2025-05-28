package com.leopardseal.inventorymanagerapp.ui.main.setting

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.ui.main.org.SettingViewModel

@Composable
fun SettingScreen(
    viewModel : SettingViewModel = hiltViewModel(),
    onUnauthorized : () -> Unit
){
    val versionResource by viewModel.versionResponse.collectAsState()
    val context = LocalContext.current
    when(versionResource){
        is Resource.Success<String> ->{
            Text("Server version : ${(versionResource as Resource.Success<String>).value}")
        }
        is Resource.Loading, Resource.Init -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure -> {
            val error = versionResource as Resource.Failure
            LaunchedEffect(error) {
                if (error.isNetworkError) {
                    Toast.makeText(context, "Please check your internet and try again", Toast.LENGTH_LONG).show()
                } else if (error.errorCode == HttpStatus.SC_UNAUTHORIZED) {
                    onUnauthorized()
                } else {
                    Toast.makeText(context, "An error occurred, please try again later", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}