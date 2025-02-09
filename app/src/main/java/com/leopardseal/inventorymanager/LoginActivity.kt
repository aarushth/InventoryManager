package com.leopardseal.inventorymanager


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.crashlytics.buildtools.api.net.Constants.Http
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
//import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpHeaders
//import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json


class LoginActivity : AppCompatActivity() {
    lateinit var client: HttpClient
    lateinit var signInButton : Button
    companion object {
        var globalVar = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        client = HttpClient(CIO)


        signInButton = findViewById<Button>(R.id.GoogleSignInBtn);

        signInButton.setOnClickListener {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
                .setAutoSelectEnabled(true)
                .build()

            val credentialManager = CredentialManager.create(this)
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            runBlocking {

                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@LoginActivity,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "something failed")
                }
            }
        }
    }
    private suspend fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        try{
                            val verify : io.ktor.client.statement.HttpResponse = client.get("http://192.168.68.77:8080/signIn" ){
                                headers{
                                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                                    append(HttpHeaders.Authorization, googleIdTokenCredential.idToken)
                                }
                            }
                            if((verify.status.value == HttpStatus.SC_BAD_REQUEST) or (verify.status.value == HttpStatus.SC_INTERNAL_SERVER_ERROR)){
                                Toast.makeText(this@LoginActivity, "an error occured.", Toast.LENGTH_LONG).show()
                            } else if(verify.status.value == HttpStatus.SC_NOT_FOUND){
                                Toast.makeText(this@LoginActivity, "user not found in system", Toast.LENGTH_LONG).show()
                            }else{
                                val user: User = Json.decodeFromString<User>(verify.bodyAsText())
                                Toast.makeText(this@LoginActivity, "logged in as " + user.email, Toast.LENGTH_LONG).show()
                            }
                //            var body : User = Json.decodeF
//                            if(verify.body() != "") {
//                                val intent = Intent(this, MainActivity::class.java)
//                                intent.
//                                startActivity(intent)
//                            }

                        }catch(_: Error){}
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }
            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
}