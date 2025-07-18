package com.leopardseal.inventorymanagerapp.ui.main.invite

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import coil.compose.AsyncImage
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R

import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Invite


@Composable
fun InviteScreen(
    viewModel: InviteViewModel = hiltViewModel(),
    onSkip: () -> Unit,
    onUnauthorized: () -> Unit
) {
    val invitesResource by viewModel.inviteResponse.collectAsState()
    val inviteAccepted by viewModel.acceptResponse.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(inviteAccepted){
        when(inviteAccepted){
            is Resource.Success -> {
                Toast.makeText(context, "invite accepted!", Toast.LENGTH_SHORT).show()
                viewModel.resetAcceptResponse()
                viewModel.getInvites()
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
        if(invitesResource is Resource.Success<List<Invite>> && (invitesResource as Resource.Success<List<Invite>>).value.isEmpty()){
            Toast.makeText(context, "no invites found", Toast.LENGTH_SHORT).show()
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
                items((invitesResource as Resource.Success<List<Invite>>).value) { invite ->
                    InviteCard(invite = invite, onAccept = { viewModel.acceptInvite(invite) })
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
fun InviteCard(invite: Invite, onAccept: () -> Unit) {
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
                model = invite.org.imageUrl,
                contentDescription = invite.org.name,
                placeholder = painterResource(R.drawable.default_img),
                error = painterResource(R.drawable.default_img),
                fallback = painterResource(R.drawable.default_img),
                modifier = Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = invite.org.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = invite.role.role,
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
