package com.leopardseal.inventorymanagerapp.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController

import androidx.navigation.compose.currentBackStackEntryAsState

import com.leopardseal.inventorymanagerapp.R




@Composable
fun NavigationDrawerContent(navController: NavController, closeDrawer: () -> Unit) {
    Column(modifier = Modifier.fillMaxHeight()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Image(
                    painterResource(R.drawable.default_img),
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Account", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // Menu
        NavigationDrawerItem(
            label = { Text("Organizations") },
            selected = false,
            onClick = {
                navController.navigate("org")
                closeDrawer()
            })
        NavigationDrawerItem(label = { Text("Invites") }, selected = false, onClick = {
            navController.navigate("invite")
            closeDrawer()
        })
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination == "item",
            onClick = { navController.navigate("item") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Items") },
            label = { Text("Items") }
        )
        NavigationBarItem(
            selected = currentDestination == "box",
            onClick = { navController.navigate("box") },
            icon = { Icon(Icons.Default.Email, contentDescription = "Boxes") },
            label = { Text("Boxes") }
        )
        NavigationBarItem(
            selected = currentDestination == "location",
            onClick = { navController.navigate("location") },
            icon = { Icon(Icons.Default.Email, contentDescription = "Locations") },
            label = { Text("Locations") }
        )
    }
}

@Composable
fun ExpandingFab(
    onAddItem: () -> Unit,
    onAddBox: () -> Unit,
    onAddLocation: () -> Unit,
    onBarcode: () -> Unit
) {
    // Track expansion state
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(end = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.BottomEnd,

        ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 80.dp) // Provide space above main FAB
        ) {
            // Action 1
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                MiniFabWithLabel(Icons.Default.Home, "Add new Item", onClick = {onAddItem()})
            }

            // Action 2
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                MiniFabWithLabel(Icons.Default.Home, "Add new Box", onClick = {onAddBox()})
            }

            // Action 3
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                MiniFabWithLabel(Icons.Default.Home, "Add new Location", onClick = {onAddLocation()})
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                MiniFabWithLabel(Icons.Default.Home, "ScanBarcode", onClick = {onBarcode()})
            }
        }

        // Main FAB at bottom right
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = "Toggle Actions"
            )
        }
    }
}

@Composable
fun MiniFabWithLabel(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(8.dp))
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(icon, contentDescription = label)
        }
    }
}