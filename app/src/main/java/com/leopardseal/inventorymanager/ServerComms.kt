package com.leopardseal.inventorymanager

import android.widget.Toast
import androidx.compose.material3.rememberTimePickerState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanager.entity.SignInResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.Json

class ServerComms constructor(tokenCredential: GoogleIdTokenCredential){

    var client: HttpClient = HttpClient(CIO)
    var serverAd: String = "http://192.168.68.73:8080/"
    var tokenCredential :GoogleIdTokenCredential = tokenCredential;

    suspend fun signIn(): SignInResponse? {
        val verify : io.ktor.client.statement.HttpResponse = client.get(serverAd+"signIn" ){
            headers{
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                append(HttpHeaders.Authorization, tokenCredential.idToken)
                append(HttpHeaders.Accept, "application/json")
            }
        }
        if((verify.status.value == HttpStatus.SC_BAD_REQUEST) or (verify.status.value == HttpStatus.SC_INTERNAL_SERVER_ERROR)){
            throw InternalError();
        } else if(verify.status.value == HttpStatus.SC_UNAUTHORIZED){
            throw IllegalAccessError();
        }else{
            val str = verify.bodyAsText()
            var response: SignInResponse? = null;
            try {
                response = Json.decodeFromString<SignInResponse>(str)
            }catch (e: Exception){
                throw InternalError();
            }
            return response
        }
        return null;
    }
}