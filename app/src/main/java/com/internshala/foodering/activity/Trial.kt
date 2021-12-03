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

class Trial : AppCompatActivity() {
    lateinit var txtOTP: TextView
    lateinit var etOTP: EditText
    lateinit var etNewPass: EditText
    lateinit var etNewConfirmPass: EditText
    lateinit var btnSubmit: Button
    lateinit var etMobile:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trial)
        txtOTP=findViewById(R.id.txtOTP)
        etOTP=findViewById(R.id.etOTp)
        etNewPass=findViewById(R.id.etNewPass)
        etNewConfirmPass=findViewById(R.id.etNewConfirmPass)
        etMobile=findViewById(R.id.etMobile)
        btnSubmit=findViewById(R.id.btnSubmit)


        btnSubmit.setOnClickListener {
            if(etOTP.text.isBlank()){
                etOTP.error="OTP missing"
            }
            else{
                if(etNewPass.text.isBlank()||etNewPass.text.length<=4){
                    etNewPass.error="Invalid Password"
                }
                else{
                    if(etNewConfirmPass.text.isBlank()){
                        etNewConfirmPass.error="Confirm password missing"
                    }
                    else{
                        if((etNewPass.text.toString() == etNewConfirmPass.text.toString())){
                            if(ConnectionManager().checkConnectivity(this@Trial)){
                                try{
                                    val loginUser= JSONObject()
                                    loginUser.put("mobile_number",etMobile.text)
                                    loginUser.put("password",etNewPass.text.toString())
                                    loginUser.put("otp",etOTP.text.toString())
                                    val queue = Volley.newRequestQueue(this@Trial)
                                    val URL="http://13.235.250.119/v2/reset_password/fetch_result"
                                    val jsonObjectRequest= object :JsonObjectRequest(
                                        Method.POST,
                                        URL,
                                        loginUser,
                                        Response.Listener {
                                            val response =it.getJSONObject("data")
                                            val success = response.getBoolean("success")
                                            if(success){
                                                val serverMessage=response.getString("successMessage")
                                                Toast.makeText(this@Trial, serverMessage, Toast.LENGTH_SHORT).show()
                                                val intent=Intent(this@Trial,LoginActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                            }
                                            else{
                                                val responseMessageServer=response.getString("errorMessage")
                                                Toast.makeText(this@Trial, responseMessageServer, Toast.LENGTH_SHORT).show()
                                            }

                                        },
                                        Response.ErrorListener {
                                            Toast.makeText(this@Trial, "Some error occurred!!", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    {
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String,String>()
                                            headers["Content-type"]="application/json"
                                            headers["token"]="9c10ced414a682"
                                            return headers

                                        }
                                    }
                                    queue.add(jsonObjectRequest)
                                }
                                catch (e:JSONException){
                                    Toast.makeText(this@Trial,
                                        "Some unexpected error occurred",
                                        Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            else{

                                val dialog= AlertDialog.Builder(this@Trial)
                                dialog.setTitle("Error ")
                                dialog.setMessage("Internet Connection not Found")
                                dialog.setPositiveButton("Open Settings"){text,listener->
                                    val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                    startActivity(settingsIntent)
                                    finish()
                                }
                                dialog.setNegativeButton("Exit"){text,listener->
                                    ActivityCompat.finishAffinity(this@Trial)
                                }
                                dialog.create()
                                dialog.show()
                            }
                        }
                        else{
                            etNewConfirmPass.error="passwords don't match"
                        }
                    }
                }
            }

        }

    }
}