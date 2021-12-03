package com.internshala.foodering.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.model.CartItems
import com.internshala.foodering.model.OrderHistoryRestaurant
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException

class OrderHistoryAdapter(
    val context: Context,
    val orderedRestaurantList:ArrayList<OrderHistoryRestaurant>
) : RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderOrderHistoryRestaurant>(){
    class ViewHolderOrderHistoryRestaurant(view: View) : RecyclerView.ViewHolder(view){
        val txtRestaurantname:TextView=view.findViewById(R.id.txtRestaurantname)
        val txtDate:TextView=view.findViewById(R.id.txtDate)
        val recyclerViewitemsOrdered:RecyclerView=view.findViewById(R.id.recyclerViewItemsOrdered)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderOrderHistoryRestaurant {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_recycler_view_single_row,parent,false)
        return ViewHolderOrderHistoryRestaurant(view)
    }

    override fun onBindViewHolder(holder: ViewHolderOrderHistoryRestaurant, position: Int) {
        val restaurantObject=orderedRestaurantList[position]
        holder.txtRestaurantname.text=restaurantObject.restaurantName
        var formatDate = restaurantObject.orderPlacedAt
        formatDate = formatDate.replace("-", "/")
        formatDate = formatDate.substring(0, 6) + "20" + formatDate.substring(6, 8)
        holder.txtDate.text = formatDate

        val layoutManager=LinearLayoutManager(context)
        var orderedItemAdapter:CartAdapter
        if (ConnectionManager().checkConnectivity(context)){
            try{
                val orderItemsPerRestaurant = ArrayList<CartItems>()
                val sharedPreferences=context.getSharedPreferences("Foodering Prefernces",Context.MODE_PRIVATE)
                val userId=sharedPreferences.getString("user_id","0")
                val queue= Volley.newRequestQueue(context)
                val url="http://13.235.250.119/v2/orders/fetch_result/$userId"
                val jsonObjectRequest=object :JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {
                                      val response=it.getJSONObject("data")
                        val success=response.getBoolean("success")
                        if(success){
                            val data=response.getJSONArray("data")
                            val orderObject=data.getJSONObject(position)
                            orderItemsPerRestaurant.clear()
                            val orderedFood = orderObject.getJSONArray("food_items")
                            for(i in 0 until orderedFood.length()){
                                val eachOrderItem=orderedFood.getJSONObject(i)
                                val itemsObject=CartItems(
                                    eachOrderItem.getString("food_item_id"),
                                    eachOrderItem.getString("name"),
                                    eachOrderItem.getString("cost"),
                                    "0000"
                                )
                                orderItemsPerRestaurant.add(itemsObject)
                            }
                            orderedItemAdapter=CartAdapter(context,orderItemsPerRestaurant)
                            holder.recyclerViewitemsOrdered.adapter=orderedItemAdapter
                            holder.recyclerViewitemsOrdered.layoutManager=layoutManager

                        }
                        else{
                            Toast.makeText(
                                context,
                                "Some Error occurred!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            context,
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
            catch(e:JSONException){
                Toast.makeText(
                    context,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    override fun getItemCount(): Int {
        return orderedRestaurantList.size
    }

}