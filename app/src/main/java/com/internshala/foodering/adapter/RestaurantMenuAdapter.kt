package com.internshala.foodering.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodering.R
import com.internshala.foodering.activity.CartActivity
import com.internshala.foodering.model.RestaurantMenu

class RestaurantMenuAdapter(
    val context: Context,
    val restaurantId: String,
    val restaurantName: String,
    val proceedToCartPassed: RelativeLayout,
    val buttonProceedToCart: Button,
    val restaurantMenu: ArrayList<RestaurantMenu>
) : RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {


    var itemSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout
    var itemsSelectedId = arrayListOf<String>()

    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtserialNumber)
        val txtItemName: TextView = view.findViewById(R.id.txtdishName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtdishPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnaddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_menu_recycler_view_single_row, parent, false)

        return ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        return restaurantMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {

        val restaurantMenuItem = restaurantMenu[position]
        proceedToCart = proceedToCartPassed

        buttonProceedToCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra("selectedItemsId", itemsSelectedId)
            context.startActivity(intent)
        }

        holder.btnAddToCart.setOnClickListener {

            if (holder.btnAddToCart.text.toString() == "Remove") {

                itemSelectedCount--
                itemsSelectedId.remove(holder.btnAddToCart.tag.toString())
                holder.btnAddToCart.text = "Add"

            } else {

                itemSelectedCount++
                itemsSelectedId.add(holder.btnAddToCart.tag.toString())
                holder.btnAddToCart.text = "Remove"
            }

            if (itemSelectedCount > 0) {
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.INVISIBLE
            }
        }

        holder.btnAddToCart.tag = restaurantMenuItem.id + ""
        holder.txtSerialNumber.text = (position + 1).toString()
        holder.txtItemName.text = restaurantMenuItem.name
        holder.txtItemPrice.text = "Rs. ${restaurantMenuItem.cost}"
    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

}