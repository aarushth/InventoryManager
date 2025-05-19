package com.leopardseal.inventorymanagerapp.ui.main.invite

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R

import com.leopardseal.inventorymanagerapp.data.network.Resource

import com.leopardseal.inventorymanagerapp.data.responses.Orgs


@Composable
fun InviteScreen(
    invitesResource: Resource<List<Orgs>>,
    inviteAccepted: Resource<Unit>,
    onAccept: (invite : Orgs) -> Unit,
    onAccepted: () ->Unit,
    onSkip: () -> Unit,
    onUnauthorized: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(inviteAccepted){
        when(inviteAccepted){
            is Resource.Success -> {
                Toast.makeText(context, "invite accepted!", Toast.LENGTH_SHORT).show()
                onAccepted()
            }
            is Resource.Failure -> {
                if((inviteAccepted as Resource.Failure).isNetworkError) {
                    Toast.makeText(context,"please check your internet and try again", Toast.LENGTH_LONG).show()
                }else if((inviteAccepted as Resource.Failure).errorCode == HttpStatus.SC_UNAUTHORIZED){
                    onUnauthorized()
                }else{
                    Toast.makeText(context,"an error occured, please try again later", Toast.LENGTH_LONG).show()
                }
            }
            else ->{}
        }
    }
    LaunchedEffect(invitesResource){
        if(invitesResource is Resource.Success<List<Orgs>> && (invitesResource as Resource.Success<List<Orgs>>).value.isEmpty()){
            onSkip()
        }
    }
    if(invitesResource is Resource.Success) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((invitesResource as Resource.Success<List<Orgs>>).value) { invite ->
                    InviteCard(invite = invite, onAccept = { onAccept(invite) })
                }
            }
            Button(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip")
            }
        }
    }
}

@Composable
fun InviteCard(invite: Orgs, onAccept: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(invite.imageUrl) // Make sure Invite has imageUrl
                    .placeholder(R.drawable.default_img)
                    .error(R.drawable.default_img)
                    .crossfade(true)
                    .build(),
                contentDescription = "Invite Org Image",
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = invite.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = invite.role,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .height(40.dp)
            ) {
                Text("Accept")
            }
        }
    }
}
