package com.leopardseal.inventorymanagerapp.ui.main

import BoxEditScreen
import ItemEditScreen
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.activity.compose.setContent

import androidx.appcompat.app.AppCompatActivity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.main.camera.BarcodeScannerWithPermissionScreen
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxScreen
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxViewModel
import com.leopardseal.inventorymanagerapp.ui.main.box.expanded.BoxExpandedViewModel
import com.leopardseal.inventorymanagerapp.ui.main.camera.CameraScreen
import com.leopardseal.inventorymanagerapp.ui.main.expanded.BoxExpandedScreen
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
import kotlinx.coroutines.flow.withIndex
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
        val orgImg by userPreferences.orgImg.collectAsState(initial = "")
        val userImg by userPreferences.userImg.collectAsState(initial = "")
        val userEmail by userPreferences.userEmail.collectAsState(initial = "")

        val screenTitle = when (currentRoute) {
            "org" -> "Chose An Organization"
            "invite" -> "You Have Been Invited"
            "camera" -> "Take a picture"
            "barcode" -> "Scan Barcode"
            else -> orgName
        }
        

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
                        userImg,
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
                                        model = orgImg,
                                        contentDescription = null,
                                        placeholder = painterResource(R.drawable.default_img),
                                        error = painterResource(R.drawable.default_img),
                                        fallback = painterResource(R.drawable.default_img),
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(40.dp)
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = screenTitle?:"")


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
                            onAddBox = { navController.navigate("boxEdit/${-1L}") },
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
                val isRefreshing by viewModel.isRefreshing.collectAsState()
                viewModel.getItems()
                ItemScreen(
                    itemState = itemState,
                    isRefreshing = isRefreshing,
                    isCardSizeTogglable = true,
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
                val isRefreshing by viewModel.isRefreshing.collectAsState()
                ItemExpandedScreen(
                    item = item,
                    updateResponse = updateResponse,
                    isRefreshing = isRefreshing,
                    onRefresh = {viewModel.getItem()} 
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
            ) {
                val viewModel: ItemExpandedViewModel = hiltViewModel()
                val item by viewModel.item.collectAsState()
                val updateResponse by viewModel.updateResponse.collectAsState()
                val uploadImgResponse by viewModel.uploadResult.collectAsState()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                ItemEditScreen(
                    item = item,
                    updateResponse = updateResponse,
                    uploadImgResponse = uploadImgResponse,
                    currentBackStackEntry = currentBackStackEntry,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onImageCapture = {navController.navigate("camera")},
                    onUnauthorized = { login() },
                    onSave = { updatedItem, imageChanged ->
                        viewModel.saveOrUpdateItem(updatedItem, imageChanged)
                    },
                    onSaveComplete = {imageFile ->
                        if((updateResponse as Resource.Success).value.imageUrl == null){
                            viewModel.resetUpdateResponse()
                            navController.navigate("itemExpanded/${(updateResponse as Resource.Success).value.id}")
                        }else{

                            viewModel.uploadImage(
                                (updateResponse as Resource.Success).value.imageUrl!!,
                                imageFile!!
                            )
                        }
                    },
                    onImageSaved = {
                        viewModel.resetUploadFlag()
                        navController.navigate("itemExpanded/${item!!.id}") },
                    onScanBarcodeClick = { navController.navigate("barcode") }
                )
            }
            composable("box") {
                val viewModel: BoxViewModel = hiltViewModel()
                val boxState by viewModel.boxResponse.collectAsState()
                val isRefreshing by viewModel.isRefreshing.collectAsState()
                viewModel.getBoxes()
                
                BoxScreen(
                    boxState = boxState,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.getBoxes() },
                    onBoxClick = { boxId -> navController.navigate("boxExpanded/${boxId}") },
                    onUnauthorized = { login() })
            }
            composable(
                route = "boxExpanded/{box_id}",
                arguments = listOf(navArgument("box_id") { type = NavType.LongType })
            ) {
                val viewModel: BoxExpandedViewModel = hiltViewModel()
                val box by viewModel.box.collectAsState()
                val isRefreshing by viewModel.isRefreshing.collectAsState()

                BoxExpandedScreen(
                    box = box,
                    isRefreshing = isRefreshing,
                    onRefresh = {viewModel.getBox()}
                    onEdit = { boxId -> navController.navigate("boxEdit/${boxId}") },
                )
            }
            composable(
                route = "boxEdit/{box_id}",
                arguments = listOf(navArgument("box_id") { type = NavType.LongType })
            ) {
                val viewModel: BoxExpandedViewModel = hiltViewModel()
                val box by viewModel.box.collectAsState()
                val updateResponse by viewModel.updateResponse.collectAsState()
                val uploadImgResponse by viewModel.uploadResult.collectAsState()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                BoxEditScreen(
                    box = box,
                    updateResponse = updateResponse,
                    uploadImgResponse = uploadImgResponse,
                    currentBackStackEntry = currentBackStackEntry,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onImageCapture = {navController.navigate("camera")},
                    onUnauthorized = { login() },
                    onSave = { updatedBox, uploadChanged ->
                        viewModel.saveOrUpdateBox(updatedBox, uploadChanged)
                    },
                    onSaveComplete = {imageFile ->
                        if((updateResponse as Resource.Success).value.imageUrl == null){
                            viewModel.resetUpdateResponse()
                            navController.navigate("boxExpanded/${(updateResponse as Resource.Success).value.id}")
                        }else{
                            viewModel.uploadImage(
                                (updateResponse as Resource.Success).value.imageUrl!!,
                                imageFile!!
                            )
                        }
                    },
                    onImageSaved = {
                        viewModel.resetUploadFlag()
                        navController.navigate("boxExpanded/${box!!.id}") },
                    onScanBarcodeClick = { navController.navigate("barcode") }
                )
            }
            composable("location") {
                val viewModel: LocationViewModel = hiltViewModel()
                val locationState by viewModel.locationResponse.collectAsState()
                val isRefreshing by viewModel.isRefreshing.collectAsState()
                viewModel.getLocations()
                
                LocationScreen(
                    locationState = locationState,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.getLocations() },
                    onLocationClick = { locationId -> navController.navigate("locationExpanded/${locationId}") },
                    onUnauthorized = { login() })
            }
            composable(
                route = "locationExpanded/{location_id}",
                arguments = listOf(navArgument("location_id") { type = NavType.LongType })
            ) {
                val viewModel: LocationExpandedViewModel = hiltViewModel()
                val location by viewModel.location.collectAsState()
                val isRefreshing by viewModel.isRefreshing.collectAsState()
                LocationExpandedScreen(
                    location = location,
                    isRefreshing = isRefreshing,
                    onRefresh = {viewModel.getLocation()}
                    onEdit = { locationId -> navController.navigate("locationEdit/${locationId}") },
                )
            }
            composable(
                route = "locationEdit/{location_id}",
                arguments = listOf(navArgument("location_id") { type = NavType.LongType })
            ) {
                val viewModel: LocationExpandedViewModel = hiltViewModel()
                val location by viewModel.location.collectAsState()
                val updateResponse by viewModel.updateResponse.collectAsState()
                val uploadImgResponse by viewModel.uploadResult.collectAsState()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                LocationEditScreen(
                    location = location,
                    updateResponse = updateResponse,
                    uploadImgResponse = uploadImgResponse,
                    currentBackStackEntry = currentBackStackEntry,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onImageCapture = {navController.navigate("camera")},
                    onUnauthorized = { login() },
                    onSave = { updatedLocation, uploadChanged ->
                        viewModel.saveOrUpdateLocation(updatedLocation, uploadChanged)
                    },
                    onSaveComplete = {imageFile ->
                        if((updateResponse as Resource.Success).value.imageUrl == null){
                            viewModel.resetUpdateResponse()
                            navController.navigate("locationExpanded/${(updateResponse as Resource.Success).value.id}")
                        }else{
                            viewModel.uploadImage(
                                (updateResponse as Resource.Success).value.imageUrl!!,
                                imageFile!!
                            )
                        }
                    },
                    onImageSaved = {
                        viewModel.resetUploadFlag()
                        navController.navigate("locationExpanded/${location!!.id}") },
                    onScanBarcodeClick = { navController.navigate("barcode") }
                )
            }
            composable("barcode") {
                BarcodeScannerWithPermissionScreen(
                    onBarcodeScanned = { barcode ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("barcode", barcode)

                        navController.popBackStack()
                    },
                    onPermissionDenied = {
                        Toast.makeText(LocalContext.current, "Camera permission denied", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                )
            }
            composable("camera") {

                CameraScreen(onImageCaptured = {file, uri ->
                    // Return result via savedStateHandle
                    Log.d("CameraReturn", "Returned from camera")
                    Log.d("CameraReturn", "Image file exists: ${file.exists()} - ${file.absolutePath}")
                    Log.d("CameraReturn", "URI: $uri")
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("imageUri",  uri)

                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("imageFile",  file)
                    navController.popBackStack()
                })
            }

        }
    }
}