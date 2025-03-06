package com.leopardseal.inventorymanager


//import io.ktor.http.ContentType.Application.Json
//import io.ktor.serialization.kotlinx.json.json
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
import com.leopardseal.inventorymanager.entity.SignInResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.*


class LoginActivity : AppCompatActivity() {

    lateinit var signInButton : Button
    lateinit var serverComms: ServerComms
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        signInButton = findViewById<Button>(R.id.GoogleSignInBtn);

        signInButton.setOnClickListener {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId("354946788079-aermo39q0o3gshsgf46oqhkicovqcuo8.apps.googleusercontent.com")
                .setAutoSelectEnabled(false)
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
                        serverComms = ServerComms(googleIdTokenCredential)
                        var response :SignInResponse? = null
                        try{
                            response = serverComms.signIn()
                        }catch(e: IllegalAccessError){
                            Toast.makeText(this@LoginActivity, "user not found in system", Toast.LENGTH_LONG).show()
                        }catch (e: InternalError){
                            Toast.makeText(this@LoginActivity, "an error occured.", Toast.LENGTH_LONG).show()
                        }
                        //TODO send response to Main activity, show orgs fragment
                        if(response != null) {
                            var role = ""
                            for (r in response.roles) {
                                if (r.id == response.userRoles[0].roleId) {
                                    role = r.role
                                }
                            }
                            Toast.makeText(this@LoginActivity, response.myUser.email + " in org " + response.orgs[0].name + " as " + role, Toast.LENGTH_LONG).show()
                            val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtra(name:"SignInResponse", value = response)
                            intent.putExtra(name:"ServerComms", value = serverComms)
                            this@LoginActivity.startActivity(intent)
                        }

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
}