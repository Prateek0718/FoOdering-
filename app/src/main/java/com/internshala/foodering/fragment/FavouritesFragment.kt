package com.internshala.foodering.fragment

import android.content.Context
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.foodering.R
import com.internshala.foodering.adapter.favoriteRecyclerAdapter
import com.internshala.foodering.database.RestaurantDatabase
import com.internshala.foodering.database.RestaurantEntity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesFragment : Fragment() {
    lateinit var recyclerFavorite: RecyclerView
    lateinit var layoutManger:RecyclerView.LayoutManager
    lateinit var rlLoading:RelativeLayout
    lateinit var rlFav:RelativeLayout
    lateinit var rlNoFav:RelativeLayout
    lateinit var recyclerAdapter: favoriteRecyclerAdapter
    var restaurantList = listOf<RestaurantEntity>()

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerFavorite=view.findViewById(R.id.recyclerFav)
        layoutManger=LinearLayoutManager(activity as Context)
        rlFav=view.findViewById(R.id.rlFavorites)
        rlLoading=view.findViewById(R.id.rlProgressive)
        rlNoFav=view.findViewById(R.id.rlNoFav)
        rlLoading.visibility=View.VISIBLE
        restaurantList=RetrieveFavorites(activity as Context).execute().get()
        if(!restaurantList.isEmpty()&& activity!=null){
            rlLoading.visibility=View.GONE
            rlNoFav.visibility=View.GONE
            rlFav.visibility=View.VISIBLE
            recyclerAdapter=favoriteRecyclerAdapter(activity as Context,restaurantList)
            recyclerFavorite.adapter=recyclerAdapter
            recyclerFavorite.layoutManager=layoutManger

        }
        else{
            rlLoading.visibility=View.GONE
            rlFav.visibility=View.GONE
            rlNoFav.visibility=View.VISIBLE
        }


        return view

    }
    class RetrieveFavorites(val context: Context):AsyncTask<Void,Void,List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
            return db.restaurantDao().getAllRestaurant()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}