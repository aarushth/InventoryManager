package com.leopardseal.inventorymanagerapp.ui.main.org


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Orgs


@Composable
fun OrgScreen(
    viewModel: OrgViewModel = hiltViewModel(),
    navController: NavController,
    onUnauthorized: () -> Unit
) {
    val orgsResource by viewModel.orgResponse.collectAsState()
    val orgSaved by viewModel.orgSaved.collectAsState()
    val context = LocalContext.current
    // Handle saved org navigation
    LaunchedEffect(orgSaved) {
        if (orgSaved) {
            viewModel.resetOrgSavedFlag()
            navController.navigate("item")
        }
    }

    when (orgsResource) {
        is Resource.Loading, Resource.Init -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Failure -> {
            val error = orgsResource as Resource.Failure
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

        is Resource.Success -> {
            val orgs = (orgsResource as Resource.Success<List<Orgs>>).value
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(orgs) { org ->
                    OrgCard(org = org, onClick = { viewModel.saveOrg(org) })
                }
            }
        }
    }
}
@Composable
fun OrgCard(org: Orgs, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = org.imageUrl,
                contentDescription = org.name,
                placeholder = painterResource(R.drawable.default_img),
                error = painterResource(R.drawable.default_img),
                fallback = painterResource(R.drawable.default_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = org.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = org.role,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
