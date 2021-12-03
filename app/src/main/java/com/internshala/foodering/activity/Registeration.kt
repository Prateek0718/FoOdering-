package com.internshala.foodering.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Registeration : AppCompatActivity() {
    lateinit var txtRegister:TextView
    lateinit var etName:EditText
    lateinit var etEmail:EditText
    lateinit var etNumber:EditText
    lateinit var etDeliver:EditText
    lateinit var etRPass:EditText
    lateinit var etPass:EditText
    lateinit var btnRegister:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)
        txtRegister=findViewById(R.id.txtRegister)
        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etNumber=findViewById(R.id.etNumber)
        etDeliver=findViewById(R.id.etDeliver)
        etRPass=findViewById(R.id.etRPass)
        etPass=findViewById(R.id.etPass)
        btnRegister=findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val sharedPreferences = getSharedPreferences("Foodering Prefernces",Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()

            var name=etName.text.toString()
            var email=etEmail.text.toString()
            var number=etNumber.text.toString()
            var delivery=etDeliver.text.toString()
            var pass=etRPass.text.toString()
            var password=etPass.text.toString()
            if(ConnectionManager().checkConnectivity(this@Registeration)) {


                if (pass != password) {
                    Toast.makeText(this@Registeration, "passwords dosent match", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    try {
                        val queue = Volley.newRequestQueue(this@Registeration)
                        val Url = "http://13.235.250.119/v2/register/fetch_result"

                        val jsonParams = JSONObject()
                        jsonParams.put("name", name)
                        jsonParams.put("mobile_number", number)
                        jsonParams.put("password", pass)
                        jsonParams.put("address", delivery)
                        jsonParams.put("email", email)

                        val jsonRequest = object :
                            JsonObjectRequest(Method.POST,
                                Url,
                                jsonParams,
                                Response.Listener {
                                    val response =it.getJSONObject("data")
                                    val success = response.getBoolean("success")
                                    if(success){
                                        val data = response.getJSONObject("data")
                                        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                                        sharedPreferences.edit().putString("user_id",data.getString("user_id")).apply()
                                        sharedPreferences.edit().putString("name",data.getString("name")).apply()
                                        sharedPreferences.edit().putString("email",data.getString("email")).apply()
                                        sharedPreferences.edit().putString("mobile_number",data.getString("mobile_number")).apply()
                                        sharedPreferences.edit().putString("address",data.getString("address")).apply()

                                        Toast.makeText(this@Registeration, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                        val intent=Intent(this@Registeration,MainActivity2::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else {
                                        val responseMessageServer =
                                            response.getString("errorMessage")
                                        Toast.makeText(
                                            this@Registeration,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                },
                                Response.ErrorListener {

                                }
                            ) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Conent-type"] = "application/json"
                                headers["token"] = "9c10ced414a682"
                                return headers
                            }
                        }
                        queue.add(jsonRequest)
                    } catch (e: JSONException) {


                    }
                }
            }

                else{

                        val dialog= AlertDialog.Builder(this@Registeration)
                        dialog.setTitle("Error ")
                        dialog.setMessage("Internet Connection not Found")
                        dialog.setPositiveButton("Open Settings"){text,listener->
                            val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit"){text,listener->
                            ActivityCompat.finishAffinity(this@Registeration)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }

            }

    }
