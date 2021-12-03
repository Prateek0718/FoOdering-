package com.internshala.foodering.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.adapter.OrderHistoryAdapter
import com.internshala.foodering.model.OrderHistoryRestaurant
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException

class OrderHistory : AppCompatActivity() {

    lateinit var layoutManager1: RecyclerView.LayoutManager
    lateinit var menuAdapter1: OrderHistoryAdapter
    lateinit var recyclerViewAllOrders: RecyclerView
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var orderHistoryLayout: RelativeLayout
    lateinit var noOrders: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        recyclerViewAllOrders=findViewById(R.id.recyclerViewAllOrders)
        toolbar=findViewById(R.id.toolbar)
        orderHistoryLayout=findViewById(R.id.orderHistoryLayout)
        noOrders=findViewById(R.id.noOrders)
        setToolBar()

    }
    fun setItemsForEachRestaurant(){
        layoutManager1=LinearLayoutManager(this@OrderHistory)
        val orderdRestaurantList=ArrayList<OrderHistoryRestaurant>()
        val sharedPreferences = getSharedPreferences("Foodering Prefernces", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "000")
        if(ConnectionManager().checkConnectivity(this@OrderHistory)){
            orderHistoryLayout.visibility=View.VISIBLE
            try{
                val queue = Volley.newRequestQueue(this@OrderHistory)
                val url=" http://13.235.250.119/v2/orders/fetch_result/$userId"
                val jsonObjectRequest=object :JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {
                                      val response=it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if(success){
                            val data = response.getJSONArray("data")
                            if(data.length()==0){
                                Toast.makeText(
                                    this@OrderHistory,
                                    "No Orders Placed yet!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                noOrders.visibility=View.VISIBLE
                            }
                            else{
                                noOrders.visibility = View.INVISIBLE
                                for(i in 0 until data.length()){
                                    val restaurantItem = data.getJSONObject(i)
                                    val orderObject=OrderHistoryRestaurant(
                                        restaurantItem.getString("order_id"),
                                        restaurantItem.getString("restaurant_name"),
                                        restaurantItem.getString("total_cost"),
                                        restaurantItem.getString("order_placed_at").substring(0,10)
                                    )
                                    orderdRestaurantList.add(orderObject)
                                    menuAdapter1=
                                        OrderHistoryAdapter(this@OrderHistory,orderdRestaurantList)
                                    recyclerViewAllOrders.adapter=menuAdapter1
                                    recyclerViewAllOrders.layoutManager=layoutManager1

                                }
                            }
                        }
                        orderHistoryLayout.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        orderHistoryLayout.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@OrderHistory,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                ){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9c10ced414a682"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }
            catch (e:JSONException){
                Toast.makeText(
                    this@OrderHistory,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else{
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@OrderHistory)
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
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Previous Orders"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this@OrderHistory)) {
            setItemsForEachRestaurant()
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