package com.internshala.foodering.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.foodering.R
import com.internshala.foodering.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgetPassword : AppCompatActivity() {
    lateinit var imgForgot:ImageView
    lateinit var txtFReset:TextView
    lateinit var txtFPass:TextView
    lateinit var etFNumber:TextView
    lateinit var etFEmail:TextView
    lateinit var btnNext:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        imgForgot=findViewById(R.id.imgForgot)
        txtFPass=findViewById(R.id.txtFPass)
        txtFReset=findViewById(R.id.txtFReset)
        etFNumber=findViewById(R.id.etFNumber)
        etFEmail=findViewById(R.id.etFEmail)
        btnNext=findViewById(R.id.btnNext)



        btnNext.setOnClickListener {
            if(etFNumber.text.isBlank()|| etFNumber.text.length!=10){
                etFNumber.error ="Invalid Mobile Number"
            }
            else{
                if(etFEmail.text.isBlank()){
                    etFEmail.error="Email Missing"
                }
                else{
                    if(ConnectionManager().checkConnectivity(this@ForgetPassword)){
                        try {
                            val userCredentials =JSONObject()
                            userCredentials.put("mobile_number",etFNumber.text)
                            userCredentials.put("email",etFEmail.text)
                            val queue=Volley.newRequestQueue(this@ForgetPassword)
                            val url="http://13.235.250.119/v2/forgot_password/fetch_result"
                            val jsonObjectRequest = object :JsonObjectRequest(
                                Request.Method.POST,url,userCredentials,
                                Response.Listener {
                                    val response=it.getJSONObject("data")
                                    val success=response.getBoolean("success")
                                    if(success){
                                        val firstTry=response.getBoolean("first_try")
                                        if(firstTry){
                                            Toast.makeText(this@ForgetPassword, "OTP Sent", Toast.LENGTH_SHORT).show()
                                            val intent=Intent(this@ForgetPassword, Trial::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else{
                                            Toast.makeText(this@ForgetPassword, "OTP already sent", Toast.LENGTH_SHORT).show()
                                            val intent=Intent(this@ForgetPassword,Trial::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }
                                    else{
                                        val responseMessageServer=response.getString("errorMessage")
                                        Toast.makeText(this@ForgetPassword, responseMessageServer, Toast.LENGTH_SHORT).show()

                                    }

                                },
                                Response.ErrorListener {
                                    Toast.makeText(this@ForgetPassword, "Some Unexpected Volley Error has Occured", Toast.LENGTH_SHORT).show()

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
                        catch (e:JSONException){
                            Toast.makeText(this@ForgetPassword, "Some Unexpected Json Error has Occured", Toast.LENGTH_SHORT).show()

                        }

                    }
                    else{
                        val dialog=AlertDialog.Builder(this@ForgetPassword)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection not found")
                        dialog.setPositiveButton("Open Setting"){text,listener->
                            val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit"){text,listener->
                            ActivityCompat.finishAffinity(this@ForgetPassword)
                        }
                        dialog.create()
                        dialog.show()

                    }

                }
            }



        }
    }
}