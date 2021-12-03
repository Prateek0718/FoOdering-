package com.internshala.foodering.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.foodering.*
import com.internshala.foodering.fragment.*

class MainActivity2 : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar:Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var sharedPreferences: SharedPreferences

    var previousCheckedItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=getSharedPreferences("Foodering Prefernces",Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main2)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        navigationView=findViewById(R.id.navigationView)
        toolbar=findViewById(R.id.toolBar)
        frameLayout=findViewById(R.id.frame)
        setUpToolBar()

        openHome()

        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity2,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        navigationView.setNavigationItemSelectedListener {
            if (previousCheckedItem!=null){
                previousCheckedItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousCheckedItem=it
            when(it.itemId){
                R.id.homee ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, HomeFragment())
                        .commit()
                    supportActionBar?.title="All Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment(this))
                        .commit()
                    supportActionBar?.title="My Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourite ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment())
                        .commit()
                    supportActionBar?.title="Favorites Restaurant"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory->{
                    val intent=Intent(this@MainActivity2,OrderHistory::class.java)
                    drawerLayout.closeDrawers()
                    Toast.makeText(this@MainActivity2, "Order History", Toast.LENGTH_SHORT).show()
                    startActivity(intent)

                }
                R.id.faq ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FaqsFragment())
                        .commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    drawerLayout.closeDrawers()

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setMessage("Do you wish to log out?")
                    alterDialog.setPositiveButton("Yes") { _, _ ->
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        ActivityCompat.finishAffinity(this)
                    }
                    alterDialog.setNegativeButton("No") { _, _ ->

                    }
                    alterDialog.create()
                    alterDialog.show()
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }
    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Tool  bar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openHome(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, HomeFragment())
            .commit()
        supportActionBar?.title="All Restaurants"
        drawerLayout.closeDrawers()
        navigationView.setCheckedItem(R.id.homee)
    }

    override fun onBackPressed() {
        val frame=supportFragmentManager.findFragmentById(R.id.frame)
        if(frame!is HomeFragment){
            openHome()
        }
        else{
            super.onBackPressed()
        }
    }

}