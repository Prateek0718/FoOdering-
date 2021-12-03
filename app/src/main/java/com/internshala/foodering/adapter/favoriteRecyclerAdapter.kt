package com.internshala.foodering.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.foodering.R
import com.internshala.foodering.database.RestaurantEntity
import com.squareup.picasso.Picasso

class favoriteRecyclerAdapter(val context: Context,val restaurantList:List<RestaurantEntity>):RecyclerView.Adapter<favoriteRecyclerAdapter.FavoriteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row,parent,false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val restaurant=restaurantList[position]
        holder.txtRestaurantName.text=restaurant.name
        holder.txtRestaurantRating.text=restaurant.rating
        val onePerson=restaurant.costForOne.toString() +"/Person"
        holder.txtRestaurantPerPersonPrice.text=onePerson

        Picasso.get().load(restaurant.imageUrl).error(R.drawable.burger).into(holder.imgRestaurantIcon)

    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
    class FavoriteViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView =view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating: TextView =view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantPerPersonPrice: TextView =view.findViewById(R.id.txtRestaurantPerPersonPrice)
        val imgRestaurantIcon: ImageView =view.findViewById(R.id.imgRestaurantIcon)
        val favImage:ImageView=view.findViewById(R.id.imgFavHeart)

    }
}