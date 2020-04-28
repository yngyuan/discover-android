package com.example.phase3

import ThemeRecyclerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.recycler.Report
import com.example.recycler.Theme
import org.json.JSONObject
import java.lang.Exception
import android.widget.SearchView
import android.widget.Toast
import android.widget.Button
import android.widget.ImageButton
//import sun.jvm.hotspot.utilities.IntArray




/**
 * A simple [Fragment] subclass.
 */
class DiscoverFragment : Fragment() {

    private lateinit var recycler_view: RecyclerView
    private lateinit var view_adapter: RecyclerView.Adapter<*>
    private lateinit var view_manager: RecyclerView.LayoutManager
    private lateinit var simpleSearchView :SearchView
    private lateinit var myView: View
    private var themes = ArrayList<Theme>()
    private var reports = ArrayList<Report>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myView= inflater.inflate(R.layout.fragment_discover, container, false)

        //set up
        simpleSearchView =
            myView.findViewById<SearchView>(R.id.search_view_discover)// inititate a search view

// perform set on query text listener event
        simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // do something on text submit
                performNewSearch(query, "Tag")//search by tag
                simpleSearchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // do something when text changes
                return false
            }
        })

        //set up nearby button

        val nearbyButtom = myView.findViewById(R.id.nearby_button) as Button

        nearbyButtom.setOnClickListener(View.OnClickListener {
            //OnCLick Stuff
            nearBy()
        })


        themes = ArrayList<Theme>()

        // Initialize a theme
        var theme1: Theme = Theme(
            t_coverimg = "https://i.imgur.com/H981AN7.jpg",
            t_name = "DOG",
            t_description = "This is a black dog theme."

        )
//        var theme2: Theme = Theme(
//            t_coverimg = "https://i.imgur.com/H981AN7.jpg",
//            t_name = "DOG1",
//            t_description = "This is a black dog theme."
//
//        )

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(getActivity()?.getApplicationContext())
        // ifconfig en0
        val url = "http://${getString(R.string.homepage)}/${getString(R.string.viewPath)}"

        // Request a response from the provided URL.
        var getRequest: JsonObjectRequest = CustomJsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                try {
                    // Parse the json object here
                    val jsonarr = response.getJSONArray("lists")
                    for (x in 0 until jsonarr.length()) {
                        var themeitem: Theme = Theme(
                            t_coverimg = "https://i.imgur.com/H981AN7.jpg",
                            t_name = "DOG",
                            t_description = "This is a black dog theme."
                        )

                        themeitem.t_coverimg = jsonarr.getJSONObject(x).getString("t_coverimage")
                        themeitem.t_name = jsonarr.getJSONObject(x).getString("t_name")
                        themeitem.t_description = jsonarr.getJSONObject(x).getString("t_description")
                        themes.add(themeitem)
                    }
                    view_adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                    theme1.t_description = "Parse exception : $e"
                }
            },
            Response.ErrorListener{
                theme1.t_description = "Volly error: $it}"},parentFragment?.requireContext())

        // Add the request to the RequestQueue.
        queue.add(getRequest)

        val viewOneUrl = "http://${getString(R.string.homepage)}/${getString(R.string.viewOnePath)}"
        view_manager = LinearLayoutManager(this.context)
        view_adapter = ThemeRecyclerAdapter(themes, getInfo(activity?.applicationContext, "uid")!!,
            viewOneUrl)

        recycler_view = myView.findViewById<RecyclerView>(R.id.recycler_view_discover).apply {
            layoutManager = view_manager
            adapter = view_adapter
        }

        // Inflate the layout for this fragment
        return myView
    }
    public fun nearBy(){
        performNewSearch("", "Location")
    }
    fun performNewSearch(query : String, field:String){
        themes.clear()

        //change adaptor and related
        reports.clear()
        view_adapter = RecyclerAdapterSearch(reports)
        recycler_view = myView.findViewById<RecyclerView>(R.id.recycler_view_discover).apply {
            layoutManager = view_manager
            adapter = view_adapter
        }
        val queue = Volley.newRequestQueue(getActivity()?.getApplicationContext())
        val url = "http://${getString(R.string.homepage)}/${getString(R.string.searchPath)}"
        var params = mutableMapOf<String, String>()

        params["field"] = field
        if(field.equals("Location")){
            val latitude = getInfo(activity?.applicationContext, "latitude")
            val longitude = getInfo(activity?.applicationContext, "longitude")
            Log.d("DiscoverFragment", "${latitude},${longitude}")
            params["keyword"] = "${latitude},${longitude}"
        }else{
            params["keyword"] = query
        }
        Log.d(tag,"keyword : ${params["keyword"]} , field : ${params["field"]}")
        val jsonObject = JSONObject(params as Map<*, *>)
        // Request a response from the provided URL.
        var getRequest: JsonObjectRequest = CustomJsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener<JSONObject> { response ->
                try {
                    // Parse the json object here
                    val jsonarr = response.getJSONArray("lists")
                    Log.d("DiscoverFragment", "response of search by tag : size ${jsonarr.length()}")
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
                        var rawLocationStr = currJObj.getString("r_location")
                        //here location info may be empty
                        val reportLocation = if (rawLocationStr.length == 0) listOf("0","0")  else rawLocationStr.trim().substring(1,rawLocationStr.length - 1).split(",")
//                        val reportLocation =
//                        reportitem.r_location = Pair<Double, Double>(reportLocation[0].toDouble(), reportLocation[1].toDouble())
                        reports.add(reportitem)
                    }
                    view_adapter.notifyDataSetChanged()
                }catch (e:Exception){
                    //report1.r_description = "Parse exception : $e"
                    var toast = Toast.makeText(activity?.applicationContext, "Exception $e}", Toast.LENGTH_LONG)
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
        view_adapter.notifyDataSetChanged()
    }


}
