package com.leopardseal.inventorymanagerapp.ui.main.manageorg

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.dto.ManageOrgsResponse
import com.leopardseal.inventorymanagerapp.data.responses.dto.UserResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageOrgScreen(
    viewModel : ManageOrgViewModel = hiltViewModel(),
    userEmail : String?,
    onUnauthorized : () -> Unit
){
    val userResource by viewModel.userResponse.collectAsState()
    val removeResponse by viewModel.removeResponse.collectAsState()
    val removeInviteResponse by viewModel.removeInviteResponse.collectAsState()
    val inviteResponse by viewModel.inviteResponse.collectAsState()

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("stocker") }

    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@gmail.com", ignoreCase = true) && inviteResponse !is Resource.Loading
    when(removeResponse){
        is Resource.Success<*> ->{
            viewModel.resetRemoveFlag()
        }
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure -> {
            val error = removeResponse as Resource.Failure
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
        else -> {}
    }
    when(removeInviteResponse){
        is Resource.Success<*> ->{
            viewModel.resetRemoveInviteFlag()
        }
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure -> {
            val error = removeInviteResponse as Resource.Failure
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
        else -> {}
    }
    when(inviteResponse){
        is Resource.Success<*> ->{
            viewModel.resetInviteFlag()
            Toast.makeText(context, "$email invited as $selectedRole", Toast.LENGTH_SHORT).show()
            email = ""
            selectedRole = "stocker"
        }
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure -> {
            val error = inviteResponse as Resource.Failure
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
        else -> {}
    }
    when(userResource){
        is Resource.Success<*> ->{
            val users = (userResource as Resource.Success<ManageOrgsResponse>).value.users
            val invites = (userResource as Resource.Success<ManageOrgsResponse>).value.invites

            val refreshState = rememberPullToRefreshState()
            PullToRefreshBox(
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = {viewModel.getUserList()}
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    item {
                        Column {
                            Text(
                                "Invite User:",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Gmail Address") },
                                placeholder = { Text("example@gmail.com") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Email
                                ),
                                isError = email.isNotEmpty() && !isEmailValid,
                                singleLine = true,
                                supportingText = {
                                    if (email.isNotEmpty() && !isEmailValid) {
                                        Text("Please enter a valid Gmail address.")
                                    }
                                }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RoleDropdownMenu(
                                    modifier = Modifier.weight(1f),
                                    selectedSize = selectedRole,
                                    onSizeSelected = { selectedRole = it }
                                )

                                Button(
                                    modifier = Modifier.wrapContentWidth(),
                                    onClick = {
                                        viewModel.invite(
                                            UserResponse(
                                                id = null,
                                                email = email, imgUrl = null, role = selectedRole
                                            )
                                        )
                                    },
                                    enabled = isEmailValid
                                ) {
                                    Text("Invite")
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            "Members:",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
                        )
                    }
                    items(users) { user ->
                        UserListCard(user, { viewModel.removeUser(it) }, user.email != userEmail)
                    }
                    if (invites.isNotEmpty()) {
                        item {
                            Text(
                                "Invites:",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
                            )
                        }
                        items(invites) { invite ->
                            UserListCard(invite, { viewModel.removeInvite(invite) }, true)
                        }
                    }
                }
            }
        }
        is Resource.Loading, Resource.Init -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Failure -> {
            val error = userResource as Resource.Failure
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
@Composable
fun UserListCard(user: UserResponse, onUserClick : (user: UserResponse) -> Unit, removeEnabled : Boolean){
    Column(modifier = Modifier.background(Color.White)){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Column {
                Row {
                    AsyncImage(
                        model = user.imgUrl,
                        contentDescription = user.email,
                        placeholder = painterResource(R.drawable.default_img),
                        error = painterResource(R.drawable.default_img),
                        fallback = painterResource(R.drawable.default_img),
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = user.email, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = user.role, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(removeEnabled) {
                    IconButton(onClick = { onUserClick(user) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove user",
                            modifier = Modifier.padding(end = 8.dp),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
    }
}
@Composable
fun RoleDropdownMenu(
    modifier: Modifier = Modifier,
    selectedSize: String,
    onSizeSelected: (String) -> Unit
) {
    val sizeOptions = listOf("admin", "stocker", "contributer")
    var expanded by remember { mutableStateOf(false) }
    val textFieldSize = remember { mutableStateOf(IntSize.Zero) }

    val interactionSource = remember { MutableInteractionSource() }
    val indication = LocalIndication.current
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedSize,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                expanded = true
                            }
                        }
                    }
                },
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Role") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize.value = coordinates.size
                },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.value.width.toDp() }) // match width
        ) {
            sizeOptions.forEach { sizeOpt ->
                DropdownMenuItem(
                    text = { Text(sizeOpt) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .indication(interactionSource, indication)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {}
                        ),
                    onClick = {
                        onSizeSelected(sizeOpt)
                        expanded = false
                    }
                )
            }
        }
    }
}