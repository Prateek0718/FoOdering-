package com.internshala.foodering.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodering.R
import com.internshala.foodering.activity.RestaurantMenuActivity
import com.internshala.foodering.activity.Temporary
import com.internshala.foodering.database.RestaurantDatabase
import com.internshala.foodering.database.RestaurantEntity
import com.internshala.foodering.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context:Context,val itemList:ArrayList<Restaurants>):RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant=itemList[position]

        holder.txtRestaurantName.tag = restaurant.restaurantId.toString()+""
        holder.txtRestaurantName.text=restaurant.restaurantName
        holder.txtRestaurantRating.text=restaurant.restaurantRating
        val onePerson=restaurant.restaurantCost_for_two.toString() +"/Person"
        holder.txtRestaurantPerPersonPrice.text=onePerson

        Picasso.get().load(restaurant.restaurantImage_url).error(R.drawable.burger).into(holder.imgRestaurantIcon)
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.restaurantId.toString())) {
            holder.favImage.setImageResource(R.drawable.ic_favorite_heart)
        } else {
            holder.favImage.setImageResource(R.drawable.ic_favorite)
        }
        holder.favImage.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.restaurantId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantCost_for_two.toString(),
                restaurant.restaurantImage_url
            )
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.favImage.setImageResource(R.drawable.ic_favorite_heart)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.favImage.setImageResource(R.drawable.ic_favorite)
                }
            }
        }
        holder.llContent.setOnClickListener {
            println(holder.txtRestaurantName.tag.toString())

            val intent=Intent(context,RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId",restaurant.restaurantId.toString())
            intent.putExtra("restaurantName",holder.txtRestaurantName.text.toString())
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class HomeViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtRestaurantName:TextView=view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating:TextView=view.findViewById(R.id.txtRestaurantRating)
        val txtRestaurantPerPersonPrice:TextView=view.findViewById(R.id.txtRestaurantPerPersonPrice)
        val imgRestaurantIcon:ImageView=view.findViewById(R.id.imgRestaurantIcon)
        val favImage:ImageView=view.findViewById(R.id.imgFavHeart)
        val llContent:LinearLayout=view.findViewById(R.id.llContent)

    }
    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            /*
            Mode 1 -> Check DB if the restaurant is favourite or not
            Mode 2 -> Save the restaurant into DB as favourite
            Mode 3 -> Remove the favourite restaurant
            */

            when (mode) {

                1 -> {
                    val res: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }
    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurant()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }
}