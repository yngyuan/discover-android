package com.example.phase3

import android.content.Intent
import android.content.Intent.getIntent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.recycler.Report
import com.example.recycler.Theme
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class ManageFragment : Fragment() {

    private lateinit var recycler_view: RecyclerView
    private lateinit var recycler_view_theme: RecyclerView
    private lateinit var view_adapter: RecyclerView.Adapter<*>
    private lateinit var view_adapter_theme: RecyclerView.Adapter<*>
    private lateinit var view_manager: RecyclerView.LayoutManager
    private lateinit var view_manager_theme: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val tempView: View = inflater.inflate(R.layout.fragment_manage, container, false)

        var reports = ArrayList<Report>()
        var themes = ArrayList<Theme>()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(getActivity()?.getApplicationContext())
        val url = "http://${getString(R.string.homepage)}/${getString(R.string.managePath)}"
        // format user id json
        //var intent = Intent(getActivity()?.getApplicationContext(), MainActivity::class.java)
        var params = mutableMapOf<String, String>()
        //params["uid"] = MainActivity().intent.getStringExtra("uid")
        /* temporary solution, since the code above return null,which generates exception*/
        val currUser = getInfo(activity?.applicationContext, "uid")
        Log.d("ManageFragment", "current User${currUser}")
        params["uid"] = if (currUser != null) currUser else "5da733a794196bf0ff5f06db"//TODO delete
        val jsonObject = JSONObject(params as Map<*, *>)
        // Request a response from the provided URL.
        var getRequest: JsonObjectRequest = CustomJsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener<JSONObject> { response ->
                try {
                    // Parse the json object here
                    val jsonarr = response.getJSONArray("lists")
                    val jsonArrTh = response.getJSONArray("themes")
                    for (x in 0 until jsonarr.length()) {
                        var reportitem: Report = Report(
                            r_img_url = "https://i.imgur.com/H981AN7.jpg",
                            r_description = "This is",
                            r_id = "report_id",
                            r_location = Pair(3.3, 3.333),
                            r_tag_list = arrayOf("Dog", "Travle"),
                            r_time_str = "time and date here",
                            r_title = "Hello doggy!",
                            r_tname = "travel",
                            r_uid = "admin_id",
                            r_username = "admin"
                        )
                        val currJObj = jsonarr.getJSONObject(x)
                        reportitem.r_img_url = currJObj.getString("r_url")
                        reportitem.r_description = currJObj.getString("r_description")
                        reportitem.r_title = if (currJObj.has("r_title")) currJObj.getString("r_title") else "unknown"
                        reportitem.r_time_str = currJObj.getString("r_time")
                        reportitem.r_tname = currJObj.getString("r_tname")
                        reportitem.r_username = currJObj.getString("r_username")
                        val reportLocation = currJObj.getString("r_location").substring(1,currJObj.getString("r_location").length - 1).split(",")
                        reportitem.r_location = Pair<Double, Double>(reportLocation[0].toDouble(), reportLocation[1].toDouble())
//                        reportitem.r_tag_list = currJObj.getString("r_tag_list")
                        reports.add(reportitem)
                    }
                    for (x in 0 until jsonArrTh.length()) {
                        var themeItem: Theme = Theme(
                            t_coverimg = "https://i.imgur.com/H981AN7.jpg",
                            t_description = "This is",
                            t_name = "travel"
                        )
                        val currJObj = jsonArrTh.getJSONObject(x)
                        themeItem.t_coverimg = currJObj.getString("t_coverimage")
                        themeItem.t_description = currJObj.getString("t_description")
                        themeItem.t_name = currJObj.getString("t_name")
                        themes.add(themeItem)
                    }
                    view_adapter.notifyDataSetChanged()
                    view_adapter_theme.notifyDataSetChanged()
                }catch (e:Exception){
                    e.printStackTrace()
                    //report1.r_description = "Parse exception : $e"
                    var toast = Toast.makeText(getActivity()?.getApplicationContext(), "Exception $e}", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP,0,0)
                    toast.show()
                }
            },
            Response.ErrorListener {
                //                var toast = Toast.makeText(getActivity()?.getApplicationContext(), "Error $it}", Toast.LENGTH_LONG)
//                toast.setGravity(Gravity.TOP,0,0)
//                toast.show()
            }, activity?.applicationContext)
        // Add the request to the RequestQueue.
        queue!!.add(getRequest)

        view_manager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL ,false)
        view_manager_theme = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL ,false)
        view_adapter = RecyclerAdapterManage(reports)
        view_adapter_theme = RecyclerAdapterManageTheme(themes)

        recycler_view = tempView.findViewById<RecyclerView>(R.id.recycler_view_manage).apply {
            layoutManager = view_manager
            adapter = view_adapter
        }
        recycler_view_theme = tempView.findViewById<RecyclerView>(R.id.recycler_view_manage_theme).apply {
            layoutManager = view_manager_theme
            adapter = view_adapter_theme
        }
        // Inflate the layout for this fragment
        return tempView
    }

    fun delete(){

    }
}