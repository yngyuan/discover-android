package com.example.phase3

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recycler.Report
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class SubscribeFragment : Fragment() {


    companion object {
        fun newInstance() = SubscribeFragment()
    }

    private lateinit var recycler_view: RecyclerView
    private lateinit var view_adapter: RecyclerView.Adapter<*>
    private lateinit var view_manager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.subscribe_fragment, container, false)
        val tempView: View = inflater.inflate(R.layout.fragment_subscribe, container, false)

        var reports = ArrayList<Report>()
        var report1: Report = Report(
            r_img_url = "",
            r_description = "",
            r_id = "",
            r_location = Pair(0.0,0.0),
            r_tag_list = arrayOf("123","456"),
            r_time_str = "",
            r_title = "",
            r_tname = "",
            r_uid = "",
            r_username = ""
        )


        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(getActivity()?.getApplicationContext())
        // val url = "https://vast-silicon-255820.appspot.com/managemobile"
        val url = "http://${getString(R.string.homepage)}/${getString(R.string.subscribePath)}"
        var params = mutableMapOf<String, String>()
        //params["uid"] = MainActivity().intent.getStringExtra("uid")
        /* temporary solution, since the code above return null,which generates exception*/
        val currUser = getInfo(activity?.applicationContext, "uid")
        Log.d("SubscribeFragment", "current User ${currUser}")
        params["uid"] = if (currUser != null) currUser else "5da733a794196bf0ff5f06db"//TODO delete
        val jsonObject = JSONObject(params as Map<*, *>)
        // Request a response from the provided URL.
        var getRequest: JsonObjectRequest = CustomJsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            Response.Listener<JSONObject> { response ->
                try {
                    val jsonarr = response.getJSONArray("lists")
                    Log.d("SubscribeFragment", "size of jsonarr ${jsonarr.length()}")
                    for (x in 0 until jsonarr.length()) {
                        println(x)
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
                        reports.add(reportitem)
                    }
                    view_adapter.notifyDataSetChanged()
                }catch (e:Exception){
                    e.printStackTrace()
                    report1.r_description = "Parse exception : $e"
                }
            },
            Response.ErrorListener { report1.r_description = "Volley error: $it}"
                var toast = Toast.makeText(getActivity()?.getApplicationContext(), "Error $it}", Toast.LENGTH_LONG)
                toast.setGravity(
                    Gravity.TOP,0,0)
                toast.show()},activity?.applicationContext)
        // Add the request to the RequestQueue.
        queue!!.add(getRequest)


        view_manager = LinearLayoutManager(this.context)
        view_adapter = RecyclerAdapterForSubscribe(reports)
//
//        https //stackoverflow.com/questions/6495898/findviewbyid-in-fragment
        recycler_view = tempView.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = view_manager
            adapter = view_adapter
        }

        // Inflate the layout for this fragment
        return tempView
    }
}
