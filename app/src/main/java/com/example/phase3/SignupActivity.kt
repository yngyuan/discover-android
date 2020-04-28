package com.example.phase3

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.etEmail
import kotlinx.android.synthetic.main.activity_signup.etPassword
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private var myQueue: RequestQueue?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        myQueue = Volley.newRequestQueue(applicationContext)
        setContentView(R.layout.activity_signup)

    }
    fun buRegister(view:View){
        // user login
        //this url is the local ip address of my flask server, since it can't directly connect to
        //localhost:5000,  here i use the local ip address
        val url="http:/${getString(R.string.homepage)}/${getString(R.string.registerPath)}"
        //once the button is clicked, here it will perform a aysnc task to do the login, if
        //login in successfully, then it will go to management page
        MyAsyncTask().execute(url, etEmail.text.toString(), etPassword.text.toString(), etName.text.toString())
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
                params["name"] = p0[3]
                val jsonObject = JSONObject(params as Map<*, *>)
                var postRequest: JsonObjectRequest = CustomJsonObjectRequest(
                    Request.Method.POST,url,jsonObject,
                    Response.Listener<JSONObject>{ response->
                        try{
                            onProgressUpdate(response.toString())
                        }catch(e:java.lang.Exception){
                            e.printStackTrace()
                            var toast = Toast.makeText(applicationContext, "(Listener)Register fail "+e.message , Toast.LENGTH_LONG)
                            toast.setGravity(
                                Gravity.TOP,0,0)
                            toast.show()
                        }
                    },
                    Response.ErrorListener { volleyError->
                        //                        logger.debug("volleyEroor in login :" + volleyError.message)
                        var toast = Toast.makeText(applicationContext, "(Error)Register fail " + volleyError.message, Toast.LENGTH_LONG)
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
                if (json.getString("mes").equals("success")){
                    val userID = json.getString("uid")
                    saveInfo(applicationContext, "uid", userID)

                    var intent = Intent(applicationContext, MainActivity::class.java)
                    //one way to pass userid
                    intent.putExtra("uid", userID)
                    finish()//finish RigisterActivity
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
    fun backToLogin(v: View) {
        finish()
        startActivity(Intent(applicationContext, LoginActivity::class.java))

    }

//    companion object {
//        fun newIntent(context: Context) = Intent(context, SignupActivity::class.java)
//    }
}
