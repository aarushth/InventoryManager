package com.leopardseal.inventorymanagerapp.ui.main.item.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Tag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelectScreen(
    viewModel: TagSelectViewModel = hiltViewModel(),
    navController: NavController
) {
    val tagResponse by viewModel.tagResponse.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val hasChanges by viewModel.hasChanges.collectAsState()
    val selectedTags = viewModel.selectedTags

    var showDialog by remember { mutableStateOf(false) }
    var newTagText by remember { mutableStateOf("") }

    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.fetchTags() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (tagResponse is Resource.Success && (tagResponse as Resource.Success<List<Tag>>).value.isNotEmpty())
                        "Tags:" else "No Tags found. Press + to add a new Tag",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f),
                    maxLines = 2
                )
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Tag")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (tagResponse) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                is Resource.Success -> {
                    val tags = (tagResponse as Resource.Success<List<Tag>>).value
                    if (tags.isNotEmpty()) {
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            tags.forEach { tag ->
                                val isSelected = viewModel.isSelected(tag)
                                val backgroundColor = if (isSelected) Color(0xFF007BFF) else Color.Transparent
                                val borderColor = if (isSelected) Color.Transparent else Color(0xFF007BFF)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(backgroundColor)
                                        .border(
                                            width = 2.dp,
                                            color = borderColor,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clickable { viewModel.toggleTagSelection(tag) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = tag.name,
                                        color = if (isSelected) Color.White else Color(0xFF007BFF),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                is Resource.Failure -> {
                    Text("Failed to load tags.", color = Color.Red)
                }

                else -> {}
            }

            Spacer(modifier = Modifier.weight(1f))

            if (hasChanges) {
                Button(
                    onClick = {
                        viewModel.syncSelectionWithBackend {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Save Changes")
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add New Tag") },
                text = {
                    OutlinedTextField(
                        value = newTagText,
                        onValueChange = { if (it.length <= 32) newTagText = it },
                        label = { Text("Tag name (max 32 chars)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newTagText.isNotBlank()) {
                                viewModel.addTag(newTagText.trim())
                                newTagText = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}