package com.example.phase3

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.android.volley.toolbox.HttpHeaderParser
import android.R.attr.data
import com.android.volley.toolbox.HttpHeaderParser.parseCharset


fun saveInfo(context: Context?, key:String?, value:String?) {
    if (value == null || key == null || context == null) {
        return
    }
    //processing
    var info:String? = null
    if (key == "cookie"){
        info = value.split(";")[0].trim()
    }else{
        info = value
    }


    // Save in the preferences
    val sharedPreferences : SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val editor : SharedPreferences.Editor = sharedPreferences.edit()

    Log.d("Session", "save $key  $info?")
    editor.putString(key, value)
    editor.commit()
}
class CustomJsonObjectRequest(method:Int,
                              url:String,
                              jsonRequest: JSONObject?,
                              listener: Response.Listener<JSONObject>,
                              errorListener: Response.ErrorListener?, val context: Context?):
    JsonObjectRequest(method,
        url,jsonRequest,listener,errorListener){
    override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
        val headers = response?.headers
        Log.d("ParseResponse ", "header : ${response?.headers}\ncookie: ${headers?.get("Set-Cookie")}")
        val cookie = headers?.get("Set-Cookie")
        if (cookie != null){
            saveInfo(context, "cookie", cookie)
        }
//        val responseContent = String(respons.data, HttpHeaderParser.parseCharset(response.headers))
        val responseContent = String(response!!.data)
        Log.d("ParseResponse ", "json : ${responseContent}")

        return super.parseNetworkResponse(response)
    }
    //this is the headers go along with this request
    override fun getHeaders(): MutableMap<String, String> {
        var map = mutableMapOf<String, String>()

        var cookie = getInfo(context, "cookie")
        Log.d("ModifyRequest", "current cookie: $cookie")
        if (cookie != null) {
            map["cookie"] = cookie
        }
        return map
    }

}
fun getInfo(context: Context?, key:String?):String?{
    if (context ==null || key == null){
        Log.d("Session","getInfo function : empty inputs")
        return null
    }
    val sharedPreferences : SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val info = sharedPreferences.getString(key, null)
    Log.d("Session","save $key $info?")
    return info
}