package com.leopardseal.inventorymanagerapp.ui.main

import ItemEditScreen
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.activity.compose.setContent

import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import coil.compose.AsyncImage

import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.leopardseal.inventorymanagerapp.data.network.Resource
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.leopardseal.inventorymanagerapp.data.responses.dto.SaveResponse
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.main.barcode.BarcodeScannerWithPermissionScreen
import com.leopardseal.inventorymanagerapp.ui.main.invite.InviteScreen
import com.leopardseal.inventorymanagerapp.ui.main.invite.InviteViewModel
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemScreen
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemViewModel
import com.leopardseal.inventorymanagerapp.ui.main.item.expanded.ItemExpandedScreen
import com.leopardseal.inventorymanagerapp.ui.main.item.expanded.ItemExpandedViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

import com.leopardseal.inventorymanagerapp.ui.main.org.OrgScreen
import com.leopardseal.inventorymanagerapp.ui.main.org.OrgViewModel
import com.leopardseal.inventorymanagerapp.ui.startNewActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences =  UserPreferences(this)
        setContent {
            MainScreen()
        }
    }

    fun login(){
        startNewActivity(LoginActivity::class.java)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: ""

        val drawerGesturesEnabled = currentRoute !in listOf("org", "invite", "barcode")
        val fabEnabled = currentRoute in listOf("item", "box", "location")


        val orgName by userPreferences.orgName.collectAsState(initial = "")
        val screenTitle = when (currentRoute) {
            "org" -> "Chose An Organization"
            "invite" -> "You Have Been Invited"
            else -> orgName
        }
        val orgImg by userPreferences.orgImg.collectAsState(initial = "")

        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        ModalNavigationDrawer(
            drawerContent = {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(screenWidth * 0.5f) // Open halfway
                ) {
                    NavigationDrawerContent(
                        navController,
                        closeDrawer = { scope.launch { drawerState.close() } })
                }
            },
            drawerState = drawerState,
            gesturesEnabled = drawerGesturesEnabled
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                //orgImage
                                if (drawerGesturesEnabled) {
                                    AsyncImage(
                                        model = if (orgImg!!.isNotBlank()) orgImg else null,
                                        contentDescription = null,
                                        placeholder = painterResource(R.drawable.default_img),
                                        error = painterResource(R.drawable.default_img),
                                        fallback = painterResource(R.drawable.default_img),
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(40.dp)
                                            .clip(CircleShape)
                                    )
                                }else{

                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = screenTitle!!)


                            }
                        },
                        navigationIcon = {
                            if (drawerGesturesEnabled) {
                                IconButton(onClick = {
                                    scope.launch { drawerState.open() }
                                }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* search click */ }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    )
                },
                bottomBar = {
                    if (drawerGesturesEnabled) {
                        BottomNavBar(navController)
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MyNavHost(navController)
                    if(fabEnabled) {
                        ExpandingFab(
                            onAddItem = { navController.navigate("itemEdit/${-1L}") },
                            onAddBox = { /* Navigate to box */ },
                            onAddLocation = { /* Navigate to location */ },
                            onBarcode = {navController.navigate("barcode")}
                        )
                    }
                }
            }
            LaunchedEffect(currentRoute) {
                if (!drawerGesturesEnabled && drawerState.isOpen) {
                    drawerState.close()
                }
            }
        }
    }
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

    @SuppressLint("UnrememberedGetBackStackEntry")
    @Composable
    fun MyNavHost(navController: NavHostController){
        NavHost(
            navController = navController,
            startDestination = "invite", // or any starting destination
        ) {
            composable(route = "invite") {
                val viewModel: InviteViewModel = hiltViewModel()
                val invitesResource by viewModel.inviteResponse.collectAsState()
                val inviteAccepted by viewModel.acceptResponse.collectAsState()
                viewModel.getInvites()
                InviteScreen(
                    invitesResource = invitesResource,
                    inviteAccepted = inviteAccepted,
                    onAccept = { invite -> viewModel.acceptInvite(invite) },
                    onAccepted = {
                        viewModel.resetAcceptResponse()
                        viewModel.getInvites()
                    },
                    onSkip = {
                        navController.navigate("item")
                    },
                    onUnauthorized = { login() })
            }

            composable("org") {
                val viewModel: OrgViewModel = hiltViewModel()
                val orgsResource by viewModel.orgResponse.collectAsState()
                val orgSaved by viewModel.orgSaved.collectAsState()
                viewModel.getOrgs()
                OrgScreen(
                    orgsResource = orgsResource,
                    orgSaved = orgSaved,
                    onOrgSelected = { org -> viewModel.saveOrg(org) },
                    onOrgSaved = {
                        viewModel.resetOrgSavedFlag()
                        navController.navigate("item")
                    },
                    onUnauthorized = { login() })
            }
            composable("item") {
                val viewModel: ItemViewModel = hiltViewModel()
                val itemState by viewModel.itemResponse.collectAsState()
                viewModel.getItems()
                ItemScreen(
                    itemState = itemState,
                    onRefresh = { viewModel.getItems() },
                    onItemClick = { itemId -> navController.navigate("itemExpanded/${itemId}") },
                    onUnauthorized = { login() })
            }
            composable(
                route = "itemExpanded/{item_id}",
                arguments = listOf(navArgument("item_id") { type = NavType.LongType })
            ) {
                val viewModel: ItemExpandedViewModel = hiltViewModel()
                val item by viewModel.item.collectAsState()
                val updateResponse by viewModel.updateResponse.collectAsState()
                ItemExpandedScreen(
                    item = item,
                    updateResponse = updateResponse,
                    onEdit = { itemId -> navController.navigate("itemEdit/${itemId}") },
                    onUpdate = { currentQuantity ->
                        viewModel.updateItemQuantity(
                            currentQuantity
                        )
                    },
                    onUnauthorized = { login() },
                )
            }
            composable(
                route = "itemEdit/{item_id}",
                arguments = listOf(navArgument("item_id") { type = NavType.LongType })
            ) { backStackEntry ->
                val viewModel: ItemExpandedViewModel = hiltViewModel()
                val item by viewModel.item.collectAsState()
                val updateResponse by viewModel.updateResponse.collectAsState()
                val uploadImgResponse by viewModel.uploadResult.collectAsState()
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(backStackEntry.destination.route ?: "itemEdit/{item_id}")
                }


                ItemEditScreen(
                    item = item,
                    updateResponse = updateResponse,
                    uploadImgResponse = uploadImgResponse,
                    parentEntry = parentEntry,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onUnauthorized = { login() },
                    onSave = { updatedItem, imageUri ->
                        viewModel.saveOrUpdateItem(updatedItem, (imageUri != null))
                    },
                    onSaveComplete = {imageUri ->
                        if((updateResponse as Resource.Success).value.imageUrl == null){
                            navController.navigate("itemExpanded/${(updateResponse as Resource.Success).value.id}")
                        }else{

                            viewModel.uploadImage(
                                (updateResponse as Resource.Success).value.imageUrl!!,
                                imageUri!!,
                                this@MainActivity
                            )
                        }
                    },
                    onImageSaved = {navController.navigate("itemExpanded/${item!!.id}") },
                    onScanBarcodeClick = { navController.navigate("barcode") }
                )
            }
            composable("box") {

            }
            composable("location") {}
            composable("barcode") {
                BarcodeScannerWithPermissionScreen(
                    onBarcodeScanned = { barcode ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("barcode", barcode)

                        navController.navigateUp()

                    },
                    onPermissionDenied = {
                        Toast.makeText(LocalContext.current, "Camera permission denied", Toast.LENGTH_SHORT).show()
                        navController.navigateUp()
                    }
                )}

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


}