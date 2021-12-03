package com.internshala.foodering.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.internshala.foodering.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment(val ContextParam: Context) : Fragment() {
    lateinit var imgProfileFragment:ImageView
    lateinit var txtProfileName:TextView
    lateinit var txtProfileNumber:TextView
    lateinit var txtProfileEmail:TextView
    lateinit var txtProfileAddress:TextView
    lateinit var sharedPreferences: SharedPreferences

    // TODO: Rename and change types of parameters
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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        // Inflate the layout for this fragment
        imgProfileFragment=view.findViewById(R.id.imgProfileFragment)
        txtProfileName= view.findViewById(R.id.txtProfileName)
        txtProfileNumber=view.findViewById(R.id.txtProfileNumber)
        txtProfileEmail=view.findViewById(R.id.txtProfileEmail)
        txtProfileAddress=view.findViewById(R.id.txtProfileAddress)
        sharedPreferences = ContextParam.getSharedPreferences("Foodering Prefernces",Context.MODE_PRIVATE)

        txtProfileName.text=sharedPreferences.getString("name","")
        txtProfileNumber.text=sharedPreferences.getString("mobile_number","")
        txtProfileEmail.text=sharedPreferences.getString("email","")
        txtProfileAddress.text=sharedPreferences.getString("address","")


        return view
    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}