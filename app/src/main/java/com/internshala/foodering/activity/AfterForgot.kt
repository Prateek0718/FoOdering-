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

class AfterForgot() : AppCompatActivity() {
    lateinit var txtOTP: TextView
    lateinit var etOTP: EditText
    lateinit var etNewPass: EditText
    lateinit var etNewConfirmPass: EditText
    lateinit var btnSubmit: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_forgot)
        txtOTP = findViewById(R.id.txtOTP)
        etOTP = findViewById(R.id.etOTp)
        etNewPass = findViewById(R.id.etNewPass)
        etNewConfirmPass = findViewById(R.id.etNewConfirmPass)
       /* val sharedPreferences = getSharedPreferences("Foodering Prefernces", Context.MODE_PRIVATE)

        val mobileNumber=sharedPreferences.getString("mobile_number","")
*/
        val intent=intent
        val mobileNumber=intent.getStringExtra("mobile_number")
        btnSubmit.setOnClickListener {
            if (etOTP.text.isBlank()) {
                etOTP.error = "OTP Missing"
            } else {
                if (etNewPass.text.isBlank() || etNewPass.text.length <= 4) {
                    etNewPass.error = "Invalid Password"
                } else {
                    if (etNewConfirmPass.text.isBlank()) {
                        etNewConfirmPass.error = "Please re-enter the new Password"
                    } else {
                        if (etNewPass.text.toString().toInt() == etNewConfirmPass.text.toString().toInt()) {
                            if (ConnectionManager().checkConnectivity(this@AfterForgot)) {
                                try{
                                    val userCredentials=JSONObject()
                                    userCredentials.put("mobile_number",mobileNumber)
                                    userCredentials.put("password",etNewPass.text.toString())
                                    userCredentials.put("otp",etOTP.text.toString())
                                    val queue=Volley.newRequestQueue(this@AfterForgot)
                                    val url="http://13.235.250.119/v2/reset_password/fetch_result"
                                    val jsonObjectRequest=object :JsonObjectRequest(Method.POST,url,userCredentials,Response.Listener {
                                                                                                                                      val response=it.getJSONObject("data")
                                        val success=response.getBoolean("success")
                                        if(success){
                                            val serverMessage=response.getString("successMessage")
                                            Toast.makeText(this@AfterForgot, serverMessage, Toast.LENGTH_SHORT).show()
                                            val intent=Intent(this@AfterForgot,LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()


                                        }
                                        else{
                                            val responseServerMessage=response.getString("errorMessage")
                                            Toast.makeText(this@AfterForgot, responseServerMessage.toString(), Toast.LENGTH_SHORT).show()

                                        }

                                    },
                                        Response.ErrorListener {
                                            Toast.makeText(this@AfterForgot, "Some error has ocurred", Toast.LENGTH_SHORT).show()

                                        }){
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers=HashMap<String,String>()
                                            headers["Content-type"]="application/json"
                                            headers["token"]="9c10ced414a682"
                                            return headers

                                        }

                                    }
                                    queue.add(jsonObjectRequest)

                                }
                                catch (e: JSONException){
                                    Toast.makeText(this@AfterForgot,
                                        "Some unexpected Json error has occured ",
                                        Toast.LENGTH_SHORT).show()

                                }
                            }
                            else{
                                val dialog= AlertDialog.Builder(this@AfterForgot)
                                dialog.setTitle("Error ")
                                dialog.setMessage("Internet Connection not Found")
                                dialog.setPositiveButton("Open Settings"){text,listener->
                                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                    startActivity(settingsIntent)
                                }
                                dialog.setNegativeButton("Exit"){text,listener->
                                    ActivityCompat.finishAffinity(this@AfterForgot)
                                }
                                dialog.create()
                                dialog.show()
                            }
                        }
                        else{
                            etNewConfirmPass.error="Passwords don't match"

                        }

                    }
                }
            }


        }
    }
}