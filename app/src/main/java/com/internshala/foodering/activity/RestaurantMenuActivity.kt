package com.internshala.foodering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.adapter.RestaurantMenuAdapter
import com.internshala.foodering.model.RestaurantMenu
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException
import java.lang.Exception

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var btnProceedToCart: Button
    lateinit var menuProgressLayout: RelativeLayout
    var restaurantMenuList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_description)

        proceedToCartLayout = findViewById(R.id.relativeLayoutProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        menuProgressLayout = findViewById(R.id.menuProgressLayout)
        toolBar = findViewById(R.id.ToolBar)

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurantId")!!
            restaurantName = intent.getStringExtra("restaurantName")!!
        } else {
            Toast.makeText(this@RestaurantMenuActivity, "Some error occured", Toast.LENGTH_SHORT)
                .show()
        }
        layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)
        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)


        setToolBar()
    }
    fun fetchData(){


        if (ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)) {

            menuProgressLayout.visibility = View.VISIBLE

            try {
                //send volley request to the given url along with restaurantId to fetch restaurantMenu
                menuProgressLayout.visibility = View.GONE

                val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()
                            val data = response.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurant = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    restaurant.getString("id"),
                                    restaurant.getString("name"),
                                    restaurant.getString("cost_for_one")
                                )

                                restaurantMenuList.add(menuObject)
                                menuAdapter = RestaurantMenuAdapter(
                                    this@RestaurantMenuActivity,
                                    restaurantId,
                                    restaurantName,
                                    proceedToCartLayout,
                                    btnProceedToCart,
                                    restaurantMenuList
                                )

                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager
                            }
                        }
                    },
                    Response.ErrorListener {
                        println("Error12menu is $it")

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9c10ced414a682"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
    }


    fun setToolBar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }


    //Once items added to cart, If user press back , items will be cleared
    override fun onBackPressed() {
        if (menuAdapter.getSelectedItemCount() > 0) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("Cancel") { _, _ ->

            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                if (menuAdapter.getSelectedItemCount() > 0) {
                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay") { _, _ ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("Cancel") { _, _ ->

                    }
                    alterDialog.show()
                } else {
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this)) {
            if (restaurantMenuList.isEmpty())
                fetchData()
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
        super.onResume()
    }
}
