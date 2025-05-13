package com.leopardseal.inventorymanagerapp.ui.main





import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.UserPreferences
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navController: NavController
    private lateinit var searchButton : ImageButton
    private lateinit var customTitle : TextView
    private lateinit var orgImg : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController


        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)


        var userPreferences : UserPreferences = UserPreferences(this)
        val navigationView : NavigationView = findViewById<NavigationView>(R.id.nav_side)
        setupWithNavController(navigationView, navController)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        setupWithNavController(bottomNav, navController)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()


        searchButton = findViewById<ImageButton>(R.id.search)
        customTitle = findViewById<TextView>(R.id.customTitle)
        orgImg = findViewById<ImageView>(R.id.orgImage)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
            when (destination.id) {
                R.id.fragment_org, R.id.fragment_invite -> {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    toggle.isDrawerIndicatorEnabled = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    hideViews(bottomNav, searchButton, orgImg, customTitle)
                }
                else -> {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    toggle.isDrawerIndicatorEnabled = true
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    toggle.syncState()

                    showViews(bottomNav, searchButton, orgImg, customTitle)
                    toolbar.title = ""

                    customTitle.text = runBlocking { if(userPreferences.orgName.first()!= null)userPreferences.orgName.first()!! else "" }

                    val imageUrl = runBlocking { userPreferences.orgImg.first() ?: "" }

                    if (imageUrl.isNotBlank()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.default_img)
                            .error(R.drawable.default_img)
                            .into(orgImg)
                    } else {
                        // Load fallback image directly
                        orgImg.setImageResource(R.drawable.default_img)
                    }
                }
            }
        }

        navController.navigate(R.id.fragment_invite)

    }

    fun showViews(vararg views: View) {
        views.forEach { it.visibility = View.VISIBLE }
    }
    fun hideViews(vararg views: View) {
        views.forEach { it.visibility = View.GONE }
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else {
            super.onBackPressed()
        }
    }
}