package com.leopardseal.inventorymanagerapp.ui.main

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.ui.barcodeIcon
import com.leopardseal.inventorymanagerapp.ui.boxIcon
import com.leopardseal.inventorymanagerapp.ui.itemIcon
import com.leopardseal.inventorymanagerapp.ui.orgIcon

@Composable
fun NavigationDrawerContent(navController: NavController, userImg : String?, userEmail : String?, role : String?, closeDrawer: () -> Unit, logout: () -> Unit) {
    Column(modifier = Modifier.fillMaxHeight()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                AsyncImage(
                    model = userImg,
                    contentDescription = "user profile image",
                    placeholder = painterResource(R.drawable.default_img),
                    error = painterResource(R.drawable.default_img),
                    fallback = painterResource(R.drawable.default_img),
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(userEmail?:"", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Menu
        NavigationDrawerItem(
            icon = { Icon(orgIcon, contentDescription = "Organizations") },
            label = { Text("Organizations") },
            selected = false,
            onClick = {
                closeDrawer()
                navController.navigate("org")
            }
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Email, contentDescription = "Invites") },
            label = { Text("Invites") },
            selected = false,
            onClick = {
                closeDrawer()
                navController.navigate("invite")
                
            }
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        if(role == "admin" && currentRoute != "org" && currentRoute != "invite"){
            HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.MoreVert, contentDescription = "Manage Org") },
                label = { Text("Manage Organization") },
                selected = false,
                onClick = {
                    closeDrawer()
                    navController.navigate("manage_org")
                    
                }
            )
        }
        Spacer(modifier = Modifier.weight(0.5f))
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = false,
            onClick = {
                closeDrawer()
                navController.navigate("settings")
            }
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Red) },
            label = { Text("Logout", color = Color.Red) },
            selected = false,
            onClick = {
                closeDrawer()
                logout()
            }
        )
        Spacer(modifier = Modifier.weight(0.2f))
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        NavigationBarItem(
            selected = currentDestination == "item",
            onClick = { navController.navigate("item") {
                popUpTo("item") {
                    inclusive = true
                }
                launchSingleTop = true } },
            icon = { Icon(itemIcon, contentDescription = "Items") },
            label = { Text("Items") }
        )
        NavigationBarItem(
            selected = currentDestination == "box",
            onClick = { navController.navigate("box") {
                popUpTo("box") {
                    inclusive = true
                }
                launchSingleTop = true } },
            icon = { Icon(boxIcon, contentDescription = "Boxes") },
            label = { Text("Boxes") }
        )
        NavigationBarItem(
            selected = currentDestination == "location",
            onClick = { navController.navigate("location") {
                popUpTo("location") {
                    inclusive = true
                }
                launchSingleTop = true } },
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Locations") },
            label = { Text("Locations") }
        )
    }
}

@Composable
fun ExpandingFab(
    onBarcode: () -> Unit
) {
//    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(end = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.BottomEnd,

        ) {
//        Column(
//            horizontalAlignment = Alignment.End,
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            modifier = Modifier.padding(bottom = 80.dp) // Provide space above main FAB
//        ) {
//            // Action 1
//            AnimatedVisibility(
//                visible = expanded,
//                enter = fadeIn() + slideInVertically { it },
//                exit = fadeOut() + slideOutVertically { it }
//            ) {
//                MiniFabWithLabel(itemIcon, "Add new Item", onClick = {onAddItem()})
//            }
//
//            // Action 2
//            AnimatedVisibility(
//                visible = expanded,
//                enter = fadeIn() + slideInVertically { it },
//                exit = fadeOut() + slideOutVertically { it }
//            ) {
//                MiniFabWithLabel(boxIcon, "Add new Box", onClick = {onAddBox()})
//            }
//
//            // Action 3
//            AnimatedVisibility(
//                visible = expanded,
//                enter = fadeIn() + slideInVertically { it },
//                exit = fadeOut() + slideOutVertically { it }
//            ) {
//                MiniFabWithLabel(Icons.Default.LocationOn, "Add new Location", onClick = {onAddLocation()})
//            }
//            AnimatedVisibility(
//                visible = expanded,
//                enter = fadeIn() + slideInVertically { it },
//                exit = fadeOut() + slideOutVertically { it }
//            ) {
//                MiniFabWithLabel(barcodeIcon, "ScanBarcode", onClick = {onBarcode()})
//            }
//        }

        // Main FAB at bottom right
        FloatingActionButton(
            onClick = onBarcode,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = barcodeIcon,
                contentDescription = "Scan Barcode"
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
