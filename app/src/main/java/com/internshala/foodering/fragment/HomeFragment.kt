package com.internshala.foodering.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.SettingInjectorService
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.adapter.HomeRecyclerAdapter
import com.internshala.foodering.database.RestaurantDatabase
import com.internshala.foodering.database.RestaurantEntity
import com.internshala.foodering.model.Restaurants
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerHome:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager
    lateinit var recyclerAdapter:HomeRecyclerAdapter
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar: ProgressBar
    val restaurantInfoList = arrayListOf<Restaurants>()

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
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        recyclerHome=view.findViewById(R.id.recyclerHomee)
        layoutManager=LinearLayoutManager(activity)
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE




        val queue=Volley.newRequestQueue(activity as Context)
        val url="http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest= object :JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                    try {
                        progressLayout.visibility=View.GONE
                        val success1=it.getJSONObject("data")
                        val success=success1.getBoolean("success")
                        if(success){
                            val data = success1.getJSONArray("data")
                            for(i in 0 until data.length()){
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurants(
                                    restaurantJsonObject.getString("id").toInt(),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one").toInt(),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)
                                recyclerAdapter= HomeRecyclerAdapter(activity as Context,restaurantInfoList)
                                recyclerHome.adapter=recyclerAdapter
                                recyclerHome.layoutManager=layoutManager
                            
                            }
                        }
                        else{
                            Toast.makeText(activity as Context, "Some error has occured", Toast.LENGTH_SHORT).show()
                        }
                        
                    }
                    catch (e:JSONException){
                        Toast.makeText(activity as Context, "Some unexpected Error occured", Toast.LENGTH_SHORT).show()
                    }
                   
                },Response.ErrorListener {
                    Toast.makeText(activity as Context, "Volley error occuered", Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Conent-type"]="application/json"
                    headers["token"]="9c10ced414a682"
                    return headers
                }

            }
            queue.add(jsonObjectRequest)


        }
        else{
            val dialogue=AlertDialog.Builder(activity as Context)
            dialogue.setTitle("Error")
            dialogue.setMessage("No Internet Connection")
            dialogue.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent=Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialogue.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialogue.create()
            dialogue.show()
        }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



}