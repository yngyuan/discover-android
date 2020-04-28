package com.example.phase3
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.*
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.example.phase3.MainActivity as MainActivity


class LoginActivity : AppCompatActivity() {
    private var myQueue:RequestQueue?=null
    private var TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myQueue = Volley.newRequestQueue(applicationContext)
        setContentView(R.layout.activity_login)

    }


    fun buLogin(view:View){
        // user login
        //this url is the local ip address of my flask server, since it can't directly connect to
        //localhost:5000,  here i use the local ip address
        val url="http:/${getString(R.string.homepage)}/${getString(R.string.userLoginPath)}"
        //once the button is clicked, here it will perform a aysnc task to do the login, if
        //login in successfully, then it will go to management page
        MyAsyncTask().execute(url, etEmail.text.toString(), etPassword.text.toString())
    }

    //AsyncTask performing post request for sign in
    inner class MyAsyncTask: AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            //Before task started
        }

        override fun doInBackground(vararg p0: String): String {
            try {

                val url= p0[0]
                val params = mutableMapOf<String, String>()
                params["email"] = p0[1]
                params["password"] = p0[2]
                val jsonObject = JSONObject(params as Map<*, *>)
                var postRequest:JsonObjectRequest= CustomJsonObjectRequest(Request.Method.POST,url,jsonObject,
                    Response.Listener<JSONObject>{ response->
                        try{
                            onProgressUpdate(response.toString(), params["email"], params["password"])
                        }catch(e:java.lang.Exception){
                            e.printStackTrace()
                            var toast = Toast.makeText(applicationContext, "(Listener)Login fail "+e.message , Toast.LENGTH_LONG)
                            toast.setGravity(
                                Gravity.TOP,0,0)
                            toast.show()
                        }
                    },
                    Response.ErrorListener {volleyError->
//                        logger.debug("volleyEroor in login :" + volleyError.message)
                        var toast = Toast.makeText(applicationContext, "(Error)Login fail " + volleyError.message, Toast.LENGTH_LONG)
                        toast.setGravity(
                            Gravity.TOP,0,0)
                        toast.show()
                    }, applicationContext)
                
                postRequest.setShouldCache(false)
                //add the request to queue, then it will be executed
                myQueue!!.add(postRequest)


            }catch (ex:Exception){}
            return " "

        }
        //this function is trying to process the return data after success connection
        override fun onProgressUpdate(vararg values: String?) =
            try{
                var json= JSONObject(values[0])
                var email = values[1]
                var password = values[2]

                if (json.getString("mes").equals("success")){
                    val userID = json.getString("uid")

                    //successfully login in, add uid into session
//                    val session = SessionManager(applicationContext)
//                    session.createLoginSession(email, password)
                    var toast = Toast.makeText(applicationContext, "add into session" , Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()


                    var intent = Intent(applicationContext, MainActivity::class.java)
                    //one way to pass userid
                    intent.putExtra("uid", userID)
                    saveInfo(applicationContext, "uid", userID)
                    finish()//finish loginActivity
                    startActivity(intent)



                }else{
                    var toast = Toast.makeText(applicationContext,json.getString("mes"), Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
//                finish()//this will force you close the app.

            }catch (ex:Exception){
                var toast = Toast.makeText(applicationContext,ex.message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.show()
            }

        override fun onPostExecute(result: String?) {

            //after task done
        }


    }
    fun backToRegister(v: View) {
        finish()
        startActivity(Intent(applicationContext, SignupActivity::class.java))

    }




}
