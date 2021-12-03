package com.internshala.foodering.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.adapter.CartAdapter
import com.internshala.foodering.model.CartItems
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var txtOrderingFrom:TextView
    lateinit var btnPlacedOrder:Button
    lateinit var recyclerView:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var cartAdapter:CartAdapter
    lateinit var restaurantId:String
    lateinit var restaurantName:String
    lateinit var selectedItemsId:ArrayList<String>
    lateinit var linearLayout: LinearLayout
    lateinit var cartProgressLayout:RelativeLayout
    var totalAmount=0
    var cartListItems = arrayListOf<CartItems>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        btnPlacedOrder=findViewById(R.id.btnPlaceOrder)
        txtOrderingFrom=findViewById(R.id.txtOrderingFrom)
        linearLayout=findViewById(R.id.linearLayout)
        toolbar=findViewById(R.id.Toolbar)
        cartProgressLayout=findViewById(R.id.cartProgressLayout)

        restaurantId=intent.getStringExtra("restaurantId")!!
        restaurantName=intent.getStringExtra("restaurantName")!!
        selectedItemsId=intent.getStringArrayListExtra("selectedItemsId")!!
        txtOrderingFrom.text=restaurantName

        setUpToolBar()
        btnPlacedOrder.setOnClickListener {
            val sharedPreferences = this.getSharedPreferences("Foodering Prefernces", Context.MODE_PRIVATE)

            if(ConnectionManager().checkConnectivity(this@CartActivity)){
                cartProgressLayout.visibility=View.VISIBLE
                try{
                    val foodArray = JSONArray()
                    for(foodItem in selectedItemsId){
                        val singleFoodItem=JSONObject()
                        singleFoodItem.put("food_item_id",foodItem)
                        foodArray.put(singleFoodItem)
                    }
                    val order=JSONObject()
                    order.put("user_id",sharedPreferences.getString("user_id","0"))
                    order.put("restaurant_id",restaurantId)
                    order.put("total_cost",totalAmount)
                    order.put("food",foodArray)
                    val queue= Volley.newRequestQueue(this@CartActivity)
                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                    val jsonObjectRequest= object :JsonObjectRequest(
                        Method.POST,
                        url,
                        order,
                        Response.Listener {
                            val response=it.getJSONObject("data")
                            val success=response.getBoolean("success")
                            if(success){
                                Toast.makeText(this@CartActivity,
                                    "Order Successfully placed",
                                    Toast.LENGTH_SHORT).show()
                                    /*createNotification()*/
                                val intent=Intent(this@CartActivity,OrderPlaced::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                            else{
                                val responseMessageServer=response.getString("errorMessage")
                                Toast.makeText(this@CartActivity,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }
                            cartProgressLayout.visibility=View.INVISIBLE
                        },
                        Response.ErrorListener {

                            Toast.makeText(
                                this@CartActivity,
                                "Some Error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    ){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers=HashMap<String,String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="9c10ced414a682"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)

                }
                catch (e:JSONException){
                    Toast.makeText(
                        this@CartActivity,
                        "Some unexpected error occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else{
                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@CartActivity)
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
        layoutManager=LinearLayoutManager(this@CartActivity)
        recyclerView=findViewById(R.id.recyclerViewCart)

    }
    fun fetchData(){
        if(ConnectionManager().checkConnectivity(this@CartActivity)){
            cartProgressLayout.visibility=View.VISIBLE
            try{
                val queue=Volley.newRequestQueue(this@CartActivity)
                val url="http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"
                val jsonObjectRequest=object :JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {
                                      val response=it.getJSONObject("data")
                        val success=response.getBoolean("success")
                        if(success){
                            val data=response.getJSONArray("data")
                            cartListItems.clear()
                            totalAmount=0
                            for(i in 0 until data.length()){
                                val cartItem=data.getJSONObject(i)
                                if(selectedItemsId.contains(cartItem.getString("id"))){
                                    val menuObject=CartItems(
                                        cartItem.getString("id"),
                                        cartItem.getString("name"),
                                        cartItem.getString("cost_for_one"),
                                        cartItem.getString("restaurant_id")
                                    )
                                    totalAmount+=cartItem.getString("cost_for_one").toString()
                                        .toInt()
                                    cartListItems.add(menuObject)
                                }
                                cartAdapter=CartAdapter(this@CartActivity,cartListItems)
                                recyclerView.adapter=cartAdapter
                                recyclerView.layoutManager=layoutManager

                            }
                            btnPlacedOrder.text="Place Order(Total: Rs. ${totalAmount})"
                        }
                        cartProgressLayout.visibility=View.INVISIBLE

                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@CartActivity,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        cartProgressLayout.visibility = View.INVISIBLE
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
                    this@CartActivity,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else{
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@CartActivity)
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
    fun setUpToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
    }
    fun createNotification() {
        val notificationId = 1;
        val channelId = "personal_notification"
        val notificationBuilder = NotificationCompat.Builder(this@CartActivity, channelId)
        notificationBuilder.setSmallIcon(R.drawable.applogo)
        notificationBuilder.setContentTitle("Order Placed")
        notificationBuilder.setContentText("Your order has been successfully placed!")
        notificationBuilder.setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Ordered from ${restaurantName}. Please pay Rs.$totalAmount")
        )

        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        val notificationManagerCompat = NotificationManagerCompat.from(this@CartActivity)
        notificationManagerCompat.notify(notificationId, notificationBuilder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Order Placed"
            val description = "Your order has been successfully placed!"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, name, importance)
            notificationChannel.description = description

            val notificationManager =
                (getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
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

        if (ConnectionManager().checkConnectivity(this@CartActivity)) {
            fetchData()
        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this@CartActivity)
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