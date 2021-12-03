package com.internshala.foodering.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class  LoginActivity : AppCompatActivity() {
    lateinit var etNumber:EditText
    lateinit var etPassword:EditText
    lateinit var btnLogin:Button
    lateinit var txtForgot:TextView
    lateinit var txtSignUp:TextView
    lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences=getSharedPreferences("Foodering Prefernces",Context.MODE_PRIVATE)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_main)
        if (isLoggedIn){
            val intent=Intent(this@LoginActivity, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }


        etNumber=findViewById(R.id.etMobileNumber)
        etPassword=findViewById(R.id.etPassword)
        btnLogin=findViewById(R.id.btnLogin)
        txtForgot=findViewById(R.id.txtForgetPass)
        txtSignUp=findViewById(R.id.txtSignUp)


        btnLogin.setOnClickListener {
            val mobile_number=etNumber.text.toString()
            val pass=etPassword.text.toString()

            if(ConnectionManager().checkConnectivity(this@LoginActivity)){
                try{
                    val login=JSONObject()
                    login.put("mobile_number",mobile_number)
                    login.put("password",pass)

                    val queue=Volley.newRequestQueue(this@LoginActivity)
                    val URL="http://13.235.250.119/v2/login/fetch_result"

                    val jsonObjectRequest = object :  JsonObjectRequest(
                        Method.POST,
                        URL,
                        login,
                        Response.Listener {
                            val response=it.getJSONObject("data")
                            val success=response.getBoolean("success")
                            if(success){
                                val data=response.getJSONObject("data")
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                                sharedPreferences.edit().putString("user_id",data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name",data.getString("name")).apply()
                                sharedPreferences.edit().putString("email",data.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number",data.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address",data.getString("address")).apply()

                                Toast.makeText(this@LoginActivity, "Welcome Foodiee", Toast.LENGTH_SHORT).show()
                                val intent=Intent(this@LoginActivity,MainActivity2::class.java)
                                startActivity(intent)
                                finish()

                            }
                            else{
                                val responseMessageServer = response.getString("errorMessage")
                                Toast.makeText(this@LoginActivity,responseMessageServer.toString(), Toast.LENGTH_SHORT).show()

                            }
                        },Response.ErrorListener {
                            Toast.makeText(this@LoginActivity, "SORRY, Try Again ", Toast.LENGTH_SHORT).show()
                        }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String,String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="9c10ced414a682"
                            return headers
                        }

                    }
                    queue.add(jsonObjectRequest)
                }catch (e:JSONException){
                    Toast.makeText(this@LoginActivity, "Some Unexpected Error Occured", Toast.LENGTH_SHORT).show()
                }
            }
            else{

                val dialog= AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error ")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings"){text,listener->
                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit"){text,listener->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()
            }
        }


        txtForgot.setOnClickListener {
            val intent=Intent(this@LoginActivity, ForgetPassword::class.java)
            Toast.makeText(this@LoginActivity, "Don't Worry Foodies", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
        txtSignUp.setOnClickListener {
            val intent=Intent(this@LoginActivity, Registeration::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this@LoginActivity, "keep patience foodiiee", Toast.LENGTH_SHORT).show()
        }

    }



}