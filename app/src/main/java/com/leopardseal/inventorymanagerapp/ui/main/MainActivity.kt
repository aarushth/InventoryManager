package com.leopardseal.inventorymanagerapp.ui.main

import BoxEditScreen
import ItemEditScreen
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
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
import com.leopardseal.inventorymanagerapp.ui.login.LoginActivity
import com.leopardseal.inventorymanagerapp.ui.main.box.BoxScreen
import com.leopardseal.inventorymanagerapp.ui.main.box.expanded.BoxExpandedScreen
import com.leopardseal.inventorymanagerapp.ui.main.box.select.BoxSelectScreen
import com.leopardseal.inventorymanagerapp.ui.main.camera.BarcodeScannerWithPermissionScreen
import com.leopardseal.inventorymanagerapp.ui.main.camera.CameraScreen
import com.leopardseal.inventorymanagerapp.ui.main.camera.PhotoPickerScreen
import com.leopardseal.inventorymanagerapp.ui.main.invite.InviteScreen
import com.leopardseal.inventorymanagerapp.ui.main.item.ItemScreen
import com.leopardseal.inventorymanagerapp.ui.main.item.expanded.ItemExpandedScreen
import com.leopardseal.inventorymanagerapp.ui.main.item.select.ItemSelectScreen
import com.leopardseal.inventorymanagerapp.ui.main.location.LocationScreen
import com.leopardseal.inventorymanagerapp.ui.main.location.expanded.LocationEditScreen
import com.leopardseal.inventorymanagerapp.ui.main.location.expanded.LocationExpandedScreen
import com.leopardseal.inventorymanagerapp.ui.main.org.OrgScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this)
        setContent {
            MainScreen()
        }
    }
    fun login(){
        Intent(this, LoginActivity::class.java).also{
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }
    fun logoutAndRedirectToLogin() {
        lifecycleScope.launch {
            userPreferences.clear()

            // Navigate to LoginActivity and clear back stack
            val intent = Intent(this@MainActivity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
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

        val screenTitle = when {
            currentRoute == "org" -> "Choose An Organization"
            currentRoute == "invite" -> "You Have Been Invited"
            currentRoute == "camera" -> "Take a picture"
            currentRoute == "barcode" -> "Scan Barcode"
            currentRoute.startsWith("itemSelect")-> "Select Items"
            currentRoute.startsWith("boxSelectSingle")-> "Select A Box"
            currentRoute.startsWith("boxSelect")-> "Select Boxes"
            currentRoute.startsWith("locationSelect")-> "Select A Location"
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
                        userEmail,
                        closeDrawer = { scope.launch { drawerState.close() } },
                        logout = {logoutAndRedirectToLogin()}
                    )
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
                    MyNavHost(navController, scope)
                    if(fabEnabled) {
                        ExpandingFab(
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
    fun MyNavHost(navController: NavHostController, coroutineScope: CoroutineScope){
        NavHost(
            navController = navController,
            startDestination = "invite"
        ) {
            composable(route = "invite") {
                InviteScreen(
                    onSkip = {
                        coroutineScope.launch {
                            val orgId = userPreferences.orgId.first()
                            if (orgId == null) {
                                navController.navigate("org")
                            } else {
                                navController.navigate("item")
                            }
                        }
                    },
                    onUnauthorized = { login() })
            }
            composable("org") {
                OrgScreen(
                    navController = navController,
                    onUnauthorized = { login() })
            }
            composable("item") {
                ItemScreen(
                    navController = navController,
                    onUnauthorized = { login() }
                )
            }
            composable(
                route = "itemExpanded/{item_id}",
                arguments = listOf(navArgument("item_id") { type = NavType.LongType })
            ) {
                ItemExpandedScreen(
                    navController = navController,
                    onUnauthorized = { login() },
                )
            }
            composable(
                route = "itemEdit/{item_id}",
                arguments = listOf(navArgument("item_id") { type = NavType.LongType })
            ) {
                ItemEditScreen(
                    navController = navController,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onUnauthorized = { login() }
                )
            }
            composable(
                route = "itemSelect/{box_id}",
                arguments = listOf(navArgument("box_id"){type = NavType.LongType})
            ){
                ItemSelectScreen(onConfirmSelection = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh", true)
                        navController.popBackStack()})
            }
            composable("box") {
                BoxScreen(
                    navController = navController,
                    onBoxClick = { boxId -> navController.navigate("boxExpanded/${boxId}") },
                    onUnauthorized = { login() })
            }
            composable(
                route = "boxSelectSingle/{box_id}",
                arguments = listOf(navArgument("box_id") { type = NavType.LongType })
            ) {backStackEntry ->
                val boxSelectedId : Long = backStackEntry.arguments?.getLong("box_id")?:-1L
                BoxScreen(
                    navController = navController,
                    boxSelected = boxSelectedId,
                    isAddable = false,
                    onBoxClick = { boxId ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("box_id", boxId)
                        navController.popBackStack() },
                    onUnauthorized = { login() })
            }
            composable(
                route = "boxSelect/{location_id}",
                arguments = listOf(navArgument("location_id"){type = NavType.LongType})
            ){
                BoxSelectScreen(onConfirmSelection = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh", true)
                    navController.popBackStack()})
            }
            composable(
                route = "boxExpanded/{box_id}",
                arguments = listOf(navArgument("box_id") { type = NavType.LongType })
            ) {
                BoxExpandedScreen(
                    navController = navController,
                    onUnauthorized = { login() })
            }
            composable(
                route = "boxEdit/{box_id}",
                arguments = listOf(navArgument("box_id") { type = NavType.LongType })
            ) {
                BoxEditScreen(
                    navController = navController,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onUnauthorized = { login() }
                )
            }
            composable("location") {
                LocationScreen(
                    navController = navController,
                    onLocationClick = { locationId -> navController.navigate("locationExpanded/${locationId}") },
                    onUnauthorized = { login() }
                )
            }
            composable(
                route = "locationSelectSingle/{location_id}",
                arguments = listOf(navArgument("location_id") { type = NavType.LongType })
            ) {backStackEntry ->
                val locationSelectedId : Long = backStackEntry.arguments?.getLong("location_id")?:-1L
                LocationScreen(
                    navController = navController,
                    locationSelected = locationSelectedId,
                    isAddable = false,
                    onLocationClick = { locationId ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("location_id", locationId)
                        navController.popBackStack() },
                    onUnauthorized = { login() })
            }
            composable(
                route = "locationExpanded/{location_id}",
                arguments = listOf(navArgument("location_id") { type = NavType.LongType })
            ) {
                LocationExpandedScreen(navController = navController,)
            }
            composable(
                route = "locationEdit/{location_id}",
                arguments = listOf(navArgument("location_id") { type = NavType.LongType })
            ) {
                LocationEditScreen(
                    navController = navController,
                    orgId = runBlocking { userPreferences.orgId.first()?:-1L },
                    onUnauthorized = { login() }
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
                CameraScreen(
                    navController = navController
                )
            }
            composable("photoPicker") {
                PhotoPickerScreen(
                    onImageSelected =  { uri, file ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("imageUri",  uri)

                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("imageFile",  file)

                        navController.popBackStack()
                        },
                    onPickerCancelled = {navController.popBackStack()})
            }

        }
    }
}